package bg.sofia.uni.fmi.mjt.torrent.server.repositories;

import bg.sofia.uni.fmi.mjt.torrent.models.SharedFile;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.interfaces.FileRepository;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class InMemoryFileRepositoryTests {
    private FileRepository fileRepository;

    private static SharedFile sampleFile;

    @BeforeClass
    public static void setUpClass() {
        sampleFile = new SharedFile("root", "/opt/google-chrome/data.sh");
    }

    @Before
    public void setUp() {
        this.fileRepository = new InMemoryFileRepository();
    }

    @Test
    public void testFileRepositoryRegisterFileAddsFileToRepository() throws FileAlreadyExistsException {
        this.fileRepository.registerFile(sampleFile);
        assertEquals("File Repository register file does not add file to repository",
                1,
                this.fileRepository.getRegisteredFiles().size());
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void testFileRepositoryRegisterFileThrowsExceptionWithDuplicateFiles() throws FileAlreadyExistsException {
        this.fileRepository.registerFile(sampleFile);
        this.fileRepository.registerFile(sampleFile);
        fail("File Repository allows duplicate registering of filed");
    }

    @Test(expected = FileNotFoundException.class)
    public void testFileRepositoryUnregisterFileThrowsExceptionWithFileMissingInRepository()
            throws FileNotFoundException {
        this.fileRepository.unregisterFile(sampleFile);
        fail("File Repository allows removal of files that are not in the repository");
    }

    @Test
    public void testFileRepositoryUnregisterFileRemovesFileFromRepository() throws FileNotFoundException,
            FileAlreadyExistsException {
        this.fileRepository.registerFile(sampleFile);
        assertEquals("File Repository register does not update registered files",
                1, this.fileRepository.getRegisteredFiles().size());
        this.fileRepository.unregisterFile(sampleFile);
        assertEquals("File Repository unregister does not update registered files",
                0, this.fileRepository.getRegisteredFiles().size());
    }

    @Test
    public void testFileRepositoryRegisterFileWithUsernameAndPathWorksTheSameAsFileOverload()
            throws FileAlreadyExistsException {
        this.fileRepository.registerFile(sampleFile.getUser(), sampleFile.getPath());

        assertEquals("File Repository register file does not update registered files",
                1 ,
                this.fileRepository.getRegisteredFiles().size());
        SharedFile file = this.fileRepository.getRegisteredFiles().iterator().next();
        assertEquals("Added file differs from registered file", sampleFile, file);
    }

    @Test
    public void testFileRepositoryUnregisterFileWithUsernameAndPathWorksTheSameAsFileOverload()
            throws FileNotFoundException, FileAlreadyExistsException {
        this.fileRepository.registerFile(sampleFile.getUser(), sampleFile.getPath());

        assertEquals("File Repository register file does not update registered files",
                1,
                this.fileRepository.getRegisteredFiles().size());

        this.fileRepository.unregisterFile(sampleFile.getUser(), sampleFile.getPath());
        assertEquals("File wasn't removed by unregister file method using user and path",
                0,
                this.fileRepository.getRegisteredFiles().size());
    }
}
