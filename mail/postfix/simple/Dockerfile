FROM alpine:3.11
RUN apk update
RUN apk add postfix mailx rsyslog openssl ca-certificates cyrus-sasl tzdata
RUN mkdir /cert; \
  openssl genrsa -aes256 -passout pass:dummy -out "/cert/key.pass.pem" 2048; \
  openssl rsa -passin pass:dummy -in "/cert/key.pass.pem" -out "/cert/key.pem"; \
  rm -f /cert/key.pass.pem;
RUN {\
 echo 'inet_interfaces = all'; \
 echo 'smtpd_tls_cert_file = /cert/cert.pem'; \
 echo 'smtpd_tls_key_file = /cert/key.pem'; \
 echo 'smtpd_tls_security_level = may'; \
 echo 'smtp_tls_CApath = /etc/ssl/certs'; \
 echo 'smtp_tls_CAfile = /etc/ssl/certs/ca-certificates.crt'; \
 echo 'smtp_tls_security_level = may'; \
 echo 'smtpd_sasl_path = smtpd'; \
 echo 'smtpd_helo_required = yes'; \
 echo 'smtpd_helo_restrictions = permit_mynetworks, reject_invalid_hostname, reject_non_fqdn_hostname, reject_unknown_hostname'; \
 echo 'smtpd_sender_restrictions = reject_non_fqdn_sender, reject_unknown_sender_domain'; \
 echo 'smtpd_tls_received_header = yes'; \
 echo 'smtpd_tls_loglevel = 1'; \
 echo 'smtp_tls_loglevel = 1'; \
 echo 'smtp_tls_session_cache_database = btree:${data_directory}/smtp_scache'; \
 echo 'tls_random_source = dev:/dev/urandom'; \
 echo 'mynetworks = 127.0.0.0/8, 192.168.0.0/16', peer; \
 echo 'debug_peer_list = 127.0.0.0/8, 192.168.0.0/16, peer,  gmail.com'; \
 echo 'myhostname = localhost'; \
 echo 'smtpd_banner = $myhostname ESMTP unknown'; \
 echo 'message_size_limit = 10240000'; \
 echo 'smtpd_sasl_auth_enable = no'; \
} >> /etc/postfix/main.cf

RUN sed -i 's/^\(module(load="imklog")\)$/#\1/' /etc/rsyslog.conf; # disable logging kernel messages

RUN { \
  echo '#!/bin/sh'; \
  echo 'cp /usr/share/zoneinfo/${TIMEZONE} /etc/localtime'; \
  echo 'apk del tzdata'; \
  echo 'rm -rf /var/cache/apk/*'; \
  echo 'test -f /etc/sasl2/sasldb2 && rm -f /etc/sasl2/sasldb2'; \
  echo 'echo "${AUTH_PASSWORD}" | /usr/sbin/saslpasswd2 -p -c -u ${DOMAIN_NAME} ${AUTH_USER}'; \
  echo 'test -f /etc/sasl2/sasldb2 && chown postfix:postfix /etc/sasl2/sasldb2'; \
  echo 'openssl req -new -sha384 -key "/cert/key.pem" -subj "/CN=${HOST_NAME}" -out "/cert/csr.pem"'; \
  echo 'openssl x509 -req -days 36500 -in "/cert/csr.pem" -signkey "/cert/key.pem" -out "/cert/cert.pem" &>/dev/null'; \
  echo 'echo "mydomain = ${DOMAIN_NAME}" >> /etc/postfix/main.cf'; \
  echo 'postfix start'; \
  echo 'rsyslogd -n'; \
} > /root/entrypoint.sh; \
chmod +x /root/entrypoint.sh

ENV TIMEZONE Asia/Tokyo

ENV HOST_NAME localhost
ENV DOMAIN_NAME example.org

ENV MESSAGE_SIZE_LIMIT 10240000

ENV AUTH_USER user
ENV AUTH_PASSWORD password

EXPOSE 25

ENTRYPOINT ["/root/entrypoint.sh"]

CMD ["sh", "-c", "tail -f /dev/null"]

