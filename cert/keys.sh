#!/usr/bin/env bash

## Llavero Servidor: Par de claves del servidor (privada y pública) formato PEM
keytool -genkeypair -alias serverKeyPair -keyalg RSA -keysize 4096 -validity 365 -storetype PKCS12 -keystore server_keystore.p12 -storepass 1234567

## Llavero Cliente: Par de claves del cliente (privada y pública) formato JKS
keytool -genkeypair -alias serverKeyPair -keyalg RSA -keysize 4096 -validity 365 -storetype JKS -keystore server_keystore.jks -storepass 1234567