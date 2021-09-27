package client_api;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class NetworkService {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8089;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private ChatMassageService chatMassageService;

    public NetworkService(ChatMassageService chatMassageService) throws IOException {
        this.chatMassageService = chatMassageService;
        Socket socket = new Socket(HOST, PORT);
        this.out = new DataOutputStream(socket.getOutputStream());
        this.in = new DataInputStream(socket.getInputStream());
    }

    public void readMessages(){
        Thread t = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted()){
                try{
                    String message = in.readUTF();
                    chatMassageService.receive(message);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public void sendMessage(String message) {
        try{
            out.writeUTF(message);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
