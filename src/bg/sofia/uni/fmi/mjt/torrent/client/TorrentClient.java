package bg.sofia.uni.fmi.mjt.torrent.client;

import bg.sofia.uni.fmi.mjt.torrent.client.commands.ClientCommand;
import bg.sofia.uni.fmi.mjt.torrent.client.commands.RegisterUserClientCommand;
import bg.sofia.uni.fmi.mjt.torrent.client.parsers.DefaultClientCommandParser;
import bg.sofia.uni.fmi.mjt.torrent.client.parsers.DefaultResponseParser;
import bg.sofia.uni.fmi.mjt.torrent.client.parsers.interfaces.ClientCommandParser;
import bg.sofia.uni.fmi.mjt.torrent.client.parsers.interfaces.ResponseParser;
import bg.sofia.uni.fmi.mjt.torrent.client.repositories.InMemorySharedFilesRepository;
import bg.sofia.uni.fmi.mjt.torrent.client.repositories.InMemoryPeerRepository;
import bg.sofia.uni.fmi.mjt.torrent.client.repositories.interfaces.SharedFilesRepository;
import bg.sofia.uni.fmi.mjt.torrent.client.repositories.interfaces.PeerRepository;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * A class meant to connect to a TorrentServer
 */
public class TorrentClient implements Closeable {
    private static final String USERNAME_TAKEN_SERVER_RESPONSE = "User with given username is already online";

    private static final int DEFAULT_SERVER_PORT = 3553;
    private static final String EXIT_COMMAND = "exit";
    private final Scanner inputScanner;
    private final ResponseParser responseParser;
    private final PrintWriter outputWriter;
    private final SharedFilesRepository fileMappingRepository;
    private PrintWriter writer;
    private BufferedReader reader;
    private Socket socket;
    private PeerRepository peerRepository;
    private String username;
    private int localServerPort;
    private PeerMappingDaemonThread backgroundSyncThread;
    private TorrentShareServerThread fileShareThread;

    public TorrentClient(String torrentServerHostName,
                         int torrentServerPort) throws IOException {
        this(torrentServerHostName, torrentServerPort, DEFAULT_SERVER_PORT);
    }

    public TorrentClient(String torrentServerHostName,
                         int torrentServerPort,
                         int localServerPort) throws IOException {
        this(torrentServerHostName,
                torrentServerPort,
                localServerPort,
                new InMemoryPeerRepository(),
                new InMemorySharedFilesRepository(),
                System.in,
                System.out);
    }

    public TorrentClient(String torrentServerHostName,
                         int torrentServerPort,
                         int localServerPort,
                         PeerRepository clientRepository,
                         SharedFilesRepository fileMappingRepository,
                         InputStream dataInputStream,
                         OutputStream dataOutputStream) throws IOException {
        this.localServerPort = localServerPort;
        this.fileMappingRepository = fileMappingRepository;
        this.socket = new Socket(torrentServerHostName, torrentServerPort);
        this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.writer = new PrintWriter(this.socket.getOutputStream(), true);
        this.inputScanner = new Scanner(dataInputStream);
        this.outputWriter = new PrintWriter(dataOutputStream, true);
        this.responseParser = new DefaultResponseParser();
        this.peerRepository = clientRepository;
    }


    public void start() throws IOException {
        this.registerUser();
        ClientCommandParser commandParser = new DefaultClientCommandParser(this.writer,
                this.reader,
                this.peerRepository,
                this.fileMappingRepository,
                this.responseParser,
                this.username,
                this.outputWriter);

        // Start background sync task
        PeerMappingDaemonThread daemonThread = new PeerMappingDaemonThread(this.writer,
                this.reader,
                this.peerRepository,
                this.responseParser);
        daemonThread.start();
        this.backgroundSyncThread = daemonThread;

        // Start file sharing server
        TorrentShareServerThread fileShareThread = new TorrentShareServerThread(this.localServerPort, this.fileMappingRepository);
        fileShareThread.start();
        this.fileShareThread = fileShareThread;

        String inputLineToExecute = this.inputScanner.nextLine();

        while (!inputLineToExecute.equals(EXIT_COMMAND)) {
            ClientCommand command = commandParser.getCommand(inputLineToExecute);

            String response = command == null ? "Invalid command" : command.execute();

            this.outputWriter.println(response);
            if (inputLineToExecute.trim().startsWith("disconnect")) {
                this.close();
                break;
            }
            inputLineToExecute = this.inputScanner.nextLine();
        }
    }

    public void registerUser() {
        this.outputWriter.println("Enter a username to log in with: ");
        String response = USERNAME_TAKEN_SERVER_RESPONSE;
        String username = null;
        while ((response.equals(USERNAME_TAKEN_SERVER_RESPONSE)) || response.equals("Invalid command")) {
            username = this.inputScanner.nextLine();

            ClientCommand command = new RegisterUserClientCommand(this.writer,
                    this.reader,
                    username,
                    this.localServerPort);
            try {
                response = command.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            outputWriter.println(response);
        }
        this.username = username;
    }

    @Override
    public void close() throws IOException {
        this.socket.close();
        this.reader.close();
        this.writer.close();
        this.inputScanner.close();
        this.backgroundSyncThread.shutDown();
        this.fileShareThread.shutDown();
    }
}
