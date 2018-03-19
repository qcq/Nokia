package com.nokia.config;

import org.apache.commons.configuration2.tree.xpath.XPathExpressionEngine;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public interface IConfig {

  Iterator<String> getKeys();

  boolean containsKey(String key);

  boolean getBoolean(String key);

  int getInt(String key);

  float getFloat(String key);

  String getString(String key);

  List getList(String key);

  Properties toProperties();

  void setExpressionEngine(XPathExpressionEngine engine);

  /**
   * add or update parameter.
   * @param key parameter key
   * @param value parameter value
   */
  void setParameter(String key, Object value);

}
