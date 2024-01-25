package rm.titansdata.web.jars;

import java.net.URL;
import java.net.URLClassLoader;

/**
 *
 * @author rmarq
 */
public class URLClassLoaderWrapper extends URLClassLoader {

  private final ClassLoader contextClassLoader;

  public URLClassLoaderWrapper(URL[] urls, ClassLoader contextClassLoader) {
    super(urls, contextClassLoader);
    this.contextClassLoader = contextClassLoader;
  }

  @Override
  public void addURL(URL url) {
    super.addURL(url);
  }

  @Override
  protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    try {
      return super.loadClass(name, resolve);  
    } catch (ClassNotFoundException e) { 
      return contextClassLoader.loadClass(name);
    }
  }

}
