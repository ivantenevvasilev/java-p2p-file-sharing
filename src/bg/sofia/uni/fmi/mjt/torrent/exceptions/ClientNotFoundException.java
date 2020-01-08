package bg.sofia.uni.fmi.mjt.torrent.exceptions;

public class ClientNotFoundException extends Exception {
    private static final String CLIENT_DOES_NOT_EXIST = "Client %s does not exist.";
    public ClientNotFoundException(String username) {
        super(String.format(CLIENT_DOES_NOT_EXIST, username));
    }
}
