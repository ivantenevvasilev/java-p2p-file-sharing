package bg.sofia.uni.fmi.mjt.torrent;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Abstracts the common logic between the TorrentServer
 * and TorrentClientServer.
 */
public abstract class AbstractServer implements Closeable {
    private static final int DEFAULT_BUFFER_SIZE = 1024;

    protected Selector selector;
    protected ByteBuffer commandBuffer;
    protected ServerSocketChannel channel;

    private boolean isRunning = true;

    protected AbstractServer(int port) throws IOException {
        this(port, DEFAULT_BUFFER_SIZE);
    }

    protected AbstractServer(int port, int bufferSize) throws IOException {
        this.selector = Selector.open();
        this.commandBuffer = ByteBuffer.allocate(bufferSize);
        this.channel = ServerSocketChannel
                .open()
                .bind(new InetSocketAddress(port));
    }

    /**
     * Created for testing purposes, could be used as a
     * way to let people specify more freely the parameters
     * that the server uses. e.g. the way the ByteBuffer is allocated
     * and its size
     */
    protected AbstractServer(Selector selector, ByteBuffer commandBuffer, ServerSocketChannel channel) {
        this.selector = selector;
        this.commandBuffer = commandBuffer;
        this.channel = channel;
    }

    public void start() throws IOException {
        channel.configureBlocking(false);

        channel.register(selector, SelectionKey.OP_ACCEPT);

        while (this.isRunning) {
            int selected = selector.select();
            if (selected <= 0) {
                continue;
            }
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isAcceptable()) {
                    this.accept(key);
                }
                else if (key.isReadable()) {
                    this.read(key);
                }

                iterator.remove();
            }
        }
    }

    /**
     * Stop the server
     */
    public void stop() {
        this.isRunning = false;
        this.selector.wakeup();
    }

    /**
     * Accept a new connection
     *
     * @param key The key for which an accept was received
     * @throws IOException In case of problems with the accept
     */
    public void accept(SelectionKey key) throws IOException {
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        SocketChannel sc = ssc.accept();
        sc.configureBlocking(false);
        sc.register(selector, SelectionKey.OP_READ);
    }

    public abstract void read(SelectionKey key);

    @Override
    public void close() throws IOException {
        this.stop();
        this.channel.close();
    }
}
