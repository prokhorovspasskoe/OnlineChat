package client_api;

import client_api.client_gui.MainChatController;

import java.io.*;

public class LoggingToFile {
    private File logFile;
    private String logString;

    public LoggingToFile(String filename){
        logFile = new File(filename);
        if(!logFile.exists()){
            try {
                if(!logFile.createNewFile()){
                    MainChatController mainChatController = new MainChatController();
                    mainChatController.mainChatArea.appendText("Error create logFile!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String readLogFile(){
        String readLine = "";
        try {
            FileReader logFileReader = new FileReader(logFile);
            BufferedReader bufLogReader = new BufferedReader(logFileReader);
            readLine = bufLogReader.readLine();
            bufLogReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return readLine;
    }

    public boolean writeLogFile(String log) {
        try {
            FileWriter fileWriter = new FileWriter(logFile);
            fileWriter.append(log);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
