package titans.nam;

import common.geom.SridUtils;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import javax.measure.Measure;
import javax.measure.quantity.Length;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import rm.titansdata.properties.Bounds;
import rm.titansdata.properties.Dimensions;
import rm.titansdata.raster.Raster;
import titans.nam.core.NamInventoryReader;
import titans.noaa.core.NoaaParameter;
import titans.noaa.grib.ForecastTimeReference;

/**
 *
 * @author Ricardo Marquez
 */
@RunWith(JUnitParamsRunner.class)
public class NamRasterFactoryIT extends BaseSpringTest{

  @Autowired
  private NamRasterFactory factory;



  @Test
  @Parameters({
    "0", "-1"
  })
  public void getRasters(String userId) {
    long ms = System.currentTimeMillis();
    GeometryFactory geomfactory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326); 
    int projectid = 0;
    Point p1 = geomfactory.createPoint(new Coordinate(-119.43, 42.26));
    Point p2 = geomfactory.createPoint(new Coordinate(-121.43, 44.26));
    int targetSrid = 32610;    
    Bounds b = Bounds.fromPoints( //
      SridUtils.transform(p1, targetSrid), //
      SridUtils.transform(p2, targetSrid));    
    Measure<Length> dx = Measure.valueOf(1000.0, SI.METRE);
    Measure<Length> dy = Measure.valueOf(1000.0, SI.METRE);
    Dimensions dims = Dimensions.create(b, dx, dy);
    ZoneId utc = ZoneId.of("UTC");
    ZonedDateTime datetime = ZonedDateTime.now(utc)
      .truncatedTo(ChronoUnit.DAYS)
      .minusDays(1);
    ForecastTimeReference d = new ForecastTimeReference(0, 0);
    String var = "TMP_2-HTGL";
    Unit<?> unit = new NamInventoryReader().getUnit(var);
    NoaaParameter p = new NoaaParameter("NAM", datetime, d, var, unit);
    
    Raster r = factory.create(projectid, p, b, dims);
    Point point = geomfactory.createPoint(new Coordinate(-120.43, 43.26));
    double value = r.getValue(SridUtils.transform(point, targetSrid));
    System.out.println("value = " + value);
    System.out.println("elapsed time: " + (System.currentTimeMillis() - ms)/1000.0);
    
  }
}
