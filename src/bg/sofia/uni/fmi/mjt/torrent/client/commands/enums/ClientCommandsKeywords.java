package bg.sofia.uni.fmi.mjt.torrent.client.commands.enums;

import java.util.MissingResourceException;

public enum ClientCommandsKeywords {
    LIST_USERS_COMMAND("list-users"),
    LIST_FILES_COMMAND("list-files"),
    REGISTER_USER_COMMAND("register-user"),
    REGISTER_FILES_COMMAND("register"),
    UNREGISTER_FILES_COMMAND("unregister"),
    DOWNLOAD_FILE_COMMAND("download"),
    DISCONNECT("disconnect");


    private final String keyword;

    ClientCommandsKeywords(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }
}
