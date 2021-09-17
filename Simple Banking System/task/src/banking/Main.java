package banking;

import java.sql.*;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;


public class Main {

    final static Scanner input = new Scanner(System.in);
    
    public static class Account {
        private String cardnum;
        private String pin;
        private double balance;

        public static String Luhn(String cardnum) {
            int[] temp = new int[16];
            int sum = 0;
            for (int i = 0; i < 16; i++) {
                temp[i] = Character.getNumericValue(cardnum.charAt(i));
                if ((i+1) % 2 != 0) {
                    temp[i] = temp[i] * 2; //multiply odd by 2
                }
                if (temp[i] > 9) {
                    temp[i] = temp[i] - 9; //subtract 9 to numbers over 9
                }
                if (i < 15) { //omit last digit
                    sum = sum + temp[i];
                }
            }
            int x = 10 - (sum%10);
            if (x == 10) {
                x = 0;
            }

            char checksum = (char) (x + '0');
            String buffer = cardnum.substring(0,15) + checksum;
            return buffer;
        }

        public void generateCardnum() {
            long num = ThreadLocalRandom.current().nextLong(9999999999L);
            String cardnum = String.format("400000%010d", num);
            cardnum = Luhn(cardnum);
            System.out.println("Your card number: \n" + cardnum);
            this.cardnum = cardnum;
        }

        public void generatePin() {
            int num = ThreadLocalRandom.current().nextInt(9999);
            String pin = String.format("%04d", num);
            System.out.println("Your card PIN: \n" + pin);
            this.pin = pin;
        }

        public void makeAccount() {
            generateCardnum();
            generatePin();
        }
        
        public String getCardnum() {
            return cardnum;
        }
        
        public String getPin() {
            return pin;
        }
        
        public double getBalance() {
            return balance;
        }
    }
    
    public static void main(String[] args) {

        String jdbcUrl = "jdbc:sqlite:" + args[1];

        try{
            Connection connection = DriverManager.getConnection(jdbcUrl);

            String sql = "CREATE TABLE IF NOT EXISTS card (\n"
                    + " id INTEGER,\n"
                    + "number VARCHAR(20),\n"
                    + "pin VARCHAR(20),\n"
                    + "balance INTEGER DEFAULT 0"
                    + ");";

            Statement statement = connection.createStatement();

            ResultSet result = statement.executeQuery(sql);

        }
        catch (SQLException e) {
            System.out.println("Error connecting to SQLite database");
            //e.printStackTrace();
            //add comment
        }

        int choice = getChoice();
        Account user = new Account();

        while (choice != 0) {
            if (choice == 1) {
                System.out.println("Your card has been created");
                user.makeAccount();
                choice = getChoice();
            }
            else if (choice == 2) {
                System.out.println("Enter your card number:");
                String accnum = input.nextLine();
                System.out.println("Enter your PIN:");
                String pin = input.nextLine();
                if (accnum.equals(user.getCardnum()) && pin.equals(user.getPin())) {
                    System.out.println("You have successfully logged in!");
                    int menu = accountMenu();
                    while (menu != 3) {
                        if (menu == 1) {
                            System.out.println("Balance: " + user.getBalance());
                        }
                        else if (menu == 2) {
                            System.out.println("You have successfully logged out!");
                            break;
                        }
                        else if (menu == 0) {
                            System.out.println("Bye!");
                            return;
                        }
                        menu = accountMenu();
                    }
                    choice = getChoice();
                }
                else {
                    System.out.println("Wrong card number or PIN!");
                }
            }
            else{
                System.out.println("Bye!");
                return;
            }
        }
    }
    
    public static int getChoice(){
        System.out.println("1. Create an account\n" +
            "2. Log into account\n" +
            "0. Exit");
        return Integer.parseInt(input.nextLine());
    }

    public static int accountMenu(){
        System.out.println("1. Balance\n" +
                "2. Log out\n" +
                "0. Exit");
        return Integer.parseInt(input.nextLine());
    }

}