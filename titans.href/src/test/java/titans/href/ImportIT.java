package titans.href;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import javax.measure.unit.SI;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import titans.href.core.HrefGribSource;
import titans.noaa.core.NoaaGribImporter;
import titans.noaa.core.NoaaVariable;
import titans.noaa.grib.GribFile;

/**
 *
 * @author Ricardo Marquez
 */
@RunWith(JUnitParamsRunner.class)
public class ImportIT extends BaseSpringITest {

  /**
   *
   */
  @Autowired
  private HrefRasterFactory factory;

  /**
   *
   * @param varname
   */
  @Test
  @Parameters({
    "UGRD-HTGL", //    "TMP_5000-ISBL, 2024-12-03T10:00:00", 
  })
  public void test(String varname) {
    NoaaGribImporter importer = this.factory.getImporter(1);
    ZonedDateTime datetimeref = ZonedDateTime.now(ZoneId.of("UTC")).truncatedTo(ChronoUnit.DAYS);
    NoaaVariable var = new NoaaVariable(varname, SI.METRES_PER_SECOND);
    GribFile gribfile = importer.getGribFile(var, datetimeref, 0);
    HrefGribSource source = new HrefGribSource();
    source.download(gribfile);
  }
}
