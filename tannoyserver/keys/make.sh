#!/bin/bash

echo "You will be asked to enter in a PEM passphrase."
echo "Please write this down somewhere, as the server will"
echo "request this key every time it needs to start up."
echo
echo "You do not have to fill out any of the information"
echo "that OpenSSL requests, i.e. email address, company name."
echo "Just press ENTER until the end is reached."
echo "Very bad practice, but this is proof of concept."
openssl req -x509 -newkey rsa:2048 -keyout key.pem -out cert.crt -days 365
