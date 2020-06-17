package es.um.redes.nanoChat.messageFV;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


// 1
public class NCRoomInfoMessage extends NCMessage {
	//Campos de los que, al menos, se compone una descripción de una sala
	public String roomName;
	public List<String> members;
	public long timeLastMessage;

	private static final String USER_DELIMITER = ",";

	//Constructor a partir de los valores para los campos
	public NCRoomInfoMessage(byte type, String roomName, List<String> members, long timeLastMessage) {
		this.opcode = type;
		this.roomName = roomName;
		this.members = members;
		this.timeLastMessage = timeLastMessage;
	}

	//Método que devuelve una representación de la Descripción lista para ser impresa por pantalla
	public String toEncodedString() {
		StringBuilder sb = new StringBuilder();
		sb.append(OPCODE_FIELD + DELIMITER).append(opcodeToOperation(opcode)).append(END_LINE);
		sb
				.append("Room Name")
				.append(DELIMITER)
				.append(roomName)
				.append(END_LINE);
		sb
				.append("Members (")
				.append(members.size())
				.append(")")
				.append(DELIMITER);
		for (String member : members) {
			sb.append(member)
				.append(USER_DELIMITER);
		}
		sb
			.append(END_LINE);
		sb
			.append("Last message")
			.append(DELIMITER);
		if (timeLastMessage != 0)
			sb
				.append(new Date(timeLastMessage).toString());
		else
			sb
				.append("not yet");
		sb.append(END_LINE).append(END_LINE);
		return sb.toString();
	}

	// Esto hace lo contrario de lo de arriba :croak:
	public static NCRoomInfoMessage readFromString(byte code, String desc) throws ParseException {
		String roomName = "";
		ArrayList<String> members = new ArrayList<String>();
		long timeLastMessage = 0;
		String[] lines = desc.split(String.valueOf(END_LINE));
		String[] users;
		String f, v; // Campo, Valor.
		int idx;

		for (String line : lines) {
			idx = line.indexOf(DELIMITER);
			f = line.substring(0, idx).toLowerCase().trim();
			v = line.substring(idx + 1);

			if (f.equalsIgnoreCase("Room Name"))
				roomName = v;
			if (f.contains("members")) {
				users = v.trim().split(USER_DELIMITER);
				// Siempre va a haber un usuario que haga el info // if (users.length > 0)
				Collections.addAll(members, users);
			}
			if (f.equalsIgnoreCase("Last Message"))
				if (!v.equalsIgnoreCase("not yet"))
					timeLastMessage = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy",
							Locale.ENGLISH).parse(v).getTime();
		}
		return new NCRoomInfoMessage(code, roomName, members, timeLastMessage);
	}

	/**
	 * @return the room
	 */
	public String getRoom() {
		return roomName;
	}

	/**
	 * @return the users
	 */
	public List<String> getUsers() {
		return members;
	}

	public long getTime() {
		return timeLastMessage;
	}
}
