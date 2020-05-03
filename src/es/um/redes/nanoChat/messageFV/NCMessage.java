package es.um.redes.nanoChat.messageFV;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public abstract class NCMessage {
	protected byte opcode;

	////? Implementar el resto de los opcodes para los distintos mensajes
	public static final byte OP_INVALID_CODE = 0;
	public static final byte OP_NICK = 1;
	public static final byte OP_GET_ROOMS = 2;
	public static final byte OP_ROOMLIST = 3;
	public static final byte OP_ENTER = 4;
	public static final byte OP_IN_ROOM = 5;
	public static final byte OP_SEND = 6;
	public static final byte OP_EXIT = 7;
	public static final byte OP_INFO = 8;
	public static final byte OP_NICK_OK = 9;
	public static final byte OP_NICK_DUP = 10;

	//Constantes con los delimitadores de los mensajes de field:value
	public static final char DELIMITER = ':';    //Define el delimitador
	public static final char END_LINE = '\n';    //Define el carácter de fin de línea

	public static final String OPCODE_FIELD = "operation";

	/**
	 * Códigos de los opcodes válidos  El orden
	 * es importante para relacionarlos con la cadena
	 * que aparece en los mensajes
	 */
	private static final Byte[] _valid_opcodes = {
		OP_NICK, //
		OP_GET_ROOMS, //
		OP_ROOMLIST, //
		OP_ENTER, //
		OP_IN_ROOM, //
		OP_SEND, // 
		OP_EXIT, //
		OP_INFO, //
		OP_NICK_OK, //
		OP_NICK_DUP, //
	};

	/**
	 * cadena exacta de cada orden
	 */
	private static final String[] _valid_operations_str = {
		"Nick",
		"Get Rooms",
		"Room List",
		"Enter",
		"In room",
		"Send",
		"Exit",
		"Info",
		"Nick OK",
		"Nick Duplicated"
	};

	private static final Map<String, Byte> _operation_to_opcode;
	private static final Map<Byte, String> _opcode_to_operation;
	
	static {
		_operation_to_opcode = new TreeMap<>();
		_opcode_to_operation = new TreeMap<>();
		for (int i = 0 ; i < _valid_operations_str.length; ++i)
		{
			_operation_to_opcode.put(_valid_operations_str[i].toLowerCase(), _valid_opcodes[i]);
			_opcode_to_operation.put(_valid_opcodes[i], _valid_operations_str[i]);
		}
	}
	
	/**
	 * Transforma una cadena en el opcode correspondiente
	 */
	protected static byte operationToOpcode(String opStr) {
		return _operation_to_opcode.getOrDefault(opStr.toLowerCase(), OP_INVALID_CODE);
	}

	/**
	 * Transforma un opcode en la cadena correspondiente
	 */
	protected static String opcodeToOperation(byte opcode) {
		return _opcode_to_operation.getOrDefault(opcode, null);
	}

	//Devuelve el opcode del mensaje
	public byte getOpcode() {
		return opcode;
	}

	//Método que debe ser implementado específicamente por cada subclase de NCMessage
	protected abstract String toEncodedString();

	//Extrae la operación del mensaje entrante y usa la subclase para parsear el resto del mensaje
	public static NCMessage readMessageFromSocket(DataInputStream dis) throws IOException {
		String message = dis.readUTF();
		String[] lines = message.split(String.valueOf(END_LINE));
		if (!lines[0].isEmpty()) { // Si la línea no está vacía
			int idx = lines[0].indexOf(DELIMITER); // Posición del delimitador
			String field = lines[0].substring(0, idx).toLowerCase(); // minúsculas
			String value = lines[0].substring(idx + 1).trim();
			if (!field.equalsIgnoreCase(OPCODE_FIELD))
				return null;
			byte code = operationToOpcode(value);
			switch (code) {
				case OP_NICK:
				case OP_SEND:
				case OP_ENTER:
					return NCRoomMessage.readFromString(code, message);
				case OP_GET_ROOMS:
				case OP_EXIT:
				case OP_NICK_OK:
				case OP_NICK_DUP:
					return NCImmediateMessage.readFromString(code);
				case OP_ROOMLIST:
					return NCRoomListMessage.readFromString(code, message);
				case OP_INFO:
					return NCInfoMessage.readFromString(code, message);
				case OP_INVALID_CODE:
				default:
					System.err.println("Unknown or invalid message type received:" + code);
					return null;
			}
		} else
			return null;
	}


	//Método para construir un mensaje de tipo RoomList a partir del opcode y del nombre
	public static NCMessage makeRoomListMessage(byte code, List<String> rooms) {
		return new NCRoomListMessage(code, rooms);
	}

	//Método para crear un mensaje Room proveyendo un nombre de sala, un nick o un mensaje
	public static NCMessage makeRoomMessage(byte code, String name) {
		return new NCRoomMessage(code, name);
	}

	// Método para construir un mensaje de tipo Info a partir del opcode, la sala dada y los usuarios en ello
	public static NCMessage makeInfoMessage(byte code, String room, List<String> users) {
		return new NCInfoMessage(code, room, users);
	}

	// Método para construir un mensaje inmediato con el opcode de salida, confirmación
	// o denegación de nick, u obtención de salas
	public static NCMessage makeImmediateMessage(byte code) {
		return new NCImmediateMessage(code);
	}
}
