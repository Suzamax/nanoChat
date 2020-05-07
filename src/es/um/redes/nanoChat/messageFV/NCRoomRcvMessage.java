package es.um.redes.nanoChat.messageFV;

public class NCRoomRcvMessage extends NCMessage {

    private String user;
    private String msg;

    //Campo específico de este tipo de mensaje
    static protected final String USER_FIELD = "user";
    static protected final String MSG_FIELD = "msg";

    /**
     * Creamos un mensaje de tipo Room a partir del código de operación y del nombre
     */
    public NCRoomRcvMessage(byte type, String user, String msg) {
        this.opcode = type;
        this.user = user;
        this.msg = msg;
    }

    //Pasamos los campos del mensaje a la codificación correcta en field:value
    @Override
    public String toEncodedString() {
        StringBuffer sb = new StringBuffer();
        sb.append(OPCODE_FIELD + DELIMITER)
            .append(opcodeToOperation(this.opcode))
            .append(END_LINE); //Construimos el campo
        sb.append(USER_FIELD + DELIMITER)
            .append(this.user)
            .append(END_LINE);
        sb.append(MSG_FIELD + DELIMITER)
            .append(this.msg)
            .append(END_LINE); //Construimos el campo
        sb.append(END_LINE);  //Marcamos el final del mensaje
        return sb.toString(); //Se obtiene el mensaje
    }

    //Parseamos el mensaje contenido en message con el fin de obtener los distintos campos
    public static NCRoomRcvMessage readFromString(byte code, String message) {
        String[] lines = message.split(String.valueOf(END_LINE));
        String msg = "";
        String user = "";
        int idx = lines[1].indexOf(DELIMITER); // Posición del delimitador
        String field_user = lines[1].substring(0, idx).toLowerCase();                                                                                                                                                // minúsculas
        String value_user = lines[1].substring(idx + 1).trim();
        String field_msg = lines[1].substring(0, idx).toLowerCase();                                                                                                                                                // minúsculas
        String value_msg = lines[1].substring(idx + 1).trim();
        if (field_msg.equalsIgnoreCase(MSG_FIELD)
            && field_user.equalsIgnoreCase(USER_FIELD)) {
            user = value_user;
            msg = value_msg;
        }

        return new NCRoomRcvMessage(code, user, msg);
    }

    public String getMsg() {
        return msg;
    }

    public String getUser() {
        return user;
    }
}
