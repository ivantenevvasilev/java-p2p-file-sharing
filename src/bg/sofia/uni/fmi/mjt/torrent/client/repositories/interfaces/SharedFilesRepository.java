package bg.sofia.uni.fmi.mjt.torrent.client.repositories.interfaces;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Set;

public interface SharedFilesRepository {
    boolean isFileShared(String path);
    long getFileSize(String path) throws FileNotFoundException;

    void registerFile(String pathToFile, long fileSize);
    void unregisterFile(String pathToRemove);

    void registerFiles(Map<String, Long> filesToAdd);
    void unregisterFiles(Set<String> pathsToRemove);

    Set<String> getAvailableFiles();
}
