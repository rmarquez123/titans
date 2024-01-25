package rm.titansdata.images;

/**
 *
 * @author rmarq
 */
public class ColorMapObject {

  public final double xmin;
  public final double xmax;
  public final String colorMapName;

  public ColorMapObject(double xmin, double xmax, String colorMapName) {
    this.xmin = xmin;
    this.xmax = xmax;
    this.colorMapName = colorMapName;
  }
}
