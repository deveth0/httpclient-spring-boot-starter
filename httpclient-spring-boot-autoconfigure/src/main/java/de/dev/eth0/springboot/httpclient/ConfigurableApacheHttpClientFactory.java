/*
 * Copyright (c) 2020. dev-eth0.de All rights reserved.
 */

package de.dev.eth0.springboot.httpclient;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.commons.httpclient.DefaultApacheHttpClientFactory;
import org.springframework.util.StringUtils;

import de.dev.eth0.springboot.httpclient.proxy.ConfigurableProxySelector;

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
    configureProxies(builder);
    return builder;
  }

  private void configureTimeouts(HttpClientBuilder builder) {
    builder.setDefaultRequestConfig(RequestConfig.custom()
        .setConnectTimeout(httpClientProperties.getTimeouts().getConnectionTimeout())
        .setSocketTimeout(httpClientProperties.getTimeouts().getSocketTimeout())
        .build());
  }

  private void configureProxies(HttpClientBuilder builder) {
    HttpClientProperties.ProxyConfiguration[] proxyConfigs = httpClientProperties.getProxies();

    if (proxyConfigs == null || proxyConfigs.length == 0) {
      LOG.debug("No proxy configurations found");
      return;
    }

    ConfigurableProxySelector proxySelector = new ConfigurableProxySelector(proxyConfigs);
    SystemDefaultRoutePlanner routePlanner = new SystemDefaultRoutePlanner(proxySelector);
    builder.setRoutePlanner(routePlanner);

    configureProxyAuthentication(builder, proxyConfigs);
  }

  private void configureProxyAuthentication(HttpClientBuilder builder, HttpClientProperties.ProxyConfiguration[] proxyConfigs) {
    CredentialsProvider credsProvider = new BasicCredentialsProvider();
    boolean hasProxyCredentials = false;
    for (HttpClientProperties.ProxyConfiguration proxyConfig : proxyConfigs) {
      if (!StringUtils.isEmpty(proxyConfig.getProxyUser()) && !StringUtils.isEmpty(proxyConfig.getProxyPassword())) {
        credsProvider.setCredentials(
            new AuthScope(proxyConfig.getHost(), proxyConfig.getPort()),
            new UsernamePasswordCredentials(proxyConfig.getProxyUser(), proxyConfig.getProxyPassword()));
        hasProxyCredentials = true;
      }
    }
    if (hasProxyCredentials) {
      builder.setDefaultCredentialsProvider(credsProvider);
      builder.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());
    }
  }
}
