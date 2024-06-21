package titans.noaa.core;

import com.google.common.collect.TreeTraverser;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.Comparator;
import rm.titansdata.properties.Bounds;
import rm.titansdata.properties.Dimensions;
import rm.titansdata.raster.RasterObj;
import titans.noaa.grib.GribFile;
import titans.noaa.grib.NetCdfExtractor;
import titans.noaa.netcdf.NetCdfFile;
import titans.noaa.netcdf.NetCdfRasterProvider;

/**
 *
 * @author Ricardo Marquez
 */
public abstract class NoaaGribImporter implements NoaaImporter {

  private final NetCdfRasterProvider rasterLoader = new NetCdfRasterProvider();
  private final File gribRootFolder;
  private final File netCdfRootFolder;
  private final int subfolderId;
  private final File degribExe;

  /**
   *
   * @param gribRootFolder
   * @param netCdfRootFolder
   * @param subfolderId
   * @param degribExe
   */
  public NoaaGribImporter(File gribRootFolder, File netCdfRootFolder, int subfolderId, File degribExe) {
    this.gribRootFolder = gribRootFolder;
    this.netCdfRootFolder = netCdfRootFolder;
    this.subfolderId = subfolderId;
    this.degribExe = degribExe;
  }

  /**
   *
   * @param var
   * @param datetime
   * @param fcststep
   * @param bounds
   * @param dims
   * @return
   */
  @Override
  public final RasterObj getRaster(NoaaVariable var, // 
          ZonedDateTime datetime, int fcststep, Bounds bounds, Dimensions dims) {
    NetCdfFile netCdfFile;
    NetCdfExtractor extractor = new NetCdfExtractor(this.degribExe, this.netCdfRootFolder, this.subfolderId, var);
    if (!extractor.netCdfFileExists(datetime, fcststep)) {
      netCdfFile = this.downloadAndExtract(extractor, bounds, datetime, fcststep);
    } else {
      netCdfFile = extractor.getNetCdfFile(datetime, fcststep);
    }
    if (!netCdfFile.exists()) {
      String message = "NetCdf File not extracted: " + netCdfFile.file;
      throw new RuntimeException(message);
    }
    RasterObj result;
    if (bounds == null) {
      result = this.rasterLoader.getRaster(netCdfFile);
    } else {
      result = this.rasterLoader.getRaster(netCdfFile, bounds, dims);
    }

    return result;
  }

  /**
   *
   * @param extractor
   * @param datetimeref
   * @param forecaststep
   * @return
   */
  private NetCdfFile downloadAndExtract(NetCdfExtractor extractor, //
          Bounds bounds, ZonedDateTime datetimeref, int forecaststep) {
    NetCdfFile result;
    NoaaVariable var = extractor.getVar();
    GribFile gribFile = this.downloadGribFile(var, bounds, datetimeref, forecaststep);
    result = extractor.extract(gribFile);
    return result;
  }

  /**
   *
   * @param forecaststep
   * @param datetimeref
   * @return
   */
  private GribFile downloadGribFile(NoaaVariable var, Bounds bounds, // 
          ZonedDateTime datetimeref, int forecaststep) {
    GribFile gribFile = this.getGribFile(var, datetimeref, forecaststep);
    GribFile subPathGribFile = gribFile.setSubPath(subfolderId);
    GribFile result;
    if (gribFile.exists()) {
      result = this.crop(gribFile, bounds, subPathGribFile);
    } else if (subPathGribFile.exists()) {
      result = subPathGribFile;
    } else {
      NoaaGribSource source = this.getGribSource();
      result = source.download(gribFile);
      if (this.onIsGribCroppable()) {
        result = this.crop(result, bounds, subPathGribFile);
      }

    }
    return result;
  }

  /**
   *
   * @param gribFile
   * @param bounds
   * @param subPathGribFile
   * @return
   */
  private GribFile crop(GribFile gribFile, Bounds bounds, GribFile subPathGribFile) {
    NoaaGribSource source = this.getGribSource();
    GribFile result;
    boolean successful = source.crop(this.degribExe, gribFile, this.subfolderId, bounds);
    if (successful) {
      gribFile.delete();
      result = subPathGribFile;
    } else {
      result = gribFile;
    }
    return result;
  }

  /**
   *
   * @param var
   * @param forecaststep
   * @param datetimeref
   * @return
   */
  public final GribFile getGribFile(NoaaVariable var, ZonedDateTime datetimeref, int forecaststep) {
    String filename = this.getGribFileName(var, datetimeref, forecaststep);
    String fileNameCorrected = filename.replace("\\", File.separator);
    File grib = new File(this.gribRootFolder, fileNameCorrected);
    File gribIdx = new File(this.gribRootFolder, fileNameCorrected + ".idx");
    GribFile result = new GribFile(datetimeref, forecaststep, var, grib, gribIdx);
    return result;
  }

  /**
   *
   * @param var
   * @param datetimeref
   * @param forecaststep
   */
  @Override
  public void removeIntermediateFiles( //
          NoaaVariable var, ZonedDateTime datetimeref, int forecaststep) {
    GribFile gribFile = this.getGribFile(var, datetimeref, forecaststep);
    if (gribFile.exists()) {
      gribFile.delete();
    }
  }

  /**
   *
   * @param dateTime
   */
  @Override
  public void removeRastersBefore(ZonedDateTime dateTime) {
    String subPath = String.format("%04d", this.subfolderId);
    GribFileManager fileManager = new GribFileManager(this.netCdfRootFolder, this.subfolderId);
    File rootFolder = new File(this.netCdfRootFolder, subPath);
    
    TreeTraverser<File> traverser = Files.fileTreeTraverser();
    Iterable<File> yearFiles = traverser.children(rootFolder);
    
    for (File yearFile : yearFiles) {
      if (yearFile.isFile()) {
        continue;
      }
      
      int year;
      try {
        year = Integer.parseInt(yearFile.getName());
      } catch (Exception ex) {
        continue;  // Skip this iteration if parsing fails
      }

      if (year < dateTime.getYear()) {
        this.removeFolder(yearFile);
      } else if (year == dateTime.getYear()) {
        Iterable<File> monthFiles = traverser.children(yearFile);
        for (File monthFile : monthFiles) {
          if (!monthFile.isDirectory()) {
            continue;
          }
          int month;
          try {
            month = Integer.parseInt(monthFile.getName());
          } catch (Exception ex) {
            continue;  // Skip this iteration if parsing fails
          }
          if (month < dateTime.getMonthValue()) {
            this.removeFolder(monthFile);
          } else if (month == dateTime.getMonthValue()) {
            Iterable<File> dayFiles = traverser.children(monthFile);
            for (File dayFile : dayFiles) {
              if (!dayFile.isDirectory()) {
                continue; // Skip files, process only directories
              }
              int day;
              try {
                day = Integer.parseInt(dayFile.getName());
              } catch (Exception ex) {
                continue; // Skip this iteration if parsing fails
              }
              if (day < dateTime.getDayOfMonth()) {
                this.removeFolder(dayFile);
              }
            }
          }
        }
      }
    }
  }

  /**
   *
   * @param forecaststep
   * @param datetimeref
   * @return
   */
  private String getGribFileName(NoaaVariable var, ZonedDateTime datetimeref, int fcstHour) {
    String filename = this.onGetGribFileName(var, datetimeref, fcstHour);
    return filename;
  }

  /**
   *
   * @param var
   * @param datetimeref
   * @param fcstHour
   * @return
   */
  protected abstract String onGetGribFileName(NoaaVariable var, ZonedDateTime datetimeref, int fcstHour);

  /**
   *
   * @return
   */
  protected abstract NoaaGribSource getGribSource();

  /**
   *
   * @return
   */
  protected boolean onIsGribCroppable() {
    return false;
  }

  /**
   *
   * @param folder
   */
  private void removeFolder(File folder) {
    try {
      java.nio.file.Files.walk(folder.toPath())
              .sorted(Comparator.reverseOrder())
              .map(Path::toFile)
              .forEach(File::delete);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
