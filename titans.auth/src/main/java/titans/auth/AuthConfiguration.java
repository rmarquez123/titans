package titans.auth;

import common.db.DbConnection;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Ricardo Marquez
 */
@Configuration
public class AuthConfiguration {

  @Autowired
  @Qualifier("appProps")
  private Properties appProps;

  /**
   *
   * @return
   */
  @Bean("auth.db")
  public DbConnection conn() {
    DbConnection result = new DbConnection.Builder()
            .setUrl(appProps.getProperty("app.db.host"))
            .setPort(Integer.valueOf(appProps.getProperty("app.db.port")))
            .setDatabaseName(appProps.getProperty("app.db.database"))
            .setUser(appProps.getProperty("app.db.user"))
            .setPassword(appProps.getProperty("app.db.password"))
            .createDbConnection();
    return result;
  }
}
