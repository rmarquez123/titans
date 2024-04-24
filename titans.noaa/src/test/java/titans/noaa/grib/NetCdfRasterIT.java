package titans.noaa.grib;

import common.RmObjects;
import java.io.File;
import java.time.ZonedDateTime;
import javax.measure.unit.Unit;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.locationtech.jts.geom.Point;
import rm.titansdata.properties.Bounds;
import rm.titansdata.properties.Dimensions;
import titans.noaa.core.NoaaParameter;
import titans.noaa.netcdf.NetCdfFile;
import titans.noaa.netcdf.NetCdfRaster;

/**
 *
 * @author rmarq
 */
@RunWith(JUnitParamsRunner.class)
public class NetCdfRasterIT {

  @Test
  @Parameters({
    "-117.29805044499986, 44.37620281200002, false, 0",
    "-117.29805044499986, 44.37620281200002, true, 0", //    "-117.29805044499986, 44.37620281200002, false, 1",
  //    "-117.29805044499986, 44.37620281200002, true, 1", 
  //    "-117.29805044499986, 44.37620281200002, false, 2",
  //    "-117.29805044499986, 44.37620281200002, true, 2", 
  })
  public void test(double lon, double lat, boolean cache, int fcstStep) throws Exception {
    Point point = RmObjects.pointWgs84(lat, lon);
    String parentKey = "test";
    ZonedDateTime datetime = RmObjects.dateTimeOfInUtc("yyyy/MM/dd HH:mm", "2023/12/01 00:00");
    String noaaVar = "TMP_2-HTGL";
    NoaaParameter namParameter = new NoaaParameter(parentKey, datetime, fcstStep, noaaVar, Unit.ONE);
    File baseFolder = new File("D:\\tests\\data\\netcdf\\hrrr.archive");
    int subFolderId = 2;

    NetCdfFile netCdfFile = NetCdfFile.create(baseFolder, subFolderId, namParameter);
    Bounds bounds = null;
    Dimensions dims = null;
    NetCdfRaster raster = new NetCdfRaster(netCdfFile, bounds, dims);
    double value;
    if (cache) {
      value = raster.getValue(point);
    } else {
      value = raster.getValueNoCaching(point);
    }
    System.out.println("value = " + value);
  }

  /**
   *
   * @param lon
   * @param lat
   * @param cache
   * @param fcstStep
   * @throws Exception
   */
  @Test
  @Parameters({
    "-117.29805044499986, 44.37620281200002, true, 0",})
  public void test02(double lon, double lat, boolean cache, int fcstStep) throws Exception {

    String parentKey = "test";
    ZonedDateTime datetime = RmObjects.dateTimeOfInUtc("yyyy/MM/dd HH:mm", "2023/12/01 00:00");
    String noaaVar = "TMP_2-HTGL";
    NoaaParameter namParameter = new NoaaParameter(parentKey, datetime, fcstStep, noaaVar, Unit.ONE);
    File baseFolder = new File("D:\\tests\\data\\netcdf\\hrrr.archive");
    int subFolderId = 2;

    NetCdfFile netCdfFile = NetCdfFile.create(baseFolder, subFolderId, namParameter);
    Bounds bounds = null;
    Dimensions dims = null;
    try (NetCdfRaster raster = new NetCdfRaster(netCdfFile, bounds, dims)) {
      double value;
      if (cache) {
        for (int i = 0; i < 10000; i++) {
          Point point = RmObjects.pointWgs84(lat + Math.random(), lon + Math.random());
          value = raster.getValue(point);
        }
      } else {
        Point point = RmObjects.pointWgs84(lat + Math.random(), lon + Math.random());

        value = raster.getValueNoCaching(point);
      }
    }
  }
}
