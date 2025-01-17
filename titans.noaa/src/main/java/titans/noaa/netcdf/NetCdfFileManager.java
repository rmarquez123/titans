package titans.noaa.netcdf;

import com.google.common.collect.TreeTraverser;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rmarq
 */
public class NetCdfFileManager {

  private final File netCdfRootFolder;
  private final int subfolderId;

  /**
   *
   * @param netCdfRootFolder
   * @param subfolderId
   */
  public NetCdfFileManager(File netCdfRootFolder, int subfolderId) {
    this.netCdfRootFolder = netCdfRootFolder;
    this.subfolderId = subfolderId;
  }

  /**
   *
   * @param dateTime
   */
  public void removeRastersBefore(ZonedDateTime dateTime) {
    String subPath = String.format("%04d", this.subfolderId);
    File rootFolder = new File(this.netCdfRootFolder, subPath);
    TreeTraverser<File> traverser = Files.fileTreeTraverser();
    Logger.getLogger(this.getClass().getName()) //
            .log(Level.INFO, "removing rasters in rootfolder {0}", rootFolder);
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
   * @param folder
   */
  private void removeFolder(File folder) {
    try {
      java.nio.file.Files.walk(folder.toPath())
              .sorted(Comparator.reverseOrder())
              .map(Path::toFile)
              .forEach(f -> {
                if (!f.delete()) {
                  throw new RuntimeException("File not deleted.");
                }
              });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
