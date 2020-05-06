/*
 * Copyright (c) 2020. dev-eth0.de All rights reserved.
 */

package de.dev.eth0.springboot.httpclient.impl.proxy;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

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

  private final Map<Proxy, HttpClientProperties.HostConfiguration> proxyConfigurations;

  public OkHttpProxyAuthenticator(HttpClientProperties.HostConfiguration[] proxyConfig) {
    this.proxyConfigurations = proxyConfig != null
        ? Arrays.stream(proxyConfig)
        .filter(
            proxyConfiguration -> StringUtils.isNoneBlank(proxyConfiguration.getProxyUser(), proxyConfiguration.getProxyPassword()))
        .collect(Collectors.toMap(
            proxyConfiguration -> new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyConfiguration.getProxyHost(), proxyConfiguration.getProxyPort())),
            Function.identity()))
        : new HashMap<>();
  }

  @Override
  public Request authenticate(Route route, Response response) {
    if (proxyConfigurations.isEmpty()) {
      return null;
    }
    if (response.request().header("Proxy-Authorization") != null) {
      // Give up, we already failed to authenticate
      return null;
    }
    HttpClientProperties.HostConfiguration matchingConfig = this.proxyConfigurations.get(route.proxy());
    if (matchingConfig != null) {
      String credential = Credentials.basic(matchingConfig.getProxyUser(), matchingConfig.getProxyPassword());
      return response.request().newBuilder()
          .header("Proxy-Authorization", credential)
          .build();
    }
    return null;
  }
}
