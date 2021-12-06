FROM hseeberger/scala-sbt:eclipse-temurin-11.0.13_1.5.5_3.1.0 AS build

COPY source /opt/authorizer/src/
WORKDIR /opt/authorizer/src

RUN sbt test && \
    sbt stage

FROM scratch AS export

COPY --from=build /opt/authorizer/src/target/universal/stage /authorizer/
