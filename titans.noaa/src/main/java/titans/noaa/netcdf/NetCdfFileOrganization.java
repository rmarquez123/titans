package titans.noaa.netcdf;

import common.RmObjects;
import java.io.File;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.measure.unit.Unit;
import titans.noaa.core.NoaaParameter;
import titans.noaa.core.NoaaVariable;
import titans.noaa.grib.ForecastTimeReference;

/**
 *
 * @author Ricardo Marquez
 */
public class NetCdfFileOrganization {

  private final File baseFolder;
  private final int subFolderId;
  private final int fcststep;
  private final ZonedDateTime datetime;
  private final NoaaVariable var;

  /**
   *
   * @param baseFolder
   * @param subFolderId
   * @param fcststep
   * @param datetime
   * @param var
   */
  public NetCdfFileOrganization( //
          File baseFolder, int subFolderId, int fcststep, ZonedDateTime datetime, NoaaVariable var) {
    this.baseFolder = baseFolder;
    this.subFolderId = subFolderId;
    this.datetime = datetime;
    this.fcststep = fcststep;
    this.var = var;
  }

  /**
   *
   * @return
   */
  public File getFile() {
    String subfilepath = this.getSubFilePath();
    File realBaseFolder = this.getBaseFolder();
    File result = new File(realBaseFolder, subfilepath.replaceAll("\\\\", File.separator));
    return result;
  }

  /**
   *
   * @return
   */
  private String getSubFilePath() {
    String filename = this.getFileName();
    String dateSubPath = this.getDateSubPath();
    String child = String.format("%s/%s", dateSubPath, filename);
    return child;
  }

  /**
   *
   * @return
   */
  private String getFileName() {
    String formattedDate = RmObjects.formatUtc(datetime, "yyyyMMddHHmm");
    String varName = var.getGribVarName();
    String varNameText = varName.replace(".", "_dot_");
    String filename = String.format("%s_%s_%03d.nc", varNameText, formattedDate, this.fcststep);
    return filename;
  }

  /**
   *
   * @return
   */
  private String getWildCardVarFileName() {
    String formatedDate = RmObjects.formatUtc(datetime, "yyyyMMddHHmm");
    String varNameText = "*";
    String filename = String.format("%s_%s_%03d.nc", varNameText, formatedDate, this.fcststep);
    return filename;
  }

  /**
   *
   * @return
   */
  private File getBaseFolder() {
    String subfolder = this.subFolderId < 0 ? "test" : String.format("%04d", this.subFolderId);
    File realBaseFolder = new File(baseFolder, subfolder.replaceAll("\\\\", File.separator));
    return realBaseFolder;
  }

  /**
   *
   * @return
   */
  public NetCdfFile getNetCdfFile() {
    String gribVarName = this.var.getGribVarName();
    ForecastTimeReference d = new ForecastTimeReference(0, fcststep);
    NoaaParameter namParameter = new NoaaParameter(gribVarName, datetime, d, gribVarName, Unit.ONE);
    NetCdfFile result = NetCdfFile.create(baseFolder, subFolderId, namParameter);
    return result;
  }

  /**
   *
   * @return
   */
  private String getDateSubPath() {
    int year = this.datetime.getYear();
    int month = this.datetime.getMonthValue();
    int day = this.datetime.getDayOfMonth();
    String result = String.format("%04d/%02d/%02d", year, month, day);
    return result;
  }

  /**
   *
   * @return
   */
  List<File> getFiles() {
    String filename = this.getWildCardVarFileName();
    String dateSubPath = this.getDateSubPath();
    File subBaseFolder = this.getBaseFolder();
    File targetBaseFolder = new File(subBaseFolder, dateSubPath.replaceAll("\\\\", File.separator));
    File[] a = targetBaseFolder.listFiles((File dir, String name) -> {
      ///   should compare with filename which has a wildcard (*).
      //  For example, if name is abcdef and wildcard is ab*f, then this would be a match
      //  If name is abcd and wildcard is ab*f, then this would not be a match.
      String regex = filename.replace("*", ".*");
      boolean matches = name.matches(regex);
      return matches;
    });

    List<File> result = new ArrayList<>();
    if (a != null) {
      result.addAll(Arrays.asList(a));
    }
    return result;
  }
}
