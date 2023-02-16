
package titans.hrrr;

import java.util.List;
import org.junit.Test;
import titans.hrrr.core.grib.HrrrGribSource;
import titans.hrrr.core.grib.HrrrInventoryReader;
import titans.nam.NoaaParameter;
import titans.nam.classes.NoaaVarClazz;

/**
 *
 * @author Ricardo Marquez
 */
public class HrrrGribSourceIT {

  @Test
  public void test01() {
    HrrrGribSource source = new HrrrGribSource();
    int minusDays = 0;
    List<NoaaParameter> list = source.getCurrentParameters("HRRR", minusDays);
    list.forEach(System.out::println);
  }
  
  @Test
  public void test02() {
    HrrrInventoryReader reader = new HrrrInventoryReader();
    List<NoaaVarClazz> vars = reader.read();
    vars.forEach(System.out::println);
  }
}
