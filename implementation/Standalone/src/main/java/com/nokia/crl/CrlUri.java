package com.nokia.crl;

import java.util.Objects;

public class CrlUri {
  private UriSource uriSource;
  private String primaryCrlUri;
  private String secondaryCrlUri;

  public CrlUri(UriSource uriSource, String primaryCrlUri, String secondaryCrlUri) {
    this.uriSource = uriSource;
    this.primaryCrlUri = primaryCrlUri;
    this.secondaryCrlUri = secondaryCrlUri;
  }

  public UriSource getUriSource() {
    return uriSource;
  }

  public String getPrimaryCrlUri() {
    return primaryCrlUri;
  }

  public String getSecondaryCrlUri() {
    return secondaryCrlUri;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    CrlUri that = (CrlUri) o;

    return Objects.equals(this.primaryCrlUri, that.primaryCrlUri) &&
          Objects.equals(this.secondaryCrlUri, that.secondaryCrlUri) &&
          Objects.equals(this.uriSource, that.uriSource);
  }

  @Override
  public int hashCode() {
    return Objects.hash(primaryCrlUri, secondaryCrlUri, uriSource);
  }
}
