package client_api;

import java.io.IOException;

public class ChatMassageService {
    private MessageProcessor messageProcessor;
    private NetworkService networkService;

    public ChatMassageService(MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    public void connect(){
        if(isConnected()) return;
        try{
            this.networkService = new NetworkService(this);
            networkService.readMessages();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected(){
        return this.networkService != null && this.networkService.getSocket().isConnected();
    }

    public void send (String message){
        this.networkService.sendMessage(message);
    }

    public void receive(String message) {
        messageProcessor.processMessage(message);
    }
}
