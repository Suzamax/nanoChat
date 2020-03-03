package es.um.redes.nanoChat.directory;

import es.um.redes.nanoChat.directory.connector.DirectoryConnector;

import java.io.IOException;
import java.net.InetSocketAddress;

public class PruebaC4 {
    public static void main(String[] args) throws IOException {
        DirectoryConnector dc = new DirectoryConnector("127.0.0.1");
        boolean res = dc.registerServerForProtocol(666, 6868);
        System.out.println("Registro: " + res);
        InetSocketAddress addr = dc.getServerForProtocol(666);
        System.out.println("Servidor: " + addr);
    }
}
