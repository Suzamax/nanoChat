package es.um.redes.nanoChat.directory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public enum DirectoryOpCodes implements Serializable {
    OK(1),
    NOSERVER(2),
    REGISTER(3),
    GETSERVER(4),
    SERVERRES(5);

    private final int value;
    private static final Map map = new HashMap<>(); // Mapeo para relacionar valores

    DirectoryOpCodes(int value) {
        this.value = value;
    }

    static { // Al crear el enumerado se hace también el mapa, muy útil!
        for (DirectoryOpCodes opcode : DirectoryOpCodes.values()) {
            map.put(opcode.value, opcode);
        }
    }

    public byte getByteValue() { // Obtenemos el número del código como Byte
        return (byte) value;
    }
}
