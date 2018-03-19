package com.nokia.certificate;

import com.nokia.exception.CertificateManagerException;

import org.apache.log4j.PropertyConfigurator;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

import javax.security.auth.x500.X500Principal;

@Component
public class CertificateManagement {
  private static final Logger LOG = LoggerFactory
      .getLogger(CertificateManagement.class);
  private static final String LOG_CONFIG_FILE_PATH = "D:\\workspace\\Java\\src\\log4j.properties";
  private static final String SPRING_CONFIG_FILE_PATH = "classpath*:/spring.xml";

  private static final long LOG_CONFIG_CHECK_DELAY_MS = 10;
  private static final String CSR_FILE_NAME = "csr.csr";
  private static final File CSR_FILE = new File(
      "D:/Downloads" + File.separator + CSR_FILE_NAME);
  private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
  private Keys keys;
  private static final File csrFile = new File("D:/csr.csr");

  @Autowired
  public CertificateManagement(Keys keys) {
    this.keys = keys;
  }

  public boolean generateCsr(String subJectName) {
    if (null == subJectName) {
      LOG.error("the subJectName is null or verify wrong");
      return false;
    }
    ContentSigner signGen = null;
    try {
      keys.generateNewKeyPair();
      signGen = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM)
          .build(Keys.keyPair().getPrivate());
    } catch (OperatorCreationException e) {
      LOG.debug("generate csr failed with exception {}.", e);
    }

    X500Principal subject = new X500Principal(subJectName);
    PKCS10CertificationRequestBuilder builder = new JcaPKCS10CertificationRequestBuilder(
        subject, Keys.keyPair().getPublic());
    PKCS10CertificationRequest request = builder.build(signGen);

    try (JcaPEMWriter pem = new JcaPEMWriter(
        new BufferedWriter(new FileWriter(csrFile)))) {
      pem.writeObject(request);
    } catch (IOException e) {
      LOG.debug("write csr file wrong with exception {}.", e);
      return false;
    }
    return true;
  }

  public List<X509Certificate> readCertificateFromPem(String fileName) {
    List<X509Certificate> certs = new ArrayList<>();
    if (null == fileName || "".equals(fileName)) {
      LOG.error("file is null or fileName is empty.");
      return certs;
    }
    try (BufferedInputStream fis = new BufferedInputStream(
        new FileInputStream(fileName))) {
      CertificateFactory cf = CertificateFactory.getInstance("X.509");
      while (fis.available() > 0) {
        Certificate cert = cf.generateCertificate(fis);
        certs.add((X509Certificate) cert);
      }
    } catch (CertificateException e) {
      LOG.error("failure in parsing certificateInfo with exception {}.", e);
      certs.clear();
    } catch (IOException e) {
      LOG.error("read file {} failed with exception {}!", fileName, e);
      certs.clear();
    }
    // LOG.debug("retrive the certificate file from pem file {}, {}.", fileName,
    // certs);
    return certs;
  }

  public Optional<X509Certificate> getNeCert(List<X509Certificate> certList) {
    Set<Principal> issuerDns = new HashSet<>();
    certList.stream().filter(cert -> null != cert)
        .forEach(cert -> issuerDns.add(cert.getIssuerDN()));
    return certList.stream()
        .filter(
            cert -> null != cert && !issuerDns.contains(cert.getSubjectDN()))
        .findFirst();
  }

  public boolean isCaCertificateValid(X509Certificate x509Certificate) {
    String fingerPrint = CertificateManagedObjects
        .getThumbPrint(x509Certificate);
    try {
      x509Certificate.checkValidity();
      LOG.debug("The Certificate valid from {} to {}.",
          x509Certificate.getNotBefore(), x509Certificate.getNotAfter());
    } catch (CertificateExpiredException e) {
      LOG.warn("The Certificate {} expired with exception {}.", fingerPrint, e);
      return false;
    } catch (CertificateNotYetValidException e) {
      LOG.warn("The Certificate {} not yet valid with exception {}.",
          fingerPrint, e);
      return false;
    }
    return true;
  }

  public boolean isSelfSigned(X509Certificate cert) {
    return cert.getIssuerX500Principal().getName()
        .equals(cert.getSubjectX500Principal().getName());
  }

  public static void main(String[] args)
      throws CertificateManagerException, ParseException {
    // TODO Auto-generated method stub
    String file_name_cert = "D:/Downloads/what.pem";
    String file_name_cert1 = "D:/Downloads/what1.pem";
    String file_name_cert2 = "D:/Downloads/what2.pem";
    String file_name_cert4 = "D:/Downloads/IPsecAllL4.pem";
    String file_name_cert5 = "D:/Downloads/FOR_CI_TEST_TA.pem";
    String file_name_cert3 = "D:/Downloads/14441_Expiring_testing.pem";
    PropertyConfigurator.configureAndWatch(LOG_CONFIG_FILE_PATH,
        LOG_CONFIG_CHECK_DELAY_MS);
    ApplicationContext context = new ClassPathXmlApplicationContext(
        SPRING_CONFIG_FILE_PATH);
    CertificateManagement certman = context
        .getBean(CertificateManagement.class);

    Scanner scan = new Scanner(System.in);
    int i = 0;
    String in = null;
    /*
     * while ((in = scan.nextLine()) != null) { if (i == 0) {
     * certman.generateCsr("O=Nokia,CN=BTSMED5"); i++; } else {
     * List<X509Certificate> certs =
     * certman.readCertificateFromPem(file_name_cert); if
     * (certs.get(0).getPublicKey().equals(Keys.keyPair().getPublic())) {
     * System.out.println("yes:" + certs.size()); } else {
     * System.out.println("No:" + certs.size()); } CertificatePath certpath =
     * new CertificatePath(certs); } }
     */
    List<X509Certificate> certs = certman
        .readCertificateFromPem(file_name_cert4);
    if (certman.isCaCertificateValid(certs.get(0))) {
      System.out.println("valid");
    } else {
      System.out.println("invalid");
    }
    SimpleDateFormat formatter = new SimpleDateFormat(
        "YYYY-MM-dd'T'HH:mm:ss.SSSXXX");
    LOG.debug("{}", certs.get(0).getNotBefore());
    LOG.debug("{}", certs.get(0).getNotBefore().toString().length());
    LOG.debug("{}", formatter.format(certs.get(0).getNotBefore()));
    LOG.debug("{}", formatter.format(certs.get(0).getNotBefore()).length());
    LOG.debug("{}:{}", certs.get(0).getSubjectDN(),
        certs.get(0).getSubjectX500Principal());
    LOG.debug("{}:{}", certs.get(0).getIssuerDN(),
        certs.get(0).getIssuerX500Principal());
    System.out.println(csrFile.getAbsolutePath());
    certman.generateCsr("O=Nokia,CN=BTSMED5");
    for (X509Certificate x509Certificate : certs) {
      System.out.println(certman.isSelfSigned(x509Certificate));
    }
    String filed = new String("yes");
    LOG.debug("{} asidusaid", filed, new Exception("no"));
    String dataaa = null;
    LOG.debug("Get={}", dataaa);

    //KeyStoreService keyStoreService = new KeyStoreService(new Keys());
   // keyStoreService.loadKeyStore();

    //System.out.println(new File("../yes.java").getPath());

  }

}
