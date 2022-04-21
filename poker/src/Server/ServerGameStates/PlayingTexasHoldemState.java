package Server.ServerGameStates;

import Game.Card;
import Game.Player;
import Game.TexasHoldem;
import Game.utils.Request;
import Server.ClientHandler;
import Server.Room;
import Server.Server;


import java.util.List;

public class PlayingTexasHoldemState extends GameState{


    private Player player;
    private boolean endgame;


    public PlayingTexasHoldemState(ClientHandler clientHandler , Room room) {
        super(clientHandler, room);
        this.player=room.getGame().getPlayer(clientHandler.getClientUsername());
        startGame();
    }


    @Override
    public void analyseRequest(String messageFromClient) {
        if(!messageFromClient.matches("\\d\\d\\d.+")) {

            broadCastMessageToEveryone(clientHandler.getClientUsername()+":"+messageFromClient);

        }else if(messageFromClient.matches(Request.FOLD)){

            if(!room.getGame().canFold(player)){
                writeToClient(Request.INVALID);
            }else {
                player.fold(room.getGame());
                clientHandler.cancelTask(messageFromClient);//(╯°□°)╯︵ ┻━┻
                broadCastTask(Request.ACTION_RECIEVED);//(╯°□°)╯︵ ┻━┻
                broadCastMessage("510 " + clientHandler.getClientUsername() + " FOLD");
                writeToClient("400 ACCEPTED");
                rotateTurn();
            }

        }else if(messageFromClient.matches(Request.CHECK)){

            if(!room.getGame().canCheck(player)){
                writeToClient(Request.INVALID);
            }else{
                player.check(room.getGame());
                clientHandler.cancelTask(messageFromClient);//(╯°□°)╯︵ ┻━┻
                broadCastTask(Request.ACTION_RECIEVED);//(╯°□°)╯︵ ┻━┻
                broadCastMessage("511 "+ clientHandler.getClientUsername() +" CHECK");
                writeToClient("400 ACCEPTED");
                rotateTurn();
            }

        }else if(messageFromClient.matches(Request.CALL)){

            if(!room.getGame().canCall(player)){
                writeToClient(Request.INVALID);
            }else{
                player.call(room.getGame());
                clientHandler.cancelTask(messageFromClient);//(╯°□°)╯︵ ┻━┻
                broadCastTask(Request.ACTION_RECIEVED);//(╯°□°)╯︵ ┻━┻
                broadCastMessage("512 "+ clientHandler.getClientUsername() +" CALL");
                writeToClient("400 ACCEPTED");
                rotateTurn();
            }

        }else if(messageFromClient.matches(Request.RAISE)) {

            int raise = Integer.parseInt(messageFromClient.substring(10));
            if(!room.getGame().canRaise(player,raise)){
                writeToClient(Request.INVALID);
            }else {
                player.raise(room.getGame(), raise);//(╯°□°)╯︵ ┻━┻
                clientHandler.cancelTask(messageFromClient);//(╯°□°)╯︵ ┻━┻
                broadCastTask(Request.ACTION_RECIEVED);
                broadCastMessage("513 " + clientHandler.getClientUsername() + " RAISE " + raise);
                writeToClient("400 ACCEPTED");
                rotateTurn();
            }

        }else if(messageFromClient.matches(Request.ACTION_RECIEVED)){

            clientHandler.cancelTask(messageFromClient);//(╯°□°)╯︵ ┻━┻

        }else if(messageFromClient.matches(Request.CARDS_RECIEVED)){


            clientHandler.cancelTask(messageFromClient);//(╯°□°)╯︵ ┻━┻

        }else if (messageFromClient.matches(Request.QUIT)) {

            clientQuit();
            rotateTurn();

        } else if (messageFromClient.matches(Request.QUIT_RECIEVED)) {

            clientHandler.cancelTask(messageFromClient);//(╯°□°)╯︵ ┻━┻

        }else if(messageFromClient.matches(Request.GETSTATE)) {

            writeToClient("666 PlayingTexasHoldemState");

        }else if(messageFromClient.matches(Request.GETALLCARDS)) {

            leakCards();

        }else if(messageFromClient.matches(Request.GETPLAYERS)) {

            StringBuilder playerList= new StringBuilder("666 " + room.getGame().getPlayers().size() + " ALLPLAYERS");
            for(Player player : room.getGame().getPlayers()){
                playerList.append(" ").append(player.getName());
            }
            writeToClient(playerList.toString());

        }else if(messageFromClient.matches(Request.GET_ACTIVE_PLAYERS)) {

            StringBuilder playerList= new StringBuilder("666 " + room.getGame().getPlayers().size() + " PLAYERS");
            for(ClientHandler ch : room.getClientHandlers()){
                playerList.append(" ").append(ch.getClientUsername());
            }
            writeToClient(playerList.toString());

        }else{

            clientHandler.writeToClient(Request.ERROR);

        }
    }



    @Override
    public void clientQuit() {
        writeToClient(Request.QUIT_ACCEPTED);
        broadCastTask(Request.QUIT_RECIEVED);//(╯°□°)╯︵ ┻━┻
        player.quit(room.getGame());
        room.removeClient(clientHandler);
        broadCastMessage("211 " + clientHandler.getClientUsername() + " QUIT");//fixme sommmmmetimes it gives concurrent Exception??
        if(room.isEmpty()){
            Server.removeRoom(room);
        }
        clientHandler.purge();
        clientHandler.setGameState(new MenuState(clientHandler));
    }


    public void startGame(){
        if(room.isAdmin(clientHandler)){
            room.getGame().setCurrentPlayer(room.getGame().nextPlayer(0));
            rotateTurn();
        }
    }

    public void rotateTurn(){
        if(endgame) {
            return;
        }else if(room.isEmpty()) {
            endgame=true;
            return;
        }if(room.getGame().isRoundFinished()){
            broadCastMessageToEveryone("server : EndGame");
            endgame=true;

            int count=room.getGame().getPlayers().size()-room.getGame().getFoldedPlayers();
            broadCastMessageToEveryone("810 REVEALCARD "+count);
            room.getGame().setWinners();

            String winners="810 ";
            for (Player player : room.getGame().getWinners()){
                winners+=player.getName()+" ";
            }
            winners+="WIN";
            broadCastMessageToEveryone(winners);
            int i=1;
            for (Player player : room.getGame().getPlayers()){
                if(!player.hasFolded()) {
                    String playerinfo="810 ";
                    playerinfo+=player.getName()+" "+i+" HAS";
                    for (Card card : player.getCards()) playerinfo+=" "+card.toString();
                    broadCastMessageToEveryone(playerinfo);
                    i+=1;
                }
            }
            return;
        }else if(!room.turnIsUpToDate()) {
            room.updateTurn();
            switch (room.getTurn()) {
                case 0:
                    broadCastMessageToEveryone("server : preflop");
                    ((TexasHoldem) room.getGame()).fixSmallBigBlind();
                    broadCastMessageToEveryone("server : small blind and bigblind paid");
                    room.getGame().burn();
                    room.getGame().distributeCards(2);
                    notifyCardDistribution();
                    break;
                case 1:
                    broadCastMessageToEveryone("server : flop");
                    room.getGame().burn();
                    revealCards(3);
                    break;

                case 2:
                    broadCastMessageToEveryone("server : turn");
                    room.getGame().burn();
                    revealCards(1);
                    break;
                case 3:
                    broadCastMessageToEveryone("server : river");
                    room.getGame().burn();
                    revealCards(1);
                    break;
                default:
                    broadCastMessageToEveryone("server : endgame");
            }
        }

        String currentPlayerName = room.getGame().getCurrentPlayer().getName();
        for (ClientHandler ch : room.getClientHandlers()) {
            if (ch.getClientUsername().equals(currentPlayerName)) {
                ch.addTask("41[0123].*");
                ch.writeToClient("Server : It is ur turn");
            } else {
                ch.writeToClient("Server : It is " + currentPlayerName + "'s turn");
            }
        }
    }



    public void notifyCardDistribution(){
        for(Player player : room.getGame().getPlayers()){
            Card[] cards= player.getCards();
            StringBuilder cardDistribution= new StringBuilder("610 CARDS ");
            cardDistribution.append(cards.length);
            for(Card card : cards) cardDistribution.append(" ").append(card.toString());
            ClientHandler ch=room.getClientHandler(player.getName());
            //ch.addTask(Request.CARDS_RECIEVED);//(╯°□°)╯︵ ┻━┻//fixme it gives concurrent Exception??
            ch.writeToClient(cardDistribution.toString());
        }
    }

    public void revealCards(int n){
        Card[] cards= room.getGame().revealCards(n);
        StringBuilder cardDistribution= new StringBuilder("610 CARDS " + n);
        for(Card card : cards) cardDistribution.append(" ").append(card.toString());
        //broadCastTaskToEveryone(Request.CARDS_RECIEVED);//(╯°□°)╯︵ ┻━┻//fixme it gives concurrent Exception??
        broadCastMessageToEveryone(cardDistribution.toString());
    }

    public void leakCards(){
        int count=0;
        for(Player player : room.getGame().getPlayers()){
            Card[] cards= player.getCards();
            StringBuilder cardDistribution= new StringBuilder("666 " + player.getName() + " " + count + " CARDS ");
            cardDistribution.append(cards.length);
            for(Card card : cards) cardDistribution.append(" ").append(card.toString());
            writeToClient(cardDistribution.toString());
            count+=1;
        }
        List<Card> cards= room.getGame().getTable().getCards();
        StringBuilder cardDistribution= new StringBuilder("666 Table CARDS ");
        cardDistribution.append(cards.size());
        for(Card card : cards) cardDistribution.append(" ").append(card.toString());
        writeToClient(cardDistribution.toString());
    }

}

