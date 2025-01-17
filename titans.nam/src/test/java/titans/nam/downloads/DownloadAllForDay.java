package titans.nam.downloads;

import java.io.File;
import java.util.List;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import titans.nam.BaseSpringTest;
import titans.nam.core.NamImporter;
import titans.nam.core.NamInventoryReader;
import titans.nam.grib.NamGribSource;
import titans.noaa.core.NoaaParameter;
import titans.noaa.core.NoaaVariable;

/**
 *
 * @author Ricardo Marquez
 */
public class DownloadAllForDay extends BaseSpringTest {

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
    NamGribSource source = new NamGribSource();
    String parentKey = "NAM";
    int minusDays = 0; 
    List<NoaaParameter> params = source.getParameters(parentKey, minusDays); 
    String varName = "TMP_2-HTGL";
    NoaaVariable namVariable = new NoaaVariable(varName, new NamInventoryReader().getUnit(varName));
    params.stream().forEach(p -> {
      System.out.println("p = " + p);
      long tic = System.currentTimeMillis();
      NamImporter importer = new NamImporter(gribRootFolder, netCdfRootFolder, 0, degribExe);  
      importer.getRaster(namVariable, p.datetime, p.fcststep, null, null);
      System.out.println("elapsed time = " + (System.currentTimeMillis() - tic));
    });

  }
}
