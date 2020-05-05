/*
 * Copyright (c) 2020. dev-eth0.de All rights reserved.
 */

package de.dev.eth0.springboot.httpclient.proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;

import de.dev.eth0.springboot.httpclient.HttpClientProperties;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Authenticator for Proxy requests
 */
public class OkHttpProxyAuthenticator implements Authenticator {

  private final Map<Proxy, HttpClientProperties.ProxyConfiguration> proxyConfigurations;

  public OkHttpProxyAuthenticator(HttpClientProperties.ProxyConfiguration[] proxyConfig) {
    this.proxyConfigurations = proxyConfig != null
        ? Arrays.stream(proxyConfig)
        .filter(
            proxyConfiguration -> !StringUtils.isEmpty(proxyConfiguration.getProxyUser()) && !StringUtils.isEmpty(proxyConfiguration.getProxyPassword()))
        .collect(Collectors.toMap(
            proxyConfiguration -> new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyConfiguration.getHost(), proxyConfiguration.getPort())),
            Function.identity()))
        : new HashMap<>();
  }

  @Override
  public Request authenticate(Route route, Response response) throws IOException {
    if (proxyConfigurations.isEmpty()) {
      return null;
    }
    if (response.request().header("Proxy-Authorization") != null) {
      // Give up, we already failed to authenticate
      return null;
    }
    HttpClientProperties.ProxyConfiguration matchingConfig = this.proxyConfigurations.get(route.proxy());
    if (matchingConfig != null) {
      String credential = Credentials.basic(matchingConfig.getProxyUser(), matchingConfig.getProxyPassword());
      return response.request().newBuilder()
          .header("Proxy-Authorization", credential)
          .build();
    }
    return null;
  }
}
