package rm.titansdata.raster;

import common.RmTimer;
import common.geom.SridUtils;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import javafx.util.Pair;
import javax.measure.Measure;
import javax.measure.quantity.Length;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import rm.titansdata.properties.Bounds;
import rm.titansdata.properties.Dimensions;
import rm.titansdata.properties.Properties;

/**
 *
 * @author Ricardo Marquez
 */
public class RasterObj implements Closeable{

  private final String name;
  private final Properties properties;
  private final Raster raster;

  public RasterObj(String name, Properties properties, Raster raster) {
    this.name = name;
    this.properties = properties;
    this.raster = raster;
  }

  public String getName() {
    return name;
  }

  public Bounds getBounds() {
    return properties.getBounds();
  }
  
  public int getNumPixels() {
    return getDimensions().numPixels();
  }

  /**
   *
   * @param point
   * @return
   */
  public double getValue(Point point) {
    if (point == null) {
      throw new RuntimeException("Point cannot be null");
    }
    double result = this.raster.getValue(point);
    return result;
  }
  
  
  /**
   * 
   * @param point
   * @return 
   */
  public double getValueNoCaching(Point point) {
    double result = this.raster.getValue(point);
    return result;
  }

  /**
   *
   * @return
   */
  public RasterCells interleave() {
    RasterSearch helper = getRasterSearchHelper();
    double[] values = new double[helper.size()];
    helper.stream(this::getValue)
      .forEach((pair) -> this.interleaveMapping(values, pair));
    RasterCells result = new RasterCells(this.getBounds(), getDimensions(), values);
    return result;
  }

  /**
   *
   * @return
   */
  public Dimensions getDimensions() {
    return this.properties.getDimensions();
  }

  /**
   *
   * @param name
   * @param geometry
   * @return
   */
  public RasterObj getSubsetRaster(String name, Geometry geometry) {
    RmTimer timer = RmTimer.start();
    Geometry correctedEnvelope = this.correctEnvelopeSrid(geometry);
    timer.endAndPrint("getting corrected envelope. ");
    List<Cell> cells = this.getCells(geometry);
    RasterObj result = this.getSubsetRaster(name, correctedEnvelope, cells);
    return result;
  }

  /**
   *
   * @return
   */
  @Override
  public int hashCode() {
    int hash = 7;
    hash = 53 * hash + Objects.hashCode(this.name);
    return hash;
  }

  /**
   *
   * @param obj
   * @return
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final RasterObj other = (RasterObj) obj;
    if (!Objects.equals(this.name, other.name)) {
      return false;
    }
    return true;
  }

  private RasterSearch getRasterSearchHelper() {
    Dimensions dimensions = getDimensions();
    RasterSearch helper = new RasterSearch(this.getBounds(), dimensions);
    return helper;
  }

  private void interleaveMapping(double[] values, Pair<Integer, Cell> pair) {
    int index = pair.getKey();
    Cell cell = pair.getValue();
    values[index] = cell.getValue();
  }

  /**
   *
   * @param correctedEnvelope
   * @param name1
   * @param cells
   * @return
   */
  private RasterObj getSubsetRaster(String name1, // 
    Geometry correctedEnvelope, List<Cell> cells) {
    Dimensions dims = getDimensions();
    Measure<Length> dx = dims.x.length;
    Measure<Length> dy = dims.y.length;
    Envelope envelopeInternal = correctedEnvelope.getEnvelopeInternal();
    Properties newproperties = Properties.create(this.getFactory(), envelopeInternal, dx, dy);
    Unit<? extends Quantity> units = this.raster.getUnits();
    CellsRaster newraster = new CellsRaster(cells, units!=null?units:Unit.ONE, newproperties.getBounds(), dx, dy);
    RasterObj result = new RasterObj(name1, newproperties, newraster);
    return result;
  }

  /**
   *
   * @param geometry
   * @return
   */
  private List<Cell> getCells(Geometry geometry) {
    RasterSearch helper = this.getRasterSearchHelper();
    List<Cell> cells = helper.getCells(geometry, point -> this.getValue(point));
    return cells;
  }
  
  /**
   * 
   * @param p
   * @return 
   */
  private Geometry correctEnvelopeSrid(Geometry p) {
    int srid = this.getFactory().getSRID();
    Geometry transformed = SridUtils.transform(p, srid);
    Geometry envelope = transformed.getEnvelope();
    return envelope;
  }

  /**
   * 
   * @throws IOException 
   */
  @Override
  public void close() throws IOException {
    this.raster.close();
  }
  
  
  
  /**
   * 
   * @return 
   */
  private GeometryFactory getFactory() {
    GeometryFactory result = this.getBounds().getFactory();
    return result;
  }

  @Override
  public String toString() {
    return "{" + "name=" + name + ", properties=" + properties + '}';
  }
  
  /**
   * 
   * @return 
   */
  public Raster getRaster() {
    return this.raster;
  }

  public static class Builder {

    private String name;
    private Properties properties;
    private Raster raster;

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder setProperties(Properties properties) {
      this.properties = properties;
      return this;
    }

    public Builder setRaster(Raster raster) {
      this.raster = raster;
      return this;
    }

    /**
     *
     * @return
     */
    public RasterObj build() {
      RasterObj result = new RasterObj(name, properties, this.raster);
      return result;
    }
  }

}
