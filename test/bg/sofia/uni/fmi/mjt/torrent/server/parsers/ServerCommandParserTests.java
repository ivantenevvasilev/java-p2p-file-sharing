package bg.sofia.uni.fmi.mjt.torrent.server.parsers;

import bg.sofia.uni.fmi.mjt.torrent.server.commands.*;

import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;

public class ServerCommandParserTests {
    private static ServerCommandParser serverCommandParser;

    @BeforeClass
    public static void setUpClass() {
        serverCommandParser = new ServerCommandParser(null, null, null);
    }

    @Test
    public void testServerCommandParserReturnsListFilesServerCommandGivenExpectedFormat() {
        final String commandLine = "list-files";

        ServerCommand result = serverCommandParser.getCommand(commandLine);

        assertTrue("Server ServerCommand Parser did not return List Files command when given correct format",
                result instanceof ListFilesServerCommand);
    }

    @Test
    public void testServerCommandParserReturnsListUsersServerCommandGivenExpectedFormat() {
        final String commandLine = "list-users";

        ServerCommand result = serverCommandParser.getCommand(commandLine);

        assertTrue("Server ServerCommand Parser did not return List Users command when given correct format",
                result instanceof ListUsersServerCommand);
    }

    @Test
    public void testServerCommandParserReturnsRegisterUserCommandGivenExpectedFormat() {
        final String commandLine = "register-user user 4212";
        ServerCommand result = serverCommandParser.getCommand(commandLine);

        assertTrue("Server ServerCommand Parser did not return Register User command when given correct format",
                result instanceof RegisterUserServerCommand);
    }

    @Test
    public void testServerCommandParserReturnsNullWhenGivenRegisterUserCommandWithoutPort() {
        final String commandLine = "register-user user";

        ServerCommand result = serverCommandParser.getCommand(commandLine);

        assertNull("Server ServerCommand Parser returned command when given too few arguments for register user",
                result);
    }

    @Test
    public void testServerCommandParserReturnsNullWhenGivenRegisterUserCommandWithNonNumericPort() {
        final String commandLine = "register-user user notanumber53haha";

        ServerCommand result = serverCommandParser.getCommand(commandLine);

        assertNull("Server ServerCommand Parser returned command when given too non-numeric port for register user",
                result);
    }

    @Test
    public void testServerCommandParserReturnsRegisterFilesCommandGivenExpectedFormat() {
        final String commandLine = "register user /var/test /mn/external/usb/file";
        ServerCommand result = serverCommandParser.getCommand(commandLine);

        assertTrue("Server ServerCommand Parser did not return Register Files ServerCommand given correct format",
                result instanceof RegisterFilesServerCommand);
    }

    @Test
    public void testServerCommandParserReturnsNullWhenGivenRegisterFilesCommandWithInsufficientParameters() {
        final String commandLine = "register user";
        ServerCommand result = serverCommandParser.getCommand(commandLine);

        assertNull("Server ServerCommand Parser returned command when given too few arguments for register user",
                result);
    }

    @Test
    public void testServerCommandParserReturnsUnregisterFilesCommandGivenExpectedFormat() {
        final String commandLine = "unregister user /var/test";
        ServerCommand result = serverCommandParser.getCommand(commandLine);
        assertTrue(result instanceof UnregisterFilesServerCommand);
    }

    @Test
    public void testServerCommandParserReturnsUnregisterUserCommandGivenExpectedFormat() {
        final String commandLine = "unregister-user user";

        ServerCommand result = serverCommandParser.getCommand(commandLine);

        assertTrue(result instanceof UnregisterUserServerCommand);
    }

    @Test
    public void testServerCommandParserReturnsNullForUnregisterUserCommandGivenInsufficientParameters() {
        final String commandLine = "unregister-user";

        ServerCommand result = serverCommandParser.getCommand(commandLine);

        assertNull(result);
    }

    @Test
    public void testServerCommandParserReturnsNullGivenUnknownCommand() {
        final String commandLine = "make me rich";

        ServerCommand result = serverCommandParser.getCommand(commandLine);

        assertNull(result);
    }

    @Test
    public void testServerCommandParserWithNullShouldNotThrowExceptionAndShouldReturnNull() {
        ServerCommand result = serverCommandParser.getCommand(null);

        assertNull(result);
    }
}
