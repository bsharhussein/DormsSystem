package dorms;

import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class InputValidator {
	
    public static String getValidId(Scanner scanner) {
        while (true) {
            System.out.print("Enter ID (exactly 9 digits): ");
            String id = scanner.nextLine().trim();
            
            if (id.length() == 9 && id.matches("\\d+")) {
                return id; 
            }
            System.out.println("Error: The ID must be exactly 9 numbers. Try again.");
        }
    }

    public static String getValidYesNo(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt + " (yes/no): ");
            String input = scanner.nextLine().trim().toLowerCase();
            
            if (input.equals("yes") || input.equals("no")) {
                return input;
            } else {
                System.out.println("Invalid input. Please type exactly 'yes' or 'no'.");
            }
        }
    }

    public static int getValidNumber(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid input. You must type a numeric number.");
            }
        }
    }
        
    public static String getValidPhoneNumber(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt + " (10 digits, starts with 05): ");
            String phone = scanner.nextLine().trim();
            
            // Regex enforces 10 digits starting with 05
            if (phone.matches("^05\\d{8}$")) {
                return phone; 
            }
            System.out.println("Error: Invalid format. Must be exactly 10 numbers starting with '05' (Example: 0501234567).");
        }
    }
        
    public static String getValidName(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String name = scanner.nextLine().trim();

            if (name.matches("^[a-zA-Zא-ת\\s]{2,}$")) {
                return name; 
            }
            System.out.println("Error: Name cannot be blank and must contain only letters.");
        }
    }
        
    public static String getValidDate(Scanner scanner, String prompt) {
        String dateRegex = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/\\d{4}$";
        while (true) {
            System.out.print(prompt + " (DD/MM/YYYY): ");
            String input = scanner.nextLine().trim();
            if (input.matches(dateRegex)) {
                return input;
            } else {
                System.out.println("Error: Date must be exactly in DD/MM/YYYY format.");
            }
        }
    }

    public static String getValidChoice(Scanner scanner, String prompt, String[] validOptions) {
        while (true) {
            System.out.print(prompt + " (");
            for (int i = 0; i < validOptions.length; i++) {
                System.out.print(validOptions[i]);
                if (i < validOptions.length - 1) System.out.print("/");
            }
            System.out.print("): ");
            
            String input = scanner.nextLine().trim().toUpperCase();
            
            for (String option : validOptions) {
                if (input.equals(option.toUpperCase())) {
                    return input;
                }
            }
            System.out.println("Error: Invalid input. Please type one of the allowed options.");
        }
    }

    public static double getValidDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            String input = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (value < 0) {
                    System.out.println("Error: Amount cannot be negative.");
                } else {
                    return value; 
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number (e.g., 1500.50).");
            }
        }
    }
        
    public static String getValidFutureDate(Scanner scanner, String prompt) {
        String dateRegex = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/\\d{4}$";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        while (true) {
            System.out.print(prompt + " (DD/MM/YYYY): ");
            String input = scanner.nextLine().trim();
            
            if (input.matches(dateRegex)) {
                try {
                    LocalDate enteredDate = LocalDate.parse(input, formatter);
                    LocalDate today = LocalDate.now();
                    
                    if (enteredDate.isBefore(today)) {
                        System.out.println("Error: This date has already passed!");
                    } else {
                        return input;
                    }
                } catch (Exception e) {
                    System.out.println("Error: Invalid calendar date.");
                }
            } else {
                System.out.println("Error: Date must be exactly in DD/MM/YYYY format.");
            }
        }
    }

    public static String getValidPassport(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            String input = scanner.nextLine().trim();
            
            if (input.matches("^[a-zA-Z0-9]{6,15}$")) {
                return input.toUpperCase(); 
            } else {
                System.out.println("Error: Passport must be between 6 and 15 letters or numbers.");
            }
        }
    }

    public static String getValidLettersOnly(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            String input = scanner.nextLine().trim();
            
            if (input.matches("^[a-zA-Z\\s]+$")) {
                return input;
            } else {
                System.out.println("Error: This field can only contain letters and spaces.");
            }
        }
    }
        
    public static int getValidNumberInRange(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            int input = getValidNumber(scanner, prompt); 
            
            if (input >= min && input <= max) {
                return input;
            } else {
                System.out.println("Error: Please enter a number between " + min + " and " + max + ".");
            }
        }
    }
}