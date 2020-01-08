package bg.sofia.uni.fmi.mjt.torrent.client.commands;

import bg.sofia.uni.fmi.mjt.torrent.client.models.Peer;
import bg.sofia.uni.fmi.mjt.torrent.client.parsers.DefaultResponseParser;
import bg.sofia.uni.fmi.mjt.torrent.client.parsers.interfaces.ResponseParser;
import bg.sofia.uni.fmi.mjt.torrent.client.repositories.interfaces.PeerRepository;
import bg.sofia.uni.fmi.mjt.torrent.client.repositories.InMemoryPeerRepository;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;

public class UpdateUserMappingClientCommandTests {
    @Test
    public void testUpdateUserMappingClientCommandUpdatesRemoteUsersRepository() throws IOException {
        ResponseParser parser = new DefaultResponseParser();
        final int randomPort = 1422;
        Peer[] users = {
            new Peer( "userOne", "localhost", randomPort),
            new Peer( "root", "localhost", randomPort),
            new Peer("peter", "192.168.4.2", randomPort)
        };

        StringBuilder expectedServerResponse = new StringBuilder();
        expectedServerResponse.append(users.length);
        expectedServerResponse.append(System.lineSeparator());

        ListClientsClientCommandTests.generateExpectedServerResponse(users, expectedServerResponse);

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(expectedServerResponse.toString().getBytes());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(outputStream);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            PeerRepository peerRepository = new InMemoryPeerRepository();

            ClientCommand updateUserMappingClientCommand = new UpdateUserMappingClientCommand(
                writer,
                reader,
                    peerRepository,
                parser
            );
            final int expectedInitialCount = 0;
            final int actualInitialCount = peerRepository.getRemoteUsers().size();
            assertEquals(expectedInitialCount, actualInitialCount);
            updateUserMappingClientCommand.execute();
            final int expectedCountAfterUpdate = users.length;
            final int actualCountAfterUpdate = peerRepository.getRemoteUsers().size();
            assertEquals(expectedCountAfterUpdate, actualCountAfterUpdate);
        }
    }


}
