package titans.noaa.netcdf;

import common.RmExceptions;
import common.RmObjects;
import common.geom.SridUtils;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import rm.titansdata.properties.Bounds;
import rm.titansdata.properties.Dimensions;
import rm.titansdata.raster.BasicRaster;
import ucar.ma2.Array;
import ucar.nc2.dt.GridCoordSystem;
import ucar.nc2.dt.GridDatatype;
import ucar.nc2.dt.grid.GridDataset;
import ucar.unidata.geoloc.LatLonPoint;

/**
 *
 * @author Ricardo Marquez
 */
public final class NetCdfRaster extends BasicRaster implements Closeable {

  private final NetCdfFile netCdfFile;
  private GridDataset griddatasetsCache = null;
  private Array arr;

  public NetCdfRaster(NetCdfFile netCdfFile, Bounds bounds, Dimensions dims) {
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
    this.initGridDataset(netCdfFile);
    GridDatatype grid = this.getGridDatatype(netCdfFile);
    int[] xyIndex = this.getQueryIndices(grid, point);
    double result = this.readCachedValueForIndices(grid, xyIndex);
    return result;
  }

  /**
   *
   * @param point
   * @return
   */
  @Override
  public double getValueNoCaching(Point point) {
    this.initGridDataset(netCdfFile);
    GridDatatype grid = this.getGridDatatype(netCdfFile);
    int[] xyIndex = this.getQueryIndices(grid, point);
    double result = this.readValueForIndices(grid, xyIndex);
    return result;
  }

  /**
   *
   * @throws IOException
   */
  @Override
  public void close() throws IOException {
    if (this.griddatasetsCache != null) {
      this.griddatasetsCache.close();
    }
  }

  /**
   *
   * @param netCdfFile
   */
  private synchronized void initGridDataset(NetCdfFile netCdfFile) {
    if (this.griddatasetsCache == null) {
      String filepath = netCdfFile.file.getAbsolutePath();
      try {
        this.griddatasetsCache = GridDataset.open(filepath);
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }
    
  }

  /**
   *
   * @param grid
   * @param argpoint
   * @return
   */
  private int[] getQueryIndices(GridDatatype grid, Point argpoint) {
    Point point = SridUtils.transform(argpoint, 4326);
    GridCoordSystem gcs = grid.getCoordinateSystem();
    double lat = point.getY();
    double lon = point.getX();
    int[] xyIndex = gcs.findXYindexFromLatLon(lat, lon, null);
    return xyIndex;
  }

  /**
   *
   * @param netCdfFile
   * @return
   * @throws NullPointerException
   */
  private GridDatatype getGridDatatype(NetCdfFile netCdfFile) {
    String varName = netCdfFile.getVarName();
    List<GridDatatype> list = getGrid();
    GridDatatype grid = list
            .stream() //
            .filter((g) -> g.getName().equals(varName)) //
            .findFirst() //
            .orElse(list.get(0));
    if (grid == null) {
      throw RmExceptions.create( //
              "Netcdf file '%s' does not contain variable %s", // 
              griddatasetsCache.getNetcdfFile(), varName);
    }
    return grid;
  }

  /**
   *
   * @return
   */
  private List<GridDatatype> getGrid() {
    this.initGridDataset(netCdfFile);
    List<GridDatatype> result = this.griddatasetsCache.getGrids();
    return result;
  }

  /**
   *
   * @param grid
   * @param xyIndex
   * @return
   * @throws IOException
   */
  private double readValueForIndices(GridDatatype grid, int[] xyIndex) {
    double value;
    int xindex = xyIndex[0];
    int yindex = xyIndex[1];
    Array array = this.getValueFromGrid(grid, xyIndex);
    if (xindex == -1 || yindex == -1) {
      value = Double.NaN;
    } else {
      if (array.getSize() > 1) {
        int index = this.getOneDimensionIndex(grid, yindex, xindex);
        value = array.getDouble(index);
      } else {
        value = array.getDouble(0);
      }
      return value;
    }
    return value;
  }

  /**
   *
   * @param grid
   * @param xyIndex
   * @return
   * @throws IOException
   */
  private double readCachedValueForIndices(GridDatatype grid, int[] xyIndex) {
    double value;
    int xindex = xyIndex[0];
    int yindex = xyIndex[1];
    Array array = this.getFullValueArray(grid);
    if (xindex == -1 || yindex == -1) {
      value = Double.NaN;
    } else {
      int index = this.getOneDimensionIndex(grid, yindex, xindex);
      value = array.getDouble(index);
      return value;
    }
    return value;
  }

  /**
   *
   * @param grid
   * @param yindex
   * @param xindex
   * @return
   */
  private int getOneDimensionIndex(GridDatatype grid, int yindex, int xindex) {
    int nx = grid.getXDimension().getLength();
    int index = nx * yindex + xindex;
    return index;
  }

  /**
   *
   * @param grid
   * @param xyIndex
   * @return
   */
  private synchronized Array getValueFromGrid(GridDatatype grid, int[] xyIndex) {
    int xindex = xyIndex[0];
    int yindex = xyIndex[1];
    Array result;
    try {
      if (this.arr == null) {
        this.arr = grid.readDataSlice(0, 0, yindex, xindex);
      }
      result = this.arr;
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    return result;
  }

  /**
   *
   * @param grid
   * @param xyIndex
   * @return
   */
  private synchronized Array getFullValueArray(GridDatatype grid) {
    Array result;
    try {
      if (this.arr == null) {
        this.arr = grid.readDataSlice(0, 0, -1, -1);
      }
      result = this.arr;
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    return result;
  }

  /**
   * Retrieves grid points nearest to each coordinate of the specified
   * LineString. This method iterates through the LineString's coordinates,
   * converting each to its nearest grid point within the dataset's grid
   * coordinate system, and then back to geographic coordinates.
   *
   * Each coordinate of the LineString is processed independently, ensuring that
   * the resulting list of points represents the closest grid point to each part
   * of the LineString. This is particularly useful for approximating the
   * LineString with points that exist in the dataset's grid.
   *
   * The method leverages the grid coordinate system to accurately map between
   * geographic coordinates and grid indices, ensuring that the nearest grid
   * point is found for each LineString coordinate.
   *
   * @param string The LineString whose coordinates are used to find the nearest
   * grid points. The LineString can be in any coordinate system, and its
   * coordinates will be transformed to the grid's coordinate system for
   * processing.
   * @return A List of Point objects representing the nearest grid points to the
   * LineString's coordinates, all in the dataset's grid coordinate system.
   *
   * Note: The method assumes the LineString's coordinates can be accurately
   * mapped to the grid. In cases where a LineString coordinate falls outside
   * the grid bounds, handling such situations needs to be considered and
   * implemented as per application requirements.
   *
   * @throws IllegalArgumentException if the transformation of the LineString's
   * coordinates to the grid's coordinate system fails or if any coordinate
   * cannot be mapped to a valid grid index.
   */
  @Override
  public List<Point> getPoints(LineString string) {
    List<Point> resultPoints = new ArrayList<>();
    GeometryFactory factory = RmObjects.getWgs84Factory(); // Assuming WGS 84 is the desired output CRS
    GridCoordSystem gcs = this.getCoordinateSystem(); // Retrieves the grid coordinate system

    for (Coordinate coord : string.getCoordinates()) {
      // Transform the coordinate to the CRS of the grid if necessary
      Point point = RmObjects.createPoint(factory, coord.x, coord.y);

      // Find the nearest grid index for the coordinate
      int[] gridIndex = gcs.findXYindexFromLatLon(point.getY(), point.getX(), null);

      // Convert the grid index back to geographic coordinates
      LatLonPoint latLon = gcs.getLatLon(gridIndex[0], gridIndex[1]);
      double lon = latLon.getLongitude();
      double lat = latLon.getLatitude();

      // Create a point for the grid coordinate and add it to the results
      Point gridPoint = RmObjects.createPoint(factory, lon, lat);
      resultPoints.add(gridPoint);
    }
    return resultPoints;
  }

  /**
   * Retrieves all grid points within the specified LinearRing. This method
   * transforms the input LinearRing to the WGS 84 coordinate system
   * (EPSG:4326), computes its bounding box, and iterates through the grid
   * indices within this box to identify points that are enclosed by the
   * LinearRing. Each grid index is converted to a geographic point (latitude
   * and longitude), which is then checked for containment within the
   * LinearRing. Points that are found to be inside the LinearRing are collected
   * and returned.
   *
   * The method makes use of a specific GeometryFactory obtained from
   * RmObjects.getWgs84Factory() to ensure consistency in the creation of Point
   * objects within the WGS 84 spatial reference system. Additionally, it
   * utilizes a centralized point creation method,
   * RmObjects.createPoint(factory, lon, lat), to abstract the details of Point
   * object creation, improving the readability and maintainability of the code.
   *
   * @param linearRingArg The LinearRing defining the area within which grid
   * points are to be found. The LinearRing can be in any coordinate system, and
   * it will be transformed to WGS 84 for processing.
   * @return A List of Point objects representing the grid points within the
   * specified LinearRing. Each Point is in the WGS 84 coordinate system.
   *
   * Note: This method assumes that the input LinearRing is a closed shape
   * defining an area. Points are determined to be inside this area using JTS's
   * geometric operations. The accuracy of the containment check is subject to
   * the precision of the input LinearRing and the grid's resolution.
   *
   * @throws IllegalArgumentException if the transformation of the input
   * LinearRing to the WGS 84 coordinate system fails.
   * @throws RuntimeException if any other unexpected errors occur during
   * processing, such as issues with accessing grid coordinates, unclosed linear
   * ring, or creating Point objects.
   */
  @Override
  public List<Point> getPoints(LinearRing linearRingArg) {
    if (linearRingArg.isClosed()) {
      throw new RuntimeException("Linear ring must be closed.");
    }
    List<Point> resultPoints = new ArrayList<>();
    int wgs = 4326;
    LinearRing linearRing = (LinearRing) SridUtils.transform(linearRingArg, wgs);
    Envelope envelope = linearRing.getEnvelopeInternal();
    GeometryFactory factory = RmObjects.getWgs84Factory();
    GridCoordSystem gcs = this.getCoordinateSystem();
    int[] minIndex = gcs.findXYindexFromLatLon(envelope.getMinY(), envelope.getMinX(), null);
    int[] maxIndex = gcs.findXYindexFromLatLon(envelope.getMaxY(), envelope.getMaxX(), null);

    for (int xIndex = minIndex[0]; xIndex <= maxIndex[0]; xIndex++) {
      for (int yIndex = minIndex[1]; yIndex <= maxIndex[1]; yIndex++) {
        LatLonPoint latLon = gcs.getLatLon(xIndex, yIndex);
        double lon = latLon.getLongitude();
        double lat = latLon.getLatitude();
        Point gridPoint = RmObjects.createPoint(factory, lon, lat);
        if (linearRing.contains(gridPoint)) {
          resultPoints.add(gridPoint);
        }
      }
    }
    return resultPoints;
  }

  /**
   *
   * @return
   */
  private GridCoordSystem getCoordinateSystem() {
    List<GridDatatype> grid = this.getGrid();
    return grid.get(0).getCoordinateSystem();
  }

}
