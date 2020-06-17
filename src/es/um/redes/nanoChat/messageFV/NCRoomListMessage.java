package es.um.redes.nanoChat.messageFV;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.ListIterator;

public class NCRoomListMessage extends NCMessage {

    private final ArrayList<NCRoomInfoMessage> rooms;

    static public final String NAME_FIELD = "room name";
    static public final String USER_FIELD = "members";
    static public final String LAST_MSG   = "last message";

    static public final String ROOM_DELIM = ";";

    public NCRoomListMessage(byte type, ArrayList<NCRoomInfoMessage> rooms) {
        this.opcode = type;
        this.rooms = new ArrayList<>(rooms);
    }

    @Override
    public String toEncodedString() {
        StringBuilder sb = new StringBuilder();

        sb.append(OPCODE_FIELD + DELIMITER).append(opcodeToOperation(opcode)).append(END_LINE);

        for (NCRoomInfoMessage current : rooms) {
            sb.append(NAME_FIELD + DELIMITER);
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
        sb.append(END_LINE);

        return sb.toString();
    }

    public static NCRoomListMessage readFromString(byte code, String message) {
        ArrayList<NCRoomInfoMessage> rooms = new ArrayList<>();
        HashMap<String, ArrayList<String>> users = new HashMap<>();
        ArrayList<String> users_aux;

        String[] lines = message.split(System.getProperty("line.separator"));
        String[] user_raw;
        int idx;
        String f, v, room = "";
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
                    if (!v.isEmpty()) { // Para no devolver un fantasma
                        users_aux = new ArrayList<>();
                        user_raw = v.trim().split(String.valueOf(','));
                        Collections.addAll(users_aux, user_raw);
                        users.put(room, users_aux);
                    }
                    break;
                case LAST_MSG:
                    time = Long.parseLong(v);
                    rooms.add(new NCRoomInfoMessage(NCMessage.OP_INFO, room, users.get(room), time));
                    break;
            }

        }

        return new NCRoomListMessage(code, rooms);
    }

    /**
     * @return the users
     */
    public ArrayList<NCRoomInfoMessage> getRooms() {
        return rooms;
    }
}
