FROM sameersbn/squid:3.5.27-2
# thx @ https://github.com/sameersbn/docker-squid/issues/59#issuecomment-583789230
RUN cp /etc/squid/squid.conf /etc/squid/squid.conf.default \
     && apt-get update \
     && DEBIAN_FRONTEND=noninteractive apt-get install -y apache2-utils \
     && rm -rf /var/lib/apt/lists/* \
     && htpasswd -b -c /etc/squid/.squid_users test test123 \
     && chown proxy /etc/squid/.squid_users \
     && echo "auth_param basic program /usr/lib/squid/basic_ncsa_auth /etc/squid/.squid_users" >> /etc/squid/squid.conf \ 
     && echo "auth_param basic children 5" >> /etc/squid/squid.conf \ 
     && echo "auth_param basic realm Proxy Authentication Required" >> /etc/squid/squid.conf \ 
     && echo "auth_param basic credentialsttl 2 hours" >> /etc/squid/squid.conf \ 
     && echo "auth_param basic casesensitive off" >> /etc/squid/squid.conf \ 
     && echo "acl auth_users proxy_auth test" >> /etc/squid/squid.conf \ 
     && echo "http_access allow auth_users" >> /etc/squid/squid.conf \
     && sed -i 's/http_access deny all/#http_access deny all/g' /etc/squid/squid.conf
    
    


