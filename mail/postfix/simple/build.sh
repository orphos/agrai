#!/bin/sh

# install dependencies
apk update
apk add --no-cache postfix mailx rsyslog openssl ca-certificates cyrus-sasl tzdata opendkim opendkim-utils supervisor

# configure postfix
mkdir /cert
openssl genrsa -aes256 -passout pass:dummy -out "/cert/key.pass.pem" 2048
openssl rsa -passin pass:dummy -in "/cert/key.pass.pem" -out "/cert/key.pem"
rm -f /cert/key.pass.pem
if test -f /etc/sasl2/sasldb2; then
  rm -f /etc/sasl2/sasldb2
fi

# configure rsyslog
sed -i 's/^\(module(load="imklog")\)$/#\1/' /etc/rsyslog.conf # disable logging kernel messages

# configure opendkim
echo 'Include /etc/opendkim/additional.conf' >> /etc/opendkim/opendkim.conf
mkdir -p /run/opendkim

# configure supervisor
mkdir /run/supervisor
sed -i 's/^\s*;*\s*\(nodaemon\)\s*=.*/\1=true/' /etc/supervisord.conf
sed -i 's/^\s*;*\s*\(user\)\s*=\s*chrism/\1=root/' /etc/supervisord.conf
sed -i '/^\[unix_http_server\]$/a username=dummy\npassword=dummy/' /etc/supervisord.conf
sed -i '/^\[supervisorctl\]$/a username=dummy\npassword=dummy/' /etc/supervisord.conf
mkdir -p /etc/supervisor.d
