/*
 * Copyright (c) 2020. dev-eth0.de All rights reserved.
 */

package de.dev.eth0.springboot.httpclient;

import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.cloud.commons.httpclient.DefaultApacheHttpClientFactory;

/**
 * Factory used to create a HttpClient Instance
 */
public class ConfigurableApacheHttpClientFactory extends DefaultApacheHttpClientFactory {

  private final HttpClientProperties httpClientProperties;

  public ConfigurableApacheHttpClientFactory(HttpClientBuilder builder, HttpClientProperties httpClientProperties) {
    super(builder);
    this.httpClientProperties = httpClientProperties;
  }

  @Override
  public HttpClientBuilder createBuilder() {
    HttpClientBuilder builder = super.createBuilder();

    return builder;
  }
}
