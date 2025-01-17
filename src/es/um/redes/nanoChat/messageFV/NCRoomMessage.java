package es.um.redes.nanoChat.messageFV;

/*
 * ROOM
----

operation:<operation>
name:<name>

Defined operations:
Nick
*/

public class NCRoomMessage extends NCMessage {

	private String msg;
	
	//Campo específico de este tipo de mensaje
	static protected final String NAME_FIELD = "msg";

	/**
	 * Creamos un mensaje de tipo Room a partir del código de operación y del nombre
	 */
	public NCRoomMessage(byte type, String msg) {
		this.opcode = type;
		this.msg = msg;
	}

	//Pasamos los campos del mensaje a la codificación correcta en field:value
	@Override
	public String toEncodedString() {
		StringBuffer sb = new StringBuffer();			
		sb.append(OPCODE_FIELD+DELIMITER+opcodeToOperation(opcode)+END_LINE); //Construimos el campo
		sb.append(NAME_FIELD+DELIMITER+msg+END_LINE); //Construimos el campo
		sb.append(END_LINE);  //Marcamos el final del mensaje
		return sb.toString(); //Se obtiene el mensaje
	}

	//Parseamos el mensaje contenido en message con el fin de obtener los distintos campos
	public static NCRoomMessage readFromString(byte code, String message) {
		String[] lines = message.split(String.valueOf(END_LINE));
		String msg = null;
		int idx = lines[1].indexOf(DELIMITER); // Posición del delimitador
		String field = lines[1].substring(0, idx).toLowerCase();                                                                                                                                                // minúsculas
		String value = lines[1].substring(idx + 1).trim();
		if (field.equalsIgnoreCase(NAME_FIELD))
			msg = value;

		return new NCRoomMessage(code, msg);
	}

	public String getMsg() {
		return msg;
	}

}
