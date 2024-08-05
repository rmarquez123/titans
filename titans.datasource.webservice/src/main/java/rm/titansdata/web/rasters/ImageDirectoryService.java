package rm.titansdata.web.rasters;

import common.RmExceptions;
import common.RmKeys;
import common.RmObjects;
import java.io.File;
import java.time.ZonedDateTime;
import java.util.Properties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import rm.titansdata.Parameter;
import titans.noaa.core.NoaaParameter;

/**
 *
 * @author rmarq
 */
@Service
public class ImageDirectoryService implements InitializingBean {

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
   * @param projectId
   * @param rasterId
   * @param p
   * @return
   */
  public File createImageFile(int projectId, long rasterId, Parameter p) {
    String filename = getFileName(projectId, rasterId, p);
    File relativePath = this.getRelativePath();
    File imageFile = new File(relativePath, filename);
    RmObjects.createDirectoryIfDoesNotExist(imageFile.getParentFile());
    return imageFile;
  }

  /**
   *
   * @param p
   * @param rasterId
   * @return
   */
  private String getFileName(int projectId, long rasterId, Parameter p) {
    String filename;
    if (p instanceof NoaaParameter) {
      ZonedDateTime dateTime = ((NoaaParameter) p).datetime;
      int fcstStep = ((NoaaParameter) p).fcststep;
      String noaaVar = ((NoaaParameter) p).noaaVar;
      String dateTimeText = RmObjects.formatUtc(dateTime, "yyyyMMddHHmm");
      filename = String.format("%03d_%03d_%s_%s_%03d.png", projectId, rasterId, noaaVar, dateTimeText, fcstStep);
    } else {
      filename = rasterId + "_" + RmKeys.createKey() + ".png";
    }
    return filename;
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

  /**
   *
   * @param projectId
   * @param params
   */
  void cleanUpFiles(int projectId, Parameter params) {
    String filename;
    if (params instanceof NoaaParameter) {
      ZonedDateTime dateTime = ((NoaaParameter) params).datetime;
      int fcstStep = ((NoaaParameter) params).fcststep;
      String noaaVar = ((NoaaParameter) params).noaaVar;
      String dateTimeText = RmObjects.formatUtc(dateTime, "yyyyMMddHHmm");
      filename = String.format("%03d_%s_%s_%s_%03d.png", projectId, "*", noaaVar, dateTimeText, fcstStep);
      File[] filesToDelete = this.getRelativePath().listFiles((File dir, String name) -> {
        String regex = filename.replace("*", ".*");
        return name.matches(regex);
      });
      if (filesToDelete != null) {
        for (File file : filesToDelete) {
          file.delete();
        }
      }
    }
  }
  
  /**
   * 
   * @param projectId
   * @param dateTime 
   */
  void cleanUpFiles(int projectId, ZonedDateTime dateTime) {
    String dateTimeText = RmObjects.formatUtc(dateTime, "yyyyMMddHHmm");
    String filename = String.format("%03d_%s_%s_%s_%s.png", projectId, "*", "*", dateTimeText, "*");
    File[] filesToDelete = this.getRelativePath().listFiles((File dir, String name) -> {
      String regex = filename.replace("*", ".*");
      boolean result = name.matches(regex);
      if (!result) {
        String[] parts = name.split("_");
        if (parts.length == 5) {
          ZonedDateTime fileDate = RmObjects.dateTimeOfInUtc("yyyyMMddHHmm", parts[3]);
          result = fileDate.isBefore(dateTime);
        }
      }
      return result;
    });
    if (filesToDelete != null) {
      for (File file : filesToDelete) {
        file.delete();
      }
    }
  }
}
