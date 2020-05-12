FROM sameersbn/squid:3.5.27-2
# thx @ https://github.com/sameersbn/docker-squid/issues/59#issuecomment-583789230
RUN cp /etc/squid/squid.conf /etc/squid/squid.conf.default && \
    sed 's/http_access deny all/http_access allow all/' \
    /etc/squid/squid.conf.default > /etc/squid/squid.conf
