package titans.noaa.grib;

import java.io.File;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;
import javax.measure.unit.Unit;
import org.junit.Test;
import titans.noaa.core.NoaaVariable;
import titans.noaa.netcdf.NetCdfFile;
import titans.noaa.netcdf.NetCdfRaster;

/**
 *
 * @author rmarq
 */
public class GribFileVarsReaderIT {
  
  @Test
  public void test() {
    File grib = new File("D:\\tests\\data\\grib\\hrrr.archive\\2023\\12\\26\\hrrr.t00z.wrfsfcf00.grib2");    
    File degribExe = new File("C:\\ndfd\\degrib\\bin\\degrib.exe");
    GribFileVarsReader reader = new GribFileVarsReader(degribExe, grib);
    Set<String> varNames = reader.parseVarNames();
    System.out.println("size = " + varNames.size());
    varNames.stream().sorted().forEach(System.out::println); 
  }
  
  @Test
  public void test2() {
    File grib = new File("D:\\tests\\data\\grib\\hrrr.archive\\2023\\12\\26\\hrrr.t00z.wrfsfcf00.grib2");
    File degribExe = new File("C:\\Users\\rmarq\\Downloads\\wgrib2\\wgrib2.exe");
    GribFileVarsReader reader = new GribFileVarsReader(degribExe, grib);
    Set<String> varNames = reader.parseVarNames();
    System.out.println("size = " + varNames.size());
    varNames.stream().sorted().forEach(System.out::println);
  }
  
  @Test
  public void test3() {
    NoaaVariable var = new NoaaVariable("TMP_2-HTGL", Unit.ONE);
    File degribExe = new File("C:\\Users\\rmarq\\Downloads\\wgrib2\\wgrib2.exe");
    File netcdfdir = new File("D:\\tests\\data"); 
    NetCdfExtractor reader = new NetCdfExtractor(degribExe, netcdfdir, 0, var); 
    ZonedDateTime datetimeref = ZonedDateTime.of(2023, 12, 26, 0, 0, 0, 0, ZoneId.of("UTC"));
    File rootFolder = new File("D:\\tests\\data\\grib\\hrrr.archive"); 
    int fcstStep = 0;
    int subFolder = -1;
    String filename = "hrrr.t00z.wrfsfcf00.grib2";
    GribFile gribFile = GribFile.create(rootFolder, var, subFolder, datetimeref, fcstStep, filename);
    NetCdfFile netCdfFile = reader.extract(gribFile);
    NetCdfRaster raster = new NetCdfRaster(netCdfFile, netCdfFile.getBounds(), netCdfFile.getDimensions());
    double value = raster.getValue(netCdfFile.getBounds().getCenter()); 
    System.out.println("value = " + value);
  }
  
}
