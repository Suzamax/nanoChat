FROM openjdk:8-alpine
COPY src /usr/src/clientnano
WORKDIR /usr/src/clientnano
RUN javac -cp "./*" \
es/um/redes/nanoChat/directory/server/Directory.java \
es/um/redes/nanoChat/directory/connector/DirectoryConnector.java \
es/um/redes/nanoChat/directory/server/DirectoryThread.java \
es/um/redes/nanoChat/server/roomManager/NCRoomDescription.java \
es/um/redes/nanoChat/server/roomManager/NCRoomManager.java \
es/um/redes/nanoChat/server/NanoChatServer.java \
es/um/redes/nanoChat/server/NCServerManager.java \
es/um/redes/nanoChat/server/NCServerThread.java \
es/um/redes/nanoChat/messageFV/NCMessage.java \
es/um/redes/nanoChat/messageFV/NCRoomMessage.java \
es/um/redes/nanoChat/messageML/NCMessage.java \
es/um/redes/nanoChat/messageML/NCRoomMessage.java \
es/um/redes/nanoChat/client/application/NanoChat.java \
es/um/redes/nanoChat/client/application/NCController.java \
es/um/redes/nanoChat/client/comm/NCConnector.java \
es/um/redes/nanoChat/client/shell/NCCommands.java \
es/um/redes/nanoChat/client/shell/NCShell.java
# aquí pondremos todos los ficheros a usar, y se pondrá una contrabarra (\) para
# poner el siguiente fichero en la próxima línea.
CMD ["java", "es.um.redes.nanoChat.client.application.NanoChat", "NCDirectory"]
