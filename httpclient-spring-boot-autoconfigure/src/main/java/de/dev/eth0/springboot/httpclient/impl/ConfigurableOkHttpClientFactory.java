/*
 * Copyright (c) 2020. dev-eth0.de All rights reserved.
 */

package de.dev.eth0.springboot.httpclient.impl;

import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.commons.httpclient.DefaultOkHttpClientFactory;

import de.dev.eth0.springboot.httpclient.HttpClientProperties;
import de.dev.eth0.springboot.httpclient.impl.proxy.ConfigurableProxySelector;
import de.dev.eth0.springboot.httpclient.impl.proxy.OkHttpProxyAuthenticator;
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
    configureTimeouts(builder);
    configureProxies(builder);
    return builder;
  }

  private void configureTimeouts(OkHttpClient.Builder builder) {
    builder.connectTimeout(httpClientProperties.getTimeouts().getConnectionTimeout(), TimeUnit.MILLISECONDS);
    builder.readTimeout(httpClientProperties.getTimeouts().getSocketTimeout(), TimeUnit.MILLISECONDS);
    builder.writeTimeout(httpClientProperties.getTimeouts().getSocketTimeout(), TimeUnit.MILLISECONDS);
  }

  private void configureProxies(OkHttpClient.Builder builder) {
    HttpClientProperties.HostConfiguration[] proxyConfig = httpClientProperties.getHosts();

    if (proxyConfig == null || proxyConfig.length == 0) {
      LOG.debug("No proxy configurations found");
      return;
    }

    builder.proxySelector(new ConfigurableProxySelector(proxyConfig));
    builder.proxyAuthenticator(new OkHttpProxyAuthenticator(proxyConfig));
  }
}
