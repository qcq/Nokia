package com.nokia.property;


import com.nokia.config.Config;

import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;


/**
 * used for spring ,replace any placeholder by {@link Config}.
 *
 * @author bige
 */
public class ConfigPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
  private static final Logger impLogger = LoggerFactory.getLogger(ConfigPropertyPlaceholderConfigurer.class);
  public static final String NE3SADAPTER_MODE = "Ne3sAdapter.mode";
  public static final String NE3SADAPTER_MODE_DEFAULT = "joma";

  final ApplicationContext context;

  public ConfigPropertyPlaceholderConfigurer(ApplicationContext context) {
    this.context = context;
  }

  @VisibleForTesting
  @Override
  protected void loadProperties(Properties props) throws IOException {
    super.loadProperties(props);
    if (context == null) {
      impLogger.warn("context object is null");
      return;
    }
    Config config = context.getBean("config", Config.class);
    Iterator<String> keys = config.getKeys();
    while (keys.hasNext()) {
      String key = keys.next();
      String value = config.getString(key);
      Object oldValue = props.put(key, value);
      if (oldValue != null) {
        impLogger.warn("The key is already existed in properties file and is now overrode by the one in "
            + "Config file. key = {}, old value = {}, new value = {}", key, oldValue, value);
      }
    }

    if (props.get(NE3SADAPTER_MODE) == null) {
      impLogger.warn(NE3SADAPTER_MODE + " is NOT figure out, use default value " + NE3SADAPTER_MODE_DEFAULT);
      props.put(NE3SADAPTER_MODE, NE3SADAPTER_MODE_DEFAULT);
    }
  }
}
