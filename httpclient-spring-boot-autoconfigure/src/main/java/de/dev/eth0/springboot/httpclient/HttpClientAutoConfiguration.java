/*
 * Copyright (c) 2020. dev-eth0.de All rights reserved.
 */

package de.dev.eth0.springboot.httpclient;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.commons.httpclient.ApacheHttpClientFactory;
import org.springframework.cloud.commons.httpclient.OkHttpClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import okhttp3.OkHttpClient;

/**
 * AutoConfiguration for the HttpClient
 */
@Configuration
@EnableConfigurationProperties(HttpClientProperties.class)
public class HttpClientAutoConfiguration {

  /**
   * Configuration if Apache HttpClient is used
   */
  @Configuration
  @ConditionalOnProperty(name = "httpclientfactories.apache.enabled", matchIfMissing = true)
  @ConditionalOnClass(HttpClient.class)
  static class ApacheHttpClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ApacheHttpClientFactory apacheHttpClientFactory(HttpClientBuilder builder, HttpClientProperties httpClientProperties) {
      return new ConfigurableApacheHttpClientFactory(builder, httpClientProperties);
    }
  }

  /**
   * Configuration if OkHttp HttpClient is used
   */
  @Configuration
  @ConditionalOnProperty(name = "httpclientfactories.ok.enabled", matchIfMissing = true)
  @ConditionalOnClass(OkHttpClient.class)
  static class OkHttpClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public OkHttpClientFactory okHttpClientFactory(OkHttpClient.Builder builder, HttpClientProperties okHttpClientProperties) {
      return new ConfigurableOkHttpClientFactory(builder, okHttpClientProperties);
    }
  }
}
