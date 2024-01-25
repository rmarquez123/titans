package rm.titansdata.web.rasters;

import common.RmKeys;
import java.io.File;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 *
 * @author rmarq
 */
@Service
public class ImageDirectoryService {
  
  @Autowired
  @Qualifier("appProps")
  private Properties appProps;
  
  public File createImageFile(long rasterId) {
    String filename = rasterId + "_" + RmKeys.createKey() + ".png";
    File relativePath = this.getRelativePath();
    File imageFile = new File(relativePath, filename);
    if (!imageFile.getParentFile().exists()) {
      imageFile.getParentFile().mkdirs();
    }
    return imageFile;
  }

  private File getRelativePath() {
    String relativePathString = appProps.getProperty("imagedatapath");
    File relativePath = new File(relativePathString);
    return relativePath;
  }
    
  /**
   * 
   * @param code
   * @return 
   */
  public File getImageFile(String code) {
    File relativePath = this.getRelativePath();
    File file = new File(relativePath, code);
    return file;
  }
}
