package es.um.redes.nanoChat.server.roomManager;

import es.um.redes.nanoChat.messageFV.NCRoomInfoMessage;

import java.io.IOException;
import java.net.Socket;

public abstract class NCRoomManager {
	String roomName;

	//Método para registrar a un usuario u en una sala (se anota también su socket de comunicación)
	public abstract boolean registerUser(String u, Socket s) throws IOException;
	//Método para hacer llegar un mensaje enviado por un usuario u
	public abstract void broadcastMessage(String u, String message) throws IOException;
	//Método para eliminar un usuario de una sala
	public abstract void removeUser(String u) throws IOException;
	//Método para nombrar una sala
	public abstract void setRoomName(String roomName);
	//Método para devolver la descripción del estado actual de la sala
	public abstract NCRoomInfoMessage getDescription();

	//Método para devolver el número de usuarios conectados a una sala
	public abstract int usersInRoom();

	// public abstract void sendMessage(Socket s, String u, String msg, boolean priv) throws IOException;

	// public abstract Socket getUserSocket(String u);



}
