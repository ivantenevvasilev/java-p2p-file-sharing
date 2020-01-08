package bg.sofia.uni.fmi.mjt.torrent.server;

import bg.sofia.uni.fmi.mjt.torrent.server.repositories.InMemoryClientRepository;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.InMemoryFileRepository;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.interfaces.ClientRepository;
import bg.sofia.uni.fmi.mjt.torrent.server.repositories.interfaces.FileRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TorrentServerTests {
    @Mock
    private SelectionKey keyMock;

    @Mock
    private ServerSocketChannel channelMock;

    @Mock
    private SocketChannel clientConnectionMock;

    @Mock
    private Selector selectorMock;

    private TorrentServer server;
    private ByteBuffer buffer;

    private static final int BUFFER_CAPACITY = 1024;

    @Before
    public void setUp() {
        this.buffer = ByteBuffer.allocate(BUFFER_CAPACITY);
        FileRepository fileRepository = new InMemoryFileRepository();
        ClientRepository clientRepository = new InMemoryClientRepository();
        server = new TorrentServer(
                fileRepository,
                clientRepository,
                this.selectorMock,
                this.buffer,
                this.channelMock
        );
    }

    @Test
    public void testReadMethodParsesCommand() throws IOException {
        String commandText = "register test /file";
        when(this.keyMock.channel()).thenReturn(this.clientConnectionMock);
        when(this.clientConnectionMock.read(this.buffer)).then(getBufferWriteCommand(commandText));
        ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
        when(this.clientConnectionMock.write(this.buffer)).then(readBufferResponse(responseStream));

        this.server.read(this.keyMock);
        final String response = new String(responseStream.toByteArray()).trim();
        final String expected = "Client test is not online. Please use register-user command to register before adding";
        assertEquals("Server did not respond with expected output.", expected, response);
    }

    private Answer<Void> readBufferResponse(ByteArrayOutputStream responseStream) {
        return (invocation) -> {
            String response = StandardCharsets.UTF_8.decode(this.buffer).toString();
        responseStream.write(response.getBytes());
        return null;
    };
    }

    private Answer<Integer> getBufferWriteCommand(String commandLine) {
        ByteArrayInputStream command = new ByteArrayInputStream(commandLine.getBytes());

        return (invocation) -> {
            final int bufferSize = 32;
            byte[] buffer = new byte[bufferSize];
            int q = command.read(buffer, 0, bufferSize);
            if (q <= 0) {
                return q;
            }
            this.buffer.put(buffer, 0, q);
            return q;
        };

    }

    @Test
    public void testReadMethodClosesSocketWhenClientDisconnects() throws IOException {
        when(this.keyMock.channel()).thenReturn(this.clientConnectionMock);
        when(this.clientConnectionMock.read(this.buffer)).thenThrow(IOException.class);

        this.server.read(this.keyMock);

        verify(this.keyMock, times(1)).cancel();
    }


   /* @Test
    public void testAcceptMethodDoesNotThrow() throws IOException {
        when(this.keyMock.channel()).thenReturn(this.channelMock);
        when(this.channelMock.accept()).thenReturn(this.clientConnectionMock);

        this.server.accept(this.keyMock);
        // I would verify that the methods are called, but they are final method
        // and can't be mocked
    }*/
}
