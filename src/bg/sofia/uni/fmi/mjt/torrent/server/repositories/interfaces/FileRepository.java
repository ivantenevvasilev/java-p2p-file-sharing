package bg.sofia.uni.fmi.mjt.torrent.server.repositories.interfaces;

import bg.sofia.uni.fmi.mjt.torrent.models.SharedFile;

import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Collection;

public interface FileRepository {

    /**
     * @param file represents the file that is to be added to the repository.
     * @throws FileAlreadyExistsException If same file is present in repository.
     */
    void registerFile(SharedFile file) throws FileAlreadyExistsException;

    /**
     * @param user User who has shared the file.
     * @param path Path on user's machine to said file.
     * @throws FileAlreadyExistsException If file is already shared.
     */
    void registerFile(String user, String path) throws FileAlreadyExistsException;

    /**
     * @param file represents the file that is to be added to the repository.
     * @throws FileNotFoundException If given file is not present in repository.
     */
    void unregisterFile(SharedFile file) throws FileNotFoundException;

    /**
     * @param user User who has shared the file.
     * @param path Path on user's machine to said file.
     * @throws FileNotFoundException If given file is not present in repository.
     */
    void unregisterFile(String user, String path) throws FileNotFoundException;

    /**
     * @return All currently registered files as a read-only collection.
     */
    Collection<SharedFile> getRegisteredFiles();
}
