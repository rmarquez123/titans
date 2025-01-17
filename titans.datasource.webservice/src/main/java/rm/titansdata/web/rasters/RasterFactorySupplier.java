package rm.titansdata.web.rasters;

import java.time.ZonedDateTime;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rm.titansdata.Parameter;
import rm.titansdata.plugin.RasterFactory;
import rm.titansdata.properties.Bounds;
import rm.titansdata.properties.Dimensions;
import rm.titansdata.raster.Raster;

/**
 *
 * @author Ricardo Marquez
 */
@Component
public class RasterFactorySupplier {

  @Autowired
  private RasterModelsRegistry rasterModelsRegistry;

  /**
   *
   * @return
   */
  public Builder builder() {
    Builder result = new Builder(this.rasterModelsRegistry);
    return result;
  }
  
  /**
   * 
   * @param dateTime 
   */
  void deleteStoredFilesBefore(int projectId, ZonedDateTime dateTime) {
    this.rasterModelsRegistry.forEach(factory->{
      factory.deleteStoredFilesBefore(projectId, dateTime);
    });
  }
  
  /***
   * 
   * @param projectId
   * @param dateTime 
   */
  void deleteIntermediateFiles(int projectId, Parameter param, ZonedDateTime dateTime, int fcstStep) {
    this.rasterModelsRegistry.forEach(factory->{
      factory.deleteIntermediateFiles(projectId, param, dateTime, fcstStep);
    });
  }

  public static class Builder {

    private final RasterModelsRegistry rasterModelsRegistry;
    private Bounds bounds;
    private Dimensions dims;
    private long typeId;
    private String sourceTitle;

    private Builder(RasterModelsRegistry rasterModelsRegistry) {
      this.rasterModelsRegistry = rasterModelsRegistry;
    }

    public Builder setBounds(Bounds bounds) {
      this.bounds = bounds;
      return this;
    }

    public Builder setDimensions(Dimensions dims) {
      this.dims = dims;
      return this;
    }

    public Builder setTypeId(long typeId) {
      this.typeId = typeId;
      return this;
    }

    public Builder setSourceTitle(String sourceTitle) {
      this.sourceTitle = sourceTitle;
      return this;
    }

    /**
     * 
     * @param projectId
     * @param p
     * @return 
     */
    public Raster createRaster(int projectId, Parameter p) {
      Raster result;
      Objects.requireNonNull(p, "Parameter cannot be null."); 
      int intValue = Long.valueOf(this.typeId).intValue();
      switch (intValue) {
        case 0:
          RasterFactory rasterFactory = Objects //
            .requireNonNull(this.rasterModelsRegistry.get(this.sourceTitle), 
            String.format("No raster factory found for '%s'", this.sourceTitle));
          result = rasterFactory.create(projectId, p, bounds, dims);
          break;
        default:
          throw new RuntimeException();
      }
      return result;
    }
  }
}
