package parser;

import commands.AddCommand;
import commands.Command;
import commands.ExitCommand;
import commands.ListCommand;
import exception.InvalidInputException;



public class Parser {
    public static Command parse(String fullCommand) throws InvalidInputException {
        String[] commandTypeAndArgs = splitCommandTypeAndArgs(fullCommand);
        String commandType = commandTypeAndArgs[0].trim().toLowerCase();
        String commandArgs = commandTypeAndArgs[1].trim();

        switch (commandType) {
        case ListCommand.COMMAND_WORD:
            return prepareList(commandArgs);
        case AddCommand.COMMAND_WORD:
            return prepareAdd(commandArgs);
        case ExitCommand.COMMAND_WORD:
            return prepareExit(commandArgs);
        default:
            throw new InvalidInputException();
        }
    }

    private static String[] splitCommandTypeAndArgs(String userCommand) {
        String[] commandTypeAndParams = userCommand.trim().split(" ", 2);
        if (commandTypeAndParams.length != 2) {
            commandTypeAndParams = new String[]{commandTypeAndParams[0], ""};
        }
        return commandTypeAndParams;
    }

    private static Command prepareList(String commandArgs) throws InvalidInputException {
        if (!commandArgs.isEmpty()) {
            throw new InvalidInputException();
        }
        return new ListCommand();
    }

    private static Command prepareAdd(String commandArgs) throws InvalidInputException {
        try {
            String[] args = commandArgs.split(AddCommand.QUESTION_ANSWER_PREFIX, 2);
            String question = parseQuestion(args[0]);
            String answer = parseAnswer(args[1]);
            return new AddCommand(question, answer);
        } catch (IndexOutOfBoundsException | InvalidInputException e) {
            throw new InvalidInputException();
        }
    }

    private static String parseQuestion(String arg) throws InvalidInputException {
        if (!(arg.trim().toLowerCase().startsWith(AddCommand.QUESTION_PREFIX))) {
            throw new InvalidInputException();
        }

        String question = arg.substring(2).trim();
        if (question.isEmpty()) {
            throw new InvalidInputException();
        }

        return question;
    }

    private static String parseAnswer(String arg) throws InvalidInputException {
        if (!(arg.trim().toLowerCase().startsWith(AddCommand.ANSWER_PREFIX))) {
            throw new InvalidInputException();
        }

        String answer = arg.substring(2).trim();
        if (answer.isEmpty()) {
            throw new InvalidInputException();
        }

        return answer;
    }

    private static Command prepareExit(String commandArgs) throws InvalidInputException {
        if (!commandArgs.isEmpty()) {
            throw new InvalidInputException();
        }
        return new ExitCommand();
    }
}
