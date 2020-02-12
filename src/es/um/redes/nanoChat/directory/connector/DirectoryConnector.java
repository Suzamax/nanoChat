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

		//TODO Generar el mensaje de consulta llamando a buildQuery()
		//TODO Construir el datagrama con la consulta
		//TODO Enviar datagrama por el socket
		//TODO preparar el buffer para la respuesta
		//TODO Establecer el temporizador para el caso en que no haya respuesta
		//TODO Recibir la respuesta
		//TODO Procesamos la respuesta para devolver la dirección que hay en ella
		
		return null;
	}


	//Método para generar el mensaje de consulta (para obtener el servidor asociado a un protocolo)
	private byte[] buildQuery(int protocol) {
		//TODO Devolvemos el mensaje codificado en binario según el formato acordado
		return null;
	}

	//Método para obtener la dirección de internet a partir del mensaje UDP de respuesta
	private InetSocketAddress getAddressFromResponse(DatagramPacket packet) throws UnknownHostException {
		//TODO Analizar si la respuesta no contiene dirección (devolver null)
		//TODO Si la respuesta no está vacía, devolver la dirección (extraerla del mensaje)
		return null;
	}
	
	/**
	 * Envía una solicitud para registrar el servidor de chat asociado a un determinado protocolo
	 * 
	 */
	public boolean registerServerForProtocol(int protocol, int port) throws IOException {

		//TODO Construir solicitud de registro (buildRegistration)
		//TODO Enviar solicitud
		//TODO Recibe respuesta
		//TODO Procesamos la respuesta para ver si se ha podido registrar correctamente
		return false;
	}


	//Método para construir una solicitud de registro de servidor
	//OJO: No hace falta proporcionar la dirección porque se toma la misma desde la que se envió el mensaje
	private byte[] buildRegistration(int protocol, int port) {
		//TODO Devolvemos el mensaje codificado en binario según el formato acordado
		return null;
	}

	public void close() {
		socket.close();
	}
}
