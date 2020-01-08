package bg.sofia.uni.fmi.mjt.torrent.client.repositories;

import bg.sofia.uni.fmi.mjt.torrent.client.repositories.interfaces.SharedFilesRepository;

import java.io.FileNotFoundException;
import java.util.*;

public class InMemorySharedFilesRepository implements SharedFilesRepository {
    private Map<String, Long> sharedFiles = new HashMap<>();


    @Override
    public synchronized boolean isFileShared(String path) {
        return this.sharedFiles.containsKey(path);
    }

    @Override
    public synchronized long getFileSize(String path) throws FileNotFoundException {
        if (!this.sharedFiles.containsKey(path)) {
            throw new FileNotFoundException();
        }
        return this.sharedFiles.get(path);
    }

    @Override
    public void registerFile(String pathToFile, long fileSize) {
        this.sharedFiles.put(pathToFile, fileSize);
    }

    @Override
    public void registerFiles(Map<String, Long> filesToAdd) {
        this.sharedFiles.putAll(filesToAdd);
    }

    @Override
    public synchronized void unregisterFile(String pathToRemove) {
        this.sharedFiles.remove(pathToRemove);
    }

    @Override
    public synchronized void unregisterFiles(Set<String> pathsToRemove) {
        for (String path : pathsToRemove) {
            this.sharedFiles.remove(path);
        }
    }

    @Override
    public synchronized Set<String> getAvailableFiles() {
        return Collections.unmodifiableSet(this.sharedFiles.keySet());
    }
}
