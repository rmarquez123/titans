package rm.titansdata.units;

import javax.measure.quantity.Quantity;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

/**
 *
 * @author Ricardo Marquez
 */
public class MassDensityRate implements Quantity{
  
  public static Unit<?> UNIT = SI.KILOGRAM.divide(SI.SQUARE_METRE).divide(SI.SECOND);
    ;
  
}
