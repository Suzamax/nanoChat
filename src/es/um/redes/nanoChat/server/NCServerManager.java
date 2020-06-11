package es.um.redes.nanoChat.server;

import java.net.Socket;
import java.util.*;

import es.um.redes.nanoChat.server.roomManager.NCRoom;
import es.um.redes.nanoChat.messageFV.NCRoomInfoMessage;
import es.um.redes.nanoChat.server.roomManager.NCRoomManager;

/**
 * Esta clase contiene el estado general del servidor (sin la lógica relacionada con cada sala particular)
 */
class NCServerManager {

	//Primera habitación del servidor
	final static byte INITIAL_ROOM = 'A';
	final static String ROOM_PREFIX = "Room";
	//Siguiente habitación que se creará
	byte nextRoom;
	//Usuarios registrados en el servidor
	private Set<String> users = new HashSet<String>();
	//Habitaciones actuales asociadas a sus correspondientes RoomManagers
	private Map<String,NCRoomManager> rooms = new HashMap<String,NCRoomManager>();

	NCServerManager() {
		nextRoom = INITIAL_ROOM;
	}

	//Método para registrar un RoomManager
	public void registerRoomManager(NCRoomManager rm) {
		//// Dar soporte para que pueda haber más de una sala en el servidor
		String roomName = ROOM_PREFIX + (char) nextRoom;
		rooms.put(roomName, rm);
		this.nextRoom++;
		rm.setRoomName(roomName);
	}

	//Devuelve la descripción de las salas existentes
	public synchronized ArrayList<NCRoomInfoMessage> getRoomList() {
		ArrayList<NCRoomInfoMessage> roomList = new ArrayList<>();
		//// Pregunta a cada RoomManager cuál es la descripción actual de su sala
		//// Añade la información al ArrayList
		for (NCRoomManager room : rooms.values()) roomList.add(room.getDescription());
		return roomList;
	}


	//Intenta registrar al usuario en el servidor.
	public synchronized boolean addUser(String user) {
		//// Devuelve true si no hay otro usuario con su nombre
		if (this.users.contains(user)) return false;
		//// Devuelve false si ya hay un usuario con su nombre
		this.users.add(user);
		return true;
	}

	//Elimina al usuario del servidor
	public synchronized void removeUser(String user) {
		//// Elimina al usuario del servidor
		this.users.remove(user);
	}

	//Un usuario solicita acceso para entrar a una sala y registrar su conexión en ella
	public synchronized NCRoomManager enterRoom(String u, String room, Socket s) {
		//// Verificamos si la sala existe
		if (!this.rooms.containsKey(room)) {
			rooms.put(room, new NCRoom()); // Se crea sala, nada de errores
		}
		if (this.rooms.containsKey(room))
			return rooms.get(room);
		//// Decidimos qué hacer si la sala no existe (devolver error O crear la sala)
		//// Si la sala existe y si es aceptado en la sala entonces devolvemos el RoomManager de la sala
		return null; // Banned lol 😂👌
	}

	//Un usuario deja la sala en la que estaba
	public synchronized void leaveRoom(String u, String room) {
		//// Verificamos si la sala existe
		if (this.rooms.containsKey(room)) {
			rooms.get(room).removeUser(u);
			if (rooms.get(room).usersInRoom() == 0) // Miramos si no hay usuarios
				rooms.remove(room); // Negros ghanaeses bailando con un ataúd con la habitación
		}
		//// Si la sala existe sacamos al usuario de la sala
		//// Decidir qué hacer si la sala se queda vacía
	}
}
