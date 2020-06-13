package es.um.redes.nanoChat.messageFV;

public class NCRoomSndRcvMessage extends NCMessage {

    private String user;
    private String rcvr;
    private boolean priv;
    private String msg;

    //Campo específico de este tipo de mensaje
    static protected final String USER_FIELD = "user";
    static protected final String MSG_FIELD = "msg";
    static protected final String DST_FIELD = "receiver";
    static protected final String PRIV_FIELD = "private";

    /**
     * Creamos un mensaje de tipo Room a partir del código de operación y del nombre
     */
    public NCRoomSndRcvMessage(byte type, String user, String msg, boolean priv) {
        this.opcode = type;
        this.user = user;
        this.msg = msg;
        this.priv = priv;
        this.rcvr = null;
    }

    // Si hay tres string es que es mensaje privado
    public NCRoomSndRcvMessage(byte type, String user, String dst, String msg, boolean priv) {
        this.opcode = type;
        this.user = user;
        this.rcvr = dst;
        this.priv = priv; // Siempre true lmao
        this.msg = msg;
    }

    //Pasamos los campos del mensaje a la codificación correcta en field:value
    @Override
    public String toEncodedString() {
        StringBuffer sb = new StringBuffer();
        sb.append(OPCODE_FIELD + DELIMITER)
            .append(opcodeToOperation(this.opcode))
            .append(END_LINE); //Construimos el campo
        if (this.priv) {
            if (this.rcvr != null)
                sb.append(DST_FIELD + DELIMITER)
                        .append(this.rcvr)
                        .append(END_LINE);
            else sb.append(PRIV_FIELD + DELIMITER)
                        .append("true")
                        .append(END_LINE);
        }
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
        String rcv = null;
        boolean prv = false;
        String[] lines = message.split(System.getProperty("line.separator"));

        int idx;
        String f, v;
        for (String l : lines) {
            idx = l.indexOf(DELIMITER); // Posición del delimitador
            f   = l.substring(0, idx).toLowerCase();
            v   = l.substring(idx + 1).trim();
            if (f.equalsIgnoreCase(USER_FIELD)) u = v;
            if (f.equalsIgnoreCase(PRIV_FIELD)) prv = true;
            if (f.equalsIgnoreCase(DST_FIELD)) rcv = v;
            if (f.equalsIgnoreCase(MSG_FIELD)) msg = v;
        }
        if (rcv != null) return new NCRoomSndRcvMessage(code, u, rcv, msg, true);
        return new NCRoomSndRcvMessage(code, u, msg, prv);
    }

    public String getMsg() {
        return msg;
    }

    public String getUser() {
        return user;
    }

    public String getRcvr() {
        return rcvr;
    }

    public boolean isPriv() {
        return priv;
    }
}
