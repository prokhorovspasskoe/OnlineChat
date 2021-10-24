package ru.geekbrains.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.geekbrains.network.auth.AuthService;
import ru.geekbrains.network.auth.DatabaseAuthService;
import ru.geekbrains.network.auth.InMemoryAuthService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static final int PORT = 8089;
    private AuthService authService;
//    private List<Handler> handlers;
    private Map<String, Handler> handlers;
    private static final Logger log = LogManager.getLogger();

    public Server() {
//        this.authService = new InMemoryAuthService();
        this.authService = new DatabaseAuthService();
//        this.handlers = new ArrayList<>();
        this.handlers = new HashMap<>();
    }

    public void start(){
        try(ServerSocket serverSocket = new ServerSocket(PORT)){
            //System.out.println("Server start.");
            log.info("Server start.");
            while (true){
//                System.out.println("Waiting for connection...");
                log.info("Waiting for connection...");
                Socket socket = serverSocket.accept();
//                System.out.println("Client connected.");
                log.info("Client connected.");
                new Handler(socket, this).handle(socket);
            }
        }catch (IOException e){
            log.info("The server is disabled...");
//            System.out.println("The server is disabled...");
        }
    }

    public void broadcastMassage(String from, String message){
        message = String.format("[%s]: %s", from, message);
        for (Handler handler: handlers.values()) {
            handler.sendMassage(message);
        }
        log.info("Send broadcast massage.");
    }

    public synchronized void removeAuthorizedClientFromList(Handler handler){
        this.handlers.remove(handler.getCurrentUser());
        sendClientsOnline();
    }

    public synchronized void addAuthorizedClientToList(Handler handler){
        this.handlers.put(handler.getCurrentUser(), handler);
        sendClientsOnline();
    }

    public AuthService getAuthService() {
        return authService;
    }

    public void sendClientsOnline(){
        StringBuilder sb = new StringBuilder("/list:").append(Handler.REGEX);
        for(Handler handler: handlers.values()){
            sb.append(handler.getCurrentUser()).append(Handler.REGEX);
        }
        String message = sb.toString();
        for(Handler handler: handlers.values()){
            handler.sendMassage(message);
        }
    }

    public void sendPrivateMessage(String sender, String recipient, String message, Handler senderHandler) {
        Handler handler = handlers.get(recipient);
        if(handler == null){
            senderHandler.sendMassage(String.format("ERROR:" + Handler.REGEX + "recipient not found %s", recipient));
            return;
        }
        message = String.format("[%s] -> [%s]: %s", sender, recipient, message);
        handler.sendMassage(message);
        senderHandler.sendMassage(message);
        log.info("Send private massage.");
    }

    public boolean isNickNameBusy(String nickname){
        return this.handlers.containsKey(nickname);
    }
}
