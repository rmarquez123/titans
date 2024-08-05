/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package titans.noaa.grib;

import java.io.File;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatterBuilder;
import org.junit.Test;

/**
 *
 * @author rmarq
 */
public class GribFileIT {
  
  @Test
  public void test() {
    ZonedDateTime datetimeref = ZonedDateTime.now();
    String datetext = datetimeref //
            .toOffsetDateTime() //
            .atZoneSameInstant(ZoneId.of("UTC")) //
            .format(new DateTimeFormatterBuilder()
                    .appendPattern("yyyy/MM/dd")
                    .toFormatter());
    File parent;
    int subfolder = 0;
    File rootFolder = new File("");
    if (subfolder >= 0) {
      parent = new File(rootFolder, String.format("%04d/%s", subfolder, datetext));
    } else {
      parent = new File(rootFolder, datetext);
    }
    System.out.println(parent);
  }
}
