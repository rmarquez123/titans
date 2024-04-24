package rm.titansdata.web;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Ricardo Marquez
 */
public class ZippedOutputUtil {

  /**
   *
   * @param jsontext
   * @param zippedOutputStream
   */
  public static void zipToOutput(String jsontext, OutputStream zippedOutputStream) {
    try (ZipOutputStream out = new ZipOutputStream(zippedOutputStream)) {
      ZipEntry e = new ZipEntry("result.json");
      byte[] data = jsontext.getBytes();
      out.putNextEntry(e);
      out.write(data, 0, data.length);
      out.closeEntry();
      out.finish();  
      out.flush();
    } catch (Exception ex) {
      throw new RuntimeException("Error zipping the data: " + ex.getMessage(), ex);
    }
  }

  /**
   *
   * @return
   */
  private static File getTempFile() {
    File jsonfile;
    try {
      jsonfile = File.createTempFile("rastervalues", ".json");
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    return jsonfile;
  }
}
