package es.um.redes.nanoChat.messageFV;

import es.um.redes.nanoChat.server.roomManager.NCRoomDescription;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class NCRoomListMessage extends NCMessage {

    private final List<NCRoomDescription> rooms;

    static protected final String NAME_FIELD = "roomlist";
    static protected final String USER_FIELD = "users";
    static protected final String LAST_MSG = "lastmsg";

    public NCRoomListMessage(byte type, List<NCRoomDescription> rooms) {
        this.opcode = type;
        this.rooms = rooms;
    }

    @Override
    public String toEncodedString() {
        StringBuffer sb = new StringBuffer();

        sb.append(OPCODE_FIELD + DELIMITER).append(opcodeToOperation(opcode)).append(END_LINE);
        sb.append(NAME_FIELD + DELIMITER);

        for (NCRoomDescription current : rooms) {
            sb.append(current.roomName).append(END_LINE).append(USER_FIELD + DELIMITER);
            ListIterator<String> name = current.members.listIterator();
            while (name.hasNext()) {
                sb.append(name.next());
                if (name.hasNext()) sb.append(',');
            }
            sb.append(END_LINE);
            sb.append(LAST_MSG + DELIMITER);
            sb.append(current.timeLastMessage);
            sb.append(END_LINE);
        }
        sb.append(END_LINE);

        return sb.toString();
    }

    public static NCRoomListMessage readFromString(byte code, String message) {
        String[] lines = message.split(System.getProperty("line.separator"));
        List<NCRoomDescription> ds = new ArrayList<>();
        int idx;
        List<String> users = new ArrayList<>();
        String[] user_raw;
        String f, v, room = null;
        long time = 0;
        for (String l : lines) {
            idx = l.indexOf(DELIMITER);
            f = l.substring(0, idx).toLowerCase();
            v = l.substring(idx+1).trim();
            switch (f) {
                case NAME_FIELD:
                    room = v;
                    break;
                case USER_FIELD:
                    user_raw = v.trim().split(String.valueOf(','));
                    Collections.addAll(users, user_raw);
                    break;
                case LAST_MSG:
                    time = Long.parseLong(v);
                    break;
            }
        }

        ds.add(new NCRoomDescription(room, users, time));
        return new NCRoomListMessage(code, ds);
    }

    /**
     * @return the users
     */
    public List<NCRoomDescription> getRooms() {
        return rooms;
    }
}
