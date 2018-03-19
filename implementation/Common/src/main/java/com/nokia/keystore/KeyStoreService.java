package com.nokia.keystore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

@Component
public class KeyStoreService {
  private static final Logger LOG = LoggerFactory.getLogger(KeyStoreService.class);

  private static final String NE_CHAIN_ALIAS = "/BTSMED-1/CERTH-1/NECERT-1";
  private static final String EXPORT_CERTS_SCRIPTS = "../bin/security/security_tool.py";
  private static final int NE_PATH_LEN_CONSTRAINT = -1;

  private KeyStore keyStore;
  private File keyStorePath;
  private File keyStoreBasePath;
  private char[] keyStorePassword;
  private MessageDigest md;

  @Autowired
  public KeyStoreService(SecurityConfigService securityConfigService) {
    keyStorePath = securityConfigService.getKeyStorePath();
    keyStoreBasePath = keyStorePath.getParentFile();
    keyStorePassword = securityConfigService.getKeyStorePassword();
    loadKeyStore();
    try {
      md = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      LOG.warn("Failed to instance of MessageDigest", e);
    }
  }

  public long getKeyStoreFileSizeInByte() {
    return keyStorePath.length();
  }

  public KeyStore getKeyStore() {
    return keyStore;
  }

  public File getKeyStorePath() {
    return keyStorePath;
  }

  public char[] getKeyStorePassword() {
    return keyStorePassword;
  }

  public boolean isNeChainInKeyStore() {
    try {
      return null != keyStore.getCertificateChain(NE_CHAIN_ALIAS);
    } catch (KeyStoreException e) {
      LOG.warn("get certificate chain failed with exception", e);
      return false;
    }
  }

  public boolean setKeyEntry(String alias, PrivateKey privateKey, X509Certificate[] certChain) {
    try {
      keyStore.setKeyEntry(alias, privateKey, keyStorePassword, certChain);
      storeKeyStore();
    } catch (KeyStoreException | CertificateException e) {
      LOG.error("fail to store trust chain in keystore with exception {}.", e);
      return false;
    }
    return true;
  }

  public boolean setCert(String alias, X509Certificate cert) {
    try {
      if (keyStore.containsAlias(alias)) {
        return false;
      }

      keyStore.setCertificateEntry(alias, cert);
      storeKeyStore();
      return true;
    } catch (CertificateException | KeyStoreException e) {
      LOG.error("Error setting certificate into keystore with exception {}.", e);
      return false;
    }
  }

  public boolean deleteCertByAlias(String alias) {
    if (null == alias || alias.isEmpty()) {
      LOG.warn("the certificate alias is empty.");
      return false;
    }
    try {
      keyStore.deleteEntry(alias);
      storeKeyStore();
    } catch (KeyStoreException e) {
      LOG.warn("the keystore has not been initialized, or the entry {} cannot be removed with exception {}.", alias, e);
      return false;
    } catch (CertificateException e) {
      LOG.warn("when sync to keystore occurs error with exception {}.", e);
      return false;
    }
    return true;
  }

  public X509Certificate getCert(String alias) {
    if (null == alias || alias.isEmpty()) {
      return null;
    }
    try {
      return (X509Certificate) keyStore.getCertificate(alias);
    } catch (KeyStoreException e) {
      LOG.error("get certificate failed with exception {}.", e);
      return null;
    }
  }

  public X509Certificate[] getCertChain(String alias) {
    if (null == alias || alias.isEmpty()) {
      return new X509Certificate[0];
    }
    try {
      Certificate[] certChain = keyStore.getCertificateChain(alias);
      List<X509Certificate> x509Certs = new ArrayList<>();

      if (null != certChain) {
        Arrays.stream(certChain).forEach(cert -> x509Certs.add((X509Certificate) cert));
      }
      return x509Certs.toArray(new X509Certificate[x509Certs.size()]);
    } catch (KeyStoreException e) {
      LOG.error("get certificate chain failed with exception {}.", e);
      return new X509Certificate[0];
    }
  }

  public String getCaCertAliasByFingerPrint(String fingerPrint) {
    if (null == fingerPrint || fingerPrint.isEmpty()) {
      return "";
    }
    String alias;
    try {
      for (Enumeration<String> item = keyStore.aliases(); item.hasMoreElements(); ) {
        alias = item.nextElement();
        if (fingerPrint.equals(getThumbPrint(getCert(alias)))) {
          return alias.toUpperCase();
        }
      }
    } catch (KeyStoreException e) {
      LOG.error("keyStore operation is failed with exception", e);
    }
    return "";
  }

  public boolean isInTrustChain(String fingerPrint) {
    return Arrays.stream(getCertChain(getCertChainAlias())).anyMatch(cert -> null != cert
          && NE_PATH_LEN_CONSTRAINT != cert.getBasicConstraints()
          && fingerPrint.equals(getThumbPrint(cert)));
  }

  public String getCertChainAlias() {
    String alias;
    try {
      for (Enumeration<String> item = keyStore.aliases(); item.hasMoreElements(); ) {
        alias = item.nextElement();
        if (keyStore.isKeyEntry(alias)) {
          return alias;
        }
      }
    } catch (KeyStoreException e) {
      LOG.error("keyStore operation is failed with exception", e);
    }
    return "";
  }

  public Enumeration<String> getAllCertAlias() throws KeyStoreException {
    return keyStore.aliases();
  }

  private void loadKeyStore() {
    LOG.debug("Begin to load keystore.");
    if (!keyStoreBasePath.exists() && !keyStoreBasePath.mkdirs()) {
      LOG.error("Failed to create keystore directory.");
    }
    try {
      keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
    } catch (KeyStoreException e) {
      LOG.error("Failed to create keystore instance with exception", e);
      throw new SecurityException(e);
    }
    if (!keyStorePath.exists()) {
      try {
        keyStore.load(null, keyStorePassword);
        storeKeyStore();
        LOG.debug("KeyStore initialized succeed.");
      } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
        LOG.error("Failed to init load keystore with exception", e);
      }
    } else {
      try (FileInputStream keyStoreInputStream = new FileInputStream(keyStorePath)) {
        keyStore.load(keyStoreInputStream, keyStorePassword);
        LOG.debug("KeyStore reloaded succeed.");
      } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
        LOG.error("Failed to re-load keystore with exception", e);
      }
    }
  }

  private void storeKeyStore() throws CertificateException {
    LOG.debug("storeKeyStore entry");
    try (FileOutputStream out = new FileOutputStream(keyStorePath)) {
      keyStore.store(out, keyStorePassword);
    } catch (FileNotFoundException e) {
      LOG.debug("keystore non-exist of file with exception", e);
      throw new CertificateException(e);
    } catch (IOException e) {
      LOG.debug("error with parse file with exception", e);
      throw new CertificateException(e);
    } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException e) {
      throw new CertificateException(e);
    }
  }


  public List<X509Certificate> getAllCaCertificates() {
    List<X509Certificate> caCertList = new ArrayList<>();
    try {
      Enumeration<String> aliases = keyStore.aliases();
      while (aliases.hasMoreElements()) {
        String as = aliases.nextElement();
        if (!keyStore.isKeyEntry(as)) {
          caCertList.add((X509Certificate) keyStore.getCertificate(as));
        }
      }
    } catch (KeyStoreException e) {
      LOG.warn("get aliases from keystore failed.", e);
    }
    return caCertList;
  }

  private void addEeChainListToTotalCert(List<Certificate> eeChainList, List<Certificate> totalCert) {
    if ((null == eeChainList) || (null == totalCert)) {
      return;
    }
    totalCert.addAll(eeChainList);
  }

  public List<Certificate> getAllCertificates() {
    List<Certificate> eeChainList;
    List<Certificate> otherCertList = new ArrayList<>();
    List<Certificate> totalCert = new ArrayList<>();
    try {
      Enumeration<String> aliases = keyStore.aliases();
      while (aliases.hasMoreElements()) {
        String as = aliases.nextElement();
        if (keyStore.isKeyEntry(as)) {
          Certificate[] certificateChain = keyStore.getCertificateChain(as);
          eeChainList = Arrays.asList(certificateChain);
          addEeChainListToTotalCert(eeChainList, totalCert);
        } else {
          Certificate cert = keyStore.getCertificate(as);
          otherCertList.add(cert);
        }
      }
      totalCert.addAll(otherCertList);
    } catch (KeyStoreException e) {
      LOG.error("Failed to get crl distribution point.", e);
    }
    return totalCert;
  }

  private String getThumbPrint(X509Certificate cert) {
    if (null == cert) {
      LOG.warn("can not retrieve the fingerprint with null Certificate.");
      return "";
    }
    try {
      byte[] der = cert.getEncoded();
      md.update(der);
      byte[] digest = md.digest();
      return byteArrayToHexString(digest);
    } catch (CertificateEncodingException e) {
      LOG.error("The certificate exist error with exception", e);
      return "";
    }
  }

  private String byteArrayToHexString(byte[] bytes) {
    if (null == bytes || 0 == bytes.length) {
      return "";
    }

    StringBuilder stringBuilder = new StringBuilder("");
    for (byte item : bytes) {
      String hexValue = Integer.toHexString(item & 0xFF).toUpperCase();
      if (hexValue.length() < 2) {
        stringBuilder.append(0);
      }
      stringBuilder.append(hexValue);
      stringBuilder.append(":");
    }
    return stringBuilder.substring(0, stringBuilder.length() - 1);
  }
}
