/*
 * Copyright (c) 2020. dev-eth0.de All rights reserved.
 */

package de.dev.eth0.springboot.httpclient;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("http.client")
@Validated
public class HttpClientProperties {


}
