/*
 * Copyright (c) 2020. dev-eth0.de All rights reserved.
 */

package de.dev.eth0.springboot.httpclient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import okhttp3.OkHttpClient;

@ExtendWith(MockitoExtension.class)
class ConfigurableOkHttpClientFactoryTest {

  @Mock
  private HttpClientProperties httpClientProperties;

  private HttpClientProperties.TimeoutConfiguration timeoutConfiguration = new HttpClientProperties.TimeoutConfiguration();

  @BeforeEach
  public void setup() {
    when(httpClientProperties.getTimeouts()).thenReturn(timeoutConfiguration);
  }

  @Test
  public void createBuilder_defaultConfiguration() {
    ConfigurableOkHttpClientFactory underTest = new ConfigurableOkHttpClientFactory(new OkHttpClient.Builder(), httpClientProperties);
    OkHttpClient.Builder builder = underTest.createBuilder(true);

    int connectTimeout = (int)ReflectionTestUtils.getField(builder, OkHttpClient.Builder.class, "connectTimeout");
    assertThat(connectTimeout).isEqualTo(HttpClientProperties.TimeoutConfiguration.DEFAULT_CONNECTION_TIMEOUT);
    int readTimeout = (int)ReflectionTestUtils.getField(builder, OkHttpClient.Builder.class, "readTimeout");
    assertThat(readTimeout).isEqualTo(HttpClientProperties.TimeoutConfiguration.DEFAULT_SOCKET_TIMEOUT);
    int writeTimeout = (int)ReflectionTestUtils.getField(builder, OkHttpClient.Builder.class, "writeTimeout");
    assertThat(writeTimeout).isEqualTo(HttpClientProperties.TimeoutConfiguration.DEFAULT_SOCKET_TIMEOUT);
  }

  @Test
  public void createBuilder_timeoutConfiguration() {
    timeoutConfiguration.setConnectionTimeout(1234);
    timeoutConfiguration.setSocketTimeout(5678);
    ConfigurableOkHttpClientFactory underTest = new ConfigurableOkHttpClientFactory(new OkHttpClient.Builder(), httpClientProperties);
    OkHttpClient.Builder builder = underTest.createBuilder(true);

    int connectTimeout = (int)ReflectionTestUtils.getField(builder, OkHttpClient.Builder.class, "connectTimeout");
    assertThat(connectTimeout).isEqualTo(1234);
    int readTimeout = (int)ReflectionTestUtils.getField(builder, OkHttpClient.Builder.class, "readTimeout");
    assertThat(readTimeout).isEqualTo(5678);
    int writeTimeout = (int)ReflectionTestUtils.getField(builder, OkHttpClient.Builder.class, "writeTimeout");
    assertThat(writeTimeout).isEqualTo(5678);
  }

}