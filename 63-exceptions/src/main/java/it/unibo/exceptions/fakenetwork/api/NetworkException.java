package it.unibo.exceptions.fakenetwork.api;
import java.io.IOException;

public class NetworkException extends IOException {
    private static final long serialVersionUID = 1L;

    /*
     * Costruttore senza argomenti
     */
    public NetworkException() {
        super("Netwoek error: no response");
    }

    /*
     * Costruttore con messaggio
     */
    public NetworkException(final String message) {
        super("Network error while sending message: " + message);
    }
    
}
