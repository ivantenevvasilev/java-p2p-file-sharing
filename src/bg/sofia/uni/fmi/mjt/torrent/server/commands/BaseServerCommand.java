package bg.sofia.uni.fmi.mjt.torrent.server.commands;

import bg.sofia.uni.fmi.mjt.torrent.server.repositories.interfaces.ClientRepository;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.interfaces.FileRepository;

import java.nio.channels.SocketChannel;

public abstract class BaseServerCommand implements ServerCommand {
    protected final FileRepository fileRepository;
    protected final ClientRepository clientRepository;
    protected final SocketChannel clientChannel;

    public BaseServerCommand(FileRepository fileRepository,
                             ClientRepository clientRepository,
                             SocketChannel clientChannel) {
        this.fileRepository = fileRepository;
        this.clientRepository = clientRepository;
        this.clientChannel = clientChannel;
    }
}
