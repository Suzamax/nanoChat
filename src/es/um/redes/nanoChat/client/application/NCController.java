package es.um.redes.nanoChat.client.application;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import es.um.redes.nanoChat.client.comm.NCConnector;
import es.um.redes.nanoChat.client.shell.NCCommands;
import es.um.redes.nanoChat.client.shell.NCShell;
import es.um.redes.nanoChat.directory.connector.DirectoryConnector;
import es.um.redes.nanoChat.messageFV.NCBroadcastMessage;
import es.um.redes.nanoChat.messageFV.NCMessage;
import es.um.redes.nanoChat.messageFV.NCRoomSndRcvMessage;
import es.um.redes.nanoChat.messageFV.NCRoomInfoMessage;

public class NCController {

	// Como molo, imprimo los mensajes en colorines :-)
	public static final String ANSI_BLUE = "\u001B[34m"; // Privados
	public static final String ANSI_GREEN = "\u001B[32m"; // Servidor
	public static final String ANSI_RESET = "\u001B[0m";


	//Diferentes estados del cliente de acuerdo con el autómata
	private static final byte PRE_CONNECTION = 1;
	private static final byte PRE_REGISTRATION = 2;
	//Código de protocolo implementado por este cliente
	private static final int PROTOCOL = 78375777;
	//Conector para enviar y recibir mensajes del directorio
	private DirectoryConnector directoryConnector;
	//Conector para enviar y recibir mensajes con el servidor de NanoChat
	private NCConnector ncConnector;
	//Shell para leer comandos de usuario de la entrada estándar
	private final NCShell shell;
	//Último comando proporcionado por el usuario
	private byte currentCommand;
	//Nick del usuario
	private String nickname;
	//Sala de chat en la que se encuentra el usuario (si está en alguna)
	private String room;
	//Mensaje enviado o por enviar al chat
	private String chatMessage;
	//Dirección de internet del servidor de NanoChat
	private InetSocketAddress serverAddress;
	//Estado actual del cliente, de acuerdo con el autómata
	private byte clientStatus = PRE_CONNECTION;

	// El usuario con el que se habla en privado
	private String receiver;

	//Constructor
	public NCController() {
		shell = new NCShell();
	}

	//Devuelve el comando actual introducido por el usuario
	public byte getCurrentCommand() {		
		return this.currentCommand;
	}

	//Establece el comando actual
	public void setCurrentCommand(byte command) {
		currentCommand = command;
	}

	//Registra en atributos internos los posibles parámetros del comando tecleado por el usuario
	public void setCurrentCommandArguments(String[] args) {
		//Comprobaremos también si el comando es válido para el estado actual del autómata
		switch (currentCommand) {
		case NCCommands.COM_NICK:
			if (clientStatus == PRE_REGISTRATION)
				nickname = args[0];
			break;
		case NCCommands.COM_ENTER:
			room = args[0];
			break;
		case NCCommands.COM_SEND:
			chatMessage = args[0];
			break;
		case NCCommands.COM_PRIV:
			receiver = args[0];
			chatMessage = args[1];
			break;
		default:
		}
	}

	//Procesa los comandos introducidos por un usuario que aún no está dentro de una sala
	public void processCommand() throws IOException, ParseException {
		switch (currentCommand) {
		case NCCommands.COM_NICK:
			if (clientStatus == PRE_REGISTRATION)
				registerNickName();
			else
				System.out.println("* You have already registered a nickname ("+nickname+")");
			break;
		case NCCommands.COM_ROOMLIST:
			//// LLamar a getAndShowRooms() si el estado actual del autómata lo permite
			if (this.nickname != null)
				getAndShowRooms();
			//// Si no está permitido informar al usuario
			else System.out.println("* Can't change the nickname.");
			break;
		case NCCommands.COM_ENTER:
			//// LLamar a enterChat() si el estado actual del autómata lo permite
			if (this.nickname != null)
				enterChat();
			//// Si no está permitido informar al usuario
			else System.out.println("* It wasn't possible to enter a room.");
			break;
		case NCCommands.COM_QUIT:
			//Cuando salimos tenemos que cerrar todas las conexiones y sockets abiertos
			ncConnector.disconnect();			
			directoryConnector.close();
			break;
		default:
		}
	}
	
	//Método para registrar el nick del usuario en el servidor de NanoChat
	private void registerNickName() {
		try {
			//Pedimos que se registre el nick (se comprobará si está duplicado)
			// boolean registered = ncConnector.registerNickname_UnformattedMessage(nickname);
			////: Cambiar la llamada anterior a registerNickname() al usar mensajes formateados 
			boolean registered = ncConnector.registerNickname(nickname);
			if (registered) {
				// // Si el registro fue exitoso pasamos al siguiente estado del autómata
				System.out.println("* Your nickname is now " + nickname);
			}
			else
				//En este caso el nick ya existía
				System.out.println("* The nickname is already registered. Try a different one.");			
		} catch (IOException e) {
			System.out.println("* There was an error registering the nickname");
		}
	}

	//Método que solicita al servidor de NanoChat la lista de salas e imprime el resultado obtenido
	private void getAndShowRooms() throws IOException, ParseException {
		//// Lista que contendrá las descripciones de las salas existentes
		//// Le pedimos al conector que obtenga la lista de salas ncConnector.getRooms()
		ArrayList<NCRoomInfoMessage> rooms = ncConnector.getRooms();
		//// Una vez recibidas iteramos sobre la lista para imprimir información de cada sala
		for (NCRoomInfoMessage room : rooms)
			System.out.println(room.toEncodedString());
	}

	//Método para tramitar la solicitud de acceso del usuario a una sala concreta
	private void enterChat() throws IOException, ParseException {
		//// Se solicita al servidor la entrada en la sala correspondiente ncConnector.enterRoom()
		//// Si la respuesta es un rechazo entonces informamos al usuario y salimos
		if (!ncConnector.enterRoom(this.room)) System.out.println("Can't enter " + this.room +" \uD83D\uDE02\uD83D\uDC4C");
		//// En caso contrario informamos que estamos dentro y seguimos
		else {
			System.out.println("You have entered " + this.room + "!");
			//// Cambiamos el estado del autómata para aceptar nuevos comandos
			do {
				//Pasamos a aceptar sólo los comandos que son válidos dentro de una sala
				readRoomCommandFromShell();
				processRoomCommand();
			} while (currentCommand != NCCommands.COM_EXIT);
			System.out.println("* Your are out of the room");
			//// Llegados a este punto el usuario ha querido salir de la sala, cambiamos el estado del autómata
			this.room = null;
		}
	}

	//Método para procesar los comandos específicos de una sala
	private void processRoomCommand() throws IOException, ParseException {
		switch (currentCommand) {
		case NCCommands.COM_ROOMINFO:
			//El usuario ha solicitado información sobre la sala y llamamos al método que la obtendrá
			getAndShowInfo();
			break;
		case NCCommands.COM_SEND:
			//El usuario quiere enviar un mensaje al chat de la sala
			sendChatMessage();
			break;
		case NCCommands.COM_PRIV:
			// El usuario quiere enviar un mensaje a un usuario de la sala
			sendPrivateMessage();
			break;
		case NCCommands.COM_SOCKET_IN:
			//En este caso lo que ha sucedido es que hemos recibido un mensaje desde la sala y hay que procesarlo
			processIncomingMessage();
			break;
		case NCCommands.COM_EXIT:
			//El usuario quiere salir de la sala
			exitTheRoom();
		}		
	}

	//Método para solicitar al servidor la información sobre una sala y para mostrarla por pantalla
	private void getAndShowInfo() throws IOException, ParseException {
		//// Pedimos al servidor información sobre la sala en concreto
		NCRoomInfoMessage info_raw = this.ncConnector.getRoomInfo(this.room);
		//// Mostramos por pantalla la información
		System.out.println(ANSI_GREEN + "Room: " + info_raw.roomName);
		System.out.println("Members (" + info_raw.members.size() + "): ");
		for (String member : info_raw.members)
			System.out.println("\t" + member);
		if (info_raw.timeLastMessage > 0) {
			Date d = new Date(info_raw.timeLastMessage);
			System.out.println("Last message was sent on " + d);
		} else System.out.println("There's no messages in the room yet.");
		System.out.print(ANSI_RESET);

	}

	//Método para notificar al servidor que salimos de la sala
	private void exitTheRoom() throws IOException {
		//// Mandamos al servidor el mensaje de salida
		this.ncConnector.leaveRoom(room);
		//// Cambiamos el estado del autómata para indicar que estamos fuera de la sala
		this.room = null;
	}

	//Método para enviar un mensaje al chat de la sala
	private void sendChatMessage() throws IOException {
		//// Mandamos al servidor un mensaje de chat
		this.ncConnector.sendMsg(this.nickname, this.chatMessage);
	}

	//Método para enviar un mensaje a un usuario de la sala
	private void sendPrivateMessage() throws IOException {
		//// Mandamos al servidor un mensaje de chat
		this.ncConnector.sendPriv(this.nickname, this.receiver, this.chatMessage);
	}

	//Método para procesar los mensajes recibidos del servidor mientras que el shell estaba esperando un comando de usuario
	private void processIncomingMessage() throws IOException, ParseException {
		//// Recibir el mensaje
		NCMessage msg = this.ncConnector.rcvMsg();
		//// En función del tipo de mensaje, actuar en consecuencia
		byte code = msg.getOpcode();
		switch (code) {
			// ? TODO (Ejemplo) En el caso de que fuera un mensaje de chat de broadcast mostramos la
			//  información de quién envía el mensaje y el mensaje en sí
			case NCMessage.OP_MSG: // Usuario manda mensaje
				if (((NCRoomSndRcvMessage) msg).isPriv())
				System.out.println(ANSI_BLUE
						+ "[" + ((NCRoomSndRcvMessage) msg).getUser()
						+ "] " + ((NCRoomSndRcvMessage) msg).getMsg()
						+ ANSI_RESET);
				else
				System.out.println("<"
						+ ((NCRoomSndRcvMessage) msg).getUser()
						+ "> " + ((NCRoomSndRcvMessage) msg).getMsg());
				break;
			case NCMessage.OP_BROADCAST: // Servidor manda mensaje
				System.out.print(ANSI_GREEN + "* " + ((NCBroadcastMessage) msg).getUser());
				if (((NCBroadcastMessage) msg).isJoinOrLeave())
					System.out.println(" joins the room." + ANSI_RESET);
				else System.out.println(" left." + ANSI_RESET);
				break;
		}
	}

	//Método para leer un comando de la sala
	public void readRoomCommandFromShell() {
		//Pedimos un nuevo comando de sala al shell (pasando el conector por si nos llega un mensaje entrante)
		shell.readChatCommand(ncConnector);
		//Establecemos el comando tecleado (o el mensaje recibido) como comando actual
		setCurrentCommand(shell.getCommand());
		//Procesamos los posibles parámetros (si los hubiera)
		setCurrentCommandArguments(shell.getCommandArguments());
	}

	//Método para leer un comando general (fuera de una sala)
	public void readGeneralCommandFromShell() {
		//Pedimos el comando al shell
		shell.readGeneralCommand();
		//Establecemos que el comando actual es el que ha obtenido el shell
		setCurrentCommand(shell.getCommand());
		//Analizamos los posibles parámetros asociados al comando
		setCurrentCommandArguments(shell.getCommandArguments());
	}

	//Método para obtener el servidor de NanoChat que nos proporcione el directorio
	public boolean getServerFromDirectory(String directoryHostname) {
		//Inicializamos el conector con el directorio y el shell
		System.out.println("* Connecting to the directory...");
		//Intentamos obtener la dirección del servidor de NanoChat que trabaja con nuestro protocolo
		try {
			directoryConnector = new DirectoryConnector(directoryHostname);
			serverAddress = directoryConnector.getServerForProtocol(PROTOCOL);
		} catch (IOException e1) {
			//// Auto-generated catch block
			serverAddress = null;
		}
		//Si no hemos recibido la dirección entonces nos quedan menos intentos
		if (serverAddress == null) {
			System.out.println("* Check your connection, the directory is not available.");		
			return false;
		}
		return true;
	}
	
	//Método para establecer la conexión con el servidor de Chat (a través del NCConnector)
	public boolean connectToChatServer() {
			try {
				//Inicializamos el conector para intercambiar mensajes con el servidor de NanoChat (lo hace la clase NCConnector)
				ncConnector = new NCConnector(serverAddress);
			} catch (IOException e) {
				System.out.println("* Check your connection, the game server is not available.");
				serverAddress = null;
			}
			//Si la conexión se ha establecido con éxito informamos al usuario y cambiamos el estado del autómata
			if (serverAddress != null) {
				System.out.println("* Connected to "+serverAddress);
				clientStatus = PRE_REGISTRATION;
				return true;
			}
			return false;
	}

	//Método que comprueba si el usuario ha introducido el comando para salir de la aplicación
	public boolean shouldQuit() {
		return currentCommand == NCCommands.COM_QUIT;
	}

}
