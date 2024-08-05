package titans.noaa.grib;

import common.RmExceptions;
import common.RmObjects;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;
import org.apache.commons.io.IOUtils;
import rm.titansdata.units.UnitsUtils;

/**
 *
 * @author Ricardo Marquez
 */
public class GribFileVarsReader {

  private final File degribExe;
  private final File gribFile;

  /**
   *
   * @param degribExe
   * @param gribFile
   */
  public GribFileVarsReader(File degribExe, File gribFile) {
    this.degribExe = RmObjects.fileExists(degribExe, "File '%s' does not exist", degribExe) ;
    this.gribFile = gribFile;
  }

  /**
   *
   * @return
   */
  public Set<String> parseVarNames() {
    ProcessBuilder process = this.createGribInventoryProcess();
    List<String> processOutput = this.getProcessOutput(process);
    Set<String> result;
    try {
      result = this.mapProcessOutputToVarNames(processOutput);
    } catch (Exception ex) {
      String text = String.join("\n", processOutput);
      throw RmExceptions.create(ex, "An error occurred parsing variable names from file: \n%s\n", text);
    }
    return result;
  }

  /**
   *
   * @param varName
   * @return
   */
  public String getVarMsgNumber(String varName) {
    String result;
    try {
      String line = this.getLineForVarName(varName);
      result = this.lineToVarMsgNumber(line);
    } catch (Exception ex) {
      throw RmExceptions.create(ex, "An error occurred attempting to retrieve line for varName: %s", varName);
    }
    return result;
  }

  /**
   *
   * @param varName
   * @return
   */
  private String getLineForVarName(String varName) {
    ProcessBuilder process = this.createGribInventoryProcess();
    List<String> processOutput = this.getProcessOutput(process);
    if (processOutput.isEmpty()) {
      throw RmExceptions.create("An error occurred "//
              + "creating grib inventory for '%s' " //
              + "using '%s'", this.gribFile, this.degribExe);
    }
    String line;
    if (processOutput.size() > 1) {
      line = processOutput //
              .stream() //
              .filter(l -> l.contains(":") || l.contains(":")) //
              .filter(l -> this.toVarName(l).equals(varName))
              .findFirst().orElse(null);
    } else {
      line = processOutput.get(0);
    }
    return line;
  }

  /**
   *
   * @param varName
   * @return
   */
  public Unit<? extends Quantity> getUnit(String varName) {
    String line = this.getLineForVarName(varName);
    String description = line.split(",")[3];
    int istart = description.indexOf("[");
    int iend = description.indexOf("]");
    Unit<?> result;
    if (istart >= 0 && iend >= 0) {
      String unitstext = description.substring(istart + 1, iend);
      result = UnitsUtils.valueOf(unitstext);
      if (result == null) {
        System.out.println("varName, unitstext = " + varName + ", " + unitstext);
        Unit<?> a = UnitsUtils.valueOf(unitstext);
      }
    } else {
      result = null;
    }
    return result;
  }

  /**
   *
   * @param processOutput
   * @return
   */
  private Set<String> mapProcessOutputToVarNames(List<String> processOutput) {
    Set<String> result = processOutput.stream()
            .filter(l -> l.contains(",") || l.contains(":"))
            .map(this::toVarName)
            .collect(Collectors.toSet());
    return result;
  }

  /**
   *
   * @param line
   * @return
   */
  private String toVarName(String line) {
    String result = null;
    if (!this.degribExe.getName().contains("wgrib2")) {
      try {
        String[] parts = line.split(",");
        String elementPrefix = parts[3].split("=")[0].trim();
        String level = parts[4].trim();
        result = elementPrefix + "_" + level;
      } catch (Exception ex) {
        throw RmExceptions.create(ex, "An error occured on parsing line: '%s'", line);
      }
    } else {
      try {
        String[] parts = line.split(":");
        String elementPrefix = parts[3].trim();
        String level = parts[4].trim().replace(" mb", "00-ISBL")
                .replace(" m above ground", "-HTGL")
                .replace("surface", "0-SFC")
                .replace("entire atmosphere (considered as a single layer)", "EATM")
                .replace("entire atmosphere", "0-RESERVED(10)")
                .replace("boundary layer cloud layer", "BCY")
                .replace("top of atmosphere", "NTAT")
                .replace("0C isotherm", "0-0DEG")
                .replace("0C isotherm", "0-0DEG")
                .replace("highest tropospheric freezing level", "HTFL")
                .replace("cloud base", "0-CBL")
                .replace("cloud top", "0-CTL")
                .replace("mean sea level", "0-MSL")
                .replace("middle cloud layer", "0-MCY")
                .replace("low cloud layer", "0-LCY");
        result = elementPrefix + "_" + level;
      } catch (Exception ex) {
        throw RmExceptions.create(ex, "An error occured on parsing line: '%s'", line);
      }
    }
    return result;
  }

  /**
   *
   * @return
   */
  private ProcessBuilder createGribInventoryProcess() {
    ProcessBuilder process;
    if (!this.degribExe.getName().contains("wgrib2")) {
      process = new ProcessBuilder(
              this.degribExe.getAbsolutePath().replace(".exe", ""),
              this.gribFile.getAbsolutePath().replace(".gz", ""),
              "-I");
    } else {
      process = new ProcessBuilder(
              this.degribExe.getAbsolutePath().replace(".exe", ""),
              this.gribFile.getAbsolutePath().replace(".gz", ""));
    }

    File workingDirectory = gribFile.getParentFile();
    process.directory(workingDirectory);
    process.redirectErrorStream(true);
    return process;
  }

  /**
   *
   * @param process
   * @throws RuntimeException
   */
  private List<String> getProcessOutput(ProcessBuilder process) {
    List<String> lines;
    try {
      Process p = process.start();
      Thread.sleep(Duration.ofSeconds(2l));
      try (InputStream errorstream = p.getErrorStream()) {
        String message;
        if (errorstream != null && !(message = this.streamToString(errorstream)).isEmpty()) {
          throw new RuntimeException(message);
        }
      }
      try (InputStream in = p.getInputStream()) {
        lines = IOUtils.readLines(in, Charset.forName("utf8"));
      }
      lines.stream().filter(l -> l.contains("Error")).findFirst().ifPresent((errorline) -> {
        throw new RuntimeException(errorline);
      });
      if (!this.degribExe.getName().contains("wgrib2")) {
        lines.remove(0);
      }
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
    return lines;
  }

  /**
   *
   * @param in
   * @return
   * @throws IOException
   */
  private String streamToString(InputStream in) throws IOException {
    List<String> lines = IOUtils.readLines(in, Charset.forName("utf8"));
    String result = String.join(",", lines);
    return result;
  }

  /**
   *
   * @param line
   * @return
   */
  private String lineToVarMsgNumber(String line) {
    try {
      String result = line.split(",")[0];
      return result;
    } catch (Exception ex) {
      throw new RuntimeException(String.format("Parsing line ''%s", line), ex);
    }

  }

}
