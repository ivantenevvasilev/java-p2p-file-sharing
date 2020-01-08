package bg.sofia.uni.fmi.mjt.torrent.server.commands;

import bg.sofia.uni.fmi.mjt.torrent.server.repositories.InMemoryClientRepository;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.interfaces.ClientRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.InetAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class RegisterUserServerCommandTests {

    private ServerCommand registerUserCommand;
    private static String sampleUserName;

    @Mock
    private SocketChannel mockChannel;

    @Mock
    private Socket nestedSocketMock;

    @Mock
    private InetAddress inetMock;

    @Before
    public void setUp() {
        final int remotePort = 5331;
        sampleUserName = "user";
        ClientRepository clientRepository = new InMemoryClientRepository();
        this.registerUserCommand = new RegisterUserServerCommand(null,
                clientRepository,
                mockChannel,
                sampleUserName, remotePort);
    }

    @Test
    public void testRegisterUserAddsUserSuccessfully() {
        final int expectedPort = 421;
        final String expectedDomain = "localhost";
        when(this.mockChannel.socket()).thenReturn(this.nestedSocketMock);
        when(this.nestedSocketMock.getPort()).thenReturn(expectedPort);
        when(this.nestedSocketMock.getInetAddress()).thenReturn(inetMock);
        when(this.inetMock.getHostAddress()).thenReturn(expectedDomain);
        String result = this.registerUserCommand.execute();
        String expected = String.format(RegisterUserServerCommand.SUCCESSFULLY_CONNECTED_MESSAGE, sampleUserName);

        assertEquals(expected, result);
    }

    @Test
    public void testRegisterUserDoesNotAddDuplicateUsers() {
        final int expectedPort = 421;
        final String expectedDomain = "localhost";
        when(this.mockChannel.socket()).thenReturn(this.nestedSocketMock);
        when(this.nestedSocketMock.getPort()).thenReturn(expectedPort);
        when(this.nestedSocketMock.getInetAddress()).thenReturn(inetMock);
        when(this.inetMock.getHostAddress()).thenReturn(expectedDomain);

        this.registerUserCommand.execute();
        String result = this.registerUserCommand.execute();
        String expected = RegisterUserServerCommand.USER_ALREADY_EXISTS_MESSAGE;

        assertEquals(expected, result);
    }
}
