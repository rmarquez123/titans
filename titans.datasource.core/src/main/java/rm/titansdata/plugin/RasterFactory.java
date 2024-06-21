package rm.titansdata.plugin;

import java.time.ZonedDateTime;
import rm.titansdata.Parameter;
import rm.titansdata.properties.Bounds;
import rm.titansdata.properties.Dimensions;
import rm.titansdata.raster.Raster;

/**
 *
 * @author Ricardo Marquez
 */
public interface RasterFactory {  
  /**
   * A key that corresponds to how the raster factory is identified in factory
   * @return 
   */
  public String key(); 
  
  /**
   * Creates a Raster based on project id, parameter, bounds, and dimensions. 
   * @param projectId
   * @param param
   * @param bounds
   * @param dims 
   * @return  
   * @see Raster
   */
  public Raster create(int projectId, Parameter param, Bounds bounds, Dimensions dims); 
  
  /**
   * 
   * @param projectId
   * @param dateTime 
   */
  public void deleteStoredFilesBefore(int projectId, ZonedDateTime dateTime);
  
  /**
   * 
   * @param projectId
   * @param p
   * @param dateTime
   * @param fcstStep 
   */  
  public void deleteIntermediateFiles(int projectId, Parameter p, ZonedDateTime dateTime, int fcstStep);
  
}
