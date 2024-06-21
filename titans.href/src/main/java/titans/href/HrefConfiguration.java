package titans.href;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import titans.noaa.core.NoaaParameter;

/**
 *
 * @author rmarq
 */
@Configuration
public class HrefConfiguration {

  /**
   *
   * @return
   */
  @Bean("href.parameters")
  public ListProperty<NoaaParameter> getParameters() {
    ListProperty<NoaaParameter> result = new SimpleListProperty<>();
    result.setValue(FXCollections.observableArrayList());
    return result;
  }
}
