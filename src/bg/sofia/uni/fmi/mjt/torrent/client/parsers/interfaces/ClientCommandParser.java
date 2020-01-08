package bg.sofia.uni.fmi.mjt.torrent.client.parsers.interfaces;

import bg.sofia.uni.fmi.mjt.torrent.client.commands.ClientCommand;

public interface ClientCommandParser {
    ClientCommand getCommand(String line);
}
