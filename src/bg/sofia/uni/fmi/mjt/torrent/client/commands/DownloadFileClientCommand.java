package bg.sofia.uni.fmi.mjt.torrent.client.commands;

import bg.sofia.uni.fmi.mjt.torrent.client.commands.enums.ClientCommandsKeywords;
import bg.sofia.uni.fmi.mjt.torrent.client.models.Peer;
import bg.sofia.uni.fmi.mjt.torrent.client.repositories.interfaces.PeerRepository;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * This command establishes a connection with peer
 * and issues a download command to the peer's server
 * for given file and save location on local computer
 */
public class DownloadFileClientCommand implements ClientCommand {
    private static final int BUFFER_SIZE = 1024;
    private static final String USER_NOT_ONLINE_MESSAGE = "User %s is not online";
    private static final String MALFORMED_RESPONSE_MESSAGE = "Malformed response";
    private static final String INVALID_LOCAL_PATH_MESSAGE = "Invalid local path";
    private static final String UNKNOWN_HOST_MESSAGE = "Cannot resolve host %s:%d";
    private static final String FILE_UNAVAILABLE_MESSAGE = "File is unavailable";
    private static final String DOWNLOAD_COMPLETE_MESSAGE = "Completed downloading %s from %s at path %s";
    private static final String DOWNLOAD_PROGRESS_MESSAGE = "%d/%d bytes downloaded, %d remaining";
    private final PeerRepository peers;
    private final String username;
    private final String pathOnRemoteComputer;
    private final String localPath;
    private final PrintWriter outputStreamWriter;

    public DownloadFileClientCommand(PeerRepository peers, String username,
                                     String pathOnRemoteComputer, String localPath,
                                     PrintWriter outputStreamWriter) {
        this.peers = peers;
        this.username = username;
        this.pathOnRemoteComputer = pathOnRemoteComputer;
        this.localPath = localPath;
        this.outputStreamWriter = outputStreamWriter;
    }

    /**
     * Returns an error string if one of the following happens
     * - File is not shared by peer
     * - Peer with given username is not online
     * - Failed to create a local file to given path
     * - The server responded with a malformed request
     * - Could not establish a connection with peet
     * - The host could not be determined
     * If none of the mentioned above things are correct, then
     * a connection with remote server will be established and file
     * will be saved to path specified by constructor
     *
     * @return Either successfully downloaded message or an error string
     * representing what the error is.
     */
    @Override
    public String execute() {
        Peer peer = this.peers.getUser(username);
        if (peer == null) {
            return String.format(USER_NOT_ONLINE_MESSAGE, this.username);
        }
        try (Socket connectionToPeer = new Socket(peer.getHostname(), peer.getRemoteServerPort());
             PrintWriter writerToPeer = new PrintWriter(connectionToPeer.getOutputStream(), true)) {
            writerToPeer.println(ClientCommandsKeywords.DOWNLOAD_FILE_COMMAND.getKeyword()
                    + ' ' + this.pathOnRemoteComputer);

            InputStream inputStream = connectionToPeer.getInputStream();
            byte[] longAsBytes = new byte[Long.BYTES];

            inputStream.read(longAsBytes, 0, Long.BYTES);

            ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);
            byteBuffer.put(longAsBytes);
            byteBuffer.flip();
            long length = byteBuffer.getLong();
            if (length <= 0) {
                return FILE_UNAVAILABLE_MESSAGE;
            }
            try {
                File destinationFile = new File(this.localPath);
                destinationFile.getParentFile().mkdirs();
                destinationFile.createNewFile();
            } catch (Exception e) {
                return INVALID_LOCAL_PATH_MESSAGE;
            }
            //TODO
            FileOutputStream fileOutputStream = new FileOutputStream(localPath, false);


            try {
                final int printFrequency = 20;
                int count = printFrequency;

                byte[] buffer = new byte[BUFFER_SIZE];
                long totalReadBytes = 0;
                while (totalReadBytes < length) {
                    int bytesToRead = (int) Math.min((length - totalReadBytes), BUFFER_SIZE);
                    int readBytes = inputStream.read(buffer, 0, bytesToRead);
                    if (readBytes == -1) {
                        break;
                    }
                    fileOutputStream.write(buffer, 0, readBytes);
                    totalReadBytes += readBytes;
//                    if (count == printFrequency) {
//                        this.outputStreamWriter.printf(DOWNLOAD_PROGRESS_MESSAGE,
//                                totalReadBytes, length, length - totalReadBytes);
//                        this.outputStreamWriter.println();
//                        count = 0;
//                    }
//                    count++;
                }
                fileOutputStream.flush();
                fileOutputStream.close();
                inputStream.close();
            } catch (NumberFormatException e) {
                return MALFORMED_RESPONSE_MESSAGE;
            }

        } catch (IOException e) {
            return String.format(UNKNOWN_HOST_MESSAGE, peer.getHostname(), peer.getRemoteServerPort());
        }
        return String.format(DOWNLOAD_COMPLETE_MESSAGE, this.pathOnRemoteComputer,
                this.username, this.localPath);
    }
}
