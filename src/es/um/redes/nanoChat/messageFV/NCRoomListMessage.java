package es.um.redes.nanoChat.messageFV;

public class NCRoomListMessage extends NCMessage {

    private String[] rooms;

    static protected final String NAME_FIELD = "roomlist";

    public NCRoomListMessage(byte type, String[] rooms) {
        this.opcode = type;
        this.rooms = rooms;
    }

    @Override
    public String toEncodedString() {
        StringBuffer sb = new StringBuffer();

        sb.append(OPCODE_FIELD + DELIMITER + opcodeToOperation(opcode) + END_LINE);
        sb.append(NAME_FIELD + DELIMITER);
        for (String room : rooms) sb.append(room + ',');
        sb.append(END_LINE + END_LINE);

        return sb.toString();
    }

    public static NCRoomListMessage readFromString(byte code, String message) {
        String[] lines = message.split(String.valueOf(END_LINE));
        String[] rooms = null;
        int idx = lines[1].indexOf(DELIMITER);
        String field = lines[1].substring(0, idx).toLowerCase();
        String value_raw = lines[1].substring(idx + 1).trim();
        String[] value = value_raw.split(String.valueOf(','));
        if (field.equalsIgnoreCase(NAME_FIELD)) 
            rooms = value;

        return new NCRoomListMessage(code, rooms);
    }

    /**
     * @return the users
     */
    public String[] getRooms() {
        return rooms;
    }
}
