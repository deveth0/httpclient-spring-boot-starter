# httpclient-spring-boot-starter

[![Build Status](https://travis-ci.com/deveth0/httpclient-spring-boot-starter.svg)](https://travis-ci.com/github/deveth0/httpclient-spring-boot-starter)
[![Coverage Status](https://coveralls.io/repos/github/deveth0/httpclient-spring-boot-starter/badge.svg?branch=master)](https://coveralls.io/github/deveth0/httpclient-spring-boot-starter?branch=master)
[![Maven central](https://maven-badges.herokuapp.com/maven-central/de.dev-eth0.spring-boot.httpclient/httpclient-spring-boot-starter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.dev-eth0.spring-boot.httpclient/httpclient-spring-boot-starter)
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/https/oss.sonatype.org/de.dev-eth0.spring-boot.httpclient/httpclient-spring-boot-starter.svg)](https://oss.sonatype.org/content/repositories/snapshots/de/dev-eth0/spring-boot/httpclient/httpclient-spring-boot-starter/)
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

This project provides a Spring-Boot Starter that enables the additional configuration of the used Httpclients. 

It supports the configuration of [OkHttp](https://square.github.io/okhttp/) and [Apache HttpClient](https://hc.apache.org/httpcomponents-client-ga/) which are also the supported Clients in Spring.

## Usage

The `httpclient-spring-boot-starter` brings most of the required configuration with it, therefor you only need to add it as a maven dependency and enable the desired Httpclient.
 
```
<dependency>
  <groupId>de.dev-eth0.spring-boot.httpclient</groupId>
  <artifactId>httpclient-spring-boot-starter</artifactId>
</dependency>
```

Make sure, that you have either `org.apache.httpcomponents:httpclient` or `com.squareup.okhttp3:okhttp` declared as a dependency and enable the configuration as described in [spring-cloud-commons - http-clients](https://cloud.spring.io/spring-cloud-commons/reference/html/#http-clients).

This will make sure, that the `spring-cloud` dependencies use the custom client configurations.
 
### Feign

Feign uses a different configuration to enable the clients: `feign.okhttp.enabled` and `feign.httpclient.enabled`. 

### RestTemplate

To make sure, your RestTemplate uses the custom client, you need to configure it accordingly:
```
@Bean
public RestTemplate restTemplate(RestTemplateBuilder builder, ClientHttpRequestFactory clientHttpRequestFactory) {
  return builder.requestFactory(() -> clientHttpRequestFactory).build();
}
```

## Configuration 

All configuration values are prefixed with `http.client` (e.g. `http.client.timeouts.connectionTimeout`).

It's possible to configure the proxy based on the requested hostnames using the `hostPattern` config.

| Config | Description | Default | Example | 
|---|---|---|---|
| timeouts.connectionTimeout  | Connection Timeout in ms  | 5000 |  |
| timeouts.socketTimeout  |  Socket Timeout in ms, for OkHttp this is used as readTimeout and writeTimeout | 10000  |
| proxies[].hostPatterns | Pattern for matching the hostname, empty matches all  | empty | `google.*`  |
| proxies[].proxyHost | Hostname or IP of the Proxy | | `10.0.9.1` or `corp-proxy.domain` |
| proxies[].proxyPort | Port of the Proxy (optional) | 3128 | |
| proxies[].proxyUser | Proxy user name (optional) | | `testUser`|
| proxies[].proxyPassword | Proxy password (optional) | | `testPassword` |

Example:
```
http:
  client:
    proxies:
      - hostPatterns: ["google.de]
        proxyHost: localhost
        proxyPort: 3333
        proxyUser: testUser
        proxyPassword: testPassword

    timeouts:
      connectionTimeout: 5000
      socketTimeout: 10000
```

## Sample Project

You can find a sample project which configures both `Feign` and `RestTemplate` to use either `OkHttp` or `Apache HttpClient` in `/httpclient-spring-boot-sample`.

The project includes the `spring-boot-maven-plugin` therefor you can simply run `mvn clean install spring:boot-run`. By default, the project uses `Apache HttpClient`, if you want to use `OkHttp`, you can use the following command:

```
SPRING_PROFILES_ACTIVE=okhttp mvn spring-boot:run
```

The project also contains a `docker-compose.yml` file which starts a local `Squid` proxy to demonstrate the usecase.
