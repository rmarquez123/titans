package rm.titansdata.colormap;

import java.awt.Color;
import net.mahdilamb.colormap.Colormap;
import net.mahdilamb.colormap.Colormaps;
import net.mahdilamb.colormap.FluidColormap;

/**
 *
 * @author Ricardo Marquez
 */
public class ColorMap {
  
  
  public final String colorMapName;
  public final double xmin;
  public final double xmax;

  private ColorMap(String colorMapName, double xmin, double xmax) {
    this.colorMapName = colorMapName;
    this.xmin = xmin;
    this.xmax = xmax;
  }

  /**
   *
   * @param x
   * @return
   */
  public String getColorHex(double x) {
    Color color = this.getColor(x);
    String result = this.toHexString(color);
    return result;
  }
  

  /**
   *
   * @param val
   * @return
   */
  private String format(double val) {
    String in = Integer.toHexString((int) Math.round(val * 255));
    return in.length() == 1 ? "0" + in : in;
  }

  /**
   *
   * @param value
   * @return
   */
  private String toHexString(Color value) {
    String result = "#" + (format(value.getRed())
            + format(value.getGreen())
            + format(value.getBlue()))
            .toLowerCase();
    return result;
  }

  /**
   *
   * @param value
   * @return
   */
  public int getRed255(double value) {
    int result = this.getColor(value).getRed();
    return result;
  }

  /**
   *
   * @param value
   * @return
   */
  public int getGreen255(double value) {
    int result = this.getColor(value).getGreen();
    return result;
  }
  
  /**
   * 
   * @param value
   * @return 
   */
  public int getBlue255(double value) {
    int result = this.getColor(value).getBlue(); 
    return result;
  }
  
  /**
   * 
   * @param value
   * @return 
   */
  private Color getColor(double value) {
    Colormap get = Colormaps.get(this.colorMapName);
    FluidColormap maps = Colormaps.fluidColormap(get, (float) this.xmin, (float) this.xmax, false);
    Color color = maps.get(value);
    return color;
  }
  
  /**
   * 
   * @param string
   * @return 
   */
  public static ColorMap parse(String string) {
    // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  /**
   * 
   */
  public static class Builder {

    private double xmin;
    private double xmax;

    private String colorMapName;

    public Builder setXmax(double xmax) {
      this.xmax = xmax;
      return this;
    }

    public Builder setXmin(double xmin) {
      this.xmin = xmin;
      return this;
    }

    public Builder setColorMapName(String colorMapName) {
      this.colorMapName = colorMapName;
      return this;
    }

    public ColorMap build() {
      return new ColorMap(this.colorMapName, xmin, xmax);
    }
  }
}
