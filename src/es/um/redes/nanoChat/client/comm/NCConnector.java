package es.um.redes.nanoChat.client.comm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Objects;

import es.um.redes.nanoChat.messageFV.*;
import es.um.redes.nanoChat.messageFV.NCRoomInfoMessage;
import es.um.redes.nanoChat.server.roomManager.NCRoom;

//Esta clase proporciona la funcionalidad necesaria para intercambiar mensajes entre el cliente y el servidor de NanoChat
public class NCConnector {
	private Socket socket;
	protected DataOutputStream dos;
	protected DataInputStream dis;
	
	public NCConnector(InetSocketAddress serverAddress) throws UnknownHostException, IOException {
		//// Se crea el socket a partir de la dirección proporcionada
		this.socket = new Socket(serverAddress.getAddress(), serverAddress.getPort());
		//// Se extraen los streams de entrada y salida
		this.dos = new DataOutputStream(socket.getOutputStream());
		this.dis = new DataInputStream(socket.getInputStream());
	}


	//Método para registrar el nick en el servidor. Nos informa sobre si la inscripción se hizo con éxito o no.
	public boolean registerNickname_UnformattedMessage(String nick) throws IOException {
		//Funcionamiento resumido: SEND(nick) and RCV(NICK_OK) or RCV(NICK_DUPLICATED)
		//// Enviamos una cadena con el nick por el flujo de salida
		this.dos.writeUTF(nick);
		String res = dis.readUTF();
		return res.equals("NICK_OK");
		//// Leemos la cadena recibida como respuesta por el flujo de entrada
		//// Si la cadena recibida es NICK_OK entonces no está duplicado (en función de ello modificar el return)
	}

	
	//Método para registrar el nick en el servidor. Nos informa sobre si la inscripción se hizo con éxito o no.
	public boolean registerNickname(String nick) throws IOException {
		//Funcionamiento resumido: SEND(nick) and RCV(NICK_OK) or RCV(NICK_DUPLICATED)
		//Creamos un mensaje de tipo RoomMessage con opcode OP_NICK en el que se inserte el nick
		NCRoomMessage message = (NCRoomMessage) NCMessage.makeRoomMessage(NCMessage.OP_NICK, nick);
		//Obtenemos el mensaje de texto listo para enviar
		String rawMessage = message.toEncodedString();
		//Escribimos el mensaje en el flujo de salida, es decir, provocamos que se envíe por la conexión TCP
		this.dos.writeUTF(rawMessage);
		//// Leemos el mensaje recibido como respuesta por el flujo de entrada
		String res = this.dis.readUTF();
		//// Analizamos el mensaje para saber si está duplicado el nick (modificar el return en consecuencia)
		String[] lines = res.split(String.valueOf('\n'));
		int idx = lines[0].indexOf(':'); // Posición del delimitador
		return lines[0].substring(idx + 1).trim().equals("Nick OK");
	}
	
	//Método para obtener la lista de salas del servidor
	public ArrayList<NCRoomInfoMessage> getRooms() throws IOException, ParseException {
		//Funcionamiento resumido: SND(GET_ROOMS) and RCV(ROOM_LIST)
		//// completar el método
		NCImmediateMessage msg_get_rooms =
			(NCImmediateMessage) NCMessage.makeImmediateMessage(NCMessage.OP_GET_ROOMS);
		String raw = msg_get_rooms.toEncodedString();
		this.dos.writeUTF(raw);

		NCRoomListMessage res = (NCRoomListMessage) NCMessage.readMessageFromSocket(dis);

		return (Objects.requireNonNull(res).getOpcode() == NCMessage.OP_ROOM_LIST) ? res.getRooms() : null;
	}
	
	//Método para solicitar la entrada en una sala
	public boolean enterRoom(String room) throws IOException, ParseException {
		// Resumen: SND(ENTER_ROOM <room>) && RCV(IN_ROOM) || RCV(REJECT)
		NCRoomMessage msg = (NCRoomMessage) NCMessage.makeRoomMessage(NCMessage.OP_ENTER, room);
		String strmsg = msg.toEncodedString();
		dos.writeUTF(strmsg);
		NCImmediateMessage res = (NCImmediateMessage) NCMessage.readMessageFromSocket(dis);
		return (res != null ? res.getOpcode() : 0) == NCMessage.OP_IN_ROOM;
	}
	
	//Método para salir de una sala
	public void leaveRoom(String room) throws IOException {
		//Funcionamiento resumido: SND(EXIT_ROOM)
		//// completar el método
		NCImmediateMessage exit = (NCImmediateMessage) NCMessage.makeImmediateMessage(NCMessage.OP_EXIT);
		this.dos.writeUTF(exit.toEncodedString());
	}
	
	//Método que utiliza el Shell para ver si hay datos en el flujo de entrada
	public boolean isDataAvailable() throws IOException {
		return (dis.available() != 0);
	}
	
	//IMPORTANTE!!
	//// Es necesario implementar métodos para recibir y enviar mensajes de chat a una sala
	
	//Método para pedir la descripción de una sala
	public NCRoomInfoMessage getRoomInfo(String room) throws IOException, ParseException {
		//Funcionamiento resumido: SND(GET_ROOMINFO) and RCV(ROOMINFO)
		//// Construimos el mensaje de solicitud de información de la sala específica
		NCRoomMessage msg = (NCRoomMessage) NCMessage.makeRoomMessage(NCMessage.OP_GET_INFO, room);
		this.dos.writeUTF(msg.toEncodedString());
		//// Recibimos el mensaje de respuesta
		NCRoomInfoMessage res = (NCRoomInfoMessage) NCMessage.readMessageFromSocket(dis);
		//// Devolvemos la descripción contenida en el mensaje
		//NCRoomInfoMessage rd = new NCRoomInfoMessage(NCMessage.OP_INFO, res.getRoom(), res.getUsers(), res.getTime());
		return res;
	}

	// Método para enviar mensajes en una sala
	public void sendMsg(String u, String m) throws IOException {
		NCRoomSndRcvMessage msg = (NCRoomSndRcvMessage) NCMessage.makeMessage(NCMessage.OP_SEND, u, m, false);
		this.dos.writeUTF(msg.toEncodedString());
	}


	// Método para enviar mensajes a un usuario de la sala
	public void sendPriv(String u, String d, String m) throws IOException {
		NCRoomSndRcvMessage msg = (NCRoomSndRcvMessage) NCMessage.makePrivMessage(NCMessage.OP_SEND_PRIV, u, d, m, true);
		this.dos.writeUTF(msg.toEncodedString());
	}

	// Igual pero para recibir ya sea público o privado
	public NCMessage rcvMsg() throws IOException, ParseException {
		return NCMessage.readMessageFromSocket(this.dis);
	}
	
	//Método para cerrar la comunicación con la sala
	// ? OPTODO (Opcional) Enviar un mensaje de salida del servidor de Chat
	public void disconnect() {
		try {
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
		} finally {
			socket = null;
		}
	}

}
