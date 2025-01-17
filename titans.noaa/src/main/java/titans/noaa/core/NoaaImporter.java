package titans.noaa.core;

import java.time.ZonedDateTime;
import rm.titansdata.properties.Bounds;
import rm.titansdata.properties.Dimensions;
import rm.titansdata.raster.RasterObj;

/**
 *
 * @author Ricardo Marquez
 */
public interface NoaaImporter {
  /**
   *  
   * @param var
   * @param datetime
   * @param fcststep
   * @param bounds
   * @param dims
   * @return 
   */
  RasterObj getRaster(NoaaVariable var, ZonedDateTime datetime, int fcststep, // 
    Bounds bounds, Dimensions dims);

  /**
   * 
   * @param dateTime 
   */
  public void removeRastersBefore(ZonedDateTime dateTime);

  /**
   * 
   * @param var
   * @param datetimeref
   * @param forecaststep 
   */
  public void removeIntermediateFiles(NoaaVariable var, ZonedDateTime datetimeref, int forecaststep);
  
  
}
