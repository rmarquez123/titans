package rm.titansdata.plugin;

import rm.titansdata.Parameter;
import rm.titansdata.colormap.ColorMap;

/**
 *
 * @author Ricardo Marquez
 */
public interface ColorMapProvider {

  

  /**
   *
   * @param projectId
   * @param colorMapName
   * @param param
   * @return
   */
  public ColorMap getColorMap(int projectId, String colorMapName, Parameter param);
}
