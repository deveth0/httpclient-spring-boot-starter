/*
 * Copyright (c) 2020. dev-eth0.de All rights reserved.
 */

package de.dev.eth0.springboot.httpclient.impl.proxy;

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
import de.dev.eth0.springboot.httpclient.impl.host.HostConfigurationSelector;

/**
 * Custom proxy selector
 */
public class ConfigurableProxySelector extends ProxySelector {

  private static final Logger LOG = LoggerFactory.getLogger(ConfigurableProxySelector.class);

  private final HostConfigurationSelector hostConfigurationSelector;


  public ConfigurableProxySelector(HttpClientProperties.HostConfiguration[] hostConfigurations) {
    super();
    this.hostConfigurationSelector = new HostConfigurationSelector(Arrays.asList(hostConfigurations));
  }

  @Override
  public List<Proxy> select(URI uri) {
    List<Proxy> proxies = this.hostConfigurationSelector.select(uri.getHost())
        .map(config -> new Proxy(Proxy.Type.HTTP, new InetSocketAddress(config.getProxyHost(), config.getProxyPort())))
        .collect(Collectors.collectingAndThen(
            Collectors.toList(),
            proxyList -> proxyList.isEmpty() ? Collections.singletonList(Proxy.NO_PROXY) : proxyList
        ));
    LOG.debug("Matching proxies: {}", proxies);
    return proxies;
  }

  @Override
  public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
    getDefault().connectFailed(uri, sa, ioe);
  }
}
