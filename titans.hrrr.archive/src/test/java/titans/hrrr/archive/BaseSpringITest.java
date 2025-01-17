package titans.hrrr.archive;

import org.junit.Before;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.TestContextManager;
import common.geom.SridUtils;

/**
 *
 * @author Ricardo Marquez
 */
@ContextHierarchy({
  @ContextConfiguration(
    locations = {
      "/hrrr.arch.spring.xml"
    }
  )
})
public class BaseSpringITest {
  
  /**
   * 
   * @throws Exception 
   */
  @Before
  public void setup() throws Exception {
    TestContextManager testContextManager = new TestContextManager(getClass());
    testContextManager.prepareTestInstance(this);
    SridUtils.init();
  }

  
}
