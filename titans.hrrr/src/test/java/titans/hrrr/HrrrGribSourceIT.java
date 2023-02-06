
package titans.hrrr;

import java.util.List;
import org.junit.Test;
import titans.hrrr.core.grib.HrrrGribSource;
import titans.nam.NoaaParameter;

/**
 *
 * @author Ricardo Marquez
 */
public class HrrrGribSourceIT {

  @Test
  public void test01() {
    HrrrGribSource source = new HrrrGribSource();
    List<NoaaParameter> list = source.getCurrentParameters("HRRR");
    list.forEach(System.out::println);
  }
}
