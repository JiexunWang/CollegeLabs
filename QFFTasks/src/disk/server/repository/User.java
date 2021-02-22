package disk.server.repository;

import java.io.Serializable;

public class User implements Serializable {
    private final String username;
    private String password;
    private String folderName;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.folderName = username;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
}
