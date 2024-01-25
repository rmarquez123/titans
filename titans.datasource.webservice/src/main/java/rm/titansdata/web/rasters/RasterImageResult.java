package rm.titansdata.web.rasters;

import org.locationtech.jts.geom.Point;
import rm.titansdata.images.ColorMapObject;

/**
 *
 * @author Ricardo Marquez
 */
public class RasterImageResult {

  
  public final Point upperRight;
  public final Point lowerLeft;  
  public final int srid;
  public final String imageURL;
  public final ColorMapObject colorMap;

  public RasterImageResult(String imageURL, Point upperRight, Point lowerLeft, int srid, ColorMapObject colorMap) {
    this.imageURL = imageURL;
    this.upperRight = upperRight;
    this.lowerLeft = lowerLeft;  
    this.srid = srid;
    this.colorMap = colorMap;   
  }
    
  /**
   * 
   * @return   
   */
  @Override
  public String toString() {
    return "RasterImageResult{" + "upperRight=" + upperRight + ", lowerLeft=" + lowerLeft + ", srid=" + srid + ", imageURL=" + imageURL + '}';
  }
}
