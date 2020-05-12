/*
 * Copyright (c) 2020. dev-eth0.de All rights reserved.
 */

package de.dev.eth0.springboot.httpclient.samples;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "quoteClient", url = "${demoUrl}")
public interface QuoteFeignClient {

  @GetMapping
  Quote getQuote();
}
