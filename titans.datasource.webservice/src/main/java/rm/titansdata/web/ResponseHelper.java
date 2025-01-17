package rm.titansdata.web;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

/**
 *
 * @author Ricardo Marquez
 */
@Component
public class ResponseHelper {

  public ResponseHelper() {
  }

  /**
   *
   * @param result
   * @param response
   */
  public void send(String result, HttpServletResponse response) {
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    try (PrintWriter writer = response.getWriter()) {
      writer.write(result);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   *
   * @param result
   * @param response
   */
  public void send(Map<String, ? extends Object> result, HttpServletResponse response) {
    response.setHeader("Access-Control-Allow-Origin", "*");
    try (PrintWriter writer = response.getWriter()) {
      
      writer.write(JsonConverterUtil.toJson(result));
      
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   *
   * @param result
   * @param response */
  public void sendAsZippedFile(String result, HttpServletResponse response) {
    response.setContentType("application/zip");
    String filename = "sampleZip.zip";
    response.addHeader("Content-Disposition", "attachment; filename=" + filename);
    try (OutputStream responseOutputStream = response.getOutputStream()) {
      ZippedOutputUtil.zipToOutput(result, responseOutputStream);
      response.flushBuffer();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
}
