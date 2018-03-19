package com.nokia.cert;

import org.bouncycastle.asn1.x509.BasicConstraints;
import org.springframework.stereotype.Component;

import java.security.cert.CertPathValidatorException;
import java.security.cert.X509Certificate;

@Component
public class CertificateParaser {
  public CertificateParaser() {

  }
  public void prepareNextCertK(X509Certificate cert) throws CertPathValidatorException {
    BasicConstraints bc = null;/*
    cert.getPublicKey().
    try
    {
      bc = BasicConstraints.getInstance(CertPathValidatorUtilities.getExtensionValue(cert,
            RFC3280CertPathUtilities.BASIC_CONSTRAINTS));
    }
    catch (Exception e)
    {
      throw new ExtCertPathValidatorException("Basic constraints extension cannot be decoded.", e, certPath,
            index);
    }
    if (bc != null)
    {
      if (!(bc.isCA()))
      {
        throw new CertPathValidatorException("Not a CA certificate");
      }
    }
    else
    {
      throw new CertPathValidatorException("Intermediate certificate lacks BasicConstraints");
    }*/
  }
}
