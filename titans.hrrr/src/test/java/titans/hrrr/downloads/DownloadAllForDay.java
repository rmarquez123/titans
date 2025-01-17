package titans.hrrr.downloads;

import java.io.File;
import java.util.List;
import javax.measure.unit.Unit;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import titans.hrrr.BaseSpringITest;
import titans.hrrr.core.HrrrImporter;
import titans.hrrr.core.grib.HrrrGribSource;
import titans.hrrr.core.grib.HrrrSfcInventoryReader;
import titans.noaa.core.NoaaParameter;
import titans.noaa.core.NoaaVariable;

/**
 *
 * @author Ricardo Marquez
 */
public class DownloadAllForDay extends BaseSpringITest {

  @Autowired
  @Qualifier("gribRootFolder")
  File gribRootFolder;
  @Autowired
  @Qualifier("netCdfRootFolder")
  File netCdfRootFolder;
  @Autowired
  @Qualifier("degribExe")
  File degribExe;
  
  /**
   * 
   */
  @Test
  public void test() {
    HrrrGribSource source = new HrrrGribSource();
    int projectId = 0;      
    HrrrImporter importer = new HrrrImporter(gribRootFolder, netCdfRootFolder,projectId,degribExe);  
    String parentKey = "HRRR";
    int minusDays = 0; 
    List<NoaaParameter> params = source.getParameters(parentKey, minusDays); 
    String varName = "TMP_2-HTGL";
    Unit<?> unit = new HrrrSfcInventoryReader().getUnit(varName);
    NoaaVariable namVariable = new NoaaVariable(varName, unit);
    params.stream().forEach(p -> {
      System.out.println("p = " + p);
      long tic = System.currentTimeMillis();
      importer.getRaster(namVariable, p.datetime, p.fcststep, null, null);      
      System.out.println("elapsed time = " + (System.currentTimeMillis() - tic));
    });
  }
}
