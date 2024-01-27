package titans.mrms;

import java.io.File;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import titans.noaa.core.NoaaGribImporter;
import titans.noaa.core.NoaaRasterFactory;

/**
 *
 * @author Ricardo Marquez
 */
@Component
public class MrmsRasterFactory extends NoaaRasterFactory {

  private final MrmsImporter.Builder importer;

  public MrmsRasterFactory(
    @Qualifier("gribRootFolder") File gribRootFolder, 
    @Qualifier("netCdfRootFolder") File netCdfRootFolder, 
    @Qualifier("degribExe") File degribExe) {
    this.importer = new MrmsImporter.Builder() //
            .setGribRootFolder(new File(gribRootFolder, "mrms")) //
            .setNetCdfRootFolder(new File(netCdfRootFolder, "mrms"))
            .setDegribExe(degribExe);
  }

  @Override
  public String key() {
    return "Multi-Radar Multi-Sensor"; 
  }
  
  /**
   * 
   * @param projectId
   * @return 
   */
  @Override
  protected NoaaGribImporter getImporter(int projectId) {
    return this.importer.setSubfolderId(projectId).build();
  }
}
