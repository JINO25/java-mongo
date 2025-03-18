package org.example;

import org.example.db.MongoDB;

import java.util.Scanner;

public class Main {

    public static void menuUpdate(MongoDB mongoDB){
        Scanner scanner = new Scanner(System.in);
        boolean r = true;
        while(r){
            String name, email, date,address,phone, subject;
            double score;
            System.out.println("--------------------------------" +
                    "Enter number to manipulate\n" +
                    "1: Update name\n" +
                    "2: Update date\n" +
                    "3: Update address\n" +
                    "4: Update phone number\n" +
                    "5: Update all of information (name,date,address,phone)\n" +
                    "6: Update score of subject\n" +
                    "7: Insert a new subject and score\n" +
                    "8: Delete specific subjects\n"+
                    "0: Exit");
            int number = scanner.nextInt();
            scanner.nextLine();
            switch (number){
                case 0:
                    r=false;
                    break;
                case 1:
                    scanner.nextLine(); // Consume newline
                    System.out.print("Enter email to update: ");
                    email = scanner.nextLine();
                    System.out.print("Enter new name: ");
                    name = scanner.nextLine();
                    mongoDB.updateUser(email, name, null, null, null, null, 0);
                    break;

                case 2:
                    scanner.nextLine(); // Consume newline
                    System.out.print("Enter email to update: ");
                    email = scanner.nextLine();
                    System.out.print("Enter new date of birth (yyyy/mm/dd): ");
                    date = scanner.nextLine();
                    mongoDB.updateUser(email, null, date, null, null, null, 0);
                    break;

                case 3:
                    scanner.nextLine(); // Consume newline
                    System.out.print("Enter email to update: ");
                    email = scanner.nextLine();
                    System.out.print("Enter new address: ");
                    address = scanner.nextLine();
                    mongoDB.updateUser(email, null, null, address, null, null, 0);
                    break;

                case 4:
                    scanner.nextLine(); // Consume newline
                    System.out.print("Enter email to update: ");
                    email = scanner.nextLine();
                    System.out.print("Enter new phone number: ");
                    phone = scanner.nextLine();
                    mongoDB.updateUser(email, null, null, null, phone, null, 0);
                    break;

                case 5:
                    scanner.nextLine(); // Consume newline
                    System.out.print("Enter email to update: ");
                    email = scanner.nextLine();
                    System.out.print("Enter new name: ");
                    name = scanner.nextLine();
                    System.out.print("Enter new date of birth (yyyy/mm/dd): ");
                    date = scanner.nextLine();
                    System.out.print("Enter new address: ");
                    address = scanner.nextLine();
                    System.out.print("Enter new phone number: ");
                    phone = scanner.nextLine();
                    mongoDB.updateUser(email, name, date, address, phone, null, 0);
                    break;
                case 6:
                    System.out.print("Enter email to update: ");
                    email = scanner.nextLine();
                    System.out.print("Enter name of the subject: ");
                    subject = scanner.nextLine();
                    System.out.print("Enter score of the subject: ");
                    score=scanner.nextDouble();
                    mongoDB.updateUser(email, null, null, null, null, subject, score);
                    break;
                case 7:
                    System.out.print("Enter email of user: ");
                    email = scanner.nextLine();
                    mongoDB.inserSubject(email);
                    break;
                case 8:
                    System.out.print("Enter email of user: ");
                    email = scanner.nextLine();
                    mongoDB.deleteSubject(email);
                    break;
            }
        }
    }

    public static void showMenu(Scanner scanner,MongoDB mongoDB){
        boolean r = true;
        while(r){
            String name, email, date,address,phone;
            System.out.println("--------------------------------\n" +
                    "Enter number to manipulate\n" +
                    "1: Show list of users\n" +
                    "2: Create a new user\n" +
                    "3: Find a user based on email\n" +
                    "4: Update user\n" +
                    "5: Delete user\n" +
                    "0: Exit");
            int number = scanner.nextInt();
            scanner.nextLine();
            switch (number){
                case 0:
                    r=false;
                    break;
                case 1:
                    mongoDB.findAll();
                    break;
                case 2:
                    mongoDB.createUser();
                    break;
                case 3:
                    System.out.print("Enter email to find: ");
                    email = scanner.nextLine();
                    mongoDB.findUserByEmail(email);
                    break;
                case 4:
                    Main.menuUpdate(mongoDB);
                    break;
                case 5:
                    System.out.print("Enter email to delete: ");
                    email = scanner.nextLine();
                    mongoDB.deleteUser(email);
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Hello world!");
        MongoDB mongoDB = new MongoDB();
        Main.showMenu(scanner,mongoDB);
    }
}