package rm.titansdata.web.rasters;

import common.RmExceptions;
import common.RmKeys;
import common.RmObjects;
import java.io.File;
import java.util.Properties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 *
 * @author rmarq
 */
@Service
public class ImageDirectoryService implements InitializingBean{
  
  @Autowired
  @Qualifier("appProps")
  private Properties appProps;
  
  /**
   * 
   * @throws Exception 
   */
  @Override  
  public void afterPropertiesSet() throws Exception {   
    if (!this.appProps.containsKey("imagedatapath")) {
      throw new RuntimeException("'imagedatapath' is not defined"); 
    }    
    if (!this.appProps.containsKey("imagedatapath_external_url")) {
      throw new RuntimeException("'imagedatapath_external_url' is not defined"); 
    }           
  }
  
    
  /**
   * 
   * @param rasterId
   * @return 
   */
  public File createImageFile(long rasterId) {
    String filename = rasterId + "_" + RmKeys.createKey() + ".png";
    File relativePath = this.getRelativePath();
    File imageFile = new File(relativePath, filename);
    RmObjects.createDirectoryIfDoesNotExist(imageFile.getParentFile());
    return imageFile;
  }
  
  /**
   * 
   * @return 
   */
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
    if (!file.exists()) {
      throw RmExceptions.create("File for code '%s' doesn't exist", code);
    }
    return file;
  }
  
  /**
   * 
   * @return 
   */
  public String getExternalUrl() {
    String result = this.appProps.getProperty("imagedatapath_external_url"); 
    return result; 
  }
}
