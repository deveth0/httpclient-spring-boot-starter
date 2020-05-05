/*
 * Copyright (c) 2020. dev-eth0.de All rights reserved.
 */

package de.dev.eth0.springboot.httpclient.samples;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Quote {

  private String type;
  private Value value;

  public Quote() {
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Value getValue() {
    return value;
  }

  public void setValue(Value value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "Quote{" +
        "type='" + type + '\'' +
        ", value=" + value +
        '}';
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public class Value {

    private Long id;
    private String quote;

    public Value() {
    }

    public Long getId() {
      return this.id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public String getQuote() {
      return this.quote;
    }

    public void setQuote(String quote) {
      this.quote = quote;
    }

    @Override
    public String toString() {
      return "Value{" +
          "id=" + id +
          ", quote='" + quote + '\'' +
          '}';
    }
  }
}
