package banking;

import java.sql.*;
import java.util.Scanner;

public class Bank {
    private final String dbFileName;
    boolean run = true;
    Scanner sc = new Scanner(System.in);
    String currentNum;
    int c = 0;

    public Bank(String dbFileName) {
        this.dbFileName = dbFileName;
        loadData();
    }

    public void createAccount() {
        Card card = new Card();
        saveNewCard(card);
        System.out.println();
        System.out.println("Your card has been created");
        System.out.println("Your card number:");
        System.out.println(card.getCardNumber());
        System.out.println("Your card PIN:");
        System.out.println(card.getPin());
        System.out.println();
    }

    private void showMenu() {
        System.out.println("1. Create an account");
        System.out.println("2. Log into account");
        System.out.println("0. Exit");
    }

    public void menu() {
        if (run = true) {
            int choose;
            do {
                showMenu();
                choose = sc.nextInt();
                switch (choose) {
                    case 1:
                        createAccount();
                        break;
                    case 2:
                        if (isLog() == 1) {
                            ifLogIn();
                        } else {
                            System.out.println("\nWrong card number or PIN!");
                        }
                        break;
                }
            } while (choose != 0 && run);
        }
    }

    public void showBalance() {
        String connectionURL = "jdbc:sqlite:" + dbFileName;
        try (Connection conn = DriverManager.getConnection(connectionURL)) {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT balance FROM card WHERE number = ?");
            preparedStatement.setString(1, currentNum);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int balance = resultSet.getInt("balance");
                System.out.println(balance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addIncome() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter income:");
        int cash = sc.nextInt();
        String connectionURL = "jdbc:sqlite:" + dbFileName;
        try (Connection conn = DriverManager.getConnection(connectionURL)) {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT balance FROM card WHERE number = ?");
            preparedStatement.setString(1, currentNum);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int balance = resultSet.getInt("balance");
                balance += cash;

                PreparedStatement preparedStatement2 = conn.prepareStatement("UPDATE card SET balance = ? WHERE number = ?");
                preparedStatement2.setString(2, currentNum);
                preparedStatement2.setInt(1, balance);
                preparedStatement2.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Income was added!");
    }

    public int checkCard(String cardNumber) {
        String connectionURL = "jdbc:sqlite:" + dbFileName;
        Card card = new Card();
        long num = Long.parseLong(cardNumber);
        if (!card.checkLuhn(num)) {
            System.out.println("Probably you made a mistake in the card number. Please try again!");
            return 1;
        } else {
            try (Connection conn = DriverManager.getConnection(connectionURL)) {
                PreparedStatement preparedStatement = conn.prepareStatement("SELECT number FROM card WHERE number = ?");
                preparedStatement.setString(1, cardNumber);
                ResultSet resultSet = preparedStatement.executeQuery();
                String findNum;
                findNum = resultSet.getString("number");
                if (findNum.equals(currentNum)) {
                    System.out.println("You can't transfer money to the same account!");
                    return 1;
                } else if (findNum.equals(cardNumber)) {
                    return 2;
                } else {
                    System.out.println("Such a card does not exist.");
                }
            } catch (
                    SQLException e) {
                System.out.println("Such a card does not exist.");
            }
            return 0;
        }
    }

    public void doTransfer() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Transfer");
        System.out.println("Enter card number:");
        String cardNumber = sc.nextLine();
        if (checkCard(cardNumber) == 2) {
            System.out.println("Enter how much money you want to transfer:");
            int cash = sc.nextInt();

            String connectionURL = "jdbc:sqlite:" + dbFileName;
            try (Connection conn = DriverManager.getConnection(connectionURL)) {
                PreparedStatement preparedStatement = conn.prepareStatement("SELECT balance FROM card WHERE number = ?");
                preparedStatement.setString(1, currentNum);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    int balance = resultSet.getInt("balance");
                    if (cash > balance) {
                        System.out.println("Not enough money!");
                    } else {
                        balance -= cash;
                        PreparedStatement preparedStatement1 = conn.prepareStatement("UPDATE card SET balance = ? WHERE number = ?");
                        preparedStatement1.setString(2, currentNum);
                        preparedStatement1.setInt(1, balance);
                        preparedStatement1.executeUpdate();


                        PreparedStatement preparedStatement2 = conn.prepareStatement("SELECT balance FROM card WHERE number = ?");
                        preparedStatement2.setString(1, cardNumber);
                        ResultSet resultSet2 = preparedStatement.executeQuery();
                        if (resultSet.next()) {
                            int balance2 = resultSet2.getInt("balance");
                            if (c == 0) {
                                balance2 += cash;
                                balance2 -=cash;
                                c = 1;
                            } else {
                                balance2 += cash;
                            }
                            PreparedStatement preparedStatement3 = conn.prepareStatement("UPDATE card SET balance = ? WHERE number = ?");
                            preparedStatement3.setString(2, cardNumber);
                            preparedStatement3.setInt(1, balance2);
                            preparedStatement3.executeUpdate();
                            System.out.println("Success!");
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeAccount() {
        String connectionURL = "jdbc:sqlite:" + dbFileName;
        try (Connection conn = DriverManager.getConnection(connectionURL)) {
            Statement statement = conn.createStatement();
            statement.executeUpdate("DELETE FROM card WHERE number = " + currentNum);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        System.out.println("The account has been closed!\n");
    }

    public int isLog() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your card number:");
        String enterNum = scanner.nextLine();

        System.out.println("Enter your PIN:");
        String enterPin = scanner.nextLine();

        String connectionURL = "jdbc:sqlite:" + dbFileName;

        try (Connection conn = DriverManager.getConnection(connectionURL)) {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT number FROM card WHERE number = ? AND pin = ?");
            preparedStatement.setString(1, enterNum);
            preparedStatement.setString(2, enterPin);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                currentNum = enterNum;
                return 1;
            } else {
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void ifLogIn() {
        int choose;
        System.out.println("\nYou have successfully logged in!");
        do {
            System.out.println();
            System.out.println("1. Balance\n" +
                    "2. Add income\n" +
                    "3. Do transfer\n" +
                    "4. Close account\n" +
                    "5. Log out\n" +
                    "0. Exit");
            System.out.println();
            choose = sc.nextInt();
            switch (choose) {
                case 1:
                    showBalance();
                    break;
                case 2:
                    addIncome();
                    break;
                case 3:
                    doTransfer();
                    break;
                case 4:
                    closeAccount();
                    break;
                case 5:
                    System.out.println("You have successfully logged out!");
                    System.out.println();
                    break;
                case 0:
                    run = false;
                    break;
            }
        } while (choose != 0 && choose != 5 && choose != 4);
    }

    private void loadData() {
        String connectionURL = "jdbc:sqlite:" + dbFileName;
        try (Connection conn = DriverManager.getConnection(connectionURL);
             Statement stmt = conn.createStatement()) {

            String query = "CREATE TABLE IF NOT EXISTS card (" +
                    "id INTEGER PRIMARY KEY," +
                    "number TEXT," +
                    "pin TEXT," +
                    "balance INTEGER DEFAULT 0)";

            stmt.execute(query);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveNewCard(Card card) {
        String connectionURL = "jdbc:sqlite:" + dbFileName;
        try (Connection conn = DriverManager.getConnection(connectionURL);
             Statement stmt = conn.createStatement()) {

            String insertQuery = "INSERT INTO card(number, pin, balance) " +
                    "VALUES('" + card.getCardNumber() + "', '" + card.getPin() + "', " + card.getBalance() + ")";
            stmt.executeUpdate(insertQuery);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}