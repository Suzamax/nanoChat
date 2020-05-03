import es.um.redes.nanoChat.messageFV.NCImmediateMessage;
import es.um.redes.nanoChat.messageFV.NCInfoMessage;
import es.um.redes.nanoChat.messageFV.NCMessage;
import es.um.redes.nanoChat.messageFV.NCRoomListMessage;
import es.um.redes.nanoChat.messageFV.NCRoomMessage;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
    void TestRooms() {
        List<String> rooms = new ArrayList<String>();
        rooms.add("SalaEstudio");
        rooms.add("SalaRecreo");
        NCRoomListMessage msg = (NCRoomListMessage) NCMessage.makeRoomListMessage(NCMessage.OP_ROOMLIST, rooms);
        // Parseo
        String seriald = msg.toEncodedString();
        // read
        NCMessage res = NCRoomListMessage.readFromString(NCMessage.OP_ROOMLIST, seriald);
        // Comparar
        assertEquals(msg.getOpcode(), res.getOpcode());
        assertEquals(((NCRoomListMessage) msg).getRooms(), ((NCRoomListMessage) res).getRooms());
    }

    @Test
    void TestRoomInfo() {
        List<String> users = new ArrayList<String>();
        users.add("ElonMusk");
        users.add("SteveJobs");
        NCInfoMessage msg = (NCInfoMessage) NCMessage.makeInfoMessage(NCMessage.OP_INFO, "Panas", users);
        // Parseo
        String seriald = msg.toEncodedString();
        // read
        NCMessage res = NCInfoMessage.readFromString(NCMessage.OP_INFO, seriald);
        // Comparar
        assertEquals(msg.getOpcode(), res.getOpcode());
        assertEquals(((NCInfoMessage) msg).getUsers(), ((NCInfoMessage) res).getUsers());
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