/*
 * Copyright (c) 2020. dev-eth0.de All rights reserved.
 */

package de.dev.eth0.springboot.httpclient;


import java.util.Arrays;
import java.util.regex.Pattern;
import javax.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("http.client")
@Validated
public class HttpClientProperties {

  private final TimeoutConfiguration timeouts = new TimeoutConfiguration();
  private final KeystoreConfiguration keystore = new KeystoreConfiguration();
  private final TruststoreConfiguration truststore = new TruststoreConfiguration();
  private ProxyConfiguration[] proxies = {};
  private String sslContext = "TLSv1.2";

  public String getSslContext() {
    return sslContext;
  }

  public void setSslContext(String sslContext) {
    this.sslContext = sslContext;
  }

  public ProxyConfiguration[] getProxies() {
    return proxies;
  }

  public void setProxies(ProxyConfiguration[] proxies) {
    this.proxies = proxies;
  }

  public TimeoutConfiguration getTimeouts() {
    return timeouts;
  }

  public KeystoreConfiguration getKeystore() {
    return keystore;
  }

  public TruststoreConfiguration getTruststore() {
    return truststore;
  }

  @Validated
  public static class ProxyConfiguration {

    public static final int DEFAULT_PORT = 3128;

    private Pattern[] hostPatterns;

    @NotBlank
    private String proxyHost;

    private int proxyPort = DEFAULT_PORT;

    private String proxyUser;

    private String proxyPassword;

    public Pattern[] getHostPatterns() {
      return hostPatterns;
    }

    public void setHostPatterns(String[] hostPatterns) {
      this.hostPatterns = Arrays.stream(hostPatterns).map(Pattern::compile).toArray(Pattern[]::new);
    }

    public String getProxyHost() {
      return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
      this.proxyHost = proxyHost;
    }

    public int getProxyPort() {
      return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
      this.proxyPort = proxyPort;
    }

    public String getProxyUser() {
      return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
      this.proxyUser = proxyUser;
    }

    public String getProxyPassword() {
      return proxyPassword;
    }

    public void setProxyPassword(String proxyPassword) {
      this.proxyPassword = proxyPassword;
    }
  }


  @Validated
  public static class KeystoreConfiguration {

    @NotBlank
    private String path;
    @NotBlank
    private String password;
    private String type = "PKCS12";

    public String getPath() {
      return path;
    }

    public void setPath(String path) {
      this.path = path;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }
  }


  @Validated
  public static class TruststoreConfiguration {

    @NotBlank
    private String path;
    @NotBlank
    private String password;
    private String type = "JKS";

    public String getPath() {
      return path;
    }

    public void setPath(String path) {
      this.path = path;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }
  }


  /**
   * Timeout Configuration
   */
  @Validated
  public static class TimeoutConfiguration {

    public static final int DEFAULT_CONNECTION_TIMEOUT = 5000;
    public static final int DEFAULT_SOCKET_TIMEOUT = 10000;

    private int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
    private int socketTimeout = DEFAULT_SOCKET_TIMEOUT;

    public int getConnectionTimeout() {
      return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
      this.connectionTimeout = connectionTimeout;
    }

    public int getSocketTimeout() {
      return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
      this.socketTimeout = socketTimeout;
    }
  }

}
