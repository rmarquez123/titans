package titans.hrrr;

import java.io.File;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import titans.hrrr.core.HrrrImporter;
import titans.noaa.core.NoaaGribImporter;
import titans.noaa.core.NoaaRasterFactory;

/**
 *
 * @author Ricardo Marquez
 */
@Component
@DependsOn({"gribRootFolder", "degribExe"})
public class HrrrRasterFactory extends NoaaRasterFactory {

  private final HrrrImporter.Builder hrrrImporterBuilder;

  /**
   *
   * @param gribRootFolder
   * @param netCdfRootFolder
   * @param degribExe
   */
  public HrrrRasterFactory(
          @Qualifier("gribRootFolder") File gribRootFolder,
          @Qualifier("netCdfRootFolder") File netCdfRootFolder,
          @Qualifier("degribExe") File degribExe
  ) {
    this.hrrrImporterBuilder = new HrrrImporter.Builder()
            .setDegribExe(degribExe)//
            .setGribRootFolder(new File(gribRootFolder, "hrrr")) //
            .setNetCdfRootFolder(new File(netCdfRootFolder, "hrrr"));
  }

  /**
   *
   * @return
   */
  @Override
  public String key() {
    String key = "High Resolution Rapid Refresh";
    return key;
  }

  /**
   *
   * @param projectId
   * @return
   */
  @Override
  protected NoaaGribImporter getImporter(int projectId) {
    return this.hrrrImporterBuilder
            .setSubfolderId(projectId).build();
  }
}
