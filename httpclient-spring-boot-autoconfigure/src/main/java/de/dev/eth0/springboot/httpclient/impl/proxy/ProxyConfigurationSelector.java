/*
 * Copyright (c) 2020. dev-eth0.de All rights reserved.
 */

package de.dev.eth0.springboot.httpclient.impl.proxy;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import de.dev.eth0.springboot.httpclient.HttpClientProperties;

/**
 * Selector for HostConfigurations based on their pattern
 */
public class ProxyConfigurationSelector {

  private final List<HttpClientProperties.ProxyConfiguration> proxyConfigurations;

  public ProxyConfigurationSelector(List<HttpClientProperties.ProxyConfiguration> proxyConfigurations) {
    this.proxyConfigurations = Optional.ofNullable(proxyConfigurations).orElse(List.of());
  }

  /**
   * @param host requested host
   * @return Stream with all {@link HttpClientProperties.ProxyConfiguration} that match the given hostPattern
   */
  public Stream<HttpClientProperties.ProxyConfiguration> select(String host) {
    return proxyConfigurations.stream().filter(config -> matches(config, host));
  }

  private boolean matches(HttpClientProperties.ProxyConfiguration config, String host) {
    if (config.getHostPatterns().length == 0) {
      return true;
    }
    return Arrays.stream(config.getHostPatterns()).anyMatch(pattern -> pattern.matcher(host).matches());
  }

}
