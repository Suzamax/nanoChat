package es.um.redes.nanoChat.server.roomManager;

import es.um.redes.nanoChat.messageFV.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class NCRoom extends NCRoomManager {
    private final Map<String, Socket> userMap;
    private long lastMsg;


    public NCRoom(String name) {
        this.roomName = name;
        this.userMap = new HashMap<String, Socket>();
        this.lastMsg = 0;
    }

    @Override
    public boolean registerUser(String u, Socket s) throws IOException {
        if (this.userMap.containsKey(u))
            return false;
        else {
            for (Socket so : userMap.values())
                if (!so.equals(s))
                    sendSrvMessage(so, u, true);
            this.userMap.put(u, s);
            return true;
        }
    }

    @Override
    public void broadcastMessage(String u, String message) throws IOException {
        //// para cada socket, enviar un mensaje
        for (Socket s : userMap.values())
            sendMessage(s, u, message, false);
        this.lastMsg = new Date().getTime();
    }


    @Override
    public void removeUser(String u) throws IOException {
        this.userMap.remove(u);
        for (Socket so : userMap.values())
            sendSrvMessage(so, u, false);
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

    public void sendMessage(Socket s, String u, String msg, boolean priv) throws IOException {
        NCRoomSndRcvMessage builtMsg = (NCRoomSndRcvMessage) NCMessage.makeMessage(NCMessage.OP_MSG, u, msg, priv);
        String rawBuiltMsg = builtMsg.toEncodedString();
        new DataOutputStream(s.getOutputStream()).writeUTF(rawBuiltMsg);
    }

    public Socket getUserSocket(String u) {
        return this.userMap.get(u);
    }

    public void sendSrvMessage(Socket s, String u, boolean jol) throws IOException {
        NCBroadcastMessage builtMsg = (NCBroadcastMessage) NCMessage.makeBroadcastMessage(NCMessage.OP_BROADCAST, u, jol);
        String rawBuiltMsg = builtMsg.toEncodedString();
        new DataOutputStream(s.getOutputStream()).writeUTF(rawBuiltMsg);
    }

    public void sendInfo(Socket s, String r) throws IOException {
        List<String> users = new ArrayList<>();
        users.addAll(this.userMap.keySet());
        NCRoomInfoMessage builtMsg = (NCRoomInfoMessage) NCMessage.makeInfoMessage(NCMessage.OP_INFO, r, users, this.lastMsg);
        String rawBuiltMsg = builtMsg.toEncodedString();
        new DataOutputStream(s.getOutputStream()).writeUTF(rawBuiltMsg);
    }
}
