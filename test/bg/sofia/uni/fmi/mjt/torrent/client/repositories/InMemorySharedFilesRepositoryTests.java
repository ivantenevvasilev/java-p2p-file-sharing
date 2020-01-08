package bg.sofia.uni.fmi.mjt.torrent.client.repositories;

import bg.sofia.uni.fmi.mjt.torrent.client.repositories.interfaces.SharedFilesRepository;
import bg.sofia.uni.fmi.mjt.torrent.models.SharedFile;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static junit.framework.TestCase.*;

public class InMemorySharedFilesRepositoryTests {
    private static Set<String> sampleFiles;
    private static final long SAMPLE_FILE_SIZE = 5312L;
    private SharedFilesRepository repository;

    @BeforeClass
    public static void setUpClass() {
        sampleFiles = Set.of(
                "/var",
                "/downloads/test.tar.gz",
                "/tmp/download.exe"
        );

    }

    @Before
    public void setUp() {
        this.repository = new InMemorySharedFilesRepository();
    }

    @Test
    public void testRepositoryRegistersSingleFile() {
        String randomFilePath = sampleFiles.iterator().next();

        this.repository.registerFile(randomFilePath, SAMPLE_FILE_SIZE);
        assertTrue(this.repository.isFileShared(randomFilePath));
        assertTrue(this.repository.getAvailableFiles().contains(randomFilePath));
    }

    @Test
    public void testRepositoryRegistersMultipleFiles() {
        Map<String, Long> files = new HashMap<>();
        for (String file : sampleFiles) {
            files.put(file, SAMPLE_FILE_SIZE);
        }

        this.repository.registerFiles(files);

        Set<String> filesInRepository = this.repository.getAvailableFiles();
        assertEquals(files.size(), filesInRepository.size());
        for (String file : files.keySet()) {
            assertTrue(this.repository.isFileShared(file));
            assertTrue(filesInRepository.contains(file));
        }
    }

    @Test(expected = FileNotFoundException.class)
    public void testInMemoryRepositoryGetFileThrowsFileNotFoundExceptionGivenNonPresentFile()
            throws FileNotFoundException {
        String file = sampleFiles.iterator().next();

        this.repository.getFileSize(file);

        fail("In Memory Client File mapping repository getFileSize did not throw given nonexistent file");
    }

    @Test
    public void testInMemoryRepositoryGetFileReturnsGivenFileSizeThatFileWasRegisteredWith()
        throws FileNotFoundException {
        String file = sampleFiles.iterator().next();
        this.repository.registerFile(file, SAMPLE_FILE_SIZE);

        Long result = this.repository.getFileSize(file);
        Long expected = SAMPLE_FILE_SIZE;

        assertEquals("File was registered with different file size than the one provided.", result, expected);
    }

    @Test
    public void testInMemoryRepositoryUnregisterFileRemovesFileFromRepository() {
        String file = sampleFiles.iterator().next();
        this.repository.registerFile(file, SAMPLE_FILE_SIZE);

        this.repository.unregisterFile(file);

        assertFalse("isFileShared reports that file is shared after unregistering", this.repository.isFileShared(file));
        assertFalse("Available files still contains file after unregistering",
                this.repository.getAvailableFiles().contains(file));
        assertEquals("Repository size was not updated after removing file.",
                0, this.repository.getAvailableFiles().size());
    }

    @Test
    public void testInMemoryRepositoryUnregisterFilesRemovesFilesFromRepository() {
        for (String file : sampleFiles) {
            this.repository.registerFile(file, SAMPLE_FILE_SIZE);
        }

        this.repository.unregisterFiles(sampleFiles);

        for (String file : sampleFiles) {
            assertFalse(
                    String.format("File %s was still reported as available " +
                                    "after unregistering via unregisterFiles method", file),
                    this.repository.isFileShared(file));
        }
        assertEquals(0, this.repository.getAvailableFiles().size());
    }
}
