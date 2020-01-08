package bg.sofia.uni.fmi.mjt.torrent.client.models;

import java.util.Objects;

public class Peer {
    private String username;
    private String hostname;
    private int remoteServerPort;

    public Peer(String username, String hostname, int remoteServerPort) {
        this.username = username;
        this.hostname = hostname;
        this.remoteServerPort = remoteServerPort;
    }

    public String getUsername() {
        return username;
    }

    public String getHostname() {
        return hostname;
    }

    public int getRemoteServerPort() {
        return remoteServerPort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Peer that = (Peer) o;
        return remoteServerPort == that.remoteServerPort &&
                Objects.equals(username, that.username) &&
                Objects.equals(hostname, that.hostname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, hostname, remoteServerPort);
    }

    @Override
    public String toString() {
        return this.username + " : " + this.hostname + ":" + this.remoteServerPort;
    }
}
