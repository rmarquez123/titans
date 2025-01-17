package titans.hrrr;

import common.RmThreadUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.measure.unit.SI;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import titans.hrrr.core.grib.HrrrGribSource;
import titans.hrrr.core.grib.HrrrSfcInventoryReader;
import titans.noaa.core.NoaaParameter;
import titans.nam.grib.NamGribSource;
import titans.noaa.core.NoaaVarClazz;
import titans.noaa.core.NoaaVariable;
import titans.noaa.grib.GribFile;

/**
 *
 * @author Ricardo Marquez
 */
public class HrrrGribSourceIT {

  @Test
  public void test01() {
    HrrrGribSource source = new HrrrGribSource();
    int minusDays = 0;
    List<NoaaParameter> list = source.getParameters("HRRR", minusDays);
    list.forEach(System.out::println);
  }

  @Test
  public void test02() {
    HrrrSfcInventoryReader reader = new HrrrSfcInventoryReader();
    List<NoaaVarClazz> vars = reader.read();
    vars.forEach(System.out::println);
  }

  @Test
  @Ignore
  public void test03() throws Exception {
    File g = new File("D:\\tests\\data\\test.grib2");
    File gidx = new File("D:\\tests\\data\\grib2.idx");
    NamGribSource source = new NamGribSource();
    ZonedDateTime datetime = ZonedDateTime.now().minusDays(1).truncatedTo(ChronoUnit.DAYS);
    NoaaVariable var = new NoaaVariable("TMP_2-HTGL", SI.CELSIUS);
    GribFile file = new GribFile(datetime, 0, var, g, gidx);
    URL url = new URL(source.createUrl(file)); 
    InputStream input = url.openStream();
    new RmThreadUtils.ThreadBuilder("afadfasf").setRunnable(() -> {
      try (OutputStream out = file.getOutputStream()) {
        IOUtils.copy(input, out);
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }).start();
    Thread.sleep(3000);
    GribFile file2 = new GribFile(ZonedDateTime.now(), 0, var, g, gidx);
    boolean locked = file2.isLocked();
    Assert.assertTrue(locked);
    System.out.println("locked = " + locked);
  }

  @Test
  public void test04() throws Exception {
    File g = new File("D:\\tests\\data\\test.grib2");
    File gidx = new File("D:\\tests\\data\\grib2.idx");
    NoaaVariable var = new NoaaVariable("TMP_2-HTGL", SI.CELSIUS);
    GribFile file = new GribFile(ZonedDateTime.now(), 0, var, g, gidx);
    boolean locked = file.isLocked();
    Assert.assertFalse(locked);
  }
}
