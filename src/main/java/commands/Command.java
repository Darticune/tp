package commands;

import manager.chapter.CardList;
import ui.Ui;

public abstract class Command {
    public abstract void execute(CardList cards, Ui ui);

    public abstract boolean isExit();
}
