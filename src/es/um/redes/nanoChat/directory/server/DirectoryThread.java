package es.um.redes.nanoChat.directory.server;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.HashMap;


public class DirectoryThread extends Thread {

	//Tamaño máximo del paquete UDP
	private static final int PACKET_MAX_SIZE = 128;
	//Estructura para guardar las asociaciones ID_PROTOCOLO -> Dirección del servidor
	protected HashMap<Integer,InetSocketAddress> servers;

	//Socket de comunicación UDP
	protected DatagramSocket socket = null;
	//Probabilidad de descarte del mensaje
	protected double messageDiscardProbability;

	private static final byte OPCODE_OK        = 1;
	private static final byte OPCODE_NOSERVER  = 2;
	private static final byte OPCODE_REGISTER  = 3;
	private static final byte OPCODE_GETSERVER = 4;
	private static final byte OPCODE_SERVERRES = 5;
	private static final byte OPCODE_NOK	   = 6;

	public DirectoryThread(String name, int directoryPort, double corruptionProbability) throws SocketException {
		super(name);
		// // Anotar la dirección en la que escucha el servidor de Directorio
		InetSocketAddress serverAddress = new InetSocketAddress(directoryPort);
 		// // Crear un socket de servidor
		this.socket = new DatagramSocket(directoryPort);

		messageDiscardProbability = corruptionProbability;
		//Inicialización del mapa
		servers = new HashMap<Integer,InetSocketAddress>();
	}

	public void run() {
		byte[] buf = new byte[PACKET_MAX_SIZE];

		System.out.println("Directory starting...");
		boolean running = true;
		while (running) {
			// // 1) Recibir la solicitud por el socket
			DatagramPacket dp = new DatagramPacket(buf, buf.length);
			try {
				socket.receive(dp);

				// // 2) Extraer quién es el cliente (su dirección)
				InetSocketAddress cliente = (InetSocketAddress) dp.getSocketAddress();
				// 3) Vemos si el mensaje debe ser descartado por la probabilidad de descarte

				double rand = Math.random();
				if (rand < messageDiscardProbability) {
					System.err.println("Directory DISCARDED corrupt request from... ");
					continue;
				}

				//// (Solo Boletín 2) Devolver una respuesta idéntica en contenido a la solicitud
				// socket.send(dp); // Piruleta.
				//// 4) Analizar y procesar la solicitud (llamada a processRequestFromCLient)
				processRequestFromClient(dp.getData(), cliente);
				//// 5) Tratar las excepciones que puedan producirse
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		socket.close();
	}

	//Método para procesar la solicitud enviada por clientAddr
	public void processRequestFromClient(byte[] data, InetSocketAddress clientAddr) throws IOException {
		//// 1) Extraemos el tipo de mensaje recibido
		ByteBuffer bb = ByteBuffer.wrap(data);
		byte opcode = bb.get();
		switch (opcode) {
			case OPCODE_REGISTER:
				int protocolo = bb.getInt();
				int puerto = bb.getInt();
				servers.put(protocolo, new InetSocketAddress(clientAddr.getAddress(), puerto));
				sendOK(clientAddr);
				break;
			case OPCODE_GETSERVER:
				int proto = bb.getInt();
				if (servers.containsKey(proto))
					sendServerInfo(servers.get(proto), clientAddr);
				else sendEmpty(clientAddr);
				break;
		}
		////! 2) Procesar el caso de que sea un registro y enviar mediante sendOK
		////????? 3) Procesar el caso de que sea una consulta
		//// 3.1) Devolver una dirección si existe un servidor (sendServerInfo)
		//// 3.2) Devolver una notificación si no existe un servidor (sendEmpty)
	}

	//Método para enviar una respuesta vacía (no hay servidor)
	private void sendEmpty(InetSocketAddress clientAddr) throws IOException {
		//// Construir respuesta
		byte[] buf = new byte[1];
		buf[0] = OPCODE_NOSERVER;
		//// Enviar respuesta
		DatagramPacket pkt = new DatagramPacket(buf, buf.length, clientAddr);
		socket.send(pkt);
	}

	//Método para enviar la dirección del servidor al cliente
	private void sendServerInfo(InetSocketAddress serverAddress, InetSocketAddress clientAddr) throws IOException {
		//// Obtener la representación binaria de la dirección
		byte[] ip_raw = serverAddress.getAddress().getAddress();
		int puerto = serverAddress.getPort();
		//// Construir respuesta
		ByteBuffer bb = ByteBuffer.allocate(9);
		// OP + IP + PORT = 1 + 4 + 4 = 9
		bb.put(OPCODE_SERVERRES);
		bb.put(ip_raw);
		bb.putInt(puerto);
		//// Enviar respuesta
		DatagramPacket pkt = new DatagramPacket(bb.array(), bb.array().length, clientAddr);
		socket.send(pkt);
	}

	//Método para enviar la confirmación del registro
	private void sendOK(InetSocketAddress clientAddr) throws IOException {
		//// Construir respuesta
		byte[] buf = new byte[1];
		buf[0] = OPCODE_OK;
		//// Enviar respuesta
		DatagramPacket pkt = new DatagramPacket(buf, buf.length, clientAddr);
		socket.send(pkt);
	}
}
