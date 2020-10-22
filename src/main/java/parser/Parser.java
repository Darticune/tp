package parser;

import access.Access;

import commands.AddCommand;
import commands.BackCommand;
import commands.Command;
import commands.EditCommand;
import commands.ExitCommand;
import commands.GoCommand;
import commands.HelpCommand;
import commands.HistoryCommand;
import commands.ListCommand;
import commands.ListDueCommand;
import commands.PreviewCommand;
import commands.RemoveCommand;
import commands.RescheduleCommand;
import commands.ReviseCommand;
import commands.HistoryCommand;
import commands.PreviewCommand;
import commands.ExcludeCommand;

import exception.IncorrectAccessLevelException;
import exception.InvalidFileFormatException;
import exception.InvalidInputException;
import storage.Storage;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;


import static common.Messages.MESSAGE_EXTRA_ARGS;
import static common.Messages.MESSAGE_INCORRECT_ACCESS;
import static common.Messages.MESSAGE_MISSING_ARGS;
import static common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

public class Parser {
    private static final String QUESTION_ANSWER_PREFIX = " \\| ";
    private static final String QUESTION_PREFIX = "q:";
    private static final String ANSWER_PREFIX = "a:";

    private static final String ADMIN_LEVEL = "admin";
    private static final String MODULE_LEVEL = "module";
    private static final String CHAPTER_LEVEL = "chapter";

    public static Command parse(String fullCommand, Access access)
            throws InvalidInputException, IncorrectAccessLevelException {
        String[] commandTypeAndArgs = splitCommandTypeAndArgs(fullCommand);
        String commandType = commandTypeAndArgs[0].trim().toLowerCase();
        String commandArgs = commandTypeAndArgs[1].trim();

        System.out.println("Command Type: " + commandType);

        switch (commandType) {
        case ListCommand.COMMAND_WORD:
            return prepareList(commandArgs);
        case AddCommand.COMMAND_WORD:
            return prepareAdd(commandArgs, access);
        case RemoveCommand.COMMAND_WORD:
            return prepareRemove(commandArgs);
        case ReviseCommand.COMMAND_WORD:
            return prepareRevise(commandArgs, access);
        case ExitCommand.COMMAND_WORD:
            return prepareExit(commandArgs);
        case HelpCommand.COMMAND_WORD:
            return prepareHelp(commandArgs);
        case EditCommand.COMMAND_WORD:
            return prepareEdit(commandArgs, access);
        case BackCommand.COMMAND_WORD:
            return prepareBack(commandArgs);
        case GoCommand.COMMAND_WORD:
            return prepareGo(commandArgs);
        case ListDueCommand.COMMAND_WORD:
            return prepareListDue(commandArgs);
        case HistoryCommand.COMMAND_WORD:
            return prepareHistory(commandArgs);
        case PreviewCommand.COMMAND_WORD:
            return preparePreview(commandArgs);
        case RescheduleCommand.COMMAND_WORD:
            return prepareReschedule(commandArgs);
        case ExcludeCommand.COMMAND_WORD:
            return prepareExclude(commandArgs);
        default:
            throw new InvalidInputException("There is no such command type.\n");
        }
    }

    private static Command prepareHistory(String commandArgs) throws InvalidInputException {
        if (commandArgs.isEmpty()) {
            LocalDate date = java.time.LocalDate.now();
            commandArgs = date.toString();
        }

        try {
            LocalDate date = LocalDate.parse(commandArgs);
        } catch (DateTimeParseException e) {
            throw new InvalidInputException("The date should be in the format of yyyy-MM-dd\n"
                    + HistoryCommand.MESSAGE_USAGE);
        }

        return new HistoryCommand(commandArgs);
    }

    private static Command prepareGo(String commandArgs) throws InvalidInputException {
        if (commandArgs.isEmpty()) {
            throw new InvalidInputException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    GoCommand.COMMAND_WORD) + GoCommand.MESSAGE_USAGE);
        }
        return new GoCommand(commandArgs);
    }

    private static Command prepareBack(String commandArgs) throws InvalidInputException {
        if (!commandArgs.isEmpty()) {
            throw new InvalidInputException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    BackCommand.COMMAND_WORD) + BackCommand.MESSAGE_USAGE);
        }
        return new BackCommand();
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
            throw new InvalidInputException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ListCommand.COMMAND_WORD) + ListCommand.MESSAGE_USAGE);
        }
        return new ListCommand();
    }

    private static Command prepareAdd(String commandArgs, Access access)
            throws InvalidInputException, IncorrectAccessLevelException {
        if (access.isChapterLevel()) {
            if (commandArgs.isEmpty()) {
                throw new InvalidInputException(MESSAGE_MISSING_ARGS + AddCommand.CARD_MESSAGE_USAGE);
            }
            return prepareAddCard(commandArgs);
        } else if (access.isModuleLevel()) {
            if (commandArgs.isEmpty()) {
                throw new InvalidInputException(MESSAGE_MISSING_ARGS + AddCommand.CHAPTER_MESSAGE_USAGE);
            }
            return prepareAddChapter(commandArgs);
        } else if (access.isAdminLevel()) {
            if (commandArgs.isEmpty()) {
                throw new InvalidInputException(MESSAGE_MISSING_ARGS + AddCommand.MODULE_MESSAGE_USAGE);
            }
            return prepareAddModule(commandArgs);
        } else {
            assert !access.isChapterLevel() && !access.isAdminLevel() && !access.isModuleLevel() : access.getLevel();
            throw new IncorrectAccessLevelException(String.format(MESSAGE_INCORRECT_ACCESS,
                    AddCommand.COMMAND_WORD));
        }
    }

    private static Command prepareAddCard(String commandArgs) throws InvalidInputException {
        try {
            String[] args = commandArgs.split(QUESTION_ANSWER_PREFIX, 2);
            String question = parseQuestion(args[0]);
            String answer = parseAnswer(args[1]);
            if (question.isEmpty() || answer.isEmpty()) {
                throw new InvalidInputException("The content for question / answer is empty.\n"
                        + AddCommand.CARD_MESSAGE_USAGE);
            }
            return new AddCommand(question, answer, CHAPTER_LEVEL);
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidInputException("The format for the add command is incorrect.\n"
                    + AddCommand.CARD_MESSAGE_USAGE);
        }
    }

    private static Command prepareAddChapter(String commandArgs) {
        return new AddCommand(commandArgs, MODULE_LEVEL);
    }

    private static Command prepareAddModule(String commandArgs) {
        return new AddCommand(commandArgs, ADMIN_LEVEL);
    }

    private static Command prepareRemove(String commandArgs) throws InvalidInputException {
        int removeIndex;
        if (commandArgs.isEmpty()) {
            throw new InvalidInputException("The index for module / chapter / flashcard is missing.\n"
                    + RemoveCommand.MESSAGE_USAGE);
        }
        try {
            removeIndex = Integer.parseInt(commandArgs) - 1;
        } catch (NumberFormatException e) {
            throw new InvalidInputException("The index for module / chapter / flashcard should be an integer.\n"
                    + RemoveCommand.MESSAGE_USAGE);
        }
        return new RemoveCommand(removeIndex);
    }

    private static Command prepareEdit(String commandArgs, Access access)
            throws InvalidInputException, IncorrectAccessLevelException {
        if (access.isChapterLevel() && commandArgs.isEmpty()) {
            throw new InvalidInputException(MESSAGE_MISSING_ARGS + EditCommand.CARD_MESSAGE_USAGE);
        }
        if (access.isAdminLevel() && commandArgs.isEmpty()) {
            throw new InvalidInputException(MESSAGE_MISSING_ARGS + EditCommand.MODULE_MESSAGE_USAGE);
        }
        if (access.isModuleLevel() && commandArgs.isEmpty()) {
            throw new InvalidInputException(MESSAGE_MISSING_ARGS + EditCommand.CHAPTER_MESSAGE_USAGE);
        }


        if (access.isChapterLevel()) {
            return prepareEditCard(commandArgs);
        } else if (access.isAdminLevel()) {
            return prepareEditModule(commandArgs);
        } else if (access.isModuleLevel()) {
            return prepareEditChapter(commandArgs);
        } else {
            assert !access.isChapterLevel() && !access.isAdminLevel() && !access.isModuleLevel() : access.getLevel();
            throw new IncorrectAccessLevelException(String.format(MESSAGE_INCORRECT_ACCESS,
                    EditCommand.COMMAND_WORD));
        }
    }

    private static Command prepareEditCard(String commandArgs) throws InvalidInputException {
        try {
            String[] args = commandArgs.split(" ", 2);
            if (args[0].trim().isEmpty()) {
                throw new InvalidInputException("The flashcard number is missing.\n"
                        + EditCommand.CARD_MESSAGE_USAGE);
            }

            int editIndex = Integer.parseInt(args[0].trim()) - 1;

            String[] questionAndAnswer = args[1].trim().split(QUESTION_ANSWER_PREFIX, 2);
            String question = parseQuestion(questionAndAnswer[0]);
            String answer = parseAnswer(questionAndAnswer[1]);

            if (question.isEmpty() && answer.isEmpty()) {
                throw new InvalidInputException("The content for question and answer are both empty.\n"
                        + EditCommand.CARD_MESSAGE_USAGE);
            }

            return new EditCommand(editIndex, question, answer, CHAPTER_LEVEL);
        } catch (NumberFormatException e) {
            throw new InvalidInputException("The flashcard number needs to be an integer.\n"
                    + EditCommand.CARD_MESSAGE_USAGE);
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidInputException("The format for the edit command is incorrect.\n"
                    + EditCommand.CARD_MESSAGE_USAGE);
        }
    }

    private static Command prepareEditModule(String commandArgs)
            throws InvalidInputException, IncorrectAccessLevelException {
        try {
            String[] args = commandArgs.split(" ", 2);
            if (args[0].trim().isEmpty()) {
                throw new InvalidInputException("The module number is missing.\n"
                        + EditCommand.MODULE_MESSAGE_USAGE);
            }

            if (args[1].trim().isEmpty()) {
                throw new InvalidInputException("The module name is missing.\n"
                        + EditCommand.MODULE_MESSAGE_USAGE);
            }

            if (containsCardPrefix(args[1].trim().toLowerCase())) {
                throw new IncorrectAccessLevelException("This command should be called at chapter level only.\n");
            }

            int editIndex = Integer.parseInt(args[0].trim()) - 1;
            return new EditCommand(editIndex, args[1].trim().toLowerCase(), ADMIN_LEVEL);
        } catch (NumberFormatException e) {
            throw new InvalidInputException("The module number needs to be an integer.\n"
                    + EditCommand.MODULE_MESSAGE_USAGE);
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidInputException("The format for the edit command is incorrect.\n"
                    + EditCommand.MODULE_MESSAGE_USAGE);
        }
    }

    private static Command prepareEditChapter(String commandArgs)
            throws InvalidInputException, IncorrectAccessLevelException {
        try {
            String[] args = commandArgs.split(" ", 2);
            if (args[0].trim().isEmpty()) {
                throw new InvalidInputException("The chapter number is missing.\n"
                        + EditCommand.CHAPTER_MESSAGE_USAGE);
            }

            if (args[1].trim().isEmpty()) {
                throw new InvalidInputException("The chapter name is missing.\n"
                        + EditCommand.CHAPTER_MESSAGE_USAGE);
            }

            if (containsCardPrefix(args[1].trim().toLowerCase())) {
                throw new IncorrectAccessLevelException("This command should be called at chapter level only.\n");
            }

            int editIndex = Integer.parseInt(args[0].trim()) - 1;
            return new EditCommand(editIndex, args[1].trim().toLowerCase(), MODULE_LEVEL);
        } catch (NumberFormatException e) {
            throw new InvalidInputException("The chapter number needs to be an integer.\n"
                    + EditCommand.CHAPTER_MESSAGE_USAGE);
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidInputException("The format for the edit command is incorrect.\n"
                    + EditCommand.CHAPTER_MESSAGE_USAGE);
        }
    }

    private static boolean containsCardPrefix(String arg) {
        return arg.contains("q:") || arg.contains("a:");
    }

    private static String parseQuestion(String arg) throws InvalidInputException {
        if (!(arg.trim().toLowerCase().startsWith(QUESTION_PREFIX))) {
            throw new InvalidInputException("There needs to be a \"q:\" prefix before the question.\n"
                    + "Example: " + AddCommand.COMMAND_WORD + AddCommand.CARD_PARAMETERS + "\n"
                    + "         " + EditCommand.COMMAND_WORD + EditCommand.CARD_PARAMETERS);
        }

        return arg.substring(2).trim();
    }

    private static String parseAnswer(String arg) throws InvalidInputException {
        if (!(arg.trim().toLowerCase().startsWith(ANSWER_PREFIX))) {
            throw new InvalidInputException("There needs to be a \"a:\" prefix before the answer.\n"
                    + "Example: " + AddCommand.COMMAND_WORD + AddCommand.CARD_PARAMETERS + "\n"
                    + "         " + EditCommand.COMMAND_WORD + EditCommand.CARD_PARAMETERS);
        }

        return arg.substring(2).trim();
    }

    private static Command prepareRevise(String commandArgs, Access access)
            throws InvalidInputException, IncorrectAccessLevelException {
        if (access.isAdminLevel() || access.isChapterLevel()) {
            throw new IncorrectAccessLevelException("Revise command can only be called at module level.\n");
        } else if (commandArgs.isEmpty()) {
            throw new InvalidInputException("The index for chapter to revise is missing.\n"
                    + ReviseCommand.MESSAGE_USAGE);
        }
        int chapterIndex;
        try {
            chapterIndex = Integer.parseInt(commandArgs) - 1;
        } catch (NumberFormatException e) {
            throw new IncorrectAccessLevelException("The index for chapter should be an integer.\n"
                    + ReviseCommand.MESSAGE_USAGE);
        }
        return new ReviseCommand(chapterIndex);
    }

    private static Command prepareExit(String commandArgs) throws InvalidInputException {
        if (!commandArgs.isEmpty()) {
            throw new InvalidInputException(String.format(MESSAGE_EXTRA_ARGS, ExitCommand.COMMAND_WORD));
        }
        return new ExitCommand();
    }

    private static Command prepareHelp(String commandArgs) throws InvalidInputException {
        if (!commandArgs.isEmpty()) {
            throw new InvalidInputException(String.format(MESSAGE_EXTRA_ARGS, HelpCommand.COMMAND_WORD));
        }
        return new HelpCommand();
    }

    public static String parseQuestionInFile(String arg) throws InvalidFileFormatException {
        if (!(arg.trim().startsWith(Storage.QUESTION_PREFIX))) {
            throw new InvalidFileFormatException("Questions in the file should begin with [Q].");
        }

        String question = arg.substring(3).trim();
        if (question.isEmpty()) {
            throw new InvalidFileFormatException("There should be a question after [Q] in the file.");
        }

        return question;
    }

    public static String parseAnswerInFile(String arg) throws InvalidFileFormatException {
        if (!(arg.trim().startsWith(Storage.ANSWER_PREFIX))) {
            throw new InvalidFileFormatException("Answers in the file should begin with [A].");
        }

        String answer = arg.substring(3).trim();
        if (answer.isEmpty()) {
            throw new InvalidFileFormatException("There should be a answer after [A] in the file.");
        }

        return answer;
    }

    public static String parsePreIntervalInFile(String arg) throws InvalidFileFormatException {
        if (!(arg.trim().startsWith(Storage.PREVIOUS_INTERVAL_PREFIX))) {
            throw new InvalidFileFormatException("Previous intervals in the file should begin with [P].");
        }

        String preInterval = arg.substring(3).trim();
        if (preInterval.isEmpty()) {
            throw new InvalidFileFormatException("There should be a interval after [P] in the file.");
        }

        return preInterval;
    }

    private static Command prepareListDue(String commandArgs) throws InvalidInputException {
        if (!commandArgs.isEmpty()) {
            throw new InvalidInputException(String.format(MESSAGE_EXTRA_ARGS, ListDueCommand.COMMAND_WORD));
        }
        return new ListDueCommand();
    }

    public static String parseTaskNameInFile(String arg) throws InvalidFileFormatException {
        String name = arg.trim();
        if (name.isEmpty()) {
            throw new InvalidFileFormatException("There should be a name of the completed task.");
        }

        return name;
    }

    public static String parsePercentInFile(String arg) throws InvalidFileFormatException {
        String percent = arg.trim().substring(0,3);
        if (percent.isEmpty()) {
            throw new InvalidFileFormatException(
                    "There should be a number to indicate how many tasks have completed.");
        }

        return percent;
    }

    private static Command preparePreview(String commandArgs) throws InvalidInputException {
        if (!commandArgs.isEmpty()) {
            String errorMessage = "There should not be any arguments for preview." + PreviewCommand.MESSAGE_USAGE;
            throw new InvalidInputException(errorMessage);
        }
        return new PreviewCommand();
    }

    private static Command prepareExclude(String commandArgs) throws InvalidInputException {
        if (commandArgs.isEmpty()) {
            throw new InvalidInputException(MESSAGE_MISSING_ARGS + ExcludeCommand.MESSAGE_USAGE);
        }
        return new ExcludeCommand(commandArgs);
    }

    private static Command prepareReschedule(String commandArgs) throws InvalidInputException {
        try {
            String[] args = commandArgs.split(" ", 2);
            if (args[0].trim().isEmpty()) {
                throw new InvalidInputException("The chapter number is missing.\n"
                        + RescheduleCommand.MESSAGE_USAGE);
            }

            if (args[1].trim().isEmpty()) {
                throw new InvalidInputException("The due date is missing.\n"
                        + RescheduleCommand.MESSAGE_USAGE);
            }

            int index = Integer.parseInt(args[0].trim()) - 1;
            LocalDate dueDate = LocalDate.parse(args[1].trim());
            if (dueDate.isBefore(LocalDate.now())) {
                throw new InvalidInputException("You cannot enter a due date that is before today.\n");
            }
            return new RescheduleCommand(index, dueDate);
        } catch (NumberFormatException e) {
            throw new InvalidInputException("The chapter number needs to be an integer.\n"
                    + RescheduleCommand.MESSAGE_USAGE);
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidInputException("The format for the reschedule command is incorrect.\n"
                    + RescheduleCommand.MESSAGE_USAGE);
        } catch (DateTimeParseException e) {
            throw new InvalidInputException("The format for the date is incorrect.\n"
                    + RescheduleCommand.MESSAGE_USAGE);
        }
    }
}


