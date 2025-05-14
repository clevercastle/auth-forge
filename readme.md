This repository provides a comprehensive solution for user authentication.
It implements essential features such as user registration, login, and Single Sign-On (SSO) to provide secure and
convenient access to your applications.

## Table of Contents

- [Overview](#Overview)
- [Features](#Features)
- [Plans](#Plans)
- [Usage](#Usage)
- [Reference](#Reference)

## Overview

The goal of this project is to offer a robust authentication system that supports:

- **User Registration:** Allow users to create accounts securely.
- **User Login:** Enable users to log in using registered credentials.
- **SSO Login:** Provide Single Sign-On (SSO) capabilities that let users authenticate via third-party providers or
  centralized authentication services.

This system is designed to be scalable, secure, and easily integrable with existing applications.

## Features

- **User Registration:** Securely capture and store user data for new account creation.
- **User Login:** Validate user credentials and manage session tokens.
- **Single Sign-On (SSO):** Streamline access across multiple systems with centralized authentication.
- **Token Management:** Implement secure token handling (e.g., JWT) for session management.
- **Multi-Database Support**: Works seamlessly with multiple databases (e.g., RDB, DynamoDB etc.).

## Plans

### Multiple database support:

- [ ] PostgreSQL (In progress)
- [ ] MySQL
- [ ] DynamoEDB (In progress)
- [ ] MongoDB

### Authentication provider

- [x] Email + password (Basic Authentication)
- [ ] Email + password (pbkdf2)
- [x] Email + one time password
- [ ] Email + Passkey
- [ ] Api key
- [ ] Multi-Factor authentication (In progress)

### SSO provider

- [x] Google
- [x] GitHub
- [ ] Apple
- [ ] Microsoft
- [ ] Okta
- [x] OIDC

### Infra

- [ ] Serverless Support (AWS Lambda)

### Others

- [ ] Publish to maven repository

## Usage

Provide one example of how to use the authentication system in `examples` directory.

## Reference

### how to generate one ecdsa key pair in shell

```shell
# ... install openssl lib
openssl ecparam -name prime256v1 -genkey -noout -out key.pem
openssl pkcs8 -topk8 -inform PEM -outform DER -in key.pem -nocrypt | base64
openssl ec -pubout -in key.pem -out public_key.pem
openssl ec -in public_key.pem -pubin -outform DER | base64
```

### Google Login

[Google oauth jwk endpoint](https://www.googleapis.com/oauth2/v3/certs)

[Google id token payload](https://developers.google.com/identity/openid-connect/openid-connect#an-id-tokens-payload)

### Github Login

[Github user info api doc](https://docs.github.com/en/rest/users/users?apiVersion=2022-11-28)

[Github user emails api doc](https://docs.github.com/en/rest/users/emails?apiVersion=2022-11-28)