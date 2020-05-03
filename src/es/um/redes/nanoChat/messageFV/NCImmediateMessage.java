package es.um.redes.nanoChat.messageFV;

public class NCImmediateMessage extends NCMessage{

    public NCImmediateMessage(byte type) {
        this.opcode = type;
    }

    @Override
    public String toEncodedString() {
        StringBuffer sb = new StringBuffer();

        sb.append(OPCODE_FIELD + DELIMITER + opcodeToOperation(opcode) + END_LINE);
        sb.append(END_LINE);

        return sb.toString();
    }

    public static NCImmediateMessage readFromString(byte code) {
        return new NCImmediateMessage(code);
    }

}