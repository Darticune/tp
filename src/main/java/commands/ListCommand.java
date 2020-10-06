package commands;

import manager.card.Card;
import manager.chapter.CardList;
import ui.Ui;

import java.util.ArrayList;

public class ListCommand extends Command {
    public static final String COMMAND_WORD = "list";

    @Override
    public void execute(CardList cards, Ui ui) {
        ArrayList<Card> allCards = cards.getAllCards();
        int cardCount = cards.getCardCount();
        ui.showCardList(allCards, cardCount);
    }

    @Override
    public boolean isExit() {
        return false;
    }
}
