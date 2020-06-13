package es.um.redes.nanoChat.messageFV;

public class NCBroadcastMessage extends NCMessage {

    private final boolean joinOrLeave;
    private final String user;

    static public final String USER_FIELD = "user";
    static public final String ACTION_FIELD = "action";
    static public final String JOINS_VALUE = "joined";
    static public final String LEAVES_VALUE = "left";


    private static final String STATUS_DELIMITER = ";";

    public NCBroadcastMessage(byte type, String user, boolean joinOrLeave) {
        this.opcode = type;
        this.user = user;
        this.joinOrLeave = joinOrLeave; // F = Leaves, T = Joins
    }


    @Override
    public String toEncodedString() {
        StringBuilder sb = new StringBuilder();
        sb.append(OPCODE_FIELD + DELIMITER)
                .append(opcodeToOperation(this.opcode))
                .append(END_LINE); //Construimos el campo
        sb.append(USER_FIELD + DELIMITER)
                .append(this.user)
                .append(END_LINE);
        sb.append(ACTION_FIELD + DELIMITER);
        if (joinOrLeave) sb.append(JOINS_VALUE);
        else sb.append(LEAVES_VALUE);
        sb.append(END_LINE);
        sb.append(END_LINE);  //Marcamos el final del mensaje
        return sb.toString(); //Se obtiene el mensaje
    }

    public static NCBroadcastMessage readFromString(byte code, String message) {
        String msg = "";
        String u = "";
        boolean jol = false;
        String[] lines = message.split(System.getProperty("line.separator"));

        int idx;
        String f, v;
        for (String l : lines) {
            idx = l.indexOf(DELIMITER); // Posición del delimitador
            f   = l.substring(0, idx).toLowerCase();
            v   = l.substring(idx + 1).trim();
            if (f.equalsIgnoreCase(USER_FIELD)) u = v;
            if (f.equalsIgnoreCase(ACTION_FIELD) && v.equalsIgnoreCase(JOINS_VALUE)) {
                jol = true;
            }
        }

        return new NCBroadcastMessage(code, u, jol);
    }

    public String getUser() {
        return user;
    }

    public boolean isJoinOrLeave() {
        return joinOrLeave;
    }
}
