package rm.titansdata.web.rasters.colormap;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rm.titansdata.Parameter;
import rm.titansdata.colormap.ColorMap;
import rm.titansdata.plugin.ColorMapProvider;
import rm.titansdata.properties.Bounds;
import rm.titansdata.raster.RasterObj;
import rm.titansdata.raster.RasterSearch;
import rm.titansdata.web.rasters.RastersSourceService;
import rm.titansdata.web.rasters.RastersValueService;

/**
 *
 * @author Ricardo Marquez
 */
@Component
public class ColorMapProviderFactory {
  
  @Autowired
  private RastersValueService rastersValueService;
  
  @Autowired
  private RastersSourceService sourceService;
  
  private final Map<Long, ColorMapProvider> providers = new HashMap<>();

  /**
   *
   * @param rasterId
   * @param provider
   */
  public void put(long rasterId, ColorMapProvider provider) {
    this.providers.put(rasterId, provider);
  }

  /**
   *
   * @param rasterId
   * @param projectId
   * @return
   */
  public ColorMapProvider getProvider(long rasterId, int projectId) {
    ColorMapProvider result;
    if (providers.containsKey(rasterId)) {      
      result = this.providers.get(rasterId);      
    } else {
      result = (int projectId1, String colorMap, Parameter param) -> defaultColorMap(rasterId, projectId1, param, colorMap);
    }
    return result;    
  }

  /**
   *
   * @param rasterId
   * @param projectId
   * @param param
   * @param colorMap
   * @return
   */
  public ColorMap defaultColorMap(long rasterId, int projectId, Parameter param, String colorMap) {
    Bounds bounds = this.sourceService.getRaster(rasterId).getBounds();
    RasterObj r = this.rastersValueService.getRasterObj(rasterId, projectId, param, bounds);
    RasterSearch s = new RasterSearch(r.getBounds(), r.getDimensions());
    double max = s.stream(b -> r.getValue(b))
            .mapToDouble(i -> i.getValue().getValue())
            .max()
            .orElseThrow(() -> new RuntimeException());
    double min = s.stream(b -> r.getValue(b))
            .mapToDouble(i -> i.getValue().getValue())
            .min()
            .orElseThrow(() -> new RuntimeException());
    ColorMap cmap = new ColorMap.Builder()
            .setXmin(min)
            .setXmax(max)
            .setColorMapName(colorMap)
            .setUnits("Unit")
            .build();
    return cmap;
  }
}
