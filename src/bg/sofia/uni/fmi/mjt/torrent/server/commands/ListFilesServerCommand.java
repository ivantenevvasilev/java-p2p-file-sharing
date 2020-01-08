package bg.sofia.uni.fmi.mjt.torrent.server.commands;

import bg.sofia.uni.fmi.mjt.torrent.models.SharedFile;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.interfaces.ClientRepository;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.interfaces.FileRepository;

import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.stream.Collectors;

public class ListFilesServerCommand extends BaseServerCommand {

    public ListFilesServerCommand(FileRepository fileRepository,
                                  ClientRepository clientRepository,
                                  SocketChannel clientChannel) {
        super(fileRepository, clientRepository, clientChannel);
    }

    @Override
    public String execute() {
        Collection<SharedFile> sharedFiles = this.fileRepository.getRegisteredFiles();
        return sharedFiles.size() +
                System.lineSeparator() +
                sharedFiles
                .stream()
                .map(SharedFile::toString)
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
