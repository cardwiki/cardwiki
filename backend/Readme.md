# Backend Template for SEPM Group Phase

## How to run it

### Start the backed
`mvn spring-boot:run`

### Start the backed with test data
If the database is not clean, the test data won't be inserted

`mvn spring-boot:run -Dspring-boot.run.profiles=generateData`

### OpenID Connect

The backend implements login via [OpenID Connect](https://openid.net/connect/).

Clients only need to know two endpoints:

* `/oauth2/authorization` returns the available authentication providers as a map `{id: displayName}`
* `/oauth2/authorization/<id>` redirects to a specific authentication provider
  On success the client gets a cookie.
  Clients can determine where they are redirected by setting the `Referer` Header.

To log out clients can simply delete the cookie.
