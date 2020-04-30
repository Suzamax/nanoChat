package es.um.redes.nanoChat.messageFV;

public class NCExitMessage extends NCMessage {

    static protected final String NAME_FIELD = "exit";

    public NCExitMessage(byte type) {
        this.opcode = type;
    }

    @Override
    public String toEncodedString() {
        StringBuffer sb = new StringBuffer();

        sb.append(OPCODE_FIELD + DELIMITER + opcodeToOperation(opcode) + END_LINE);
        sb.append(END_LINE);

        return sb.toString();
    }

    public static NCExitMessage readFromString(byte code) {
        return new NCExitMessage(code);
    }
}
