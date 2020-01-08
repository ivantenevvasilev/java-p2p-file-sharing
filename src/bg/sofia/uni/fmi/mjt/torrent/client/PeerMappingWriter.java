package bg.sofia.uni.fmi.mjt.torrent.client;

import bg.sofia.uni.fmi.mjt.torrent.client.models.Peer;
import bg.sofia.uni.fmi.mjt.torrent.client.repositories.interfaces.PeerRepository;

import java.io.Closeable;
import java.io.PrintWriter;
import java.io.OutputStream;

/**
 * Writes the contents of the PeerRepository to the output stream.
 */
public class PeerMappingWriter implements Closeable {
    private PrintWriter filePrintWriter;
    private PeerRepository repository;

    public PeerMappingWriter(PeerRepository repository, OutputStream streamToWriteTo) {
        this.filePrintWriter = new PrintWriter(streamToWriteTo, true);
        this.repository = repository;
    }

    public void write() {
        for (Peer user : repository.getRemoteUsers()) {
            this.filePrintWriter.println(user.toString());
        }
        this.filePrintWriter.close();
    }

    @Override
    public void close() {
        this.filePrintWriter.close();
    }
}
