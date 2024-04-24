package titans.noaa.netcdf;

import java.io.IOException;
import java.util.List;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import rm.titansdata.properties.Bounds;
import rm.titansdata.properties.Dimensions;
import rm.titansdata.raster.BasicRaster;

/**
 *
 * @author Ricardo Marquez
 */
public class BufferedNetCdfRaster extends BasicRaster {

  private final NetCdfFile netCdfFile;
  
  public BufferedNetCdfRaster(NetCdfFile netCdfFile, Bounds bounds, Dimensions dims) {
    super(netCdfFile.getUnits(), bounds, dims);
    this.netCdfFile = netCdfFile;
  }
  
  /**
   * 
   * @param point
   * @return 
   */
  @Override
  public double getValue(Point point) {
    //To change body of generated methods, choose Tools | Templates.
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public List<Point> getPoints(LineString string) {
    // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public List<Point> getPoints(LinearRing string) {
    // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void close() throws IOException {
    // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
}
