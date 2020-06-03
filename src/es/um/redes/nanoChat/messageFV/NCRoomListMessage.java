package es.um.redes.nanoChat.messageFV;

import es.um.redes.nanoChat.server.roomManager.NCRoomDescription;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ListIterator;

public class NCRoomListMessage extends NCMessage {

    private final ArrayList<NCRoomDescription> rooms;

    static public final String NAME_FIELD = "room name";
    static public final String USER_FIELD = "members";
    static public final String LAST_MSG = "last message";

    public NCRoomListMessage(byte type, ArrayList<NCRoomDescription> rooms) {
        this.opcode = type;
        this.rooms = new ArrayList<NCRoomDescription>(rooms);
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
        sb.append(END_LINE);

        return sb.toString();
    }

    public static NCRoomListMessage readFromString(byte code, String message) throws ParseException {
        ArrayList<String> users = new ArrayList<>();

        String[] lines = message.split(System.getProperty("line.separator"));
        String[] user_raw;
        ArrayList<NCRoomDescription> ds = new ArrayList<>();
        int idx; //= lines[1].indexOf(DELIMITER);
        String f, v, room = "";
        long time = 0;

        /*
        f = lines[1].substring(0, idx).toLowerCase();
        v = lines[1].substring(idx + 1).trim();

        sb.append(v).append(END_LINE);
        for (int i = 2; i < lines.length; i++)
            sb.append(lines[i]).append(END_LINE);
        v = sb.toString();

        if (f.equalsIgnoreCase(NAME_FIELD)) {
            String[] descriptions = v.split("\n");
            list = new ArrayList<>(descriptions.length);
            for (String description : descriptions)
                list.add(NCRoomDescription.toRoomDescription(description));
        } */

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
        /*String[] lines = message.split(System.getProperty("line.separator"));
        ArrayList<NCRoomDescription> ds = new ArrayList<>();
        int idx;
        ArrayList<String> users = new ArrayList<>();
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
    }*/

    /**
     * @return the users
     */
    public ArrayList<NCRoomDescription> getRooms() {
        return rooms;
    }
}
