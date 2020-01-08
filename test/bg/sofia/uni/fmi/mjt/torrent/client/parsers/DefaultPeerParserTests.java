package bg.sofia.uni.fmi.mjt.torrent.client.parsers;

import bg.sofia.uni.fmi.mjt.torrent.client.models.Peer;

import bg.sofia.uni.fmi.mjt.torrent.client.parsers.interfaces.PeerParser;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DefaultPeerParserTests {
    private static PeerParser parser;

    @BeforeClass
    public static void setUpClass() {
        parser = new DefaultPeerParser();
    }

    @Test
    public void testParserWithCorrectFormat() {
        final int randomPort = 1422;
        Peer expectedPeer = new Peer("user", "localhost", randomPort);
        String line = expectedPeer.toString();

        Peer result = parser.parseRemoteUser(line);

        assertEquals(expectedPeer, result);
    }

    @Test
    public void testParserWithIncorrectFormat() {
        String line = "user - localhost:4311";

        Peer result = parser.parseRemoteUser(line);

        assertNull(result);
    }

    @Test
    public void testParserWithNonNumericPort() {
        String line = "user : localhost:sandwich";

        Peer result = parser.parseRemoteUser(line);

        assertNull(result);
    }
}
