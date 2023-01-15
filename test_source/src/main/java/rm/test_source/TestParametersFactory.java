package rm.test_source;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import rm.titansdata.Parameter;
import rm.titansdata.plugin.ParameterFactory;

/**
 *
 * @author Ricardo Marquez
 */
@Component
public class TestParametersFactory implements ParameterFactory {

  @Override
  public String key() {
    return "test_source"; 
  }

  @Override
  public List<Parameter> getParameters() {
    List<Parameter> result = new ArrayList<>();
    result.add(new Parameter() {
      @Override
      public JSONObject toJSONObject() {
        return new JSONObject();
      }
      
    }); 
    return result;
  }
  
  
  
  
  @Override
  public Parameter create(JSONObject obj) {
    return new Parameter() {
      @Override
      public JSONObject toJSONObject() {
        //To change body of generated methods, choose Tools | Templates.
        throw new UnsupportedOperationException("Not supported yet.");
      }
      
    };
  }
  
}