package banking;

import java.util.ArrayList;
import java.util.Random;

public class Card {
    Random random = new Random();
    private String cardNumber;
    private String pin;

    public Card() {
        generateNum();
        generatePin();
    }

    private void generateNum() {
        ArrayList<Integer> list = new ArrayList<>();
        long cardNum = 400000;
        int sumLuhn = 8;
        for (int i = 0; i < 9; i++) {
            list.add(random.nextInt(10));
        }
        Integer[] arr = list.toArray(new Integer[0]);
        for (int i = 0; i < 9; i += 2) {
            if (arr[i] * 2 > 9) {
                sumLuhn += (arr[i] * 2 - 9);
            } else {
                sumLuhn += (arr[i] * 2);
            }
        }
        for (int i = 1; i < 8; i += 2) {
            sumLuhn += arr[i];
        }

        int numLuhn = sumLuhn % 10 == 0
                ? 0
                : (10 - (sumLuhn % 10));


        for (int i = 0; i < 9; i++) {
            cardNum *= 10L;
            cardNum += arr[i];
        }
        cardNum *= 10L;
        cardNum += numLuhn;
        cardNumber = String.valueOf(cardNum);
    }

    private void generatePin() {
        int genPin = random.nextInt(10);
        if (genPin == 0) {
            genPin = 1;
        }
        for (int i = 0; i < 3; i++) {
            genPin *= 10;
            genPin += random.nextInt(10);
        }
        pin = String.valueOf(genPin);
    }

    public boolean checkLuhn(long num) {
        double digit;
        int sum = 0;
        int i = 0;
        while (num > 0) {
            digit = num % 10;
            num = num / 10;

            if (i % 2 != 0) {
                digit *= 2;
            }
            if (digit > 9) {
                digit = (digit % 10) + 1;
            } else {
                digit *= 1;
            }
            sum += digit;
            i++;
        }
        return sum % 10 == 0;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getPin() {
        return pin;
    }

    public int getBalance() {
        return 0;
    }
}