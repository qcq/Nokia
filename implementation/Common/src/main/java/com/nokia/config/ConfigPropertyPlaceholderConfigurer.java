package com.nokia.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

public class ConfigPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

  private static final Logger LOG = LoggerFactory.getLogger(ConfigPropertyPlaceholderConfigurer.class);

  final Config config;

  public ConfigPropertyPlaceholderConfigurer(Config config) {
    if (java.util.Objects.isNull(config)) {
      throw new IllegalArgumentException("property object should not be null");
    }
    this.config = config;
  }

  @Override
  protected void loadProperties(Properties props) throws IOException {
    super.loadProperties(props);
    Iterator<String> keys = config.getKeys();
    while (keys.hasNext()) {
      String key = keys.next();
      String value = config.getString(key);
      Object oldValue = props.put(key, value);
      if (oldValue != null) {
        LOG.warn("The key is already existed in properties file and is now overrode by the one in "
            + "Config file. key = {}, old value = {}, new value = {}", key, oldValue, value);
      }
    }
  }
}
