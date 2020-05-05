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
  private ProxyConfiguration[] proxies = {};

  public ProxyConfiguration[] getProxies() {
    return proxies;
  }

  public void setProxies(ProxyConfiguration[] proxies) {
    this.proxies = proxies;
  }

  public TimeoutConfiguration getTimeouts() {
    return timeouts;
  }

  @Validated
  public static class ProxyConfiguration {

    public static final int DEFAULT_PORT = 3128;

    private Pattern[] hostPatterns;

    @NotBlank
    private String host;

    private int port = DEFAULT_PORT;

    public Pattern[] getHostPatterns() {
      return hostPatterns;
    }

    public void setHostPatterns(String[] hostPatterns) {
      this.hostPatterns = Arrays.stream(hostPatterns).map(Pattern::compile).toArray(Pattern[]::new);
    }

    public String getHost() {
      return host;
    }

    public void setHost(String host) {
      this.host = host;
    }

    public int getPort() {
      return port;
    }

    public void setPort(int port) {
      this.port = port;
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
