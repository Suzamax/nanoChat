package es.um.redes.nanoChat.messageFV;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class NCInfoMessage extends NCMessage {

    private List<String> users;
    private String room;
    private long time;

    static protected final String NAME_FIELD = "info";
    static protected final String USER_FIELD = "users";
    static protected final String TIME_FIELD = "lastmsg";

    public NCInfoMessage(byte type, String room, List<String> users, long time) {
        this.opcode = type;
        this.users = users;
        this.room = room;
        this.time = time;
    }

    @Override
    public String toEncodedString() {
        StringBuffer sb = new StringBuffer();

        sb.append(OPCODE_FIELD + DELIMITER)
            .append(opcodeToOperation(opcode))
            .append(END_LINE);
        sb.append(NAME_FIELD + DELIMITER)
            .append(room)
            .append(END_LINE);
        sb.append(USER_FIELD + DELIMITER);
        ListIterator<String> lis = null;
        lis = users.listIterator();
        
        while (lis.hasNext()) {
            sb.append(lis.next());
            if (lis.hasNext()) sb.append(",");
            else sb.append(END_LINE);
        }

        sb.append(TIME_FIELD + DELIMITER)
            .append(this.time)
            .append(END_LINE);
        sb.append(END_LINE);

        return sb.toString();
    }

    public static NCInfoMessage readFromString(byte code, String message) {
        String[] lines = message.split(String.valueOf(END_LINE));
        List<String> users = new ArrayList<>();
        String room = "";
        long time = 0;
        int idx = lines[1].indexOf(DELIMITER);
        String field_room = lines[1].substring(0, idx).toLowerCase();
        String value_room = lines[1].substring(idx + 1).trim();
        idx = lines[2].indexOf(DELIMITER);
        String field_users = lines[2].substring(0, idx).toLowerCase();
        String[] value_users_raw = lines[2].substring(idx + 1).trim().split(String.valueOf(','));
        idx = lines[2].indexOf(DELIMITER);
        String field_time = lines[3].substring(0, idx).toLowerCase();
        Long value_time = Long.parseLong(lines[2].substring(idx+1).trim());
        List<String> value_users = new ArrayList<String>();
        Collections.addAll(value_users, value_users_raw);
        if (field_room.equalsIgnoreCase(NAME_FIELD)
                && field_users.equalsIgnoreCase(USER_FIELD)
                && field_time.equalsIgnoreCase(TIME_FIELD)) {
            room = value_room;
            users = value_users;
            time = value_time;
        }

        return new NCInfoMessage(code, room, users, time);
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

    public long getTime() {
        return time;
    }
}
