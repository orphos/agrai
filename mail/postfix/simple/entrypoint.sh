#!/bin/sh

# configure timezone
cp /usr/share/zoneinfo/${TIMEZONE} /etc/localtime
apk del tzdata

# configure postfix
echo "${AUTH_PASSWORD}" | /usr/sbin/saslpasswd2 -p -c -u ${DOMAIN_NAME} ${AUTH_USER}
openssl req -new -sha384 -key "/cert/key.pem" -subj "/CN=${HOST_NAME}" -out "/cert/csr.pem"
openssl x509 -req -days 36500 -in "/cert/csr.pem" -signkey "/cert/key.pem" -out "/cert/cert.pem" &>/dev/null
cat /root/postfix-additional.cf >> /etc/postfix/main.cf
rm /root/postfix-additional.cf
echo "mydomain = ${DOMAIN_NAME}" >> /etc/postfix/main.cf

# configure opendkim
mkdir -p /opendkim/${DOMAIN_NAME}
if test ! -f "/opendkim/${DOMAIN_NAME}/default.private"; then
  chmod u=rwX,g=rX,o= /opendkim/${DOMAIN_NAME}
  opendkim-genkey -D /opendkim/${DOMAIN_NAME}/ -d ${DOMAIN_NAME} -s default
fi
chmod ug=rX,o= /opendkim/${DOMAIN_NAME}
_dkim_conf=/etc/opendkim/opendkim.conf
sed -i "s/^\(KeyFile\)\s.*/\1 \/opendkim\/${DOMAIN_NAME}\/default.private/" $_dkim_conf
sed -i "s/^\(Domain\)\s.*/\1 ${DOMAIN_NAME}/" $_dkim_conf
sed -i "s/^\(ReportAddress\)\s.*/\1 ${OPENDKIM_REPORT_ADDRESS}/" $_dkim_conf

# run supervisord
supervisord -c /etc/supervisord.conf
