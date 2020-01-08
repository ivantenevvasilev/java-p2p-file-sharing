package bg.sofia.uni.fmi.mjt.torrent.client;

import bg.sofia.uni.fmi.mjt.torrent.client.repositories.interfaces.SharedFilesRepository;

import java.io.IOException;

public class TorrentShareServerThread extends Thread {
    private TorrentClientShareServer shareServer;

    public TorrentShareServerThread(int port, SharedFilesRepository sharedFiles) throws IOException {
        this.shareServer = new TorrentClientShareServer(port, sharedFiles);
    }

    public void shutDown() throws IOException {
        this.shareServer.close();
    }

    @Override
    public void run() {
        try {
            this.shareServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
