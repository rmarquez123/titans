package titans.noaa.core;

import java.time.ZonedDateTime;
import javax.measure.unit.Unit;
import rm.titansdata.Parameter;
import rm.titansdata.plugin.RasterFactory;
import rm.titansdata.properties.Bounds;
import rm.titansdata.properties.Dimensions;
import rm.titansdata.raster.Raster;
import rm.titansdata.raster.RasterObj;

/**
 *
 * @author Ricardo Marquez
 */
public abstract class NoaaRasterFactory implements RasterFactory {

  /**
   *
   * @param projectId
   * @param p
   * @param bounds
   * @param dims
   * @return
   */
  @Override
  public final Raster create(int projectId, Parameter p, Bounds bounds, Dimensions dims) {
    if (p instanceof NoaaParameter) {
      NoaaParameter namparam = (NoaaParameter) p;
      int fcststep = namparam.fcststep;
      ZonedDateTime datetime = namparam.datetime;
      String varName = namparam.noaaVar;
      Unit<?> unit = namparam.getUnit();
      NoaaVariable var = new NoaaVariable(varName, unit);
      NoaaImporter importer = this.getImporter(projectId);
      RasterObj rasterObj = importer.getRaster(var, datetime, fcststep, bounds, dims);
      Raster result = rasterObj.getRaster();
      return result;
    } else {
      throw new InvalidArgumentTypeException(p, NoaaParameter.class);
    }
  }

  /**
   * An importer is needed to download data from external sources. The importer
   * is responsible for downloading and storing data on the local server. The
   * project id can be used for miscellaneous information such as the valid
   * bounds and dimensions.
   *
   * @param projectId
   * @return
   */
  protected abstract NoaaImporter getImporter(int projectId);

  /**
   * 
   * @param projectId
   * @param dateTime 
   */
  @Override
  public final void deleteStoredFilesBefore(int projectId, ZonedDateTime dateTime) {
    NoaaImporter importer = this.getImporter(projectId);
    importer.removeRastersBefore(dateTime);
  }
}
