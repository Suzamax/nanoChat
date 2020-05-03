package es.um.redes.nanoChat.messageFV;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class NCInfoMessage extends NCMessage {

    private List<String> users;
    private String room;

    static protected final String NAME_FIELD = "info";
    static protected final String USER_FIELD = "users";

    public NCInfoMessage(byte type, String room, List<String> users) {
        this.opcode = type;
        this.users = users;
        this.room = room;
    }

    @Override
    public String toEncodedString() {
        StringBuffer sb = new StringBuffer();

        sb.append(OPCODE_FIELD + DELIMITER + opcodeToOperation(opcode) + END_LINE);
        sb.append(NAME_FIELD + DELIMITER + room + END_LINE);
        sb.append(USER_FIELD + DELIMITER);
        ListIterator<String> lis = null;
        lis = users.listIterator();
        
        while (lis.hasNext()) {
            sb.append(lis.next());
            if (lis.hasNext()) sb.append(",");
            else sb.append(END_LINE);

        }
        sb.append(END_LINE);

        return sb.toString();
    }

    public static NCInfoMessage readFromString(byte code, String message) {
        String[] lines = message.split(String.valueOf(END_LINE));
        List<String> users = null;
        String room = null;
        int idx = lines[1].indexOf(DELIMITER);
        String field_room = lines[1].substring(0, idx).toLowerCase();
        String value_room = lines[1].substring(idx + 1).trim();
        idx = lines[2].indexOf(DELIMITER);
        String field_users = lines[2].substring(0, idx).toLowerCase();
        String[] value_users_raw = lines[2].substring(idx + 1).trim().split(String.valueOf(','));
        List<String> value_users = new ArrayList<String>();
        for (String u : value_users_raw) value_users.add(u);
        if (field_room.equalsIgnoreCase(NAME_FIELD) && field_users.equalsIgnoreCase(USER_FIELD)) {
            room = value_room;
            users = value_users;
        }

        return new NCInfoMessage(code, room, users);
    }

    /**
     * @return the room
     */
    public String getRoom() {
        return room;
    }

    /**
     * @return the users
     */
    public List<String> getUsers() {
        return users;
    }
}
