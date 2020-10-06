package commands;

import manager.chapter.CardList;
import ui.Ui;

public class ExitCommand extends Command {
    public static final String COMMAND_WORD = "exit";

    @Override
    public void execute(CardList cards, Ui ui) {
        ui.showExit();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
