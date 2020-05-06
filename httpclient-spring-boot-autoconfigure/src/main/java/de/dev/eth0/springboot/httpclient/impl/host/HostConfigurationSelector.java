/*
 * Copyright (c) 2020. dev-eth0.de All rights reserved.
 */

package de.dev.eth0.springboot.httpclient.impl.host;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import de.dev.eth0.springboot.httpclient.HttpClientProperties;

/**
 * Selector for HostConfigurations based on their pattern
 */
public class HostConfigurationSelector {

  private final List<HttpClientProperties.HostConfiguration> hostConfigurations;

  public HostConfigurationSelector(List<HttpClientProperties.HostConfiguration> hostConfigurations) {
    this.hostConfigurations = Optional.ofNullable(hostConfigurations).orElse(List.of());
  }

  /**
   * @param host requested host
   * @return Stream with all {@link de.dev.eth0.springboot.httpclient.HttpClientProperties.HostConfiguration} that match the given hostPattern
   */
  public Stream<HttpClientProperties.HostConfiguration> select(String host) {
    return hostConfigurations.stream().filter(config -> matches(config, host));
  }

  private boolean matches(HttpClientProperties.HostConfiguration config, String host) {
    if (config.getHostPatterns().length == 0) {
      return true;
    }
    return Arrays.stream(config.getHostPatterns()).anyMatch(pattern -> pattern.matcher(host).matches());
  }

}
