package es.um.redes.nanoChat.server.roomManager;

import es.um.redes.nanoChat.messageFV.NCMessage;
import es.um.redes.nanoChat.messageFV.NCRoomInfoMessage;

import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class NCRoom extends NCRoomManager {
    private final Map<String, Socket> userMap;
    private long lastMsg;


    public NCRoom() {
        super();
        this.userMap = new HashMap<String, Socket>();
        this.lastMsg = 0;
    }

    @Override
    public boolean registerUser(String u, Socket s) {
        if (this.userMap.containsKey(u))
            return false;
        else {
            this.userMap.put(u, s);
            return true;
        }
    }

    @Override
    public void broadcastMessage(String u, String message) throws IOException {
        // todo: para cada socket, enviar un mensaje
        for (Socket s : userMap.values()) {

        }
        this.lastMsg = new Date().getTime();

    }

    @Override
    public void removeUser(String u) {
        this.userMap.remove(u);
    }

    @Override
    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    @Override
    public NCRoomInfoMessage getDescription() {
        List<String> users = new ArrayList<>(this.userMap.keySet());
        return new NCRoomInfoMessage(NCMessage.OP_INFO, this.roomName, users, this.lastMsg);
    }

    @Override
    public int usersInRoom() {
        return this.userMap.size(); // Obviamente
    }
}
