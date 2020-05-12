/*
 * Copyright (c) 2020. dev-eth0.de All rights reserved.
 */

package de.dev.eth0.springboot.httpclient.impl;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.commons.httpclient.DefaultOkHttpClientFactory;

import de.dev.eth0.springboot.httpclient.HttpClientProperties;
import de.dev.eth0.springboot.httpclient.impl.proxy.ConfigurableProxySelector;
import de.dev.eth0.springboot.httpclient.impl.proxy.OkHttpProxyAuthenticator;
import de.dev.eth0.springboot.httpclient.impl.certificates.CertificateLoader;
import okhttp3.OkHttpClient;

/**
 * Factory used to generate a {@link OkHttpClient.Builder} instance with the given configuration
 */
public class ConfigurableOkHttpClientFactory extends DefaultOkHttpClientFactory {

  private static final Logger LOG = LoggerFactory.getLogger(ConfigurableOkHttpClientFactory.class);

  private final HttpClientProperties httpClientProperties;

  public ConfigurableOkHttpClientFactory(OkHttpClient.Builder builder, HttpClientProperties httpClientProperties) {
    super(builder);
    this.httpClientProperties = httpClientProperties;
  }

  @Override
  public OkHttpClient.Builder createBuilder(boolean disableSslValidation) {
    OkHttpClient.Builder builder = super.createBuilder(disableSslValidation);
    configureSSL(builder);
    configureTimeouts(builder);
    configureProxies(builder);
    return builder;
  }

  private void configureTimeouts(OkHttpClient.Builder builder) {
    builder.connectTimeout(httpClientProperties.getTimeouts().getConnectionTimeout(), TimeUnit.MILLISECONDS);
    builder.readTimeout(httpClientProperties.getTimeouts().getSocketTimeout(), TimeUnit.MILLISECONDS);
    builder.writeTimeout(httpClientProperties.getTimeouts().getSocketTimeout(), TimeUnit.MILLISECONDS);
  }

  private void configureSSL(OkHttpClient.Builder builder) {
    TrustManagerFactory trustManagerFactory = CertificateLoader.getTrustManagerFactory(httpClientProperties);
    KeyManagerFactory keyManagerFactory = CertificateLoader.getKeyManagerFactory(httpClientProperties);
    SSLContext sslContext = CertificateLoader.buildSSLContext(httpClientProperties, keyManagerFactory, trustManagerFactory);
    if (sslContext != null) {
      Optional<X509TrustManager> trustManager = Arrays.stream(trustManagerFactory.getTrustManagers())
          .filter(tm -> tm instanceof X509TrustManager)
          .map(tm -> (X509TrustManager)tm)
          .findFirst();
      if (trustManager.isPresent()) {
        builder.sslSocketFactory(sslContext.getSocketFactory(), trustManager.get()).build();
      }
      else {
        LOG.warn("No valid Truststore configured, using default");
        builder.sslSocketFactory(sslContext.getSocketFactory()).build();
      }
    }
    else {
      LOG.warn("Invalid SSL Context, skipping");
    }
  }

  private void configureProxies(OkHttpClient.Builder builder) {
    HttpClientProperties.ProxyConfiguration[] proxyConfig = httpClientProperties.getProxies();

    if (proxyConfig == null || proxyConfig.length == 0) {
      LOG.debug("No proxy configurations found");
      return;
    }

    builder.proxySelector(new ConfigurableProxySelector(proxyConfig));
    builder.proxyAuthenticator(new OkHttpProxyAuthenticator(proxyConfig));
  }
}
