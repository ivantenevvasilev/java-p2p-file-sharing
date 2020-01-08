package bg.sofia.uni.fmi.mjt.torrent.client.commands;

import bg.sofia.uni.fmi.mjt.torrent.client.commands.enums.ClientCommandsKeywords;
import bg.sofia.uni.fmi.mjt.torrent.client.models.Peer;
import bg.sofia.uni.fmi.mjt.torrent.client.parsers.DefaultResponseParser;
import bg.sofia.uni.fmi.mjt.torrent.client.parsers.interfaces.ResponseParser;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;

public class ListClientsClientCommandTests {
    private ByteArrayOutputStream testOutputStream;

    private PrintWriter testOutputWriter;

    private static ResponseParser parser;

    @BeforeClass
    public static void setUpClass() {
        parser = new DefaultResponseParser();
    }

    @Before
    public void setUp() {
        this.testOutputStream = new ByteArrayOutputStream();
        this.testOutputWriter = new PrintWriter(this.testOutputStream, true);
    }

    @After
    public void cleanUp() throws IOException {
        this.testOutputWriter.close();
        this.testOutputStream.close();
    }

    @Test
    public void testListClientsCommandReturnsClientsInCorrectFormat() throws IOException {
        final int randomPort = 4541;
        Peer[] users = {
            new Peer("user", "192.168.0.2", randomPort),
            new Peer("otherUser", "192.168.0.3", randomPort),
        };

        StringBuilder expectedServerResponseBuilder = new StringBuilder();
        StringBuilder expectedResponseFromCommand = new StringBuilder();
        expectedServerResponseBuilder.append(users.length);
        expectedServerResponseBuilder.append(System.lineSeparator());

        generateExpectedServerResponse(users, expectedServerResponseBuilder);
        generateExpectedServerResponse(users, expectedResponseFromCommand);

        ByteArrayInputStream testInputStream = new ByteArrayInputStream(
                expectedServerResponseBuilder.toString().getBytes());
        try (BufferedReader inputReader = new BufferedReader(new InputStreamReader(testInputStream))) {
            ClientCommand command = new ListClientsClientCommand(this.testOutputWriter,
                    inputReader,
                    parser);

            String result = command.execute().trim();
            String expected = expectedResponseFromCommand.toString().trim();

            assertEquals(expected, result);
        }
        String commandSentToServer = new String(this.testOutputStream.toByteArray());

        // The line separator is important here so we aren't trimming both
        String expectedCommand = ClientCommandsKeywords.LIST_USERS_COMMAND.getKeyword() + System.lineSeparator();
        assertEquals(expectedCommand, commandSentToServer);
    }

    static void generateExpectedServerResponse(Peer[] users, StringBuilder expectedServerResponse) {
        for (Peer user : users) {
            expectedServerResponse.append(user.getUsername());
            expectedServerResponse.append(" : ");
            expectedServerResponse.append(user.getHostname());
            expectedServerResponse.append(":");
            expectedServerResponse.append(user.getRemoteServerPort());
            expectedServerResponse.append(System.lineSeparator());
        }
    }
}
