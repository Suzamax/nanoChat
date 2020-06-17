package es.um.redes.nanoChat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.ParseException;

import es.um.redes.nanoChat.messageFV.*;
import es.um.redes.nanoChat.server.roomManager.NCRoom;
import es.um.redes.nanoChat.server.roomManager.NCRoomManager;

/**
 * A new thread runs for each connected client
 */
public class NCServerThread extends Thread {
	
	private Socket socket = null;
	//Manager global compartido entre los Threads
	private final NCServerManager serverManager;
	//Input and Output Streams
	private DataInputStream dis;
	private DataOutputStream dos;
	//Usuario actual al que atiende este Thread
	String user;
	//RoomManager actual (dependerá de la sala a la que entre el usuario)
	NCRoomManager roomManager;
	//Sala actual
	String currentRoom;

	//Inicialización de la sala
	public NCServerThread(NCServerManager manager, Socket socket) throws IOException {
		super("NCServerThread");
		this.socket = socket;
		this.serverManager = manager;
	}

	//Main loop
	public void run() {
		try {
			//Se obtienen los streams a partir del Socket
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
			//En primer lugar hay que recibir y verificar el nick
			receiveAndVerifyNickname();
			//Mientras que la conexión esté activa entonces...
			while (true) {
				//// Obtenemos el mensaje que llega y analizamos su código de operación
				NCMessage message = NCMessage.readMessageFromSocket(dis);
				switch (message.getOpcode()) {
				//// 1) si se nos pide la lista de salas se envía llamando a sendRoomList();
					case NCMessage.OP_GET_ROOMS:
						sendRoomList();
						break;
				//// 2) Si se nos pide entrar en la sala entonces obtenemos el RoomManager de la sala,
					case NCMessage.OP_ENTER:
						if (message instanceof NCRoomMessage) {
							currentRoom = ((NCRoomMessage) message).getMsg();
							this.roomManager = serverManager.enterRoom(this.user, currentRoom, this.socket);
						}
				//// 2) notificamos al usuario que ha sido aceptado y procesamos mensajes con processRoomMessages()
						if (this.roomManager != null) {
							NCImmediateMessage ok =
									(NCImmediateMessage) NCMessage.makeImmediateMessage(NCMessage.OP_IN_ROOM);
							String okraw = ok.toEncodedString();
							this.dos.writeUTF(okraw);
							processRoomMessages();
						}
				//// 2) Si el usuario no es aceptado en la sala entonces se le notifica al cliente
						else {
							NCImmediateMessage nok =
									(NCImmediateMessage) NCMessage.makeImmediateMessage(NCMessage.OP_NO_ROOM);
							String nok2send = nok.toEncodedString();
							this.dos.writeUTF(nok2send);
						}
				}
			}
		} catch (Exception e) {
			//If an error occurs with the communications the user is removed from all the managers and the connection is closed
			System.out.println("* User " + user + " disconnected.");
			try {
				serverManager.leaveRoom(user, currentRoom);
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
			serverManager.removeUser(user);
		}
		finally {
			if (!socket.isClosed())
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		}
	}

	//Obtenemos el nick y solicitamos al ServerManager que verifique si está duplicado
	private void receiveAndVerifyNickname() throws IOException, ParseException {
		String nick;
		boolean isDup = true;
		while (isDup) { //- !socket.isClosed()) {
			NCRoomMessage nickMsg = (NCRoomMessage) NCMessage.readMessageFromSocket(dis); //this.dis.readUTF();
			nick = nickMsg.getMsg();
			/*String line = nick_raw.split(String.valueOf('\n'))[1];
			int idx = line.indexOf(':');
			user = line.substring(idx + 1).trim(); */
			NCImmediateMessage res = null;
			if (serverManager.addUser(nick)) {
				user = nick.trim();
				res = (NCImmediateMessage) NCMessage.makeImmediateMessage(NCMessage.OP_NICK_OK);
				isDup = false;
			}
			else res = (NCImmediateMessage) NCMessage.makeImmediateMessage(NCMessage.OP_NICK_DUP);
			String res_raw = res.toEncodedString();
			this.dos.writeUTF(res_raw);

		}
		//La lógica de nuestro programa nos obliga a que haya un nick registrado antes de proseguir
		//// Entramos en un bucle hasta comprobar que alguno de los nicks proporcionados no está duplicado
		//// Extraer el nick del mensaje
		//// Validar el nick utilizando el ServerManager - addUser()
		//// Contestar al cliente con el resultado (éxito o duplicado)
	}

	//Mandamos al cliente la lista de salas existentes
	private void sendRoomList() throws IOException {
		//// La lista de salas debe obtenerse a partir del RoomManager y después enviarse mediante su mensaje correspondiente
		NCRoomListMessage rl =
				(NCRoomListMessage) NCMessage
						.makeRoomListMessage(NCMessage.OP_ROOM_LIST, serverManager.getRoomList());
		String encodedRL = rl.toEncodedString();
		// Enviar un serverManager.getRoomList()
		this.dos.writeUTF(encodedRL);
	}

	private void processRoomMessages() throws IOException {
		//// Comprobamos los mensajes que llegan hasta que el usuario decida salir de la sala
		boolean exit = false;
		//// Se recibe el mensaje enviado por el usuario
		NCBroadcastMessage join = (NCBroadcastMessage) NCMessage.makeBroadcastMessage(NCMessage.OP_BROADCAST, this.user,
				true);
		this.dos.writeUTF(join.toEncodedString());
		NCMessage msg;
		while (!exit) {
			try {
				msg = NCMessage.readMessageFromSocket(this.dis);
				switch (msg.getOpcode()) {
					case NCMessage.OP_SEND:
						// Se broadcastea (?), el formato es NCRoomSndRcvMessage
						// (SndRcv básicamente es un formato para enviar y recibir)
						this.roomManager.broadcastMessage(((NCRoomSndRcvMessage) msg).getUser(), ((NCRoomSndRcvMessage) msg).getMsg());
						// easy gg
						break;
					case NCMessage.OP_GET_INFO:
						((NCRoom) this.roomManager).sendInfo(((NCRoom) this.roomManager).getUserSocket(this.user), this.currentRoom);
						// lmao should it work
						break;
					case NCMessage.OP_EXIT: // Para salir de la sala
						exit = true;
						serverManager.leaveRoom(this.user, this.currentRoom);
						break;
					case NCMessage.OP_SEND_PRIV: // Privados
						String sendTo = ((NCRoomSndRcvMessage) msg).getRcvr();
						((NCRoom) this.roomManager).sendMessage(((NCRoom) this.roomManager).getUserSocket(sendTo),
												((NCRoomSndRcvMessage) msg).getUser(),
												((NCRoomSndRcvMessage) msg).getMsg(),
											true);
						break;
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			//// Se analiza el código de operación del mensaje y se trata en consecuencia

		}
		System.out.println("An user has exited!");
	}
}
