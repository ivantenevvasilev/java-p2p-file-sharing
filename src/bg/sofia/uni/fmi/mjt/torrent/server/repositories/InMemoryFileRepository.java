package bg.sofia.uni.fmi.mjt.torrent.server.repositories;

import bg.sofia.uni.fmi.mjt.torrent.models.SharedFile;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.interfaces.FileRepository;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class InMemoryFileRepository implements FileRepository {
    private static final String FILE_EXISTS_ERROR = "User %s has already shared file at path %s";
    private HashSet<SharedFile> sharedFiles = new HashSet<>();

    /**
     * @param file represents the file that is to be added to the repository.
     * @throws FileAlreadyExistsException If same file is present in repository.
     */
    @Override
    public synchronized void registerFile(@NotNull SharedFile file) throws FileAlreadyExistsException {
        if (this.sharedFiles.contains(file)) {
            throw new FileAlreadyExistsException(String.format(FILE_EXISTS_ERROR, file.getUser(), file.getPath()));
        }
        this.sharedFiles.add(file);
    }

    /**
     * @param user User who has shared the file.
     * @param path Path on user's machine to said file.
     * @throws FileAlreadyExistsException If file is already shared.
     */
    @Override
    public synchronized void registerFile(String user, String path) throws FileAlreadyExistsException {
        SharedFile fileToAdd = new SharedFile(user, path);
        this.registerFile(fileToAdd);
    }

    /**
     * @param file represents the file that is to be added to the repository.
     * @throws FileNotFoundException If given file is not present in repository.
     */
    @Override
    public synchronized void unregisterFile(@NotNull SharedFile file) throws FileNotFoundException {
        if (!this.sharedFiles.contains(file)) {
            throw new FileNotFoundException();
        }
        this.sharedFiles.remove(file);
    }

    /**
     * @param user User who has shared the file.
     * @param path Path on user's machine to said file.
     * @throws FileNotFoundException If given file is not present in repository.
     */
    @Override
    public synchronized void unregisterFile(@NotNull String user, @NotNull String path) throws FileNotFoundException {
        SharedFile fileToRemove = new SharedFile(user, path);
        this.unregisterFile(fileToRemove);
    }

    /**
     * @return All currently registered files as a read-only collection.
     */
    @Override
    public synchronized Collection<SharedFile> getRegisteredFiles() {
        return Collections.unmodifiableCollection(this.sharedFiles);
    }
}
