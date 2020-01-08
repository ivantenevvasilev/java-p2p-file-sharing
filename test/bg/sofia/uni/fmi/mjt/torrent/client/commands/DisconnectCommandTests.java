package bg.sofia.uni.fmi.mjt.torrent.client.commands;

import bg.sofia.uni.fmi.mjt.torrent.client.repositories.InMemorySharedFilesRepository;
import bg.sofia.uni.fmi.mjt.torrent.client.repositories.interfaces.SharedFilesRepository;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

public class DisconnectCommandTests {
    private static String sampleUser;

    private ClientCommand command;

    private ByteArrayOutputStream testOutputStream;

    private PrintWriter testWriter;
    private SharedFilesRepository testRepository;

    @BeforeClass
    public static void setUpClass() {
        sampleUser = "testingUser";
    }

    @Before
    public void setUp() {
        this.testOutputStream = new ByteArrayOutputStream();
        this.testWriter = new PrintWriter(testOutputStream);
        this.testRepository = new InMemorySharedFilesRepository();
        this.command = new DisconnectClientCommand(sampleUser, testRepository, testWriter);
    }

    @Test
    public void testDisconnectCommandSendsUnregisterCommandToSererWithFilesFromRepository() {

    }
}
