package ru.geekbrains.network;

import ru.geekbrains.network.error.UserNotFoundException;
import ru.geekbrains.network.error.WrongCredentialsException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Handler {
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private Server server;
    private String currentUser;
    public static final String REGEX = "%&%";

    public Handler(Socket socket, Server server) {
        try{
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            System.out.println("Handler created.");
            this.server = server;
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void handle(Socket socket){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            authorize(socket);
            try {
                while (!Thread.currentThread().isInterrupted() && socket.isConnected()) {
                    String message = in.readUTF();
                    handleMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                server.removeAuthorizedClientFromList(this);
            }
        });
//        Thread handlerThread = new Thread(() -> {
//            authorize(socket);
//            try {
//                while (!Thread.currentThread().isInterrupted() && socket.isConnected()) {
//                    String message = in.readUTF();
//                    handleMessage(message);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                server.removeAuthorizedClientFromList(this);
//            }
//        });
//        handlerThread.start();
    }

    private void authorize(Socket socket){
        while(true){
            try{
                socket.setSoTimeout(120000);
                String message = in.readUTF();
                if(message.startsWith("/auth") || message.startsWith("/register")){
                    socket.setSoTimeout(0);
                    if(handleMessage(message)) break;
                }
            }catch (IOException e){
                try{
                    if(e instanceof SocketTimeoutException){
                        throw new SocketTimeoutException();
                    }
                } catch (SocketTimeoutException socketTimeoutException) {
                    System.out.print("The connection was interrupted due to a timeout...\n");
                }
                System.out.print("Authorization error!\n");
            }
        }
    }

    private boolean handleMessage(String message){
        try {
            String[] parsed = message.split(REGEX);
            switch (parsed[0]) {
                case "/w":
                    server.sendPrivateMessage(this.currentUser, parsed[1], parsed[2], this);
                    break;
                case "/ALL":
                    server.broadcastMassage(this.currentUser, parsed[1]);
                    break;
                case "/change_nick":
                    this.currentUser = server.getAuthService().changeNickname(this.currentUser, parsed[1]);
                    server.removeAuthorizedClientFromList(this);
                    server.addAuthorizedClientToList(this);
                    sendMassage("/change_nick_ok");
                    break;
                case "/change_pass":
                    server.getAuthService().changePassword(this.currentUser, parsed[1], parsed[2]);
                    sendMassage("/change_password_ok");
                    break;
                case "/remove":
                    server.getAuthService().deleteUser(this.currentUser);
                    this.socket.close();
                    break;
                case "/register":
                    server.getAuthService().createNewUser(parsed[1], parsed[2], parsed[3]);
                    sendMassage("register_ok");
                    break;
                case "/auth":
                    this.currentUser = server.getAuthService().getNicknameByLoginAndPassword(parsed[1], parsed[2]);
                    if(server.isNickNameBusy(currentUser)){
                        sendMassage("ERROR:" + REGEX + "u`re clone!");
                    }else {
                        this.server.addAuthorizedClientToList(this);
                        sendMassage("authok:" + REGEX + this.currentUser);
                        return true;
                    }
                    break;
                default:
                    sendMassage("ERROR:" + REGEX + "command not found!");
            }
        }catch (Exception e){
            sendMassage("ERROR:" + REGEX + e.getMessage());
        }
        return false;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void sendMassage(String massage){
        try {
            this.out.writeUTF(massage);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
