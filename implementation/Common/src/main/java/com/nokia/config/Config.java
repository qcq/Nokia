package com.nokia.config;

import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.tree.xpath.XPathExpressionEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class Config implements IConfig {

  private static Logger logger = LoggerFactory.getLogger(Config.class);
  private final XMLConfiguration btsmedConfig;
  private String filePath;

  public Config(File file) throws ConfigurationException {
    Configurations configs = new Configurations();
    this.filePath = file.getPath();
    btsmedConfig = configs.xml(file);
  }

  public Config(URL url) throws ConfigurationException {
    Configurations configs = new Configurations();
    this.filePath = url.getFile();
    btsmedConfig = configs.xml(url);
  }

  public Config(String path) throws ConfigurationException {
    Configurations configs = new Configurations();
    this.filePath = path;
    btsmedConfig = configs.xml(path);
  }

  @Override
  public List getList(String key) {
    return btsmedConfig.getList(key);
  }

  @Override
  public Iterator<String> getKeys() {
    return btsmedConfig.getKeys();
  }

  @Override
  public boolean containsKey(String key) {
    return btsmedConfig.containsKey(key);
  }

  @Override
  public boolean getBoolean(String key) {
    return btsmedConfig.getBoolean(key);
  }

  @Override
  public int getInt(String key) {
    return btsmedConfig.getInt(key);
  }

  @Override
  public float getFloat(String key) {
    return btsmedConfig.getFloat(key);
  }

  @Override
  public String getString(String key) {
    return btsmedConfig.getString(key);
  }

  @Override
  public Properties toProperties() {
    Properties prop = new Properties();
    Iterator<String> keys = btsmedConfig.getKeys();
    while (keys.hasNext()) {
      String key = keys.next();
      try {
        prop.put(key, btsmedConfig.getString(key));
      } catch (Exception e) {
        throw new SecurityException("Error when converting btsmedConfig to properties", e);
      }
    }
    return prop;
  }

  @Override
  public void setExpressionEngine(XPathExpressionEngine engine) {
    btsmedConfig.setExpressionEngine(engine);
  }

  @Override
  public void setParameter(String key, Object value) {
    btsmedConfig.setProperty(key, value);
    writeConfig(key, value);
  }

  private void writeConfig(String key, Object value) {
    File file = new File(filePath);
    try (FileWriter fileWriter = new FileWriter(file)) {
      btsmedConfig.write(fileWriter);
    } catch (ConfigurationException | IOException e) {
      logger.error("set parameter[key:" + key + ", value : " + value + "] error. ", e);
    }
  }
}
