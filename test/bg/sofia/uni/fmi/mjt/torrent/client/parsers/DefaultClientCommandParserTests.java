package bg.sofia.uni.fmi.mjt.torrent.client.parsers;

import bg.sofia.uni.fmi.mjt.torrent.client.commands.*;
import bg.sofia.uni.fmi.mjt.torrent.client.parsers.interfaces.ClientCommandParser;
import bg.sofia.uni.fmi.mjt.torrent.models.Client;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DefaultClientCommandParserTests {
    private static ClientCommandParser parser;

    @BeforeClass
    public static void setUpClass() {
        parser = new DefaultClientCommandParser(null,
                null,
                null,
                null,
                null,
                null,
                null);
    }

    @Test
    public void testDefaultClientCommandParserReturnsListClientsCommandGivenCommandFormat() {
        String commandLine = "list-users";

        ClientCommand result = parser.getCommand(commandLine);

        assertTrue(result instanceof ListClientsClientCommand);
    }

    @Test
    public void testDefaultClientCommandParserReturnsListFilesCommandGivenCommandFormat() {
        String commandLine = "list-files";

        ClientCommand result = parser.getCommand(commandLine);

        assertTrue(result instanceof ListFilesClientCommand);
    }

    @Test
    public void testDefaultClientCommandParserReturnsRegisterFilesCommandGivenCommandFormat() {
        String commandline = "register C:/DOSBOX";

        ClientCommand result = parser.getCommand(commandline);

        assertTrue(result instanceof RegisterFilesClientCommand);
    }

    @Test
    public void testDefaultClientCommandParserReturnsUnregisterFilesCommandGivenCommandFormat() {
        String commandline = "unregister C:/DOSBOX";

        ClientCommand result = parser.getCommand(commandline);

        assertTrue(result instanceof UnregisterFilesClientCommand);
    }

    @Test
    public void testDefaultClientCommandParserReturnsDownloadCommandGivenCorrectFormat() {
        String commandline = "download user C:/file C:/file2";

        ClientCommand result = parser.getCommand(commandline);

        assertTrue(result instanceof DownloadFileClientCommand);
    }

    @Test
    public void testDefaultClientCommandParserReturnsNullGivenInsufficientArgumentsForDownloadCommand() {
        String commandline = "download C:/file C:/file2";

        ClientCommand result = parser.getCommand(commandline);

        assertNull(result);
    }

    @Test
    public void testDefaultClientCommandParserReturnsDisconnectCommandGivenCorrectFormat() {
        String commandline = "disconnect";

        ClientCommand result = parser.getCommand(commandline);

        assertTrue(result instanceof DisconnectClientCommand);
    }

    @Test
    public void testDefaultClientCommandParserReturnsNullGivenInvalidCommand() {
        String commandLine = "notACommmand";

        ClientCommand result = parser.getCommand(commandLine);

        assertNull(result);
    }
}
