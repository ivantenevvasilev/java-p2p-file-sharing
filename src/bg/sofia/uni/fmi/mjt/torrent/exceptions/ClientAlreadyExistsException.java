package bg.sofia.uni.fmi.mjt.torrent.exceptions;

public class ClientAlreadyExistsException extends Exception {
    private static final String CLIENT_EXISTS_MESSAGE = "Client %s already exists.";
    public ClientAlreadyExistsException(String username) {
        super(String.format(CLIENT_EXISTS_MESSAGE, username));
    }
}
