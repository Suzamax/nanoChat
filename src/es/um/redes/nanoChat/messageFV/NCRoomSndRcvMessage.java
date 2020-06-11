package es.um.redes.nanoChat.messageFV;

public class NCRoomSndRcvMessage extends NCMessage {

    private String user;
    private String msg;

    //Campo específico de este tipo de mensaje
    static protected final String USER_FIELD = "user";
    static protected final String MSG_FIELD = "msg";

    static protected final String UM_DELIM = ";";
    /**
     * Creamos un mensaje de tipo Room a partir del código de operación y del nombre
     */
    public NCRoomSndRcvMessage(byte type, String user, String msg) {
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
    public static NCRoomSndRcvMessage readFromString(byte code, String usr, String message) {
        String msg = "";
        String u = "";
        int idx_u = usr.indexOf(DELIMITER); // Posición del delimitador
        int idx_m = message.indexOf(DELIMITER); // Posición del delimitador
        String field_user = usr.substring(0, idx_u).toLowerCase();                                                                                                                                                // minúsculas
        String value_user = usr.substring(idx_u + 1).trim();
        String field_msg = message.substring(0, idx_m).toLowerCase();                                                                                                                                                // minúsculas
        String value_msg = message.substring(idx_m + 1).trim();
        if (field_msg.equalsIgnoreCase(MSG_FIELD)
            && field_user.equalsIgnoreCase(USER_FIELD)) {
            u = value_user;
            msg = value_msg;
        }

        return new NCRoomSndRcvMessage(code, u, msg);
    }

    public String getMsg() {
        return msg;
    }

    public String getUser() {
        return user;
    }
}
