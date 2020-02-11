FROM openjdk:8-alpine
COPY . /usr/src/directorynano
WORKDIR /usr/src/directorynano
RUN javac src/es/um/redes/nanoChat/directory/server/Directory.java
CMD ["java", "Directory"]