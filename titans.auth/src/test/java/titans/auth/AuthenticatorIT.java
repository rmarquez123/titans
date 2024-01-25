package titans.auth;

import java.util.Optional;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;



/**
 *
 * @author Ricardo Marquez
 */
@RunWith(JUnitParamsRunner.class)
public class AuthenticatorIT extends BaseSpringTest {
  
  @Autowired
  private Authenticator auth;

  @Test
  @Parameters({
    "ricardo.marquez@terrabyteanalytics.com, password, true"
    , "ricardo.marquez@terrabyteanalytics.com, passwords, false"
    , "ricardo.marquez@terrabyteanalytic.com, password, false"
  })
  public void authenticate_test(String email, String password, boolean expresult) {
    Optional<String> authToken = this.auth.authenticate(email, password);
    Assert.assertEquals("expected result:", expresult, authToken.isPresent());
  }
  
}
