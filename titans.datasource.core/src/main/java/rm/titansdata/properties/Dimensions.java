package rm.titansdata.properties;

import java.io.Serializable;
import javax.measure.Measure;
import javax.measure.quantity.Length;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import org.locationtech.jts.geom.Point;
import common.geom.SridUtils;

/**
 *
 * @author Ricardo Marquez
 */
public class Dimensions implements Serializable{ 

  /**
   * 
   * @param bounds
   * @param dx
   * @param dy
   * @return 
   */
  public static Dimensions create(Bounds bounds, Measure<Length> dx, Measure<Length> dy) {
    Point l = SridUtils.transform(bounds.lowerleft(), 3857);
    Point u = SridUtils.transform(bounds.upperright(), 3857); 
    Bounds boundsModified = Bounds.fromPoints(l, u);
    double pixelsx = ((boundsModified.getMaxX() - boundsModified.getMinX())/dx.doubleValue(SI.METRE));
    double pixelsy = ((boundsModified.getMaxY() - boundsModified.getMinY())/dy.doubleValue(SI.METRE));
    Dimension dimx = new Dimension(dx, (int) pixelsx);
    Dimension dimy = new Dimension(dy, (int) pixelsy);
    Dimensions result = new Dimensions(dimx, dimy); 
    return result;
  }
  
  public final Dimension x;
  public final Dimension y;

  public Dimensions(Dimension dimensionx, Dimension dimensiony) {
    if (dimensionx.length.getValue().doubleValue() <= 0) {  
      throw new RuntimeException("Invalid dimenion value"); 
    }
    if (dimensiony.length.getValue().doubleValue() <= 0) {
      throw new RuntimeException("Invalid dimenion value");
    }
    this.x = dimensionx;
    this.y = dimensiony;
  }
    
  /**
   * 
   * @param unit
   * @return 
   */
  public double getDeltaX(Unit<Length> unit) {
    return this.x.length.doubleValue(unit); 
  }
  
  /**
   * 
   * @param unit
   * @return 
   */
  public double getDeltaY(Unit<Length> unit) {
    return this.x.length.doubleValue(unit); 
  }
  
  
  /**
   * 
   * @return 
   */
  public int numPixels() {
    int result = this.x.pixels*this.y.pixels;
    return result;
  }
  
  /**
   * 
   * @return 
   */
  @Override
  public String toString() {
    return "{" + "x=" + x + ", y=" + y + '}';
  }
  
  
}
