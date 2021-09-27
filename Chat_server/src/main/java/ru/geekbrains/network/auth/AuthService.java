package ru.geekbrains.network.auth;

import java.sql.SQLException;
import java.util.prefs.BackingStoreException;

public interface AuthService {
    void Start() throws SQLException;
    void Stop() throws SQLException;
    String getNicknameByLoginAndPassword(String login, String password) throws SQLException;
    String changeNickname(String OldNick, String newNick) throws SQLException;
    void changePassword(String nickname, String oldPassword, String newPassword);
    void createNewUser(String login, String password, String nickname) throws BackingStoreException;
    void deleteUser(String nickname);
}
