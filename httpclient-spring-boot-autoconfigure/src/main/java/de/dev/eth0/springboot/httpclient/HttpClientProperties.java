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
  private HostConfiguration[] hosts = {};

  public HostConfiguration[] getHosts() {
    return hosts;
  }

  public void setHosts(HostConfiguration[] hosts) {
    this.hosts = hosts;
  }

  public TimeoutConfiguration getTimeouts() {
    return timeouts;
  }

  @Validated
  public static class HostConfiguration {

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
