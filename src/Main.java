import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

public class Main {
    private static String secretWord;
    private static String maskWord;
    private static int userLetterCounter;
    private static Set<Character> userLetterSet;
    private static final List<String> DICTIONARY = new ArrayList<>();
    private static final String GAME_STATE_WIN = "Поздравляем, вы выиграли!";
    private static final String GAME_STATE_LOSE = "К сожалению, вы проиграли. Загаданное слово: ";
    private static final String GAME_STATE_NOT_FINISHED = "Игра не закончена.";
    private static Scanner scanner;
    private static Random random = new Random();

    public static void main(String[] args) throws IOException {
        createDictionary();
        while (true) {
            startGameRound();
        }
    }

    public static void createDictionary() throws IOException {
        FileReader fileReader = new FileReader(Paths.get("src","dictionary.txt").toFile());
        BufferedReader reader = new BufferedReader(fileReader);

        while (reader.readLine() != null) {
            DICTIONARY.add(reader.readLine().toUpperCase());
        }
    }

    public static void startGameRound() throws IOException {
        printStartInfo();
        scanner = new Scanner(System.in);
        int userInput = scanner.nextInt();

        if (userInput == 1) {
            int errorsCounter = 6;
            String[] randomWord = getRandomWord();
            secretWord = randomWord[0];
            maskWord = randomWord[1];
            userLetterCounter = secretWord.length();
            userLetterSet = new HashSet<>();

            System.out.println("Загаданное слово: " + maskWord);
            startGameLoop(errorsCounter);
        } else {
            scanner.close();
            System.exit(0);
        }
    }

    private static void printStartInfo() {
        System.out.println();
        System.out.println("Введите:");
        System.out.println("1 - для начала игры");
        System.out.println("0 - для выхода из игры");
        System.out.println("И нажмите Enter");
    }

    public static String[] getRandomWord() {
        String[] randomWord = new String[2];
        String secretWord = DICTIONARY.get(random.nextInt(DICTIONARY.size()));
        char[] maskWordArray = new char[secretWord.length()];
        Arrays.fill(maskWordArray, '*');

        randomWord[0] = secretWord;
        randomWord[1] = String.valueOf(maskWordArray);

        return randomWord;
    }

    public static void startGameLoop(int errorsCounter) {
        do {
            char userInput = getUserInput();
            userLetterSet.add(userInput);

            if (secretWord.contains(String.valueOf(userInput))) {
                maskWord = openLetterInMask(userInput);
            } else {
                errorsCounter--;
            }

            printScaffold(errorsCounter);
            printCurrentGameInfo(errorsCounter);

            String gameState = checkGameState(errorsCounter);
            if (!Objects.equals(gameState, GAME_STATE_NOT_FINISHED)) {
                System.out.println(gameState);
                return;
            }
        } while (true);
    }

    public static char getUserInput() {
        System.out.println("Введите одну букву и нажмите Enter: ");
        scanner = new Scanner(System.in);

        do {
            String inputLine = scanner.nextLine().toUpperCase();
            char userInput = inputLine.charAt(0);

            if (!Character.isLetter(userInput) || !isRussianLetter(userInput)) {
                System.out.println(
                        "Использовать можно только буквы русского алфавита. " +
                                "Введите одну букву и нажмите Enter: "
                );
            } else if (userLetterSet.contains(userInput)) {
                System.out.println("Ранее вы уже вводили эту букву. Попробуйте ввести другую букву.");
            } else {
                return userInput;
            }
        } while (true);
    }

    public static boolean isRussianLetter(char userInput) {
        String regex = "[а-яА-ЯёЁ]";
        return Pattern.matches(regex, String.valueOf(userInput));
    }

    private static String openLetterInMask(char userInput) {
        char[] maskArray = maskWord.toCharArray();
        for (int i = 0; i < secretWord.length(); i++) {
            if (secretWord.charAt(i) == userInput) {
                maskArray[i] = userInput;
                userLetterCounter--;
            }
        }
        return String.valueOf(maskArray);
    }
    public static void printScaffold(int errorsCounter) {
        switch (errorsCounter) {
            case 6 -> System.out.println("""
                    ---------
                    |/      |
                    |       
                    |
                    |
                    |
                    |__________|
                    """);
            case 5 -> System.out.println("""
                    ---------
                    |/      |
                    |       O
                    |
                    |
                    |
                    |__________|
                    """);
            case 4 -> System.out.println("""
                    ---------
                    |/      |
                    |       O
                    |       |
                    |       
                    |
                    |__________|
                    """);
            case 3 -> System.out.println("""
                    ---------
                    |/      |
                    |       O
                    |      /|
                    |         
                    |
                    |__________|
                    """);
            case 2 -> System.out.println("""
                    ---------
                    |/      |
                    |       O
                    |      /|\\
                    |
                    |
                    |__________|
                    """);
            case 1 -> System.out.println("""
                    ---------
                    |/      |
                    |       O
                    |      /|\\
                    |      /
                    |
                    |__________|
                    """);
            case 0 -> System.out.println("""
                    ---------
                    |/      |
                    |       O
                    |      /|\\
                    |      / \\
                    |
                    |__________|
                    """);
        }
    }

    private static void printCurrentGameInfo(int errorsCounter) {
        System.out.println("Загаданное слово: " + maskWord);
        System.out.println("Количество оставшихся ошибок: " + errorsCounter);
        System.out.println("Буквы, которые вы уже ввели: " + userLetterSet);
        System.out.println();
    }

    private static String checkGameState(int errorsCounter) {
        if (errorsCounter <= 0) {
            return GAME_STATE_LOSE + secretWord;
        } else if (userLetterCounter <= 0) {
            return GAME_STATE_WIN;
        } else {
            return GAME_STATE_NOT_FINISHED;
        }
    }
}
