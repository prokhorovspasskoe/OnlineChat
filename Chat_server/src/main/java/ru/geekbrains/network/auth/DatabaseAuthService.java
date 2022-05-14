package ru.geekbrains.network.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.geekbrains.network.error.BadRequestException;

import java.sql.*;
import java.util.prefs.BackingStoreException;

public class DatabaseAuthService implements AuthService{
    private Connection connection;
    private Statement statement;
    private static final Logger log = LogManager.getLogger();

    @Override
    public void Start() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:db/users.db");
        statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS authorization_data (id integer primary key autoincrement," +
                " login text, password text, nickname text);");
        log.info("Database start.");
    }

    @Override
    public void Stop() {
        try{
            if(statement != null) statement.close();
            if(connection != null) connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            log.info("Database stop.");
        }
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT nickname FROM authorization_data WHERE login = " + login +
                " password = " + password + ";");

        return resultSet.getString(0);
    }

    @Override
    public String changeNickname(String OldNick, String newNick) throws SQLException {
        String preUpdateNickName = "UPDATE authorization_data SET nickname = ? WHERE nickname = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(preUpdateNickName);
        preparedStatement.setString(1, newNick);
        preparedStatement.setString(2, OldNick);

        int updateResult = preparedStatement.executeUpdate();

        if(updateResult == 0) {
            log.error("Error change nickname.");
            throw new BadRequestException("Error change nickname.");
        }

        return newNick;
    }

    @Override
    public void changePassword(String nickname, String oldPassword, String newPassword) throws SQLException {
        String preUpdateNickName = "UPDATE authorization_data SET password = ? WHERE password = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(preUpdateNickName);
        preparedStatement.setString(1, newPassword);
        preparedStatement.setString(2, oldPassword);

        int updateResult = preparedStatement.executeUpdate();

        if(updateResult == 0) {
            log.error("Error change password.");
            throw new BadRequestException("Error change password.");
        }
    }

    @Override
    public void createNewUser(String login, String password, String nickname) throws BackingStoreException {

    }

    @Override
    public void deleteUser(String nickname) {

    }
}
