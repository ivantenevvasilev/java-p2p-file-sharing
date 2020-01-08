package bg.sofia.uni.fmi.mjt.torrent.client;

import bg.sofia.uni.fmi.mjt.torrent.client.models.Peer;
import bg.sofia.uni.fmi.mjt.torrent.client.repositories.InMemoryPeerRepository;
import bg.sofia.uni.fmi.mjt.torrent.client.repositories.interfaces.PeerRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class PeerMappingWriterTests {
    private ByteArrayOutputStream testOutputStream;
    private PeerRepository repository;

    private PeerMappingWriter testWriter;

    private static Set<Peer> users;

    @BeforeClass
    public static void setUpClass() {
        final int userPorts = 5331;
        users = Set.of(
                new Peer("guy", "localhost", userPorts),
                new Peer("person", "localhost", userPorts),
                new Peer("root", "localhost", userPorts)
        );
    }

    @Before
    public void setUp() {
        this.testOutputStream = new ByteArrayOutputStream();
        this.repository = new InMemoryPeerRepository();
        this.testWriter = new PeerMappingWriter(this.repository, this.testOutputStream);
    }

    @After
    public void tearDown() throws IOException {
        this.testWriter.close();
        this.testOutputStream.close();
    }

    @Test
    public void testWritingToOutputStream() {
        repository.updateRemoteUsers(users);

        StringBuilder expectedResponse = new StringBuilder();

        for (Peer user : this.repository.getRemoteUsers()) {
            expectedResponse.append(user);
            expectedResponse.append(System.lineSeparator());
        }

        this.testWriter.write();

        String result = new String(this.testOutputStream.toByteArray()).trim();
        String expected = expectedResponse.toString().trim();

        assertEquals(expected, result);
    }
}
