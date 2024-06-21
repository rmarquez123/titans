package titans.href;

import titans.href.core.HrefImporter;
import java.io.File;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import titans.noaa.core.NoaaGribImporter;
import titans.noaa.core.NoaaRasterFactory;

/**
 *
 * @author rmarq
 */
@Component
public class HrefRasterFactory extends NoaaRasterFactory {

  private final HrefImporter.Builder hrrrImporterBuilder;
  
  public HrefRasterFactory(@Qualifier("gribRootFolder") File gribRootFolder,
          @Qualifier("netCdfRootFolder") File netCdfRootFolder,
          @Qualifier("degribExe") File degribExe){
        this.hrrrImporterBuilder = new HrefImporter.Builder()
            .setDegribExe(degribExe)  
            .setGribRootFolder(new File(gribRootFolder, "hrrr.archive"))
            .setNetCdfRootFolder(new File(netCdfRootFolder, "hrrr.archive"));
  }
  /**
   *
   * @return
   */
  @Override
  public String key() {
    String key = "High Resolution Ensemble Forecast System";
    return key;
  }

  /**
   *
   * @param projectId
   * @return
   */
  @Override
  protected NoaaGribImporter getImporter(int projectId) {
    return this.hrrrImporterBuilder.setSubfolderId(projectId).build();
  }

}
