package ru.geekbrains.network.auth;

import ru.geekbrains.network.error.BadRequestException;
import ru.geekbrains.network.error.UserNotFoundException;
import ru.geekbrains.network.error.WrongCredentialsException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.BackingStoreException;

public class InMemoryAuthService implements AuthService{
    private List<User> users;

    public InMemoryAuthService(){
        this.users = new ArrayList<>(
                Arrays.asList(
                        new User("log1", "pass", "nick1"),
                        new User("log2", "pass", "nick2"),
                        new User("log3", "pass", "nick3"),
                        new User("log4", "pass", "nick4")
                )
        );
    }

    @Override
    public void Start() {
        System.out.println("Auth service started.");
    }

    @Override
    public void Stop() {
        System.out.println("Auth service stopped.");
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        for (User user : users){
            if(login.equals(user.getLogin())){
                if(password.equals(user.getPassword())){
                    return user.getNickname();
                }else {
                    throw new WrongCredentialsException("");
                }
            }
        }
        throw new UnsupportedOperationException("User not found.");
    }

    @Override
    public String changeNickname(String OldNick, String newNick) {
        for(User user : users){
            if(user.getNickname().equals(newNick)){
                throw new BadRequestException("This nick busy.");
            }
        }
        for(User user : users){
            if(user.getNickname().equals(OldNick)){
                user.setNickname(newNick);
                return newNick;
            }
        }
        throw new UserNotFoundException("User not found.");
    }

    @Override
    public void changePassword(String nickname, String oldPassword, String newPassword) {
        for(User user : users){
            if(user.getNickname().equals(nickname)){
                if(user.getPassword().equals(oldPassword)){
                    user.setPassword(newPassword);
                    break;
                }else throw new BadRequestException("Wrong password.");
            }
        }
        throw new UserNotFoundException("User not found.");
    }

    @Override
    public void createNewUser(String login, String password, String nickname){
        for(User user : users){
            if(user.getNickname().equals(nickname) || user.getPassword().equals(password)){
                throw new BadRequestException("This nick or login busy.");
            }
        }
        this.users.add(new User(login, password, nickname));

    }

    @Override
    public void deleteUser(String nickname) {
        users.removeIf(user -> user.getNickname().equals(nickname));
    }
}
