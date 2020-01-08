package bg.sofia.uni.fmi.mjt.torrent.server.commands;

import bg.sofia.uni.fmi.mjt.torrent.server.repositories.interfaces.ClientRepository;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.interfaces.FileRepository;

import java.nio.channels.SocketChannel;
import java.nio.file.FileAlreadyExistsException;
import java.util.Collection;

public class RegisterFilesServerCommand extends BaseServerCommand {
    static final String FILE_ALREADY_SHARED_MESSAGE = "File at path %s is already shared by %s";
    static final String SUCCESSFULLY_ADDED_MESSAGE = "File at path %s is now shared by %s";
    static final String CLIENT_NOT_ONLINE_MESSAGE = "Client %s is not online. Please use register-user command " +
            "to register before adding";
    private final String user;
    private Collection<String> paths;

    public RegisterFilesServerCommand(FileRepository fileRepository,
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
                this.fileRepository.registerFile(user, path);
                response.append(String.format(SUCCESSFULLY_ADDED_MESSAGE, path, user));
            } catch (FileAlreadyExistsException e) {
                response.append(String.format(FILE_ALREADY_SHARED_MESSAGE, path, user));
            } finally {
                response.append(System.lineSeparator());
            }
        }
        return response.toString().trim();
    }
}
