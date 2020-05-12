/*
 * Copyright (c) 2020. dev-eth0.de All rights reserved.
 */

package de.dev.eth0.springboot.httpclient.impl;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.commons.httpclient.DefaultApacheHttpClientFactory;

import de.dev.eth0.springboot.httpclient.HttpClientProperties;
import de.dev.eth0.springboot.httpclient.impl.proxy.ConfigurableProxySelector;
import de.dev.eth0.springboot.httpclient.impl.certificates.CertificateLoader;

/**
 * Factory used to create a HttpClient Instance
 */
public class ConfigurableApacheHttpClientFactory extends DefaultApacheHttpClientFactory {

  private static final Logger LOG = LoggerFactory.getLogger(ConfigurableApacheHttpClientFactory.class);

  private final HttpClientProperties httpClientProperties;

  public ConfigurableApacheHttpClientFactory(HttpClientBuilder builder, HttpClientProperties httpClientProperties) {
    super(builder);
    this.httpClientProperties = httpClientProperties;
  }

  @Override
  public HttpClientBuilder createBuilder() {
    HttpClientBuilder builder = super.createBuilder();
    configureTimeouts(builder);
    configureSSL(builder);

    HttpClientProperties.ProxyConfiguration[] hostConfigs = httpClientProperties.getProxies();

    if (hostConfigs == null || hostConfigs.length == 0) {
      LOG.debug("No host configurations found");
      return builder;
    }
    configureProxies(builder, hostConfigs);
    configureAuthentication(builder, hostConfigs);
    return builder;
  }

  private void configureTimeouts(HttpClientBuilder builder) {
    builder.setDefaultRequestConfig(RequestConfig.custom()
        .setConnectTimeout(httpClientProperties.getTimeouts().getConnectionTimeout())
        .setSocketTimeout(httpClientProperties.getTimeouts().getSocketTimeout())
        .build());
  }

  private void configureSSL(HttpClientBuilder builder) {
    TrustManagerFactory trustManagerFactory = CertificateLoader.getTrustManagerFactory(httpClientProperties);
    KeyManagerFactory keyManagerFactory = CertificateLoader.getKeyManagerFactory(httpClientProperties);
    SSLContext sslContext = CertificateLoader.buildSSLContext(httpClientProperties, keyManagerFactory, trustManagerFactory);
    if (sslContext != null) {
      SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext);
      builder.setSSLSocketFactory(sslSocketFactory).build();
    }
    else {
      LOG.warn("Invalid SSL Context, skipping");
    }
  }

  private void configureProxies(HttpClientBuilder builder, HttpClientProperties.ProxyConfiguration[] hostConfigs) {
    ConfigurableProxySelector proxySelector = new ConfigurableProxySelector(hostConfigs);
    SystemDefaultRoutePlanner routePlanner = new SystemDefaultRoutePlanner(proxySelector);
    builder.setRoutePlanner(routePlanner);
  }

  private void configureAuthentication(HttpClientBuilder builder, HttpClientProperties.ProxyConfiguration[] hostConfigs) {
    CredentialsProvider credsProvider = new BasicCredentialsProvider();
    boolean hasCredentials = false;
    for (HttpClientProperties.ProxyConfiguration hostConfig : hostConfigs) {
      if (StringUtils.isNoneBlank(hostConfig.getProxyUser(), hostConfig.getProxyPassword())) {
        credsProvider.setCredentials(
            new AuthScope(hostConfig.getProxyHost(), hostConfig.getProxyPort()),
            new UsernamePasswordCredentials(hostConfig.getProxyUser(), hostConfig.getProxyPassword()));
        hasCredentials = true;
      }

    }
    if (hasCredentials) {
      builder.setDefaultCredentialsProvider(credsProvider);
      builder.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());
    }
  }
}
