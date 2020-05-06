/*
 * Copyright (c) 2020. dev-eth0.de All rights reserved.
 */

package de.dev.eth0.springboot.httpclient.impl.proxy;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.junit.jupiter.api.Test;

import de.dev.eth0.springboot.httpclient.HttpClientProperties;

public class ConfigurableProxySelectorTest {

  private static final String WILDCARD_HOST_PATTERN = ".*";
  private static final String MATCHING_HOST_PATTERN = "example.com";
  private static final String NON_MATCHING_HOST_PATTERN = "nonmatch.com";

  private static final String MATCHING_URI = "http://example.com/foo/bar";
  private static final String NON_MATCHING_URI = "http://foobar.com/ipsum";

  private static final HttpClientProperties.HostConfiguration NO_PATTERN_CONFIG = getHostConfiguration("localhost-nopattern", 1337);
  private static final HttpClientProperties.HostConfiguration WILDCARD_CONFIG = getHostConfiguration("localhost-wildcard", 1337, WILDCARD_HOST_PATTERN);
  private static final HttpClientProperties.HostConfiguration MATCHING_CONFIG = getHostConfiguration("localhost", 3128, MATCHING_HOST_PATTERN);
  private static final HttpClientProperties.HostConfiguration NON_MATCHING_CONFIG = getHostConfiguration("localhost-non-matching", 1337,
      NON_MATCHING_HOST_PATTERN);
  private static final HttpClientProperties.HostConfiguration MULTI_HOSTPATTERN_CONFIG = getHostConfiguration("localhost", 3128,
      MATCHING_HOST_PATTERN, "example.de");

  private static final Proxy NO_PATTERN_PROXY = new Proxy(Proxy.Type.HTTP,
      new InetSocketAddress(NO_PATTERN_CONFIG.getProxyHost(), NO_PATTERN_CONFIG.getProxyPort()));
  private static final Proxy WILDCARD_PROXY = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(WILDCARD_CONFIG.getProxyHost(), WILDCARD_CONFIG.getProxyPort()));
  private static final Proxy MATCHING_PROXY = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(MATCHING_CONFIG.getProxyHost(), MATCHING_CONFIG.getProxyPort()));

  private static HttpClientProperties.HostConfiguration getHostConfiguration(String proxyHost, int proxyPort, String... pattern) {
    HttpClientProperties.HostConfiguration config = new HttpClientProperties.HostConfiguration();
    config.setProxyHost(proxyHost);
    config.setProxyPort(proxyPort);
    config.setHostPatterns(pattern);
    return config;
  }

  /**
   * The ClientFactories should only set the Proxy Configuration if there is really a configured proxy,
   * but worst case a NO_PROXY needs to be returned.
   */
  @Test
  public void select_noProxiesConfigured() throws URISyntaxException {
    HttpClientProperties.HostConfiguration[] proxyConfigs = {};

    ConfigurableProxySelector underTest = new ConfigurableProxySelector(proxyConfigs);

    URI uri = new URI(MATCHING_URI);
    assertThat(underTest.select(uri)).containsOnly(Proxy.NO_PROXY);
  }

  @Test
  public void select_noMatching() throws URISyntaxException {
    HttpClientProperties.HostConfiguration[] proxyConfigs = {
        MATCHING_CONFIG,
        NON_MATCHING_CONFIG
    };

    ConfigurableProxySelector underTest = new ConfigurableProxySelector(proxyConfigs);

    URI uri = new URI(NON_MATCHING_URI);
    assertThat(underTest.select(uri)).containsOnly(Proxy.NO_PROXY);
  }

  @Test
  public void select_singleMatching() throws URISyntaxException {
    HttpClientProperties.HostConfiguration[] proxyConfigs = {
        MATCHING_CONFIG,
        NON_MATCHING_CONFIG
    };

    ConfigurableProxySelector underTest = new ConfigurableProxySelector(proxyConfigs);

    URI uri = new URI(MATCHING_URI);
    List<Proxy> proxies = underTest.select(uri);
    assertThat(proxies).hasSize(1);
    assertThat(proxies).containsExactly(MATCHING_PROXY);
  }

  @Test
  public void select_singleMatching_multiplePattern() throws URISyntaxException {
    HttpClientProperties.HostConfiguration[] proxyConfigs = {
        MULTI_HOSTPATTERN_CONFIG,
        NON_MATCHING_CONFIG
    };

    ConfigurableProxySelector underTest = new ConfigurableProxySelector(proxyConfigs);

    URI uri = new URI(MATCHING_URI);
    List<Proxy> proxies = underTest.select(uri);
    assertThat(proxies).hasSize(1);
    assertThat(proxies).containsExactly(MATCHING_PROXY);

    uri = new URI("http://example.com/foo/bar");
    proxies = underTest.select(uri);
    assertThat(proxies).hasSize(1);
    assertThat(proxies).containsExactly(MATCHING_PROXY);

  }


  @Test
  public void select_singleMatching_noPattern() throws URISyntaxException {
    HttpClientProperties.HostConfiguration[] proxyConfigs = {
        NO_PATTERN_CONFIG,
        NON_MATCHING_CONFIG
    };

    ConfigurableProxySelector underTest = new ConfigurableProxySelector(proxyConfigs);

    URI uri = new URI(MATCHING_URI);
    List<Proxy> proxies = underTest.select(uri);
    assertThat(proxies).hasSize(1);
    assertThat(proxies).containsExactly(NO_PATTERN_PROXY);
  }

  @Test
  public void select_multipleMatching() throws URISyntaxException {
    HttpClientProperties.HostConfiguration[] proxyConfigs = {
        MATCHING_CONFIG,
        WILDCARD_CONFIG,
        NO_PATTERN_CONFIG,
        NON_MATCHING_CONFIG
    };

    ConfigurableProxySelector underTest = new ConfigurableProxySelector(proxyConfigs);

    URI uri = new URI(MATCHING_URI);
    List<Proxy> proxies = underTest.select(uri);

    assertThat(proxies).containsExactly(MATCHING_PROXY, WILDCARD_PROXY, NO_PATTERN_PROXY);
  }

}