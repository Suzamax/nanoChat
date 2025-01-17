import es.um.redes.nanoChat.messageFV.NCImmediateMessage;
import es.um.redes.nanoChat.messageFV.NCMessage;
import es.um.redes.nanoChat.messageFV.NCRoomListMessage;
import es.um.redes.nanoChat.messageFV.NCRoomMessage;
import es.um.redes.nanoChat.messageFV.NCRoomInfoMessage;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class MessageTests {

    @Test
    void TestRoomMessages() {
        // Construir un NCRoomMessage
        NCRoomMessage message = (NCRoomMessage) NCMessage.makeRoomMessage(NCMessage.OP_NICK, "ElonMusk");
        // Serializarlo a String
        String serialized = message.toEncodedString();
        // Hacer readFromString
        NCMessage res = NCRoomMessage.readFromString(NCMessage.OP_NICK, serialized);
        // Comparar ambos objetos
        assertEquals(message.getOpcode(), res.getOpcode());
        assertEquals(((NCRoomMessage) message).getMsg(), ((NCRoomMessage) res).getMsg());
    }

    @Test
    void TestRooms() throws ParseException {
        ArrayList<NCRoomInfoMessage> rooms = new ArrayList<NCRoomInfoMessage>();
        String rname = "Tesla";
        List<String> users = new ArrayList<String>();
        users.add("ElonMusk");
        users.add("Grimes");
        users.add("XÆA12");
        rooms.add(new NCRoomInfoMessage(NCMessage.OP_GET_INFO, rname, users, 0));
        NCRoomListMessage msg = (NCRoomListMessage) NCMessage.makeRoomListMessage(NCMessage.OP_ROOM_LIST, rooms);
        // Parseo
        String seriald = msg.toEncodedString();
        // read
        NCRoomListMessage res = NCRoomListMessage.readFromString(NCMessage.OP_ROOM_LIST, seriald);
        // Comparar
        assertEquals(msg.getOpcode(), res.getOpcode());
        for (NCRoomInfoMessage room : msg.getRooms()) {
            assertEquals(room.getOpcode(), NCMessage.OP_GET_INFO);
            assertEquals(room.members, users);
            assertEquals(room.roomName, rname);
            assertEquals(room.timeLastMessage, 0);
        }
    }

    @Test
    void TestRoomInfo() throws ParseException {
        List<String> users = new ArrayList<String>();
        users.add("ElonMusk");
        users.add("SteveJobs");
        NCRoomInfoMessage msg = (NCRoomInfoMessage) NCMessage.makeInfoMessage(NCMessage.OP_INFO, "Panas", users, 0);
        // Parseo
        String seriald = msg.toEncodedString();
        // read
        NCRoomInfoMessage res = NCRoomInfoMessage.readFromString(NCMessage.OP_INFO, seriald);
        // Comparar
        assertEquals(msg.getRoom(), res.getRoom());
        assertEquals(msg.getOpcode(), res.getOpcode());
        assertEquals(msg.getUsers(), res.getUsers());
        assertEquals(msg.getTime(), res.getTime());
    }

    @Test
    void TestExitMessage() {
        // Build
        NCImmediateMessage msg = (NCImmediateMessage) NCMessage.makeImmediateMessage(NCMessage.OP_EXIT);
        // ReadFromString
        NCMessage res = NCImmediateMessage.readFromString(NCMessage.OP_EXIT);
        // Assert
        assertEquals(msg.getOpcode(), res.getOpcode());

    }
}