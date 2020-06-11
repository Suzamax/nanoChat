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
    public static NCRoomSndRcvMessage readFromString(byte code, String message) {
        String msg = "";
        String u = "";
        String[] lines = message.split(System.getProperty("line.separator"));

        int idx;
        String f, v;
        for (String l : lines) {
            idx = l.indexOf(DELIMITER); // Posición del delimitador
            f   = l.substring(0, idx).toLowerCase();
            v   = l.substring(idx + 1).trim();
            if (f.equalsIgnoreCase(USER_FIELD)) u = v;
            if (f.equalsIgnoreCase(MSG_FIELD)) msg = v;
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
