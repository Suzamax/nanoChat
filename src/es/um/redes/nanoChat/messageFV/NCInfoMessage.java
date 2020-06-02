package es.um.redes.nanoChat.messageFV;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class NCInfoMessage extends NCMessage {

    private List<String> users;
    private String room;
    private long time;

    static protected final String NAME_FIELD = "room name";
    static protected final String USER_FIELD = "members";
    static protected final String TIME_FIELD = "last message";

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
        String roomName = "";
        String[] u_raw;
        long time = 0;
        int idx;
        String f, v;

        for (String line : lines) {
            idx = line.indexOf(DELIMITER);
            f = line.substring(0, idx).toLowerCase().trim();
            v = line.substring(idx + 1).trim();

            if (f.equalsIgnoreCase(NAME_FIELD))
                roomName = v;
            if (f.equalsIgnoreCase(USER_FIELD)) {
                u_raw = v.split(",");
                if (u_raw.length > 0) Collections.addAll(users, u_raw);
            }
            if (f.equalsIgnoreCase(TIME_FIELD))
                if (!v.contains("not yet")) {
                    try {
                        time = new SimpleDateFormat("MM/dd/yyyy - H:mm:ss", Locale.ENGLISH).parse(v).getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
        }

        return new NCInfoMessage(code, roomName, users, time);
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
