import commands.Command;
import exception.InvalidInputException;
import manager.chapter.CardList;
import parser.Parser;
import ui.Ui;

public class Kaji {
    private CardList cards;
    private Ui ui;

    public Kaji() {
        ui = new Ui();
        cards = new CardList();
    }

    public void run() {
        ui.showWelcome();
        boolean isExit = false;
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                Command c = Parser.parse(fullCommand);
                c.execute(cards, ui);
                isExit = c.isExit();
            } catch (InvalidInputException e) {
                System.out.println("Invalid input given");
            }
        }
    }

    public static void main(String[] args) {
        new Kaji().run();
    }
}
