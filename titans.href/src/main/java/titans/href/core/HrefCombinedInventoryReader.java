package titans.href.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.measure.unit.Unit;
import titans.noaa.core.InventoryReader;
import titans.noaa.core.NoaaVarClazz;

/**
 *
 * @author rmarq
 */
public class HrefCombinedInventoryReader implements InventoryReader {

  private final Map<String, InventoryReader> readers = new LinkedHashMap<>();

  public HrefCombinedInventoryReader() {
    this.readers.put("mean", new HrefMeanInventoryReader());
    this.readers.put("sprd", new HrefSpreadInventoryReader());
  }

  /**
   *
   * @param var
   * @return
   */
  public String getPrefix(String var) {
    String result = null;
    for (Map.Entry<String, InventoryReader> object : this.readers.entrySet()) {
      long count = object.getValue().read().stream().filter(a -> a.getVarName().equals(var)).count();
      if (count >= 1) {
        result = object.getKey();
        break;
      }
    }
    return result;
  }

  /**
   *
   * @return
   */
  @Override
  public List<NoaaVarClazz> read() {
    List<NoaaVarClazz> result = new ArrayList<>();
    this.readers.values().forEach(r -> result.addAll(r.read()));
    return result;
  }

  /**
   *
   * @param var
   * @return
   */
  @Override
  public Unit<?> getUnit(String var) {
    Unit<?> result = null;
    for (InventoryReader reader : this.readers.values()) {
      result = reader.getUnit(var);
      if (result != null) {
        break;
      }
    }
    return result;
  }

}
