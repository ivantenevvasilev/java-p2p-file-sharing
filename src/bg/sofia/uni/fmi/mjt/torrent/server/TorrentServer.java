package bg.sofia.uni.fmi.mjt.torrent.server;

import bg.sofia.uni.fmi.mjt.torrent.AbstractServer;
import bg.sofia.uni.fmi.mjt.torrent.models.Client;
import bg.sofia.uni.fmi.mjt.torrent.models.SharedFile;
import bg.sofia.uni.fmi.mjt.torrent.server.commands.ServerCommand;
import bg.sofia.uni.fmi.mjt.torrent.server.parsers.ServerCommandParser;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.interfaces.ClientRepository;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.interfaces.FileRepository;

import java.io.Closeable;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TorrentServer extends AbstractServer {
    public static final int SERVER_PORT = 5443;

    private final FileRepository fileRepository;
    private final ClientRepository clientRepository;

    public TorrentServer(FileRepository fileRepository,
                         ClientRepository clientRepository) throws IOException {
        this(SERVER_PORT, fileRepository, clientRepository);
    }

    public TorrentServer(int port,
                         FileRepository fileRepository,
                         ClientRepository clientRepository) throws IOException {
        super(port);
        this.fileRepository = fileRepository;
        this.clientRepository = clientRepository;

    }

    public TorrentServer(FileRepository fileRepository,
                         ClientRepository clientRepository,
                         Selector selector,
                         ByteBuffer commandBuffer,
                         ServerSocketChannel channel) {
        super(selector, commandBuffer, channel);
        this.fileRepository = fileRepository;
        this.clientRepository = clientRepository;

    }

    /**
     * Read data from a connection
     *
     * @param key The key for which data was received
     */
    public void read(SelectionKey key) {
        SocketChannel sc = (SocketChannel) key.channel();
        this.commandBuffer.clear();
        ServerCommandParser parser = new ServerCommandParser(this.fileRepository,
                this.clientRepository,
                sc);
        try {
            while (true) {
                int r = sc.read(commandBuffer);
                if (r <= 0) {
                    break;
                }
            }
            this.commandBuffer.flip();
            String commandLine = StandardCharsets.UTF_8.decode(this.commandBuffer).toString();

            ServerCommand commandToExecute = parser.getCommand(commandLine);
            String result = commandToExecute != null ? commandToExecute.execute().trim() : "Invalid command";

            this.commandBuffer.clear();
            this.commandBuffer.put(result.getBytes());
            this.commandBuffer.put(System.lineSeparator().getBytes());
            this.commandBuffer.flip();
            while (this.commandBuffer.hasRemaining()) {
                sc.write(commandBuffer);
            }

        } catch (IOException e) {
            System.out.println("Client Socket was closed");
            try {
                InetSocketAddress addr = ((InetSocketAddress) ((SocketChannel) key.channel()).getRemoteAddress());
                String name = addr.getAddress().getHostAddress();
                int port = addr.getPort();
                Optional<Client> client = this.clientRepository.getActiveClients().stream().filter(x->
                        x.getRemoteConnectionPort() == port && x.getRemoteAddress().equals(name)
                ).findFirst();
                if(client.isPresent()) {
                    String commandLine = "unregister-user " + client.get().getUsername();
                    ServerCommand commandToExecute = parser.getCommand(commandLine);
                    commandToExecute.execute();
                    List<String> files = this.fileRepository
                            .getRegisteredFiles()
                            .stream()
                            .filter(f -> f.getUser().equals(client.get().getUsername()))
                            .map(SharedFile::getPath)
                            .collect(Collectors.toList());
                    if (!files.isEmpty()) {
                        String removeFilesCommand = "unregister " + name + ' ' + String.join(", ", files);
                        commandToExecute = parser.getCommand(removeFilesCommand);
                        commandToExecute.execute();
                    }
                }
            } catch (IOException e1) {

            }
            key.cancel();
        }
    }
}
