package Game.simulator;

import Game.Card;
import Game.Player;

public class ChangeEvent {

    public Player player;
    public int nbCards;
    public Card[] drawnCards;
    public Card[] discradedCards;

    public ChangeEvent(Player player, int nbCards) {
        this.player = player;
        this.nbCards = nbCards;
    }

    public ChangeEvent(Player player, int nbCards, Card[] drawnCards, Card[] discradedCards) {
        this(player, nbCards);
        this.drawnCards = drawnCards;
        this.discradedCards = discradedCards;
    }

    public void setDrawnCards(Card[] drawnCards) {
        this.drawnCards = drawnCards;
    }
    public void setDiscradedCards(Card[] discradedCards) {
        this.discradedCards = discradedCards;
    }
    public void addDrawnCard(Card c){
        for (int i=0;i<drawnCards.length;i++){
            if(drawnCards[i]==null) {
                drawnCards[i] = c;
                return;
            }
        }
    }
}
