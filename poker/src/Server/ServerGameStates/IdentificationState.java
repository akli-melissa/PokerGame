package Server.ServerGameStates;

import Game.Utils.Request;
import Server.ClientHandler;
import Server.Server;

public class IdentificationState extends GameState{

    public IdentificationState(ClientHandler clientHandler) {
        super(clientHandler,0);
    }


    @Override
    public void analyseRequest(String messageFromClient) {
        if(messageFromClient.matches(Request.JOIN) ){

            String name=messageFromClient.substring(17);
            //checking the name length
            if(name.length()>30){
                writeToClient(Request.LARGE_NAME);
                // checking if the name already exists
            }else if(Server.containsName(name)){
                writeToClient(Request.USED_NAME);
            }else{
                clientHandler.setClientUsername(name);
                Server.addClient(clientHandler);
                writeToClient("101 WELCOME "+name);
                System.out.println(name+" has successfully connected");
                clientHandler.setGameState(new MenuState(clientHandler,name));
            }
        }else {
            sendError();
        }
    }
}