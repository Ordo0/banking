package banking;

public class Main {
    private static final String DEFAULT_DB_FILE_NAME = "cards.db";

    public static void main(String[] args) {
        String dbFileName = DEFAULT_DB_FILE_NAME;
        Bank bank;

        if (args.length > 1) {
            for (int i = 0; i < args.length - 1; i++) {
                if (args[i].equals("-fileName")) {
                    if (args[i + 1].length() > 1) {
                        dbFileName = args[i + 1];
                    }
                }
            }
        }
        bank = new Bank(dbFileName);
        bank.menu();
        System.out.println("Bye!");
    }
}