package es.um.redes.nanoChat.messageFV;

import java.util.Date;

public class NCBroadcastMessage extends NCMessage {

    private final boolean joinOrLeave;
    private final String user;

    static public final String JOINS = "joins";

    private static final String STATUS_DELIMITER = ";";

    public NCBroadcastMessage(byte type, String user, boolean joinOrLeave) {
        this.opcode = type;
        this.user = user;
        this.joinOrLeave = joinOrLeave; // F = Leaves, T = Joins
    }


    @Override
    public String toEncodedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Broadcast: ")
                .append(user);

        if (this.joinOrLeave)
            sb.append(" joins.");
        else
            sb.append(" left.");
        sb.append(END_LINE).append(END_LINE);
        return sb.toString();
    }

    public static NCBroadcastMessage readFromString(byte code, String str) {
        int idx = str.indexOf(DELIMITER);
        boolean joins = false;
        String f = str.substring(0, idx).toLowerCase().trim();
        String v = str.substring(idx + 1);
        idx = v.indexOf(STATUS_DELIMITER);
        String u = v.substring(0, idx).trim();
        String status = v.substring(idx + 1).trim();
        if (status.equalsIgnoreCase(JOINS)) joins = true;

        return new NCBroadcastMessage(code, u, joins);
    }

    public String getUser() {
        return user;
    }

    public boolean isJoinOrLeave() {
        return joinOrLeave;
    }
}
