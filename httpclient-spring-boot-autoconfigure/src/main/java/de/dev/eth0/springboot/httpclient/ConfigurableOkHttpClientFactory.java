/*
 * Copyright (c) 2020. dev-eth0.de All rights reserved.
 */

package de.dev.eth0.springboot.httpclient;

import java.util.concurrent.TimeUnit;
import org.springframework.cloud.commons.httpclient.DefaultOkHttpClientFactory;

import okhttp3.OkHttpClient;

/**
 * Factory used to generate a {@link OkHttpClient.Builder} instance with the given configuration
 */
public class ConfigurableOkHttpClientFactory extends DefaultOkHttpClientFactory {

  private final HttpClientProperties httpClientProperties;

  public ConfigurableOkHttpClientFactory(OkHttpClient.Builder builder, HttpClientProperties httpClientProperties) {
    super(builder);
    this.httpClientProperties = httpClientProperties;
  }

  @Override
  public OkHttpClient.Builder createBuilder(boolean disableSslValidation) {
    OkHttpClient.Builder builder = super.createBuilder(disableSslValidation);
    configureTimeouts(builder);
    return builder;
  }

  private void configureTimeouts(OkHttpClient.Builder builder) {
    builder.connectTimeout(httpClientProperties.getTimeouts().getConnectionTimeout(), TimeUnit.MILLISECONDS);
    builder.readTimeout(httpClientProperties.getTimeouts().getSocketTimeout(), TimeUnit.MILLISECONDS);
    builder.writeTimeout(httpClientProperties.getTimeouts().getSocketTimeout(), TimeUnit.MILLISECONDS);
  }
}
