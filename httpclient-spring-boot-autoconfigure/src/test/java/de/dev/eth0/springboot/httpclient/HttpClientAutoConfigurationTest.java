/*
 * Copyright (c) 2020. dev-eth0.de All rights reserved.
 */

package de.dev.eth0.springboot.httpclient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.then;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cloud.commons.httpclient.ApacheHttpClientFactory;
import org.springframework.cloud.commons.httpclient.OkHttpClientFactory;

import okhttp3.OkHttpClient;

public class HttpClientAutoConfigurationTest {

  private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
      .withBean(HttpClientAutoConfiguration.class)
      .withBean(HttpClientBuilder.class)
      .withBean(OkHttpClient.Builder.class);

  @Test
  public void apacheHttpClient_enabled() {
    this.contextRunner.withUserConfiguration(HttpClientAutoConfiguration.class)
        .withPropertyValues("httpclientfactories.apache.enabled=true")
        .run(ctx -> {
          then(ctx.getBean(ApacheHttpClientFactory.class)).isNotNull();
          then(ctx.getBean(ApacheHttpClientFactory.class)).isInstanceOf(ConfigurableApacheHttpClientFactory.class);
        });
  }

  @Test
  public void apacheHttpClient_disabled() {
    this.contextRunner.withUserConfiguration(HttpClientAutoConfiguration.class)
        .withPropertyValues("httpclientfactories.apache.enabled=false")
        .run(ctx -> {
          assertThat(ctx).doesNotHaveBean(ApacheHttpClientFactory.class);
        });
  }

  @Test
  public void apacheHttpClient_missingClass() {
    this.contextRunner.withUserConfiguration(HttpClientAutoConfiguration.class)
        .withClassLoader(new FilteredClassLoader(HttpClient.class))
        .run(ctx -> {
          assertThat(ctx).doesNotHaveBean(ApacheHttpClientFactory.class);
        });
  }

  @Test
  public void okHttpClient_enabled() {
    this.contextRunner.withUserConfiguration(HttpClientAutoConfiguration.class)
        .withPropertyValues("httpclientfactories.ok.enabled=true")
        .run(ctx -> {
          then(ctx.getBean(OkHttpClientFactory.class)).isNotNull();
          then(ctx.getBean(OkHttpClientFactory.class)).isInstanceOf(ConfigurableOkHttpClientFactory.class);
        });
  }

  @Test
  public void okHttpClient_disabled() {
    this.contextRunner.withUserConfiguration(HttpClientAutoConfiguration.class)
        .withPropertyValues("httpclientfactories.ok.enabled=false")
        .run(ctx -> {
          assertThat(ctx).doesNotHaveBean(OkHttpClientFactory.class);
        });
  }

  @Test
  public void okHttpClient_missingClass() {
    this.contextRunner.withUserConfiguration(HttpClientAutoConfiguration.class)
        .withClassLoader(new FilteredClassLoader(OkHttpClient.class))
        .run(ctx -> {
          assertThat(ctx).doesNotHaveBean(OkHttpClientFactory.class);
        });
  }

}