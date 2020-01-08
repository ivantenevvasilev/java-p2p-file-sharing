package bg.sofia.uni.fmi.mjt.torrent.client.commands;

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;

public class RegisterUserClientCommandTests {
    @Test
    public void testRegisterClientCommandSendsCorrectCommand() throws IOException {
        final String testUsername = "test";
        final int testPort = 5212;
        final String serverResponse = "Registered test" + System.lineSeparator();
        final ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
        try (BufferedReader testReader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(serverResponse.getBytes())));
             PrintWriter testWriter = new PrintWriter(resultStream, true)) {
            ClientCommand command = new RegisterUserClientCommand(testWriter, testReader, testUsername, testPort);
            String commandResult = command.execute();

            assertEquals("ServerCommand returned result different from server response.",
                    serverResponse.trim(), commandResult);

            String commandSentToServer = new String(resultStream.toByteArray()).trim();
            final String expectedCommand = "register-user test 5212";
            assertEquals(expectedCommand, commandSentToServer);
        }

    }
}
