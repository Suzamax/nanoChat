package es.um.redes.nanoChat.messageFV;

public class NCSendMessage extends NCMessage {

    private String msg;

    static protected final String NAME_FIELD = "message";

    public NCSendMessage(byte type, String msg) {
        this.opcode = type;
        this.msg = msg;
    }

    @Override
    public String toEncodedString() {
        StringBuffer sb = new StringBuffer();

        sb.append(OPCODE_FIELD + DELIMITER + opcodeToOperation(opcode) + END_LINE);
        sb.append(NAME_FIELD + DELIMITER + msg + END_LINE);
        sb.append(END_LINE);

        return sb.toString();
    }

    public static NCSendMessage readFromString(byte code, String message) {
        String[] lines = message.split(String.valueOf(END_LINE));
        String msg = null;
        int idx = lines[1].indexOf(DELIMITER);
        String field = lines[1].substring(0, idx).toLowerCase();
        String value = lines[1].substring(idx + 1).trim();
        if (field.equalsIgnoreCase(NAME_FIELD))
            msg = value;

        return new NCSendMessage(code, msg);
    }

    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }
}
