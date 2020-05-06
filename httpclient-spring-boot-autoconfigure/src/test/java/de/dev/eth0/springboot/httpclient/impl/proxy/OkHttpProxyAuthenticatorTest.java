/*
 * Copyright (c) 2020. dev-eth0.de All rights reserved.
 */

package de.dev.eth0.springboot.httpclient.impl.proxy;

import static okhttp3.Protocol.HTTP_1_1;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import javax.net.SocketFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.dev.eth0.springboot.httpclient.HttpClientProperties;
import okhttp3.Address;
import okhttp3.Credentials;
import okhttp3.FakeDns;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.internal.proxy.NullProxySelector;

public class OkHttpProxyAuthenticatorTest {

  private Route routeWithAuth;
  private Route routeWithoutAuth;

  private HttpClientProperties.ProxyConfiguration proxyConfigWithAuth;
  private HttpClientProperties.ProxyConfiguration proxyConfigWithoutAuth;

  private OkHttpProxyAuthenticator underTest;

  private Response.Builder responseBuilder;

  @BeforeEach
  public void setup() {
    proxyConfigWithAuth = new HttpClientProperties.ProxyConfiguration();
    proxyConfigWithAuth.setProxyHost("testProxyHost");
    proxyConfigWithAuth.setProxyPort(1234);
    proxyConfigWithAuth.setProxyUser("testUser");
    proxyConfigWithAuth.setProxyPassword("testPassword");

    proxyConfigWithoutAuth = new HttpClientProperties.ProxyConfiguration();
    proxyConfigWithoutAuth.setProxyHost("anotherProxyHost");
    proxyConfigWithoutAuth.setProxyPort(5678);

    underTest = new OkHttpProxyAuthenticator(new HttpClientProperties.ProxyConfiguration[] { proxyConfigWithAuth, proxyConfigWithoutAuth });

    Address address = new Address(
        "server", 443, new FakeDns(), SocketFactory.getDefault(),
        null, null, null, okhttp3.Authenticator.NONE, null, List.of(HTTP_1_1), List.of(), new NullProxySelector());
    routeWithAuth = new Route(address,
        new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(proxyConfigWithAuth.getProxyHost(), proxyConfigWithAuth.getProxyPort())),
        InetSocketAddress.createUnresolved("example.com", 443));
    routeWithoutAuth = new Route(address,
        new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(proxyConfigWithoutAuth.getProxyHost(), proxyConfigWithoutAuth.getProxyPort())),
        InetSocketAddress.createUnresolved("example.com", 443));

    responseBuilder = new Response.Builder().code(407)
        .protocol(HTTP_1_1)
        .message("message");

  }

  @Test
  public void authenticate_alreadyFailed() {
    Request request = new Request.Builder().url("http://example.com/sample").header("Proxy-Authorization", "abcdef").build();

    Request authenticationRequest = underTest.authenticate(routeWithAuth, responseBuilder.request(request).build());
    assertThat(authenticationRequest).isNull();
  }

  @Test
  public void authenticate_noCredentials() {
    Request request = new Request.Builder().url("http://example.com/sample").build();

    Request authenticationRequest = underTest.authenticate(routeWithoutAuth, responseBuilder.request(request).build());
    assertThat(authenticationRequest).isNull();
  }

  @Test
  public void authenticate_credentials() {
    Request request = new Request.Builder().url("http://example.com/sample").build();

    Request authenticationRequest = underTest.authenticate(routeWithAuth, responseBuilder.request(request).build());
    assertThat(authenticationRequest).isNotNull();

    assertThat(authenticationRequest.header("Proxy-Authorization"))
        .isEqualTo(Credentials.basic(proxyConfigWithAuth.getProxyUser(), proxyConfigWithAuth.getProxyPassword()));
  }
}