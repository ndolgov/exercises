package net.ndolgov.exercise2;

// Please write a function to generate a random 8-character password.
// It should contain 2 lowercase letters, 2 uppercase letters, two numerals,
// and two special characters from the string "!@#$%^&*"

// U = uppercase
// L = lowercase
// N = numeral
// S = special character


// Example results
// 89ab&^RT
// aB&2uC!7
// A1!a@Ub9
// 8Ga^k5N$


import java.util.*;

class PasswordGen {
    public static void main(String[] args) {
        System.out.println(generatePassword()); // It should print out 75512500
        System.out.println(generatePassword());
        System.out.println(generatePassword());
    }

    private static final String strSpecials = "!@#$%^&*";

    private static Random ucases = new Random();
    private static Random lcases = new Random();
    private static Random nums = new Random();
    private static Random specials = new Random();
    private static Random order = new Random();

    public static String generatePassword() {
        final char[] buf = new char[8];
        buf[0] = (char) ('0' + nums.nextInt(10));
        buf[1] = (char) ('0' + nums.nextInt(10));
        buf[2] = strSpecials.charAt(specials.nextInt(strSpecials.length()));
        buf[3] = strSpecials.charAt(specials.nextInt(strSpecials.length()));
        buf[4] = (char) ('a' + ucases.nextInt(26));
        buf[5] = (char) ('a' + ucases.nextInt(26));
        buf[6] = (char) ('A' + lcases.nextInt(26));
        buf[7] = (char) ('A' + lcases.nextInt(26));

        final char[] password = new char[8];
        Arrays.fill(password, ' ');

        int i = 0;
        while (i < password.length) {
            final int pos = order.nextInt(password.length);
            if (password[pos] == ' ') {
                password[pos] = buf[i++];
            }
        }

        return new String(password);
    }
}