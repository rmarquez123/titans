package titans.hrrr.archive;

import java.io.File;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import titans.hrrr.archive.core.HrrrArchiveImporter;
import titans.noaa.core.NoaaGribImporter;
import titans.noaa.core.NoaaRasterFactory;

/**
 *
 * @author Ricardo Marquez
 */
@Component
public class HrrrArchiveRasterFactory extends NoaaRasterFactory {

  private final HrrrArchiveImporter.Builder hrrrImporterBuilder;

  public HrrrArchiveRasterFactory(@Qualifier("gribRootFolder") File gribRootFolder,
          @Qualifier("netCdfRootFolder") File netCdfRootFolder,
          @Qualifier("degribExe") File degribExe) {
    this.hrrrImporterBuilder = new HrrrArchiveImporter.Builder()
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
    String key = "High Resolution Rapid Refresh (Archived)";
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
