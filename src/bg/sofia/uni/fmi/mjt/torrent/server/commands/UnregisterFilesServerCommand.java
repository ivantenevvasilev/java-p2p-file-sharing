package bg.sofia.uni.fmi.mjt.torrent.server.commands;

import bg.sofia.uni.fmi.mjt.torrent.server.repositories.interfaces.ClientRepository;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.interfaces.FileRepository;

import java.io.FileNotFoundException;
import java.nio.channels.SocketChannel;
import java.nio.file.FileAlreadyExistsException;
import java.util.Collection;
import java.util.Locale;

public class UnregisterFilesServerCommand extends BaseServerCommand {
    static final String CLIENT_NOT_ONLINE_MESSAGE = "Client %s is not currently online.";
    static final String SUCCESSFULLY_REMOVED_MESSAGE = "Successfully removed file at path %s from user %s";
    static final String FILE_IS_NOT_SHARED_MESSAGE = "File at path %s is not currently shared by user %s";

    private final String user;
    private final Collection<String> paths;

    public UnregisterFilesServerCommand(FileRepository fileRepository,
                                        ClientRepository clientRepository,
                                        SocketChannel clientChannel,
                                        String user,
                                        Collection<String> paths) {
        super(fileRepository, clientRepository, clientChannel);
        this.user = user;
        this.paths = paths;
    }

    @Override
    public String execute() {
        if (!this.clientRepository.isClientOnline(this.user)) {
            return String.format(CLIENT_NOT_ONLINE_MESSAGE, this.user);
        }
        StringBuilder response = new StringBuilder();

        response.append(this.paths.size());
        response.append(System.lineSeparator());

        for (String path : this.paths) {
            try {
                this.fileRepository.unregisterFile(user, path);
                response.append(String.format(SUCCESSFULLY_REMOVED_MESSAGE, path, user));
            } catch (FileNotFoundException e) {
                response.append(String.format(FILE_IS_NOT_SHARED_MESSAGE, path, user));
            } finally {
                response.append(System.lineSeparator());
            }
        }
        return response.toString().trim();
    }
}
