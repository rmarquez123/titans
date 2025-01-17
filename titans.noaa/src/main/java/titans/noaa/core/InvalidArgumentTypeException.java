package titans.noaa.core;


/**
 *
 * @author Ricardo Marquez
 */
public class InvalidArgumentTypeException extends RuntimeException {
  
  /**
   * 
   * @param p
   * @param aClass 
   */
  public InvalidArgumentTypeException(Object p, Class<NoaaParameter> aClass) {
    super(String.format("The object '%s' is not a type of %s", p, aClass)); 
  }
  
}
