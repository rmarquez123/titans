package rm.titansdata.raster;

import java.io.Closeable;
import java.util.List;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;



/**
 *
 * @author Ricardo Marquez
 */
public interface Raster extends Closeable{
  
  /**
   * 
   * @return 
   */
  public Unit<? extends Quantity> getUnits(); 
  
  /**
   * 
   * @param point 
   * @return  
   */  
  public double getValue(Point point); 
  
  /**
   * 
   * @param point 
   * @return  
   */  
  public double getValueNoCaching(Point point); 
  
  /**
   * 
   * @param envelope
   * @return 
   */
  public double getMeanValue(Geometry envelope);
  
  /**
   * 
   * @param string
   * @return 
   */
  public List<Point> getPoints(LineString string); 
  
  
  /**
   * 
   * @param string
   * @return 
   */
  public List<Point> getPoints(LinearRing string); 
}
