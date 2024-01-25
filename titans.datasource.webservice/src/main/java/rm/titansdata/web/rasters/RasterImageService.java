package rm.titansdata.web.rasters;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.ServletContext;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rm.titansdata.Parameter;
import rm.titansdata.SridUtils;
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
  private ServletContext servletContext;

  /**
   *
   * @param rasterId
   * @param projectId
   * @param param
   * @param bounds
   * @return
   */
  public RasterImageResult getRasterImage(long rasterId, int projectId, Parameter param, Bounds bounds) {
    RasterObj r = this.getRasterObj(rasterId, projectId, param, bounds);
    ColorMap cmap = this.getColorMap(rasterId, projectId, param, bounds);
    RasterImage img = new RasterImage(r, cmap);
    RasterImageResult result = this.toRasterImageResult(rasterId, img);
    return result;
  }

  /**
   *
   * @param img
   * @param r
   * @param rasterId
   * @return
   */
  private RasterImageResult toRasterImageResult(long rasterId, RasterImage img) {
    BufferedImage bufferedImg = img.asBufferedImage();
    int targetSrid = 3857;
    Point upperRight = SridUtils.transform(img.getBounds().upperright(), targetSrid);
    Point lowerLeft = SridUtils.transform(img.getBounds().lowerleft(), targetSrid);
    String imageURL = this.getImageURL(rasterId, bufferedImg);
    RasterImageResult result = new RasterImageResult(imageURL, upperRight, lowerLeft, targetSrid, img.colorMapObject());
    return result;
  }

  /**
   *
   * @param s
   * @param r
   * @return
   */
  private ColorMap getColorMap(long rasterId, int projectId, Parameter param, Bounds bounds) {
    ColorMapProvider cm = this.factory.getProvider(rasterId, projectId);
    ColorMap result = cm.getColorMap(projectId, param);
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
  
  
  
  @Autowired
  private ImageDirectoryService imageDirectory;
  /**
   *
   * @param rasterId
   * @param bufferedImg
   * @return
   */
  private String getImageURL(long rasterId, BufferedImage bufferedImg) {
    File imageFile = this.imageDirectory.createImageFile(rasterId); 
    try (ImageOutputStream output = new FileImageOutputStream(imageFile)) {
      ImageIO.write(bufferedImg, "png", output);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
    this.servletContext.getContextPath();
    String result = "http://localhost:8080/titansdata.web.dev/data?code=" + imageFile.getName();
    return result;
  }
}
