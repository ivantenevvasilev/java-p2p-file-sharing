package bg.sofia.uni.fmi.mjt.torrent.models;

import java.util.Objects;

public class Client {
    private String username;
    private String remoteAddress;
    private int remoteConnectionPort;
    private int remoteServerPort;


    public Client(String username, String remoteAddress, int remoteConnectionPort, int remoteServerPort) {
        this.username = username;
        this.remoteAddress = remoteAddress;
        this.remoteConnectionPort = remoteConnectionPort;
        this.remoteServerPort = remoteServerPort;
    }

    public String getUsername() {
        return username;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public int getRemoteConnectionPort() {
        return remoteConnectionPort;
    }

    public int getRemoteServerPort() {
        return remoteServerPort;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return remoteConnectionPort == client.remoteConnectionPort &&
                remoteServerPort == client.remoteServerPort &&
                Objects.equals(username, client.username) &&
                Objects.equals(remoteAddress, client.remoteAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, remoteAddress, remoteConnectionPort, remoteServerPort);
    }

    @Override
    public String toString() {
        return this.getUsername() + " : " + this.getRemoteAddress() + ":" + this.getRemoteServerPort();
    }
}
