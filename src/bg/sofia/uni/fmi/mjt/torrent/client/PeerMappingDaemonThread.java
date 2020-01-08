package bg.sofia.uni.fmi.mjt.torrent.client;

import bg.sofia.uni.fmi.mjt.torrent.client.commands.UpdateUserMappingClientCommand;
import bg.sofia.uni.fmi.mjt.torrent.client.parsers.interfaces.ResponseParser;
import bg.sofia.uni.fmi.mjt.torrent.client.repositories.interfaces.SharedFilesRepository;
import bg.sofia.uni.fmi.mjt.torrent.client.repositories.interfaces.PeerRepository;

import java.io.*;

/**
 * A daemon thread that runs every given amount of seconds (30 by default)
 * and updates the contents of the PeerRepository and writes them to the file.
 */
public class PeerMappingDaemonThread extends Thread {
    private static final int DEFAULT_WAIT_TIME_SECONDS = 30;
    private static final String PATH_TO_MAPPING_FILE = "./peers.dat";


    private final int waitTimeInSeconds;
    private final PrintWriter writer;
    private final BufferedReader reader;
    private final PeerRepository peerRepository;
    private final ResponseParser responseParser;
    private boolean isRunning = true;

    public PeerMappingDaemonThread(PrintWriter writer,
                                   BufferedReader reader,
                                   PeerRepository peerRepository,
                                   ResponseParser responseParser) {
        this(DEFAULT_WAIT_TIME_SECONDS, writer, reader,
                peerRepository, responseParser);
    }

    public PeerMappingDaemonThread(int waitTimeSeconds, PrintWriter writer,
                                   BufferedReader reader,
                                   PeerRepository peerRepository,
                                   ResponseParser responseParser) {
        this.waitTimeInSeconds = waitTimeSeconds;
        this.writer = writer;
        this.reader = reader;
        this.peerRepository = peerRepository;
        this.responseParser = responseParser;
        this.setDaemon(true);
    }

    public void shutDown() {
        this.isRunning = false;
    }

    @Override
    public void run() {
        final int millisecondsToSecondsConversionRatio = 1000;
        try {
            while (this.isRunning) {
                new UpdateUserMappingClientCommand(
                        this.writer,
                        this.reader,
                        this.peerRepository,
                        this.responseParser
                ).execute();
                this.writeToFile();
                Thread.sleep(this.waitTimeInSeconds * millisecondsToSecondsConversionRatio);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeToFile() throws IOException {
        File peersFile = new File(PATH_TO_MAPPING_FILE);
        peersFile.createNewFile();
        try (PeerMappingWriter clientMappingWriter = new PeerMappingWriter(this.peerRepository,
                new FileOutputStream(peersFile))) {
            clientMappingWriter.write();
        }
    }
}
