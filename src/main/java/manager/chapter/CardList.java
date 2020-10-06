package manager.chapter;

import manager.card.Card;

import java.util.ArrayList;

public class CardList {
    private final ArrayList<Card> cards;

    public CardList() {
        cards = new ArrayList<>();
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public int getCardCount() {
        return cards.size();
    }

    public Card getCard(int cardIndex) {
        return cards.get(cardIndex);
    }

    public ArrayList<Card> getAllCards() {
        return cards;
    }
}
