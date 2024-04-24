package rm.titansdata.web;

import common.RmObjects;
import common.geom.SridUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.quantity.Angle;
import javax.measure.unit.Unit;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 *
 * @author rmarq
 */
@Component
@Lazy(false)
public class StartupTest implements InitializingBean {
  
  /**
   * 
   * @throws Exception 
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    Unit<Angle> c = javax.measure.quantity.Angle.UNIT;
    SridUtils.init();
    if (!RmObjects.isWindows()) {
      //testWgribProces();
    }
  }

  /**
   *
   * @throws IOException
   * @throws InterruptedException
   */
  private void testWgribProces() throws IOException, InterruptedException {
    File netCdfFile = new File("/usr/local/data/netcdf/test.nc");
    Files.setPosixFilePermissions(netCdfFile.getParentFile().toPath(), //
            PosixFilePermissions.fromString("rwxrwxrwx"));
    ProcessBuilder builder = new ProcessBuilder(
            "/usr/local/bin/wgrib2/wgrib2",
            "/usr/local/data/grib/hrrr.t00z.wrfsfcf00.grib2",
            "-netcdf", "/usr/local/data/netcdf/test.nc",
            "-match",
            "d=2023122800:TMP:2 m above ground:anl");
    builder.directory(new File("/usr/local/bin/wgrib2"));
    builder.redirectErrorStream(false);
    Process process = builder.start();
    int processresult = process.waitFor();

    Logger logger = Logger.getLogger(StartupTest.class.getName());
    logger.log(Level.INFO, "process result {0}", processresult);
    Charset chartset = Charset.forName("utf8");
    IOUtils.readLines(process.getErrorStream())
            .forEach(l -> logger.log(Level.INFO, l));
    IOUtils.readLines(process.getInputStream())
            .forEach(l -> logger.log(Level.INFO, l));
    if (!netCdfFile.exists()) {
      throw new RuntimeException(String.format("Netcdf file not created {%s}", netCdfFile));
    }
  }
}
