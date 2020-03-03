FROM openjdk:8-alpine
COPY src /usr/src/directorynano
WORKDIR /usr/src/directorynano
RUN javac -cp "./*" \
es/um/redes/nanoChat/directory/server/Directory.java \
es/um/redes/nanoChat/directory/connector/DirectoryConnector.java \
es/um/redes/nanoChat/directory/server/DirectoryThread.java
# aquí pondremos todos los ficheros a usar, y se pondrá una contrabarra (\) para
# poner el siguiente fichero en la próxima línea.
CMD ["java", "Directory"]