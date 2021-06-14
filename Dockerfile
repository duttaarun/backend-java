FROM maven:3.8.1-jdk-11
WORKDIR /app


COPY . /app
RUN mvn clean package
RUN cp /app/target/backend-0.0.1-SNAPSHOT.jar /app
RUN rm -rf /app/target

EXPOSE 8080
ENTRYPOINT java -jar /app/backend-0.0.1-SNAPSHOT.jar
