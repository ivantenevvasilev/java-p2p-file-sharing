package bg.sofia.uni.fmi.mjt.torrent.client;

import bg.sofia.uni.fmi.mjt.torrent.AbstractServer;
import bg.sofia.uni.fmi.mjt.torrent.client.repositories.interfaces.SharedFilesRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class TorrentClientShareServer extends AbstractServer {
    private String username;
    private SharedFilesRepository sharedFiles;

    protected TorrentClientShareServer(int port, SharedFilesRepository sharedFiles) throws IOException {
        super(port);
        this.sharedFiles = sharedFiles;
    }

    protected TorrentClientShareServer(int port, int bufferSize, SharedFilesRepository sharedFiles)
            throws IOException {
        super(port, bufferSize);
        this.sharedFiles = sharedFiles;
    }

    protected TorrentClientShareServer(Selector selector, ByteBuffer commandBuffer,
                                       ServerSocketChannel channel,  SharedFilesRepository sharedFiles) {
        super(selector, commandBuffer, channel);
        this.sharedFiles = sharedFiles;
    }

    /**
     * Read data from a connection
     *
     * @param key The key for which data was received
     */
    @Override
    public void read(SelectionKey key) {
        SocketChannel sc = (SocketChannel) key.channel();
        this.commandBuffer.clear();

        try {
            while (true) {
                int r = sc.read(commandBuffer);
                if (r <= 0) {
                    break;
                }
            }
            this.commandBuffer.flip();
            String commandLine = StandardCharsets.UTF_8.decode(this.commandBuffer).toString();
            String[] tokens = commandLine.split("\\s+");
            if (tokens.length == 0) {
                return;
            }
            String commandKeyword = tokens[0];
            if (commandKeyword.equals("download")) {
                this.commandBuffer.clear();

                if (tokens.length < 2) {
                    this.commandBuffer.putLong(-1);
                    this.commandBuffer.flip();
                    sc.write(commandBuffer);

                    return;
                }
                String path = new File(tokens[1]).getAbsolutePath();
                if (!this.sharedFiles.isFileShared(path)) {
                    this.commandBuffer.putLong(-1);
                    this.commandBuffer.flip();
                    sc.write(commandBuffer);

                    return;
                }


                Long fileLength = this.sharedFiles.getFileSize(path);
                this.commandBuffer.putLong(fileLength);
                this.commandBuffer.flip();
                sc.write(commandBuffer);

                final int fileBufferSize = this.commandBuffer.capacity();
                byte[] fileBuffer = new byte[fileBufferSize];
                FileInputStream file = new FileInputStream(path);
                while (true) {
                    int readBytes = file.read(fileBuffer);
                    if (readBytes <= 0) {
                        break;
                    }
                    this.commandBuffer.clear();
                    this.commandBuffer.put(fileBuffer, 0, readBytes);
                    this.commandBuffer.flip();
                    sc.write(this.commandBuffer);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            key.cancel();
        }
    }
}