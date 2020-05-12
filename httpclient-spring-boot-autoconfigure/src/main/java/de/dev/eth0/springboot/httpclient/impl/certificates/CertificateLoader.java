/*
 * Copyright (c) 2020. dev-eth0.de All rights reserved.
 */

package de.dev.eth0.springboot.httpclient.impl.certificates;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import de.dev.eth0.springboot.httpclient.HttpClientProperties;

/**
 * Loader for certificate (trust/keystore) related stuff
 */
public class CertificateLoader {

  private static final Logger LOG = LoggerFactory.getLogger(CertificateLoader.class);

  private CertificateLoader() {
  }

  /**
   * @param httpClientProperties
   * @param keyManagerFactory
   * @param trustManagerFactory
   * @return configured {@link SSLContext} or null
   */
  public static SSLContext buildSSLContext(HttpClientProperties httpClientProperties,
      KeyManagerFactory keyManagerFactory,
      TrustManagerFactory trustManagerFactory) {
    try {
      SSLContext sslContext = SSLContext.getInstance(httpClientProperties.getSslContext());
      sslContext.init(keyManagerFactory != null ? keyManagerFactory.getKeyManagers() : null,
          trustManagerFactory != null ? trustManagerFactory.getTrustManagers() : null,
          null);
      return sslContext;
    }
    catch (NoSuchAlgorithmException | KeyManagementException ex) {
      LOG.error("Could not build SSLContext, skipping", ex);
    }
    return null;
  }

  /**
   * @param httpClientProperties
   * @return configured {@link TrustManagerFactory} or JVMs default Trust Managers
   */
  public static TrustManagerFactory getTrustManagerFactory(HttpClientProperties httpClientProperties) {
    HttpClientProperties.TruststoreConfiguration truststoreConfiguration = httpClientProperties.getTruststore();
    TrustManagerFactory tmf;
    try {
      tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      tmf.init((KeyStore)null);
    }
    catch (NoSuchAlgorithmException | KeyStoreException ex) {
      LOG.error("Could not instanciate Truststore", ex);
      return null;
    }
    if (StringUtils.isAnyBlank(truststoreConfiguration.getPath(), truststoreConfiguration.getPassword())) {
      LOG.warn("Truststore Configuration incomplete, skipping");
    }
    else {
      try (FileInputStream is = new FileInputStream(ResourceUtils.getFile(truststoreConfiguration.getPath()))) {
        KeyStore keyStore = KeyStore.getInstance(truststoreConfiguration.getType());
        keyStore.load(is, truststoreConfiguration.getPassword().toCharArray());
        tmf.init(keyStore);
        LOG.info("Truststore initialized successfully");
      }
      catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException ex) {
        LOG.error("Truststore could not be loaded, skipping", ex);
      }
    }
    return tmf;
  }

  /**
   * @param httpClientProperties
   * @return configured {@link KeyManagerFactory} or null
   */
  public static KeyManagerFactory getKeyManagerFactory(HttpClientProperties httpClientProperties) {
    HttpClientProperties.KeystoreConfiguration keystoreConfiguration = httpClientProperties.getKeystore();
    if (StringUtils.isAnyBlank(keystoreConfiguration.getPath(), keystoreConfiguration.getPassword())) {
      LOG.warn("Keystore Configuration incomplete, skipping");
      return null;
    }
    try (FileInputStream is = new FileInputStream(ResourceUtils.getFile(keystoreConfiguration.getPath()))) {
      KeyStore keyStore = KeyStore.getInstance(keystoreConfiguration.getType());
      keyStore.load(is, keystoreConfiguration.getPassword().toCharArray());

      KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      kmf.init(keyStore, keystoreConfiguration.getPassword().toCharArray());
      LOG.info("Keystore initialized successfully");
      return kmf;
    }
    catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException ex) {
      LOG.error("Keystore could not be loaded, skipping", ex);
      return null;
    }
  }

}
