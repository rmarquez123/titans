package titans.noaa.core;

import common.RmObjects;
import common.RmTimer;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import rm.titansdata.properties.Bounds;
import titans.noaa.grib.GribFile;

/**
 *
 * @author Ricardo Marquez
 */
public abstract class NoaaGribSource {

  /**
   *
   * @param gribFile
   * @return
   */
  public final GribFile download(GribFile gribFile) {
    URL gribUrl = this.getGribUrl(gribFile);
    this.createGribFileParent(gribFile);
    GribFile result = gribFile;
    if (!gribFile.exists()) {
      try {
        result = this.doDownload(gribUrl, gribFile);
      } catch(Exception ex) {
        String message = String.format(
                "Error on downloading gribFile: %s, class: %s", gribFile, this.getClass());
        throw new RuntimeException(message, ex);
      }
    }
    return result;
  }

  /**
   *
   * @param gribFile
   * @return
   */
  private URL getGribUrl(GribFile gribFile) {
    String urlText = this.createUrl(gribFile);
    urlText = urlText.replaceAll("\\\\", File.separator);
    URL gribUrl = this.toUrlObject(urlText);
    return gribUrl;
  }

  /**
   *
   * @param gribFile
   * @param gribUrl
   * @throws RuntimeException
   */
  private GribFile doDownload(URL gribUrl, GribFile gribFile) {
    RmTimer timer = RmTimer.start();
    try (OutputStream output = gribFile.getOutputStream(); // 
            InputStream inputStream = gribUrl.openStream()) {
      System.out.println("downloading from " + gribUrl);

      int bytesRead;
      byte[] buffer = new byte[256000];
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        output.write(buffer, 0, bytesRead);
      }
    } catch (Exception ex) {
      gribFile.delete();
      throw new RuntimeException("Error on copying stream.  Check args:{"
              + "output file : " + gribFile
              + ", connection : " + gribUrl
              + "}", ex);
    }
    timer.endAndPrint();
    GribFile result = this.onPostDownLoad(gribFile);
    return result;
  }

  private void createGribFileParent(GribFile gribFile) {
    File parentFile = gribFile.grib.getParentFile();
    boolean created = RmObjects.createDirectoryIfDoesNotExist(parentFile);
    if (!created) {
      throw new RuntimeException(String.format("Unable to make directory '%s'", parentFile));
    }
  }

  /**
   *
   * @param urlText
   * @return
   * @throws RuntimeException
   */
  private URL toUrlObject(String urlText) {
    URL gribUrl;
    try {
      gribUrl = new URL(urlText);
    } catch (MalformedURLException ex) {
      throw new RuntimeException(ex);
    }
    return gribUrl;
  }

  /**
   *
   * @param subfolderId
   * @param bounds
   * @return
   */
  boolean crop(File degribExe, GribFile gribFile, int subfolderId, Bounds bounds) {
    GribCropper cropper = new GribCropper(degribExe);
    boolean result = cropper.crop(gribFile, subfolderId, bounds);
    return result;
  }

  /**
   *
   * @param gribFile
   * @return
   */
  public abstract String createUrl(GribFile gribFile);

  /**
   *
   * @param parentKey
   * @param minusDays
   * @return
   */
  public abstract List<NoaaParameter> getParameters(String parentKey, int minusDays);

  /**
   *
   * @param gribFile
   * @return
   */
  protected GribFile onPostDownLoad(GribFile gribFile) {
    return gribFile;
  }

}
