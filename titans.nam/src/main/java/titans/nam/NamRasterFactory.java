package titans.nam;

import java.io.File;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import titans.nam.core.NamImporter;
import titans.noaa.core.NoaaGribImporter;
import titans.noaa.core.NoaaRasterFactory;

/**
 *
 * @author Ricardo Marquez
 */
@Component
@DependsOn({"gribRootFolder", "degribExe"}) 
public class NamRasterFactory extends NoaaRasterFactory {
  private final NamImporter.Builder namImporterBuilder;
  
  /**
   *
   * @param gribRootFolder
   * @param netCdfRootFolder
   * @param degribExe
   */
  public NamRasterFactory(
    @Qualifier("gribRootFolder") File gribRootFolder, 
    @Qualifier("netCdfRootFolder") File netCdfRootFolder, 
    @Qualifier("degribExe") File degribExe) {
    this.namImporterBuilder = new NamImporter.Builder() //
            . setGribRootFolder(new File(gribRootFolder, "nam")) //
            .setNetCdfRootFolder(new File(netCdfRootFolder, "nam")) //
            .setDegribExe(degribExe);
  }
  
  /**
   *
   * @return
   */
  @Override
  public String key() {
    String key = "North American Model Forecasts";
    return key;
  }
  
  /**
   * 
   * @param projectId
   * @return 
   */
  @Override
  protected NoaaGribImporter getImporter(int projectId) {
    NamImporter result = this.namImporterBuilder //
      .setSubfolderId(projectId)//
      .build(); //
    return result; 
  }

  
}
