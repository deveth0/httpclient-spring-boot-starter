/*
 * Copyright (c) 2020. dev-eth0.de All rights reserved.
 */

package de.dev.eth0.springboot.httpclient.samples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import de.dev.eth0.springboot.httpclient.HttpClientProperties;

@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties(HttpClientProperties.class)
public class HttpClientConfigSampleApplication implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(HttpClientConfigSampleApplication.class);
  @Autowired
  private ClientHttpRequestFactory clientHttpRequestFactory;
  @Autowired
  private RestTemplate restTemplate;
  @Autowired
  private ConfigurableApplicationContext context;
  @Autowired
  private QuoteFeignClient feignClient;

  public static void main(String[] args) {
    SpringApplication.run(HttpClientConfigSampleApplication.class);
  }

  /**
   * We need to set the custom {@link ClientHttpRequestFactory}, otherwise the default Client is used
   */
  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder, ClientHttpRequestFactory clientHttpRequestFactory) {
    return builder.requestFactory(() -> clientHttpRequestFactory).build();
  }

  @Override
  public void run(String... args) {
    if (clientHttpRequestFactory instanceof HttpComponentsClientHttpRequestFactory) {
      log.info("Running with Apache HttpClient");
    }
    if (clientHttpRequestFactory instanceof OkHttp3ClientHttpRequestFactory) {
      log.info("Running with Ok HttpClient");
    }

    Quote quote = restTemplate.getForObject("https://gturnquist-quoters.cfapps.io/api/random", Quote.class);
    log.info("RestTemplate Quote: {}", quote.toString());

    quote = feignClient.getQuote();
    log.info("FeignClient Quote: {}", quote.toString());

    SpringApplication.exit(context);
  }
}
