package titans.noaa.core;

import common.RmObjects;
import java.io.File;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rm.titansdata.properties.Bounds;
import rm.titansdata.properties.Dimensions;
import rm.titansdata.raster.RasterObj;
import titans.noaa.grib.GribFile;
import titans.noaa.grib.NetCdfExtractor;
import titans.noaa.netcdf.NetCdfFile;
import titans.noaa.netcdf.NetCdfFileManager;
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
    this.degribExe = RmObjects.fileExists(degribExe, "File '%s' does not exist", degribExe) ;
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
    GribFile result;
    if (gribFile.exists()) {
      result = gribFile;
    } else {
      NoaaGribSource source = this.getGribSource();
      result = source.download(gribFile);
//      if (this.onIsGribCroppable()) {
//        result = this.crop(result, bounds, gribFile);
//      }
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
      if (!gribFile.delete()) {
        throw new RuntimeException("Grib file not deleted");
      }
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
    GribFile result = GribFile.create(gribRootFolder, var, subfolderId, datetimeref, forecaststep, filename);
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
      Logger.getLogger(this.getClass().getName()) //
              .log(Level.INFO, "removing gribFile: {0}", gribFile);
      if (!gribFile.delete()) {
        throw new RuntimeException("File not deleted");
      } else {
        Logger.getLogger(this.getClass().getName()) //
                .log(Level.INFO, "deleted file: ${0}", gribFile.grib);
      }
    } 
    List<File> parent = NetCdfFile.getVarNameWildCardFiles(
            this.netCdfRootFolder, subfolderId, datetimeref, forecaststep);
    for (File file : parent) {
      boolean deleted = file.delete();
      if (!deleted) {
        throw new RuntimeException("File not deleted.");
      }
    }
  }

  /**
   *
   * @param dateTime
   */
  @Override
  public void removeRastersBefore(ZonedDateTime dateTime) {
    NetCdfFileManager fileManager = new NetCdfFileManager( //
            this.netCdfRootFolder, this.subfolderId);
    fileManager.removeRastersBefore(dateTime);
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
}
