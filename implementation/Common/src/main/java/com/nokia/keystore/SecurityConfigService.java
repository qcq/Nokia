package com.nokia.keystore;

import com.nokia.config.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class SecurityConfigService {
  private static final Logger LOG = LoggerFactory.getLogger(SecurityConfigService.class);

  private static final String MAX_CERTIFICATE_SIZE_IN_BYTE_KEY = "Security.MaxCertificateSizeInByte";
  private static final Long MAX_CERTIFICATE_SIZE_IN_BYTE_DEFAULT = 1L * 1024L * 1024L;

  private static final String KEY_STORE_PATH_KEY = "Security.KeyStorePath";
  private static final String KEY_STORE_PATH_DEFAULT = "../conf/keystore/keystore.jks";

  private static final String KEY_STORE_SECURITY_CODE_KEY = "Security.KeyStorePassword";

  private Config config;
  private Long maxCertificateSizeInByte;
  private File keyStorePath;
  private char[] keyStorePassword;

  @Autowired
  public SecurityConfigService(Config config) {
    this.config = config;
    retrieveSecurityConfigParameters();
  }

  private void retrieveSecurityConfigParameters() {
    retrieveKeyStorePassword();
    retrieveKeyStorePath();
    retrieveMaxCertificateSize();
  }

  private void retrieveKeyStorePassword() {
    if (!config.containsKey(KEY_STORE_SECURITY_CODE_KEY)) {
      LOG.error("keystore password {} should property in configs.xml.", KEY_STORE_SECURITY_CODE_KEY);
      throw new SecurityException("keystore password must be property in configs.xml.");
    }
    keyStorePassword = config.getString(KEY_STORE_SECURITY_CODE_KEY).toCharArray();
  }

  private void retrieveKeyStorePath() {
    if (!config.containsKey(KEY_STORE_PATH_KEY)) {
      LOG.warn("keystore file Path {} not property in configs.xml, will use the default {}",
            KEY_STORE_PATH_KEY, KEY_STORE_PATH_DEFAULT);
      keyStorePath = new File(KEY_STORE_PATH_DEFAULT);
    } else {
      keyStorePath = new File(config.getString(KEY_STORE_PATH_KEY));
    }
  }

  private void retrieveMaxCertificateSize() {
    if (!config.containsKey(MAX_CERTIFICATE_SIZE_IN_BYTE_KEY)) {
      LOG.warn("Max certificate size {} not exist in configs.xml, will replace with default {}.",
            MAX_CERTIFICATE_SIZE_IN_BYTE_KEY, MAX_CERTIFICATE_SIZE_IN_BYTE_DEFAULT);
      maxCertificateSizeInByte = MAX_CERTIFICATE_SIZE_IN_BYTE_DEFAULT;
    } else {
      maxCertificateSizeInByte = Long.valueOf(config.getInt(MAX_CERTIFICATE_SIZE_IN_BYTE_KEY));
    }
  }

  public Long getMaxCertificateSizeInByte() {
    return maxCertificateSizeInByte;
  }

  public char[] getKeyStorePassword() {
    return keyStorePassword;
  }

  public File getKeyStorePath() {
    return keyStorePath;
  }
}
