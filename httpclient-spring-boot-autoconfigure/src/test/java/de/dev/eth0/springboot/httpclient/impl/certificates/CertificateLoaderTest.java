/*
 * Copyright (c) 2020. dev-eth0.de All rights reserved.
 */

package de.dev.eth0.springboot.httpclient.impl.certificates;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import de.dev.eth0.springboot.httpclient.HttpClientProperties;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CertificateLoaderTest {

  @Mock
  private HttpClientProperties httpClientProperties;

  @Mock
  private HttpClientProperties.KeystoreConfiguration keystoreConfiguration;

  @Mock
  private HttpClientProperties.TruststoreConfiguration truststoreConfiguration;

  @BeforeEach
  public void setup() {
    when(httpClientProperties.getSslContext()).thenReturn("TLSv1.3");
    when(truststoreConfiguration.getPassword()).thenReturn(null);
    when(truststoreConfiguration.getPath()).thenReturn(null);
    when(truststoreConfiguration.getType()).thenReturn("JKS");
    when(httpClientProperties.getTruststore()).thenReturn(truststoreConfiguration);
    when(keystoreConfiguration.getPassword()).thenReturn(null);
    when(keystoreConfiguration.getPath()).thenReturn(null);
    when(keystoreConfiguration.getType()).thenReturn("PKCS12");
    when(httpClientProperties.getKeystore()).thenReturn(keystoreConfiguration);
  }

  @Test
  public void sslContext_nullManagers() {
    SSLContext underTest = CertificateLoader.buildSSLContext(httpClientProperties, null, null);
    assertThat(underTest).isNotNull();
    assertThat(underTest.getProtocol()).isEqualTo("TLSv1.3");
  }

  @Test
  public void sslContext_nullKeyManager() throws Exception {
    TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    trustManagerFactory.init((KeyStore)null);
    SSLContext underTest = CertificateLoader.buildSSLContext(httpClientProperties, null, trustManagerFactory);
    assertThat(underTest).isNotNull();
    assertThat(underTest.getProtocol()).isEqualTo("TLSv1.3");
  }

  @Test
  public void sslContext_nullTrustManager() throws Exception {
    KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    keyManagerFactory.init(null, null);
    SSLContext underTest = CertificateLoader.buildSSLContext(httpClientProperties, keyManagerFactory, null);
    assertThat(underTest).isNotNull();
    assertThat(underTest.getProtocol()).isEqualTo("TLSv1.3");
  }

  @Test
  public void sslContext_valid() throws Exception {
    TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    trustManagerFactory.init((KeyStore)null);
    KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    keyManagerFactory.init(null, null);
    SSLContext underTest = CertificateLoader.buildSSLContext(httpClientProperties, keyManagerFactory, trustManagerFactory);
    assertThat(underTest).isNotNull();
    assertThat(underTest.getProtocol()).isEqualTo("TLSv1.3");
  }

  @Test
  public void trustManagerFactory_nullPath() {
    mockValidTruststoreConfiguration();
    when(truststoreConfiguration.getPath()).thenReturn(null);
    TrustManagerFactory underTest = CertificateLoader.getTrustManagerFactory(httpClientProperties);
    assertThat(underTest).isNull();
  }

  @Test
  public void trustManagerFactory_nullPassword() {
    mockValidTruststoreConfiguration();
    when(truststoreConfiguration.getPassword()).thenReturn(null);
    TrustManagerFactory underTest = CertificateLoader.getTrustManagerFactory(httpClientProperties);
    assertThat(underTest).isNull();
  }

  @Test
  public void trustManagerFactory_invalidPath() {
    mockValidTruststoreConfiguration();
    when(truststoreConfiguration.getPath()).thenReturn("classpath:invalid.jks");
    TrustManagerFactory underTest = CertificateLoader.getTrustManagerFactory(httpClientProperties);
    assertThat(underTest).isNull();
  }

  @Test
  public void trustManagerFactory_invalidPassword() {
    mockValidTruststoreConfiguration();
    when(truststoreConfiguration.getPassword()).thenReturn("wrongPassword");
    TrustManagerFactory underTest = CertificateLoader.getTrustManagerFactory(httpClientProperties);
    assertThat(underTest).isNull();
  }

  @Test
  public void trustManagerFactory_valid() {
    mockValidTruststoreConfiguration();
    TrustManagerFactory underTest = CertificateLoader.getTrustManagerFactory(httpClientProperties);
    assertThat(underTest).isNotNull();
    assertThat(underTest.getTrustManagers()).hasSize(1);
  }

  @Test
  public void keyManagerFactory_nullPath() {
    mockValidKeystoreConfiguration();
    when(keystoreConfiguration.getPath()).thenReturn(null);
    KeyManagerFactory underTest = CertificateLoader.getKeyManagerFactory(httpClientProperties);
    assertThat(underTest).isNull();
  }

  @Test
  public void keyManagerFactory_nullPassword() {
    mockValidKeystoreConfiguration();
    when(keystoreConfiguration.getPassword()).thenReturn(null);
    KeyManagerFactory underTest = CertificateLoader.getKeyManagerFactory(httpClientProperties);
    assertThat(underTest).isNull();
  }

  @Test
  public void keyManagerFactory_invalidPath() {
    mockValidKeystoreConfiguration();
    when(keystoreConfiguration.getPath()).thenReturn("classpath:invalid.jks");
    KeyManagerFactory underTest = CertificateLoader.getKeyManagerFactory(httpClientProperties);
    assertThat(underTest).isNull();
  }

  @Test
  public void keyManagerFactory_invalidPassword() {
    mockValidKeystoreConfiguration();
    when(keystoreConfiguration.getPassword()).thenReturn("wrongPassword");
    KeyManagerFactory underTest = CertificateLoader.getKeyManagerFactory(httpClientProperties);
    assertThat(underTest).isNull();
  }

  @Test
  public void keyManagerFactory_valid() {
    mockValidKeystoreConfiguration();
    KeyManagerFactory underTest = CertificateLoader.getKeyManagerFactory(httpClientProperties);
    assertThat(underTest).isNotNull();
    assertThat(underTest.getKeyManagers()).hasSize(1);
  }

  private void mockValidTruststoreConfiguration() {
    when(truststoreConfiguration.getPath()).thenReturn("classpath:truststore.jks");
    when(truststoreConfiguration.getPassword()).thenReturn("changeit");
  }

  private void mockValidKeystoreConfiguration() {
    when(keystoreConfiguration.getPath()).thenReturn("classpath:truststore.jks");
    when(keystoreConfiguration.getPassword()).thenReturn("changeit");
  }
}