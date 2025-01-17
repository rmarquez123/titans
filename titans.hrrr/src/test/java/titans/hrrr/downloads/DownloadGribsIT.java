package titans.hrrr.downloads;

import java.io.File;
import java.util.List;
import javax.measure.unit.SI;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import titans.hrrr.BaseSpringITest;
import titans.hrrr.core.HrrrImporter;
import titans.hrrr.core.grib.HrrrGribSource;
import titans.noaa.core.NoaaParameter;
import titans.noaa.core.NoaaVariable;
import titans.noaa.grib.GribFile;

/**
 *
 * @author Ricardo Marquez
 */
public class DownloadGribsIT extends BaseSpringITest {

  @Autowired
  @Qualifier("gribRootFolder")
  File gribRootFolder;
  @Autowired
  @Qualifier("netCdfRootFolder")
  File netCdfRootFolder;
  @Autowired
  @Qualifier("degribExe")
  File degribExe;

  @Test
  public void test() {
    HrrrGribSource source = new HrrrGribSource();
    int minusDays = 0;
    int projectId = 0;
    HrrrImporter importer = new HrrrImporter(gribRootFolder, netCdfRootFolder, projectId, degribExe);
    String parentKey = "HRRR";
    List<NoaaParameter> params = source.getParameters(parentKey, minusDays);
    params.stream().forEach(p -> {
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
}
