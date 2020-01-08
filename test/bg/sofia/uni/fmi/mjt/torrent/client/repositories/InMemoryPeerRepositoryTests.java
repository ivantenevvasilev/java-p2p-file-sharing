package bg.sofia.uni.fmi.mjt.torrent.client.repositories;

import bg.sofia.uni.fmi.mjt.torrent.client.models.Peer;
import bg.sofia.uni.fmi.mjt.torrent.client.repositories.interfaces.PeerRepository;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Set;

import static junit.framework.TestCase.*;

public class InMemoryPeerRepositoryTests {

    private static Set<Peer> users;

    private PeerRepository repository;

    @BeforeClass
    public static void setUpClass() {
        final int userPorts = 5122;

        users = Set.of(
                new Peer("guy", "localhost", userPorts),
                new Peer("person", "localhost", userPorts),
                new Peer("root", "localhost", userPorts)
        );
    }

    @Before
    public void setUp() {
        this.repository = new InMemoryPeerRepository();
    }

    @Test
    public void testRepositoryUpdatesCorrectly() {

        assertTrue("Remote Users repository was not empty initially", this.repository.getRemoteUsers().isEmpty());

        this.repository.updateRemoteUsers(users);

        assertFalse("Remote Users repository was empty after adding users", this.repository.getRemoteUsers().isEmpty());
        assertEquals("Not all users were added to repository", users.size(), this.repository.getRemoteUsers().size());
        for (Peer user : users) {
            assertTrue("Users were changed upon adding to repository",
                    this.repository.getRemoteUsers().contains(user));
        }
    }

    @Test
    public void testRepositoryGetUserReturnsCorrectUser() {
        this.repository.updateRemoteUsers(users);
        Peer user = users.iterator().next();

        Peer returnedUser = this.repository.getUser(user.getUsername());

        assertEquals(user, returnedUser);
    }
}
