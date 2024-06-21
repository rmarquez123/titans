package titans.href.core;

import java.time.ZoneId;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import titans.noaa.core.NoaaGribSource;
import titans.noaa.core.NoaaParameter;
import titans.noaa.grib.GribFile;

/**
 * https://nomads.ncep.noaa.gov/pub/data/nccf/com/href/prod/href.20240604/ensprod/href.t00z.conus.avrg.f02.grib2
 *
 * @author Ricardo Marquez
 */
public class HrefGribSource extends NoaaGribSource {

  private final String url = "https://nomads.ncep.noaa.gov/pub/data/nccf/com/href/prod/";

  /**
   *
   * @return
   */
  @Override
  public String createUrl(GribFile gribFile) {
    String baseFileName = gribFile.getBaseFileName();
    String dateTxt = gribFile.datetimeref
            .toOffsetDateTime().atZoneSameInstant(ZoneId.of("UTC"))
            .format(new DateTimeFormatterBuilder().appendPattern("yyyyMMdd").toFormatter());
    String result = String.format("%shref.%s/ensprod/%s",
            this.url, dateTxt, baseFileName);
    return result;
  }

  /**
   *
   * @param parentKey
   * @return
   */
  @Override
  public List<NoaaParameter> getParameters(String parentKey, int minusDays) {
    throw new UnsupportedOperationException();
  }
}
