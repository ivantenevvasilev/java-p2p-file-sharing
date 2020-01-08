package bg.sofia.uni.fmi.mjt.torrent.models;

import java.util.Objects;

public class SharedFile {
    private String user;
    private String path;

    public SharedFile(String user, String path) {
        this.user = user;
        this.path = path;
    }

    public String getUser() {
        return user;
    }

    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SharedFile that = (SharedFile) o;
        return Objects.equals(user, that.user) &&
                Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, path);
    }

    @Override
    public String toString() {
        return user + " : " + path;
    }
}
