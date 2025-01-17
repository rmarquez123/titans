package rm.titansdata.web.rasters;

import common.geom.SridUtils;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.ZonedDateTime;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rm.titansdata.Parameter;
import rm.titansdata.colormap.ColorMap;
import rm.titansdata.images.RasterImage;
import rm.titansdata.plugin.ColorMapProvider;
import rm.titansdata.properties.Bounds;
import rm.titansdata.raster.RasterObj;
import rm.titansdata.web.rasters.colormap.ColorMapProviderFactory;

/**
 *
 * @author Ricardo Marquez
 */
@Component
public class RasterImageService {

  @Autowired
  private RastersValueService rastersValueService;

  @Autowired
  private ColorMapProviderFactory factory;

  @Autowired
  private ImageDirectoryService imageDirectory;

  /**
   *
   * @param rasterId
   * @param projectId
   * @param param
   * @param bounds
   * @param colorMapName
   * @return
   */
  public RasterImageResult getRasterImage(long rasterId, int projectId, // 
          Parameter param, Bounds bounds, String colorMapName) {

    RasterObj r = this.getRasterObj(rasterId, projectId, param, bounds);
    ColorMap cmap = this.getColorMap(rasterId, projectId, param, colorMapName);
    RasterImageResult result;
    try (RasterImage img = new RasterImage(r, cmap)) {
      result = this.toRasterImageResult(rasterId, projectId, param, img);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
    return result;
  }

  /**
   *
   * @param img
   * @param r
   * @param rasterId
   * @return
   */
  private RasterImageResult toRasterImageResult( //
          long rasterId, int projectId, Parameter param, RasterImage img) {
    BufferedImage bufferedImg = img.asBufferedImage();
    int targetSrid = 3857;
    Point upperRight = SridUtils.transform(img.getBounds().upperright(), targetSrid);
    Point lowerLeft = SridUtils.transform(img.getBounds().lowerleft(), targetSrid);
    String imageURL = this.getImageURL(projectId, rasterId, param, bufferedImg);
    RasterImageResult result = new RasterImageResult( //
            imageURL, upperRight, lowerLeft, targetSrid, img.colorMapObject());
    return result;
  }

  /**
   *
   * @param s
   * @param r
   * @return
   */
  private ColorMap getColorMap(long rasterId, int projectId, Parameter param, String colorMapName) {
    ColorMapProvider cm = this.factory.getProvider(rasterId, projectId);
    ColorMap result = cm.getColorMap(projectId, colorMapName, param);
    return result;

  }

  /**
   *
   * @param rasterId
   * @return
   */
  private RasterObj getRasterObj(long rasterId, int projectId, Parameter p, Bounds bounds) {
    RasterObj result = this.rastersValueService.getRasterObj(rasterId, projectId, p, bounds);
    return result;
  }

  /**
   *
   * @param rasterId
   * @param bufferedImg
   * @return
   */
  private String getImageURL(int projectId, long rasterId, Parameter p, BufferedImage bufferedImg) {
    File imageFile = this.imageDirectory.createImageFile(projectId, rasterId, p);
    try (ImageOutputStream output = new FileImageOutputStream(imageFile)) {
      ImageIO.write(bufferedImg, "png", output);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
    String externalUrl = this.imageDirectory.getExternalUrl();
    String result = externalUrl + File.separator + "data?code=" + imageFile.getName();
    return result;
  }

  /**
   *
   * @param projectId
   * @param params
   */
  public void cleanUpFiles(int projectId, Parameter params) {
    this.imageDirectory.cleanUpFiles(projectId, params);
  }

  /**
   *
   * @param projectId
   * @param dateTime
   */
  public void cleanUpFiles(int projectId, ZonedDateTime dateTime) {
    this.imageDirectory.cleanUpFiles(projectId, dateTime);
  }
}
