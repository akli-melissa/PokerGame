package Client.States;

import Client.Client;
import Game.Card;
import Game.Hand;
import Game.Player;
import Game.PokerGame;
import Game.Utils.Request;


public class PlayingTexasHoldemState extends GameState {

    private int turn;
    private String username;
    private PokerGame currentGame;
    private String futureAction;



    public PlayingTexasHoldemState(Client client, String username, PokerGame currentGame) {
        super(client, 3);
        System.out.println("[Client][gameState][PlayingTexasHoldemState] playing Texas Holdem poker....");
        this.username = username;
        this.currentGame = currentGame;
        this.futureAction = "";
        this.turn=-1;
        startGame();
    }


    @Override
    public void analyseMessageToSend(String messageToSend) {
        if (messageToSend.matches(Request.FOLD)) {
            futureAction ="FOLD";
        } else if (messageToSend.matches(Request.CHECK)) {
            futureAction = "CHECK";
        } else if (messageToSend.matches(Request.CALL)) {
            futureAction = "CALL";
        } else if (messageToSend.matches(Request.RAISE)) {
            int raise = Integer.parseInt(messageToSend.substring(10));
            futureAction = "RAISE " + raise;
        }
    }

    @Override
    public void analyseComingMessage(String comingMessage) {
        if (comingMessage.matches(Request.PLAYER_FOLD)) {

            String username = comingMessage.substring(4, comingMessage.length() - 5);
            Player player = currentGame.getPlayer(username);
            player.fold(currentGame);
            rotateTurn();
            writeToServer(Request.ACTION_RECIEVED);

        } else if (comingMessage.matches(Request.PLAYER_CHECK)) {

            String username = comingMessage.substring(4, comingMessage.length() - 6);
            Player player = currentGame.getPlayer(username);
            player.check(currentGame);
            rotateTurn();
            writeToServer(Request.ACTION_RECIEVED);

        } else if (comingMessage.matches(Request.PLAYER_CALL)) {

            String username = comingMessage.substring(4, comingMessage.length() - 5);
            Player player = currentGame.getPlayer(username);
            player.call(currentGame);
            rotateTurn();
            writeToServer(Request.ACTION_RECIEVED);

        }
        else if (comingMessage.matches(Request.PLAYER_RAISE)) {

            int raise=Integer.parseInt(comingMessage.substring(comingMessage.lastIndexOf("RAISE ")));
            String username = comingMessage.substring(4, comingMessage.lastIndexOf(" RAISE"));
            Player player = currentGame.getPlayer(username);
            player.raise(currentGame,raise);
            rotateTurn();
            writeToServer(Request.ACTION_RECIEVED);

        } else if (comingMessage.matches(Request.ACTION_ACCEPTED)) {

            if (futureAction.equals("")) throw new RuntimeException("there is no action sent");
            Player player=currentGame.getPlayer(username);

            if(futureAction.equals("FOLD")){
                player.fold(currentGame);
            }else if(futureAction.equals("CHECK")){
                player.check(currentGame);
            }else if(futureAction.equals("CALL")){
                player.call(currentGame);
            }else if(futureAction.startsWith("RAISE")){
                player.raise(currentGame,Integer.parseInt(futureAction.substring(6)));
            }
            futureAction = "";
            rotateTurn();


        } else if (comingMessage.matches(Request.CARDS_DISTRIBUTION)) {

            String[] data = comingMessage.substring(10).split("\\s+");
            Hand hand=currentGame.getPlayer(username).getHand();

            if(hand.getCards().isEmpty() || hand.getCards().size()<2){
                for (int i = 1; i < data.length; i++) hand.add(new Card(data[i]));
            } else {
                for (int i = 1; i < data.length; i++) currentGame.getTable().add(new Card(data[i]));
            }
            writeToServer(Request.CARDS_RECIEVED);

        } else if (comingMessage.matches(Request.QUIT_ACCEPTED)) {

            quit();

        } else if (comingMessage.matches(Request.PLAYER_QUIT)) {

            String name = comingMessage.substring(4, comingMessage.length()-5);
            Player player=currentGame.getPlayer(name);
            player.quit(currentGame);
            writeToServer(Request.QUIT_RECIEVED);

        }
    }

    @Override
    public void quit() {
        this.client.setGameState(new MenuState(client, username));
    }

    public void startGame(){
        currentGame.setCurrentPlayer(currentGame.nextPlayer(0));
    }


    public void rotateTurn(){
        if(currentGame.isRoundFinished()){
            System.out.println("client : endgame");return;
        }

        if(turn!=currentGame.getBidTurn()){
            turn=currentGame.getBidTurn();
            switch (turn) {
                case 0:
                    System.out.println("client : small & bigBlind round");
                    break;
                case 1:
                    System.out.println("client : first betting round");
                    break;
                case 2:
                    System.out.println("client : second betting round");
                    break;
                case 3:
                    System.out.println("client : third betting round");
                    break;
                default: System.out.println("client : endgame");
            }
        }

        if(currentGame.isCurrentPlayer(username)) {
            System.out.println("client : It is ur turn");
        }else{
            System.out.println("client : It is "+currentGame.getCurrentPlayer().getName()+"'s turn");
        }
    }
}