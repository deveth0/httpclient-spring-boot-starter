/*
 * Copyright (c) 2020. dev-eth0.de All rights reserved.
 */

package de.dev.eth0.springboot.httpclient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.ProxySelector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.dev.eth0.springboot.httpclient.impl.ConfigurableOkHttpClientFactory;
import de.dev.eth0.springboot.httpclient.impl.proxy.ConfigurableProxySelector;
import de.dev.eth0.springboot.httpclient.impl.proxy.OkHttpProxyAuthenticator;
import okhttp3.OkHttpClient;

@ExtendWith(MockitoExtension.class)
class ConfigurableOkHttpClientFactoryTest {

  @Mock
  private HttpClientProperties httpClientProperties;

  private final HttpClientProperties.TimeoutConfiguration timeoutConfiguration = new HttpClientProperties.TimeoutConfiguration();

  private final HttpClientProperties.ProxyConfiguration[] proxyConfiguration = {};

  private HttpClientProperties.ProxyConfiguration hostConfig;
  private HttpClientProperties.ProxyConfiguration hostConfigWithAuth;

  @BeforeEach
  public void setup() {
    when(httpClientProperties.getTimeouts()).thenReturn(timeoutConfiguration);
    when(httpClientProperties.getProxies()).thenReturn(proxyConfiguration);

    hostConfig = new HttpClientProperties.ProxyConfiguration();
    hostConfigWithAuth = new HttpClientProperties.ProxyConfiguration();
    hostConfigWithAuth.setProxyHost("testProxyHost");
    hostConfigWithAuth.setProxyPort(1234);
    hostConfigWithAuth.setProxyUser("testUser");
    hostConfigWithAuth.setProxyPassword("testPassword");
  }

  @Test
  public void createBuilder_defaultConfiguration() {
    ConfigurableOkHttpClientFactory underTest = new ConfigurableOkHttpClientFactory(new OkHttpClient.Builder(), httpClientProperties);
    OkHttpClient.Builder builder = underTest.createBuilder(true);
    OkHttpClient client = builder.build();

    assertThat(client.connectTimeoutMillis()).isEqualTo(HttpClientProperties.TimeoutConfiguration.DEFAULT_CONNECTION_TIMEOUT);
    assertThat(client.readTimeoutMillis()).isEqualTo(HttpClientProperties.TimeoutConfiguration.DEFAULT_SOCKET_TIMEOUT);
    assertThat(client.writeTimeoutMillis()).isEqualTo(HttpClientProperties.TimeoutConfiguration.DEFAULT_SOCKET_TIMEOUT);

    assertThat(client.proxySelector()).isEqualTo(ProxySelector.getDefault());
  }

  @Test
  public void createBuilder_timeoutConfiguration() {
    timeoutConfiguration.setConnectionTimeout(1234);
    timeoutConfiguration.setSocketTimeout(5678);
    ConfigurableOkHttpClientFactory underTest = new ConfigurableOkHttpClientFactory(new OkHttpClient.Builder(), httpClientProperties);
    OkHttpClient.Builder builder = underTest.createBuilder(true);
    OkHttpClient client = builder.build();

    assertThat(client.connectTimeoutMillis()).isEqualTo(1234);
    assertThat(client.readTimeoutMillis()).isEqualTo(5678);
    assertThat(client.writeTimeoutMillis()).isEqualTo(5678);
  }

  @Test
  public void createBuilder_proxyConfiguration() {
    when(httpClientProperties.getProxies()).thenReturn(new HttpClientProperties.ProxyConfiguration[] { hostConfig });

    ConfigurableOkHttpClientFactory underTest = new ConfigurableOkHttpClientFactory(new OkHttpClient.Builder(), httpClientProperties);
    OkHttpClient.Builder builder = underTest.createBuilder(true);
    OkHttpClient client = builder.build();

    assertThat(client.proxySelector()).isInstanceOf(ConfigurableProxySelector.class);
  }


  @Test
  public void createBuilder_proxyConfiguration_noAuthentication() {
    when(httpClientProperties.getProxies()).thenReturn(new HttpClientProperties.ProxyConfiguration[] { hostConfig });

    ConfigurableOkHttpClientFactory underTest = new ConfigurableOkHttpClientFactory(new OkHttpClient.Builder(), httpClientProperties);
    OkHttpClient.Builder builder = underTest.createBuilder(true);
    OkHttpClient client = builder.build();

    assertThat(client.proxyAuthenticator()).isInstanceOf(OkHttpProxyAuthenticator.class);
  }

  @Test
  public void createBuilder_proxyConfiguration_authentication() throws IOException {
    when(httpClientProperties.getProxies()).thenReturn(new HttpClientProperties.ProxyConfiguration[] { hostConfigWithAuth, hostConfig });

    ConfigurableOkHttpClientFactory underTest = new ConfigurableOkHttpClientFactory(new OkHttpClient.Builder(), httpClientProperties);
    OkHttpClient.Builder builder = underTest.createBuilder(true);
    OkHttpClient client = builder.build();

    assertThat(client.proxyAuthenticator()).isInstanceOf(OkHttpProxyAuthenticator.class);
  }
}