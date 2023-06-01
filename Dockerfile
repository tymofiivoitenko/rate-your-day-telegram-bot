FROM openjdk:17
ENV APP_NAME RateYourDayBot-0.0.1-SNAPSHOT
COPY build/libs/${APP_NAME}.jar ${APP_NAME}.jar
EXPOSE 8080
ENTRYPOINT java -jar ${APP_NAME}.jar
