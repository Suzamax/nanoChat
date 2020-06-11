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

	private static final String ROOM_DELIMITER = ";";
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
		sb.append("Room Name: ")
				.append(roomName)
				.append(END_LINE)
				.append("\t Members (")
				.append(members.size())
				.append(") : ");
		for (String member: members) {
			sb.append(member)
				.append(" ");
		}
		if (timeLastMessage != 0)
			sb
				.append(END_LINE).append("\tLast message: ")
				.append(new Date(timeLastMessage).toString());
		else
			sb.append("\tLast message: not yet");
		return sb.toString();
	}

	// Esto hace lo contrario de lo de arriba :croak:
	public static NCRoomInfoMessage readFromString(byte code, String desc) throws ParseException {
		String roomName = "";
		ArrayList<String> members = new ArrayList<String>();
		long timeLastMessage = 0;
		String[] lines = desc.split(ROOM_DELIMITER);
		String[] users;
		String f, v; // Campo, Valor.
		int idx;

		for (String line : lines) {
			idx = line.indexOf(DELIMITER);
			f = line.substring(0, idx).toLowerCase().trim();
			v = line.substring(idx + 1);

			if (f.equalsIgnoreCase("room name"))
				roomName = v;
			if (f.contains("members")) {
				users = v.trim().split(USER_DELIMITER);
				if (users.length > 0) Collections.addAll(members, users);
			}
			if (f.equalsIgnoreCase("last message"))
				if (!v.contains("not yet"))
					timeLastMessage = new SimpleDateFormat("MM/dd/yyyy - H:mm:ss", Locale.ENGLISH).parse(v).getTime();
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
