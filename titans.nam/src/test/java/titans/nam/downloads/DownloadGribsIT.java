package titans.nam.downloads;

import java.io.File;
import java.util.List;
import javax.measure.unit.SI;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import titans.nam.BaseSpringTest;
import titans.nam.core.NamImporter;
import titans.nam.grib.NamGribSource;
import titans.noaa.core.NoaaParameter;
import titans.noaa.core.NoaaVariable;
import titans.noaa.grib.GribFile;

/**
 *
 * @author Ricardo Marquez
 */
public class DownloadGribsIT extends BaseSpringTest {

  @Autowired
  @Qualifier("gribRootFolder")
  File gribRootFolder;
  @Autowired
  @Qualifier("netCdfRootFolder")
  File netCdfRootFolder;
  @Autowired
  @Qualifier("nam.degribExe")
  File degribExe;

  @Test
  public void test() {
    NamGribSource source = new NamGribSource();
    String parentKey = "NAM";
    int minusDays = 0;
    List<NoaaParameter> params = source.getParameters(parentKey, minusDays);
    NamImporter importer = new NamImporter(gribRootFolder, netCdfRootFolder, 0, degribExe);
    params.forEach(p -> {
      System.out.println("p = " + p);
      long tic = System.currentTimeMillis();
      NoaaVariable var = new NoaaVariable("TMP_2-HTGL", SI.CELSIUS);
      GribFile gribfile = importer.getGribFile(var, p.datetime, p.fcststep);
      if (gribfile.notExists()) {
        source.download(gribfile);
      }
      System.out.println("elapsed time = " + (System.currentTimeMillis() - tic));
    });
  }
  
  @Test
  public void test2() {
    NamGribSource source = new NamGribSource();
    String parentKey = "NAM";
    int minusDays = 0;
    List<NoaaParameter> params = source.getParameters(parentKey, minusDays);
    System.out.println("params = " + params.size());
  }
}
