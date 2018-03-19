package com.nokia.keystore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfigsParser {
  private static final Logger LOG = LoggerFactory.getLogger(ConfigsParser.class);
  private String password;
  @Autowired
  public ConfigsParser(@Value("${Security.KeyStorePassword}") String password) {
    this.password = password;
    LOG.error("{}", password);
  }
}
