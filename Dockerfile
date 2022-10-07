FROM openjdk:17-slim
ARG uid=1009

RUN groupadd --gid ${uid} -r app \
 && useradd --uid ${uid} -r -g app -M app \
 && mkdir /app \
 && chown -R app:app /app

ARG jar=SpringBack-0.0.1
COPY target/${jar}.jar /app/app.jar

USER ${uid}
WORKDIR /app

ENTRYPOINT ["java","-jar","app.jar"]