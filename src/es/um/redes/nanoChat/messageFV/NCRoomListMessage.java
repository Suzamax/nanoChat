package es.um.redes.nanoChat.messageFV;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class NCRoomListMessage extends NCMessage {

    private final List<String> rooms;

    static protected final String NAME_FIELD = "roomlist";

    public NCRoomListMessage(byte type, List<String> rooms) {
        this.opcode = type;
        this.rooms = rooms;
    }

    @Override
    public String toEncodedString() {
        StringBuffer sb = new StringBuffer();

        sb.append(OPCODE_FIELD + DELIMITER + opcodeToOperation(opcode) + END_LINE);
        sb.append(NAME_FIELD + DELIMITER);
        ListIterator<String> lis = null;
        lis = rooms.listIterator();
        
        while (lis.hasNext()) {
            sb.append(lis.next().trim());
            if (lis.hasNext()) sb.append(",");
            else sb.append(END_LINE);
        }
        sb.append(END_LINE);

        return sb.toString();
    }

    public static NCRoomListMessage readFromString(byte code, String message) {
        String[] lines = message.split(String.valueOf(END_LINE));
        List<String> rooms = null;
        int idx = lines[1].indexOf(DELIMITER);
        String field = lines[1].substring(0, idx).toLowerCase();
        String[] value_raw = lines[1].substring(idx + 1).trim().split(String.valueOf(","));
        List<String> value = new ArrayList<String>();
        Collections.addAll(value, value_raw); // for (String r : value_raw) value.add(r);
        if (field.equalsIgnoreCase(NAME_FIELD)) 
            rooms = value;

        return new NCRoomListMessage(code, rooms);
    }

    /**
     * @return the users
     */
    public List<String> getRooms() {
        return rooms;
    }
}
