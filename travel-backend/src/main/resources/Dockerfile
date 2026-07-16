FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY /opt/backend/*.jar app.jar

EXPOSE 3000

ENV TZ=Asia/Shanghai

ENTRYPOINT ["java", "-jar", "app.jar"]
