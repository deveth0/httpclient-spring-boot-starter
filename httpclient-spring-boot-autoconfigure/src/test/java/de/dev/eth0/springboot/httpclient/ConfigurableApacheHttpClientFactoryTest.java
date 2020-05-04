/*
 * Copyright (c) 2020. dev-eth0.de All rights reserved.
 */

package de.dev.eth0.springboot.httpclient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import de.dev.eth0.springboot.httpclient.proxy.ConfigurableProxySelector;

@ExtendWith(MockitoExtension.class)
public class ConfigurableApacheHttpClientFactoryTest {

  @Mock
  private HttpClientProperties httpClientProperties;

  private HttpClientProperties.TimeoutConfiguration timeoutConfiguration = new HttpClientProperties.TimeoutConfiguration();

  private HttpClientProperties.ProxyConfiguration[] proxyConfiguration = {};

  @Mock
  private HttpClientProperties.ProxyConfiguration proxy;

  @BeforeEach
  public void setup() {
    when(httpClientProperties.getProxies()).thenReturn(proxyConfiguration);
    when(httpClientProperties.getTimeouts()).thenReturn(timeoutConfiguration);
  }

  @Test
  public void createBuilder_defaultConfiguration() {
    ConfigurableApacheHttpClientFactory underTest = new ConfigurableApacheHttpClientFactory(HttpClientBuilder.create(), httpClientProperties);
    HttpClientBuilder builder = underTest.createBuilder();
    RequestConfig requestConfig = (RequestConfig)ReflectionTestUtils.getField(builder, HttpClientBuilder.class, "defaultRequestConfig");
    assertThat(requestConfig).isNotNull();
    assertThat(requestConfig.getConnectTimeout()).isEqualTo(HttpClientProperties.TimeoutConfiguration.DEFAULT_CONNECTION_TIMEOUT);
    assertThat(requestConfig.getSocketTimeout()).isEqualTo(HttpClientProperties.TimeoutConfiguration.DEFAULT_SOCKET_TIMEOUT);

    HttpRoutePlanner proxySelector = (SystemDefaultRoutePlanner)ReflectionTestUtils.getField(builder, HttpClientBuilder.class, "routePlanner");
    assertThat(proxySelector).isNull();

  }

  @Test
  public void createBuilder_timeoutConfiguration() {
    timeoutConfiguration.setConnectionTimeout(1234);
    timeoutConfiguration.setSocketTimeout(5678);
    ConfigurableApacheHttpClientFactory underTest = new ConfigurableApacheHttpClientFactory(HttpClientBuilder.create(), httpClientProperties);
    HttpClientBuilder builder = underTest.createBuilder();

    RequestConfig requestConfig = (RequestConfig)ReflectionTestUtils.getField(builder, HttpClientBuilder.class, "defaultRequestConfig");
    assertThat(requestConfig.getConnectTimeout()).isEqualTo(1234);
    assertThat(requestConfig.getSocketTimeout()).isEqualTo(5678);
  }


  @Test
  public void createBuilder_proxyConfiguration() {
    when(httpClientProperties.getProxies()).thenReturn(new HttpClientProperties.ProxyConfiguration[] { proxy });

    ConfigurableApacheHttpClientFactory underTest = new ConfigurableApacheHttpClientFactory(HttpClientBuilder.create(), httpClientProperties);
    HttpClientBuilder builder = underTest.createBuilder();

    SystemDefaultRoutePlanner routePlanner = (SystemDefaultRoutePlanner)ReflectionTestUtils.getField(builder, HttpClientBuilder.class, "routePlanner");
    assertThat(routePlanner).isNotNull();

    Object proxySelector = ReflectionTestUtils.getField(routePlanner, SystemDefaultRoutePlanner.class, "proxySelector");
    assertThat(proxySelector).isInstanceOf(ConfigurableProxySelector.class);
  }
}