# vertx-web-demo

## Problem
Unexpected handling of http body that is over the configured limit.

Vertx provides BodyHandlerImpl.java for parsing the request body.
It has a configuration
public BodyHandler setBodyLimit(long bodyLimit)
But this configuration doesn't seem to work as expected.

## Expected behavior 
According to BodyHandlerImpl.java
https://github.com/vert-x3/vertx-web/blob/34e3e514374ed502f325e341b8f7f880b66c180d/vertx-web/src/main/java/io/vertx/ext/web/handler/impl/BodyHandlerImpl.java#L255
the response should return with status code 413

## Actual behavior
(Two) calls to the default error handler with status code 500.
Failure io.vertx.core.VertxException: Connection was closed

This generic VertxException that doesn't contain stack trace makes it very difficult to troubleshoot the root source of the exception.

## Reproduce steps
clone this repo and run
> ./gradlew run

In another terminal, simulate file upload with curl
> curl -i -vv --form video=@somebigfile http://localhost:9999/upload
