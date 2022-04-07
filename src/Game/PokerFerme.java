package Game;


public class PokerFerme extends PokerGame{

    public static int ANTE=0;
    public static int CHANGING_TURN=2;



    public PokerFerme(int id, int type, int maxPlayers, int minBid, int initStack) {
        super(id, type, maxPlayers, minBid, initStack);
    }

    public PokerFerme(int type, int maxPlayers, int minBid, int initStack) {
        super(type, maxPlayers, minBid, initStack);
    }


    @Override
    public boolean isRoundFinished() {
        return bidTurn>3 || players.size()-foldedPlayers<=2;
    }



    @Override
    public boolean canResetGame(){
        int nbPlayers=0;
        for(Player p:players){
            if(!p.hasQuitted()){
                nbPlayers++;
            }
        }
        return  nbPlayers>=3 &&  nbPlayers<=8;
    }
    public boolean canStartGame(){
        return  players.size()>=3 &&  players.size()<=8;
    }


    @Override
    public boolean canCall(Player player){
        if(player==getCurrentPlayer()){
            if(bidTurn!=CHANGING_TURN){
                return ((player.getBidPerRound()<bidAmount)&&((bidAmount-player.getBidPerRound())<=player.getStack()));
            }
        }
        return false;
    }

    @Override
    public boolean canFold(Player player){
        return player==getCurrentPlayer();
    }

    @Override
    public boolean canCheck(Player player){
        if(bidTurn!=CHANGING_TURN && bidTurn!=ANTE){
            if(player==getCurrentPlayer()){
                return (player.getBidPerRound() == bidAmount);
            }
        }
        return false;
    }

    @Override
    public boolean canRaise(Player player,int raiseAmount){
        if(player==getCurrentPlayer()){
            if(bidTurn==ANTE && raiseAmount==minBid){
                return true;
            }
            if(bidTurn!=CHANGING_TURN){
                int callAmount = bidAmount-player.getBidPerRound();
                return ((raiseAmount>bidAmount)&&((callAmount)<=player.getStack()));
            }
        }
        return false;
    }


    @Override
    public boolean canChange(Player player,Card[] cards){
        return (cards.length>0
                && cards.length<5
                && player.getHand().containsAll(cards)
                && bidTurn==CHANGING_TURN
                && players.get(currentPlayer)==player);
    }

    @Override
    public Card[] change(Player player,Card[] cards){
        player.setPlayed(true);
        player.getHand().removeAll(cards);
        deck.addAll(cards);
        Card[] newCards=new Card[cards.length];
        for(int i=0 ;i<cards.length;i++){
            newCards[i]=deck.getNextCard();
        }
        player.getHand().addAll(newCards);
        return newCards;
    }

}