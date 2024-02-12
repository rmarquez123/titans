package titans.noaa.core;

import java.util.List;
import javax.measure.unit.Unit;

/**
 *
 * @author Ricardo Marquez
 */
public interface InventoryReader {

  /**
   * 
   * @return 
   */
  public List<NoaaVarClazz> read();
  
  
  /**
   * 
   * @param var
   * @return 
   */
  public Unit<?> getUnit(String var);
}
