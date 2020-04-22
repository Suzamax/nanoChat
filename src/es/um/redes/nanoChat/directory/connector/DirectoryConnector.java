package es.um.redes.nanoChat.directory.connector;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

/**
 * Cliente con métodos de consulta y actualización específicos del directorio
 */
public class DirectoryConnector {
	//Tamaño máximo del paquete UDP (los mensajes intercambiados son muy cortos)
	private static final int PACKET_MAX_SIZE = 128;
	//Puerto en el que atienden los servidores de directorio
	private static final int DEFAULT_PORT = 6868;
	//Valor del TIMEOUT
	private static final int TIMEOUT = 1000;

	private static final byte OPCODE_OK        = 1;
	private static final byte OPCODE_NOSERVER  = 2;
	private static final byte OPCODE_REGISTER  = 3;
	private static final byte OPCODE_GETSERVER = 4;
	private static final byte OPCODE_SERVERRES = 5;
	private static final byte OPCODE_NOK	   = 6;


	private DatagramSocket socket; // socket UDP
	private InetSocketAddress directoryAddress; // dirección del servidor de directorio

	public DirectoryConnector(String agentAddress) throws IOException {
		//TODO A partir de la dirección y del puerto generar la dirección de conexión para el Socket
		this.directoryAddress = new InetSocketAddress(InetAddress.getByName(agentAddress), DEFAULT_PORT);
		//TODO Crear el socket UDP
		this.socket = new DatagramSocket(); // No hace falta engancharle el SocketAddr
	}

	// TODO Borrar esto cuando se pueda
	public void mandaCadena(String str) throws IOException {
		// Ejemplo chenchillo
		byte[] buf = str.getBytes();
		DatagramPacket pkt = new DatagramPacket(buf, buf.length, directoryAddress);
		byte[] buf2 = new byte[PACKET_MAX_SIZE];
		DatagramPacket pkt2 = new DatagramPacket(buf2, buf2.length);
		boolean running = true;
		while (running) {
			try {
				socket.send(pkt);
				socket.setSoTimeout(1000);
				socket.receive(pkt2);
				running = false;
			} catch (SocketTimeoutException e) {
				System.out.println("Timed out...");
			}
		}

		String cadena = new String(pkt2.getData(), 0, pkt2.getLength());
		System.out.println("Enviado: " + str + "\nRecibido: " + cadena);
		socket.close();
	}


	/**
	 * Envía una solicitud para obtener el servidor de chat asociado a un determinado protocolo
	 * 
	 */
	public InetSocketAddress getServerForProtocol(int protocol) throws IOException {

		//DONE Generar el mensaje de consulta llamando a buildQuery()
		byte[] consulta = buildQuery(protocol);
		//DONE Construir el datagrama con la consulta
		DatagramPacket pkt = new DatagramPacket(consulta, consulta.length, directoryAddress);
		//DONE Enviar datagrama por el socket
		int reintentos = 5;
		//DONE preparar el buffer para la respuesta
		byte[] buf = new byte[PACKET_MAX_SIZE];
		//DONE Recibir la respuesta
		DatagramPacket pktres = new DatagramPacket(buf, buf.length);
		while (reintentos > 0) {
			socket.send(pkt);
			//DONE Establecer el temporizador para el caso en que no haya respuesta
			socket.setSoTimeout(TIMEOUT);
			try {
				socket.receive(pktres);
				//DONE Procesamos la respuesta para devolver la dirección que hay en ella
				return getAddressFromResponse(pktres);
			} catch (SocketTimeoutException e) {
				--reintentos;
			}
		}
		// Han pasado cosas.
		return null;
	}


	//Método para generar el mensaje de consulta (para obtener el servidor asociado a un protocolo)
	private byte[] buildQuery(int protocol) {
		// DONE Devolvemos el mensaje codificado en binario según el formato acordado
		// OP (1) + PROTOCOLO (4) = 5 bytes
		ByteBuffer bb = ByteBuffer.allocate(5);
		bb.put(OPCODE_GETSERVER);
		bb.putInt(protocol);
		return bb.array();
	}

	//Método para obtener la dirección de internet a partir del mensaje UDP de respuesta
	private InetSocketAddress getAddressFromResponse(DatagramPacket packet) throws UnknownHostException {
		//DONE Analizar si la respuesta no contiene dirección (devolver null)
		ByteBuffer bb = ByteBuffer.wrap(packet.getData());
		byte opcode = bb.get(); // 1er byte

		if (opcode == OPCODE_SERVERRES) {
			byte[] ip_raw = new byte[4];
			bb.get(ip_raw);
			int puerto = bb.getInt();
			InetAddress ip = InetAddress.getByAddress(ip_raw);
			return new InetSocketAddress(ip, puerto);
		}
		if (opcode == OPCODE_NOSERVER) return null;
		//DONE Si la respuesta no está vacía, devolver la dirección (extraerla del mensaje)
		if (opcode != OPCODE_SERVERRES) {
			System.err.println("¡Recibido OpCode inesperado!" + opcode);
			return null;
		}
		return null;
	}
	
	/**
	 * Envía una solicitud para registrar el servidor de chat asociado a un determinado protocolo
	 * 
	 */
	public boolean registerServerForProtocol(int protocol, int port) throws IOException {

		//DONE Construir solicitud de registro (buildRegistration)
		byte[] booooooof = buildRegistration(protocol, port);
		byte[] gettoboof = new byte[PACKET_MAX_SIZE];
		DatagramPacket pktres = new DatagramPacket(gettoboof, gettoboof.length);
		int reintentos = 5;
		DatagramPacket pkt = new DatagramPacket(booooooof, booooooof.length, directoryAddress);
		//DONE Enviar solicitud
		//DONE Recibe respuesta
		while (reintentos > 0) {
			socket.send(pkt);
			socket.setSoTimeout(TIMEOUT);
			try {
				socket.receive(pktres);
				ByteBuffer contents = ByteBuffer.wrap(pktres.getData());
				return contents.get() == 1;
			} catch (SocketTimeoutException e) {
				--reintentos;
			}
		}
		//DONE????? Procesamos la respuesta para ver si se ha podido registrar correctamente
		return false;
	}


	//Método para construir una solicitud de registro de servidor
	//OJO: No hace falta proporcionar la dirección porque se toma la misma desde la que se envió el mensaje
	private byte[] buildRegistration(int protocol, int port) {
		//DONE????? Devolvemos el mensaje codificado en binario según el formato acordado
		ByteBuffer bb = ByteBuffer.allocate(9);
		bb.put(OPCODE_REGISTER); // 1
		bb.putInt(protocol); // 4
		bb.putInt(port); // 4
		return bb.array();
	}

	public void close() {
		socket.close();
	}
}
