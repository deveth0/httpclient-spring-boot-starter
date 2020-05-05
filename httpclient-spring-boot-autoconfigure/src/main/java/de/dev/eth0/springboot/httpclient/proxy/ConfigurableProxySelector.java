/*
 * Copyright (c) 2020. dev-eth0.de All rights reserved.
 */

package de.dev.eth0.springboot.httpclient.proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dev.eth0.springboot.httpclient.HttpClientProperties;

/**
 * Custom proxy selector
 */
public class ConfigurableProxySelector extends ProxySelector {

  private static final Logger LOG = LoggerFactory.getLogger(ConfigurableProxySelector.class);

  private final List<HttpClientProperties.ProxyConfiguration> proxyConfigurations;

  public ConfigurableProxySelector(HttpClientProperties.ProxyConfiguration[] proxyConfigurations) {
    super();
    this.proxyConfigurations = Arrays.asList(proxyConfigurations);
  }

  @Override
  public List<Proxy> select(URI uri) {
    List<Proxy> proxies = proxyConfigurations.stream()
        .filter(config -> matches(config, uri.getHost()))
        //TODO: no need to do this on-demand, this can happen in constructor
        .map(config -> new Proxy(Proxy.Type.HTTP, new InetSocketAddress(config.getHost(), config.getPort())))
        .collect(Collectors.collectingAndThen(
            Collectors.toList(),
            proxyList -> proxyList.isEmpty() ? Collections.singletonList(Proxy.NO_PROXY) : proxyList
        ));
    LOG.debug("Matching proxies: {}", proxies);
    return proxies;
  }

  private boolean matches(HttpClientProperties.ProxyConfiguration config, String host) {
    if (config.getHostPatterns().length == 0) {
      return true;
    }
    return Arrays.stream(config.getHostPatterns()).anyMatch(pattern -> pattern.matcher(host).matches());
  }


  @Override
  public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
    getDefault().connectFailed(uri, sa, ioe);
  }
}
