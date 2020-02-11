package es.um.redes.nanoChat.directory.server;

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

	public DirectoryThread(String name, int directoryPort, double corruptionProbability) throws SocketException {
		super(name);
		//TODO Anotar la dirección en la que escucha el servidor de Directorio

 		//TODO Crear un socket de servidor

		messageDiscardProbability = corruptionProbability;
		//Inicialización del mapa
		servers = new HashMap<Integer,InetSocketAddress>();
	}

	public void run() {
		byte[] buf = new byte[PACKET_MAX_SIZE];

		System.out.println("Directory starting...");
		boolean running = true;
		while (running) {

				//TODO 1) Recibir la solicitud por el socket
				//TODO 2) Extraer quién es el cliente (su dirección)
				// 3) Vemos si el mensaje debe ser descartado por la probabilidad de descarte

				double rand = Math.random();
				if (rand < messageDiscardProbability) {
					System.err.println("Directory DISCARDED corrupt request from... ");
					continue;
				}
				
				//TODO (Solo Boletín 2) Devolver una respuesta idéntica en contenido a la solicitud
				
				//TODO 4) Analizar y procesar la solicitud (llamada a processRequestFromCLient)
				//TODO 5) Tratar las excepciones que puedan producirse
		}
		socket.close();
	}

	//Método para procesar la solicitud enviada por clientAddr
	public void processRequestFromClient(byte[] data, InetSocketAddress clientAddr) throws IOException {
		//TODO 1) Extraemos el tipo de mensaje recibido
		//TODO 2) Procesar el caso de que sea un registro y enviar mediante sendOK
		//TODO 3) Procesar el caso de que sea una consulta
		//TODO 3.1) Devolver una dirección si existe un servidor (sendServerInfo)
		//TODO 3.2) Devolver una notificación si no existe un servidor (sendEmpty)
	}

	//Método para enviar una respuesta vacía (no hay servidor)
	private void sendEmpty(InetSocketAddress clientAddr) throws IOException {
		//TODO Construir respuesta
		//TODO Enviar respuesta
	}

	//Método para enviar la dirección del servidor al cliente
	private void sendServerInfo(InetSocketAddress serverAddress, InetSocketAddress clientAddr) throws IOException {
		//TODO Obtener la representación binaria de la dirección
		//TODO Construir respuesta
		//TODO Enviar respuesta
	}

	//Método para enviar la confirmación del registro
	private void sendOK(InetSocketAddress clientAddr) throws IOException {
		//TODO Construir respuesta
		//TODO Enviar respuesta
	}
}
