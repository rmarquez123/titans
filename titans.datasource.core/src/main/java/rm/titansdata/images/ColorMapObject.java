package rm.titansdata.images;

/**
 *
 * @author rmarq
 */
public class ColorMapObject {

  public final double xmin;
  public final double xmax;
  public final String units;
  public final String colorMapName;

  /**
   *
   * @param xmin
   * @param xmax
   * @param units
   * @param colorMapName
   */
  public ColorMapObject(double xmin, double xmax, String units, String colorMapName) {
    this.xmin = xmin;
    this.xmax = xmax;
    this.units = units;
    this.colorMapName = colorMapName;
  }
}
