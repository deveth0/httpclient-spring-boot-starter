/*
 * Copyright (c) 2020. dev-eth0.de All rights reserved.
 */

package de.dev.eth0.springboot.httpclient.samples;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "quoteClient", url = "https://gturnquist-quoters.cfapps.io")
public interface QuoteFeignClient {

  @GetMapping(value = "/api/random")
  Quote getQuote();
}
