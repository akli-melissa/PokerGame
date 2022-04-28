package Game.simulator;


import Game.Card;
import Game.Player;
import Game.PokerFerme;
import Game.utils.ChangeEvent;


import java.util.*;

public class FiveCardSimulator implements Simulator{


    public static Data simulate(Player ourplayer,PokerFerme game){
        if(game.getBidTurn()==0){
            return simulate_ante(ourplayer,game.getPlayers());
        }else if(game.getBidTurn()==1){
            return simulate_first_betting_round(ourplayer, game.getPlayers());
        }else if(game.getBidTurn()==2 || game.getBidTurn()==3){
            simulate_rest_of_the_game(ourplayer, game.getPlayers(), game.changeEvents);
        }else{
            return new Data();
        }
        return new Data();
    }

    public static Data simulate_ante(Player ourPlayer, ArrayList<Player> players){
        return MonteCarlos.simulateAnte(players.indexOf(ourPlayer)+1,players.size());
    }


    /*simulation for first betting round five card poker we assume that dealer is the first in the list*/
    public static Data simulate_first_betting_round(Player ourPlayer, ArrayList<Player> players){
        if(ourPlayer==null || players==null || players.isEmpty()) return new Data();
        long begin=System.nanoTime();
        //preparing necessary cards for simulation
        HashSet<Card> cardSet=Simulator.getCardSet();
        ourPlayer.getHand().getCards().forEach(cardSet::remove);
        ArrayList<Card> deck;

        //preparing simulation variables
        int ahead = 0;
        int tied=0;
        int behind = 0;

        //preparing all player hands
        FiveCards ourHand=new FiveCards(ourPlayer.getHand().getCards());
        LinkedHashMap<Player,FiveCards> oppHands=new LinkedHashMap<>();
        for (int i=0;i<=players.size();i++){
            Player p=players.get(i%players.size());
            if (p!=ourPlayer){
                oppHands.put(p,new FiveCards());
            }
        }
        int ourrank=Simulator.rankFiveCards(ourHand);
        int opprank;

        //simulation
        for(int i=0;i<1000000;i++){

            deck = new ArrayList<>(cardSet);
            Collections.shuffle(deck);
            for (Map.Entry<Player, FiveCards> entry : oppHands.entrySet()) {
                    entry.getValue().clear();
            }

            for (int cnt=0;cnt<5;cnt++){
                for (Map.Entry<Player, FiveCards> entry : oppHands.entrySet()) {
                    entry.getValue().addCard(deck.remove(0));
                }
            }

            //ranking hands
            boolean isahead=false;
            boolean istied=false;
            boolean isbehind=false;

            for (Map.Entry<Player, FiveCards> entry : oppHands.entrySet()) {
                if(!entry.getKey().hasFolded()){
                    opprank=Simulator.rankFiveCards(entry.getValue());
                    if(ourrank>opprank) {
                        isahead=true;
                    }else if (ourrank==opprank){
                        istied=true;
                    }else {
                        isbehind=true;
                        break;
                    }
                }
            }

            if(isbehind){
                behind++;
            }else if(istied){
                tied++;
            }else if(isahead){
                ahead++;
            }
        }
        long end=System.nanoTime();
        double time=((double)(end-begin))/1000000000;
        return (new FiveCardSimulator.Data(1000000,time,(double)ahead/10000,(double)tied/10000,(double)behind/10000));
    }


    /*simulation for second betting round five card poker we assume that dealer is the first in the list*/
    public static Data simulate_rest_of_the_game(Player ourPlayer, ArrayList<Player> players, ArrayList<ChangeEvent> events){

        long begin=System.nanoTime();
        //preparing simulation variables
        int ahead = 0;
        int tied=0;
        int behind = 0;

        //preparing all player hands
        FiveCards ourHand=new FiveCards(ourPlayer.getHand().getCards());
        LinkedHashMap<Player,FiveCards> oppHands=new LinkedHashMap<>();
        for (int i=0;i<=players.size();i++){
            Player p=players.get(i%players.size());
            if (p!=ourPlayer){
                oppHands.put(p,new FiveCards());
            }
        }
        int ourrank=Simulator.rankFiveCards(ourHand);
        int opprank;


        //preparing drawing phase player hands
        ChangeEvent ourDraw=null;
        for (ChangeEvent ce : events){
            if(ce.player==ourPlayer){
                ourDraw= ce;
            }
        }

        //preparing necessary cards for simulation
        HashSet<Card> cardSet=Simulator.getCardSet();
        ArrayList<Card> ourDiscardedCardes=new ArrayList<>();
        ourPlayer.getHand().getCards().forEach(cardSet::remove);
        if (ourDraw!=null){
            for (Card c : ourDraw.discradedCards){
                cardSet.remove(c);
                ourDiscardedCardes.add(c);
            }
        }
        ArrayList<Card> deck;

        //simulation
        for(int i=0;i<1000000;i++){

            //preparing deck and hands
            deck = new ArrayList<>(cardSet);
            Collections.shuffle(deck);
            for (Map.Entry<Player, FiveCards> entry : oppHands.entrySet()) {
                entry.getValue().clear();
            }

            //distributing cards
            for (int cnt=0;cnt<5;cnt++){
                for (Map.Entry<Player, FiveCards> entry : oppHands.entrySet()) {
                    entry.getValue().addCard(deck.remove(0));
                }
            }

            //drawing cards
            for(ChangeEvent e : events){
                if(e.player!=ourPlayer){
                    oppHands.get(e.player).discardAndDrawRandomlessly(e.nbCards,deck);
                }else{
                    deck.addAll(Arrays.asList(e.discradedCards));
                }
            }

            //ranking hands
            boolean isahead=false;
            boolean istied=false;
            boolean isbehind=false;

            for (Map.Entry<Player, FiveCards> entry : oppHands.entrySet()) {
                if(!entry.getKey().hasFolded()){
                    opprank=Simulator.rankFiveCards(entry.getValue());
                    if(ourrank>opprank) {
                        isahead=true;
                    }else if (ourrank==opprank){
                        istied=true;
                    }else {
                        isbehind=true;
                        break;
                    }
                }
            }
            if(isbehind){
                behind++;
            }else if(istied){
                tied++;
            }else if(isahead){
                ahead++;
            }
        }

        long end=System.nanoTime();
        double time=((double)(end-begin))/1000000000;
        return (new FiveCardSimulator.Data(1000000,time,(double)ahead/10000,(double)tied/10000,(double)behind/10000));
    }




    public static void main(String[] args) {
        Player player=new Player("ala",1000);
        player.getHand().add(new Card("T1"));
        player.getHand().add(new Card("C13"));
        player.getHand().add(new Card("S5"));
        player.getHand().add(new Card("S11"));
        player.getHand().add(new Card("D3"));
        ArrayList<Player> players=new ArrayList<>();
        players.add(new Player("hu",976));
        players.add(new Player("ddu",976));
        players.add(player);
        ArrayList<ChangeEvent> events=new ArrayList<>();
        events.add(new ChangeEvent(players.get(1),4));
        events.add(new ChangeEvent(players.get(0),3));
        Card[] cards1={new Card("T1"),new Card("C13")};
        Card[] cards2={new Card("C6"),new Card("D6")};
        events.add(new ChangeEvent(player,2,cards1,cards2));
        System.out.println( simulate_first_betting_round(player,players));
        System.out.println( simulate_rest_of_the_game(player,players,events));
    }


    static class Data{
        int tries;
        double time;
        double ahead;
        double tied;
        double behind;


        public Data(int tries ,double time, double ahead, double tied, double behind) {
            this.tries=tries;
            this.time=time;
            this.ahead = ahead;
            this.tied = tied;
            this.behind = behind;
        }

        public Data() {
            this.time=0;
            this.ahead = -1;
            this.tied = -1;
            this.behind = -1;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "tries=" + tries +
                    ", time=" + time +
                    ", ahead=" + ahead +
                    ", tied=" + tied +
                    ", behind=" + behind +
                    '}';
        }
    }




}
