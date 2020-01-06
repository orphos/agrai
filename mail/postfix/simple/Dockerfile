FROM alpine:3.11
COPY build.sh /root/
RUN /bin/sh /root/build.sh
COPY entrypoint.sh /root/
COPY conf/postfix/additional.cf /root/postfix-additional.cf
COPY conf/supervisor/postfix.ini /etc/supervisor.d/
COPY conf/supervisor/opendkim.ini /etc/supervisor.d/
COPY conf/supervisor/rsyslog.ini /etc/supervisor.d/
COPY conf/opendkim/additional.conf /etc/opendkim/
COPY conf/opendkim/TrustedHosts /etc/opendkim/

ENV TIMEZONE=Asia/Tokyo \
  HOST_NAME=localhost \
  DOMAIN_NAME=example.org \
  MESSAGE_SIZE_LIMIT=10240000 \
  AUTH_USER=user \
  AUTH_PASSWORD=password \
  OPENDKIM_REPORT_ADDRESS=postmaster@example.org

EXPOSE 25

ENTRYPOINT ["/bin/sh", "/root/entrypoint.sh"]
