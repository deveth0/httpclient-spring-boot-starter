# httpclient-spring-boot-sample -- Docker

This folder contains several Dockerfiles that startup servers to demonstrate the different use-cases of httpclient-spring-boot-sample.

You can start those servers using `docker-compose up`

* proxy: Simple squid proxy, default port: 3128
* proxy_auth: Squid proxy that requires authentication (test:test123), default port: 3129
* 