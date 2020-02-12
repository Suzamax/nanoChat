import es.um.redes.nanoChat.directory.connector.DirectoryConnector;
import es.um.redes.nanoChat.directory.server.DirectoryThread;

import java.io.IOException;
import java.net.SocketException;

public class Clase2 {
    public static void main(String[] args) throws IOException {
        DirectoryThread dt;
        try {
            dt = new DirectoryThread("Directory", 6868, 0.8);
            dt.start();
        } catch (SocketException e) {
            System.err.println("Directory cannot create UDP socket on port "
                    + 6868);
            System.err
                    .println("Most likely a Directory process is already running and listening on that port...");
            System.exit(-1);
        }
        DirectoryConnector dc = new DirectoryConnector("127.0.0.1");
        dc.mandaCadena("THIS IS FINE");
    }
}
