package ru.geekbrains.network.auth;

import ru.geekbrains.network.error.BadRequestException;

import java.sql.*;
import java.util.prefs.BackingStoreException;

public class DatabaseAuthService implements AuthService{
    private Connection connection;
    private Statement statement;
    private PreparedStatement preparedStatement;
    private String preUpdateNickName = "UPDATE authorization_data SET nickname = ? WHERE nickname = ?;";

    @Override
    public void Start() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:db/users.db");
        statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS authorization_data (id integer primary key autoincrement," +
                " login text, password text, nickname text);");
    }

    @Override
    public void Stop() {
        try{
            if(statement != null) statement.close();
            if(connection != null) connection.close();
        }catch (SQLException e){
            e.printStackTrace();
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
//        int updateResult = statement.executeUpdate("UPDATE authorization_data SET nickname = '" +
//                newNick + "' WHERE nickname = '" + OldNick + "';");
        preparedStatement = connection.prepareStatement(preUpdateNickName);
        preparedStatement.setString(1, newNick);
        preparedStatement.setString(2, OldNick);

        int updateResult = preparedStatement.executeUpdate();

        if(updateResult == 0) throw new BadRequestException("Error change nickname.");

        return newNick;
    }

    @Override
    public void changePassword(String nickname, String oldPassword, String newPassword) {

    }

    @Override
    public void createNewUser(String login, String password, String nickname) throws BackingStoreException {

    }

    @Override
    public void deleteUser(String nickname) {

    }
}
