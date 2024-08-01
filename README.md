Introduction
============
<img src="https://img.shields.io/badge/Ask%20me-anything-1abc9c.svg" alt="ask me"> &nbsp; <img src="https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot" alt="spring boot" width="100"/>


This project is a starter project for creating a rest api server with
jwt based security. It has the following functions.

1.  Allow registration with a basic auth, backed with a postgres
    database. Since JPA is being used you can replace the database with
    other ones.
2.  Allow registered users login and receive a JWT token and a refresh token.
3.  JWT Tokens have can be used until expiration which can be set using a configuration file. 
4.  Refresh tokens can only be used once. 
5.  After receiving the token, users can access secured endpoint using
    the token.
6.  Social login with GitHub, you can easily add new social login provider.
7.  Email verification for email and password login.
8.  Password reset flow using email.
9.  CSRF protection suitable for SPA frontends
10. Addition of context cookie for preventing stolen JWTs.

This kind of scenario is very common, so this starter will be a good
starting point for such applications. When the necessity arises we can
remove the basic auth and jwt minting from our application and configure
an external authorization server.

There are two frontends which are still a work in progress, one using react and the other using angular.

Build
=====

First of all we need to generate a key pair. There are may ways to do
this. One example with openssl is as follows.

```shell
    # create rsa key pair
    openssl genrsa -out keypair.pem 2048
    # extract public key
    openssl rsa -in keypair.pem -pubout -out public.pem
    # create private key in PKCS\#8 format
    openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in keypair.pem -out private.pem
```

Then place the `public.pem` and `private.pem` files under the directory
`backend\src\main\resources\certs`.

After setting this up the application can be run as a typical spring
boot project.

Remember that you need java 21 for this project. If you are not using
[sdkman](https://sdkman.io), check it out it is a good tool.

```shell
    ./gradlew bootRun
```
