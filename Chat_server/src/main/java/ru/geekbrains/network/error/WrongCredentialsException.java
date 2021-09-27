package ru.geekbrains.network.error;

public class WrongCredentialsException extends RuntimeException{
    public WrongCredentialsException(String massage){
        super(massage);
    }
}
