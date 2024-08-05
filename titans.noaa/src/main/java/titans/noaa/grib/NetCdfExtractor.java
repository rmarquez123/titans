package titans.noaa.grib;

import common.RmExceptions;
import common.RmObjects;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import titans.noaa.core.NoaaVariable;
import titans.noaa.netcdf.NetCdfFile;
import titans.noaa.netcdf.NetCdfFileOrganization;

/**
 *
 * @author Ricardo Marquez
 */
public class NetCdfExtractor {

  private final File degribExe;
  private final File netcdfdir;
  private final int subFolderId;
  private final NoaaVariable var;

  public NetCdfExtractor(File degribExe, File netcdfdir, int subFolderId, NoaaVariable var) {
    this.degribExe = RmObjects.fileExists(degribExe, "File '%s' does not exist.", degribExe);
    this.netcdfdir = netcdfdir;
    this.subFolderId = subFolderId;
    this.var = var;
  }

  /**
   *
   * @return
   */
  public NoaaVariable getVar() {
    return var;
  }

  /**
   *
   * @param gribFile
   * @return
   */
  public NetCdfFile extract(GribFile gribFile) {
    NetCdfFile netCdfFile = this.getNetCdfFile(gribFile);
    this.createParentFileIfDoesNotExists(netCdfFile);
    if (!netCdfFile.exists()) {
      ProcessBuilder processBuilder = this.createProcessBuilder(gribFile, netCdfFile);
      this.runProcess(processBuilder);
    }
    return netCdfFile;
  }

  /**
   *
   * @param netCdfFile
   */
  private void createParentFileIfDoesNotExists(NetCdfFile netCdfFile) {
    RmObjects.createFileIfDoesNotExists(netCdfFile.file.getParentFile());
  }

  /**
   *
   * @param process
   * @throws RuntimeException
   */
  private void runProcess(ProcessBuilder process) throws RuntimeException {
    try {
      Process p = process.start();
      int exitValue = p.waitFor();
      if (exitValue != 0) {
        String errorMsg;
        try (InputStream errorstream = p.getErrorStream()) { 
          if (errorstream != null) {
            errorMsg = toMessage(errorstream);
          } else {
            errorMsg = "";
          }
        }
        throw RmExceptions.create("Process failed with exit value %d. %s", exitValue, errorMsg);
      }
      List<String> lines;
      try (InputStream in = p.getInputStream()) {
        lines = toListOfLines(in);
      }
      printLines(lines);
      
      try (InputStream errorstream = p.getErrorStream()) {
        String errorMsg;
        if (errorstream != null && !(errorMsg = toMessage(errorstream)).isEmpty()) {
          printLines(Arrays.asList(errorMsg));
          throw new RuntimeException(errorMsg);
        }
      }
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   *
   * @param gribFile
   * @param netCdfFile
   * @return
   */
  private ProcessBuilder createProcessBuilder( //
          GribFile gribFile, NetCdfFile netCdfFile) {
    String messageNum = this.getVarMessageNum(gribFile);

    ProcessBuilder result;
    if (this.degribExe.getName().contains("wgrib2")) {
      int firstindex = messageNum.indexOf(":", 0);
      int seondindex = messageNum.indexOf(":", firstindex + 1);
      messageNum = messageNum.substring(seondindex + 1, messageNum.length() - 1);
      messageNum = RmObjects.isWindows() ? "\"" + messageNum + "\"" : messageNum;
      result = new ProcessBuilder(
              this.degribExe.getAbsolutePath(),
              gribFile.grib.getAbsolutePath().replace(".gz", ""),
              "-netcdf", netCdfFile.file.getAbsolutePath(),
              "-match", messageNum);
    } else {
      result = new ProcessBuilder(
              this.degribExe.getAbsolutePath().replace(".exe", ""),
              gribFile.grib.getAbsolutePath().replace(".gz", ""),
              "-msg", messageNum,
              "-C",
              "-NetCDF", "3",
              "-out", netCdfFile.file.getAbsolutePath()
      );
    }
    File workingDirectory = gribFile.grib.getParentFile();
    result.directory(workingDirectory);
    result.redirectErrorStream(true);
    return result;
  }

  private static String toMessage(InputStream errorstream) throws IOException {
    return String.join("\n", toListOfLines(errorstream));
  }

  private static List<String> toListOfLines(InputStream in) throws IOException {
    return IOUtils.readLines(in, Charset.forName("utf8"));
  }

  /**
   *
   * @param gribFile
   * @return
   */
  private NetCdfFile getNetCdfFile(GribFile gribFile) {
    ZonedDateTime datetimeref = gribFile.datetimeref;
    int fcststep = gribFile.fcststep;
    NetCdfFile result = this.getNetCdfFile(datetimeref, fcststep);
    return result;
  }

  /**
   *
   * @param datetimeref
   * @param forecaststep
   * @return
   */
  public boolean netCdfFileExists(ZonedDateTime datetimeref, int forecaststep) {
    NetCdfFile netCdfFile = this.getNetCdfFile(datetimeref, forecaststep);
    boolean result = netCdfFile.exists();
    return result;
  }

  /**
   *
   * @param datetimeref
   * @param forecaststep
   * @return
   */
  public NetCdfFile getNetCdfFile(ZonedDateTime datetimeref, int forecaststep) {
    NetCdfFileOrganization org = new NetCdfFileOrganization( //
            this.netcdfdir, this.subFolderId, forecaststep, datetimeref, var);
    NetCdfFile result = org.getNetCdfFile();
    return result;
  }

  /**
   *
   * @param lines
   */
  private void printLines(List<String> lines) {
    if (lines != null) {
      for (String string : lines) {
        Logger.getLogger(NetCdfExtractor.class.getName()) //
                .log(Level.INFO, "messages:\n{0}", string);
      }
    }
  }

  /**
   *
   * @param gribFile
   * @return
   */
  private String getVarMessageNum(GribFile gribFile) {
    GribFileVarsReader reader = new GribFileVarsReader(degribExe, gribFile.grib);
    String varName = this.var.getGribVarName();
    String result = reader.getVarMsgNumber(varName);
    return result;
  }
}
