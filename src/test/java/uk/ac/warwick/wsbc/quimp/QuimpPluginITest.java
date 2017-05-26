package uk.ac.warwick.wsbc.quimp;

import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.warwick.wsbc.quimp.plugin.IQuimpCorePlugin;
import uk.ac.warwick.wsbc.quimp.plugin.ParamList;
import uk.ac.warwick.wsbc.quimp.plugin.engine.PluginFactory;
import uk.ac.warwick.wsbc.quimp.plugin.engine.PluginProperties;
import uk.ac.warwick.wsbc.quimp.plugin.snakes.IQuimpBOAPoint2dFilter;

/**
 * Test class for HatFilter.
 * 
 * @author p.baniukiewicz
 *
 */
public class QuimpPluginITest {

  static final Logger LOGGER = LoggerFactory.getLogger(QuimpPluginITest.class.getName());

  /**
   * Here is place where tested plugins should resist.
   */
  static final Path pluginsDir = Paths.get("target/dependencies");

  private PluginFactory pf;

  /**
   * Load all plugins
   * 
   * @throws Exception
   */
  @Before
  public void setUp() throws Exception {
    pf = new PluginFactory(pluginsDir);

  }

  /**
   * @throws Exception
   */
  @After
  public void tearDown() throws Exception {
  }

  /**
   * 
   */
  @Test
  public void testPluginAPI() {
    Map<String, PluginProperties> regPlugins = pf.getRegisterdPlugins();
    if (regPlugins.isEmpty()) {
      fail("No plugins found");
    } else {
      LOGGER.info(regPlugins.toString());
    }
    // call api - all types of plugins
    ArrayList<String> names = new ArrayList<>();
    names.addAll(pf.getPluginNames(IQuimpCorePlugin.GENERAL));
    names.addAll(pf.getPluginNames(IQuimpCorePlugin.CHANGE_SIZE));
    names.addAll(pf.getPluginNames(IQuimpCorePlugin.DOES_SNAKES));
    names.addAll(pf.getPluginNames(IQuimpCorePlugin.MODIFY_INPUT));
    if (names.isEmpty()) {
      fail("No plugins. Check types");
    }
    // iterate over plugins
    Iterator<String> iterator = names.iterator();
    while (iterator.hasNext()) {
      String name = iterator.next();
      LOGGER.info("Testing plugin: " + name);
      IQuimpCorePlugin pluginInstance = pf.getInstance(name);
      Method ret;
      // call all interfaces
      try {
        ret = pluginInstance.getClass().getDeclaredMethod("about", new Class<?>[] {});
        if (ret.getReturnType() != String.class) {
          fail("Bad return type for about()");
        }
        ret = pluginInstance.getClass().getDeclaredMethod("setPluginConfig",
                new Class<?>[] { ParamList.class });
        if (ret.getReturnType() != void.class) {
          fail("Bad return type for setPluginConfig()");
        }
        ret = pluginInstance.getClass().getDeclaredMethod("getPluginConfig", new Class<?>[] {});
        if (ret.getReturnType() != ParamList.class) {
          fail("Bad return type for getPluginConfig()");
        }
        ret = pluginInstance.getClass().getDeclaredMethod("getVersion", new Class<?>[] {});
        if (ret.getReturnType() != String.class) {
          fail("Bad return type for getVersion()");
        }
        ret = pluginInstance.getClass().getDeclaredMethod("setup", new Class<?>[] {});
        if (ret.getReturnType() != int.class) {
          fail("Bad return type for setup()");
        }
        if (pluginInstance instanceof IQuimpBOAPoint2dFilter) {
          ret = pluginInstance.getClass().getDeclaredMethod("attachData",
                  new Class<?>[] { List.class });
          if (ret.getReturnType() != void.class) {
            fail("Bad return type for attachData()");
          }
          ret = pluginInstance.getClass().getDeclaredMethod("runPlugin", new Class<?>[] {});
          if (ret.getReturnType() != List.class) {
            fail("Bad return type for runPlugin()");
          }
        }

      } catch (NoSuchMethodException e) {
        fail("Incompatibile API " + e.getMessage());
      }

    }

  }

}
