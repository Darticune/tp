package manager.card;

public class Card {
    private String question;
    private String answer;

    public Card(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String toString() {
        return "[Q] " + question + " | [A] " + answer;
    }
}
