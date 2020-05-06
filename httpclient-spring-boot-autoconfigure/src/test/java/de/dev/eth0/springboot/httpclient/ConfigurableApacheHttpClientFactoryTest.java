/*
 * Copyright (c) 2020. dev-eth0.de All rights reserved.
 */

package de.dev.eth0.springboot.httpclient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
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

  private final HttpClientProperties.TimeoutConfiguration timeoutConfiguration = new HttpClientProperties.TimeoutConfiguration();

  private final HttpClientProperties.HostConfiguration[] hostConfiguration = {};

  private HttpClientProperties.HostConfiguration hostConfig;
  private HttpClientProperties.HostConfiguration hostConfigWithAuth;

  @BeforeEach
  public void setup() {
    when(httpClientProperties.getHosts()).thenReturn(hostConfiguration);
    when(httpClientProperties.getTimeouts()).thenReturn(timeoutConfiguration);

    hostConfig = new HttpClientProperties.HostConfiguration();
    hostConfigWithAuth = new HttpClientProperties.HostConfiguration();
    hostConfigWithAuth.setProxyHost("testProxyHost");
    hostConfigWithAuth.setProxyPort(1234);
    hostConfigWithAuth.setProxyUser("testUser");
    hostConfigWithAuth.setProxyPassword("testPassword");
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
    when(httpClientProperties.getHosts()).thenReturn(new HttpClientProperties.HostConfiguration[] { hostConfig });

    ConfigurableApacheHttpClientFactory underTest = new ConfigurableApacheHttpClientFactory(HttpClientBuilder.create(), httpClientProperties);
    HttpClientBuilder builder = underTest.createBuilder();

    SystemDefaultRoutePlanner routePlanner = (SystemDefaultRoutePlanner)ReflectionTestUtils.getField(builder, HttpClientBuilder.class, "routePlanner");
    assertThat(routePlanner).isNotNull();

    Object proxySelector = ReflectionTestUtils.getField(routePlanner, SystemDefaultRoutePlanner.class, "proxySelector");
    assertThat(proxySelector).isInstanceOf(ConfigurableProxySelector.class);
  }

  @Test
  public void createBuilder_proxyConfiguration_noAuthentication() {
    when(httpClientProperties.getHosts()).thenReturn(new HttpClientProperties.HostConfiguration[] { hostConfig });

    ConfigurableApacheHttpClientFactory underTest = new ConfigurableApacheHttpClientFactory(HttpClientBuilder.create(), httpClientProperties);
    HttpClientBuilder builder = underTest.createBuilder();

    Object credentialsProvider = ReflectionTestUtils.getField(builder, HttpClientBuilder.class, "credentialsProvider");
    Object proxyAuthStrategy = ReflectionTestUtils.getField(builder, HttpClientBuilder.class, "proxyAuthStrategy");
    assertThat(credentialsProvider).isNull();
    assertThat(proxyAuthStrategy).isNull();
  }

  @Test
  public void createBuilder_proxyConfiguration_authentication() {
    when(httpClientProperties.getHosts()).thenReturn(new HttpClientProperties.HostConfiguration[] { hostConfigWithAuth, hostConfig });

    ConfigurableApacheHttpClientFactory underTest = new ConfigurableApacheHttpClientFactory(HttpClientBuilder.create(), httpClientProperties);
    HttpClientBuilder builder = underTest.createBuilder();

    BasicCredentialsProvider credentialsProvider = (BasicCredentialsProvider)ReflectionTestUtils
        .getField(builder, HttpClientBuilder.class, "credentialsProvider");
    assertThat(credentialsProvider).isNotNull();

    Credentials credentials = credentialsProvider.getCredentials(new AuthScope(hostConfigWithAuth.getProxyHost(), hostConfigWithAuth.getProxyPort()));
    assertThat(credentials).isNotNull();
    assertThat(credentials.getUserPrincipal().getName()).isEqualTo(hostConfigWithAuth.getProxyUser());
    assertThat(credentials.getPassword()).isEqualTo(hostConfigWithAuth.getProxyPassword());

    Object proxyAuthStrategy = ReflectionTestUtils.getField(builder, HttpClientBuilder.class, "proxyAuthStrategy");
    assertThat(proxyAuthStrategy).isInstanceOf(ProxyAuthenticationStrategy.class);
  }
}