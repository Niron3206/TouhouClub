package ru.niron3206.cmds.OwO;

import java.util.Random;

public class OwOAlgorithm {

    private final String[] msg;
    private String finalMessage;

    public OwOAlgorithm(String[] msg) {
        this.msg = msg;
    }

    public void engOwOAlgorithm() {

        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        char[] vowels = new char[] {'a', 'e', 'i', 'o', 'u'};
        char[] consonants = new char[] {'c', 'h', 'g', 'b', 'k', 'm', 'r', 't', 'w', 'd'};
        String[] endPhrases = new String[] {"nya~", "owo~", "-w-"};

        //Converts String array in String.
        for (int i = 1; i < msg.length; i++) {
            sb.append(" ").append(msg[i]);
        }
        String preMsg = sb.toString();

        for (int i = 0; i < preMsg.length(); i++) {
            //Put char 'y' after char 'n' and add string "ya" if char 'n' stands as last letter.
            for (char vowel : vowels) {
                if ((preMsg.charAt(i) == 'n') || (preMsg.charAt(i) == 'N')) {
                    try {
                        if (preMsg.charAt(i+1) == vowel) {
                            sb.insert(i + 1, 'y');
                            preMsg = sb.toString();
                        }
                    } catch (StringIndexOutOfBoundsException err) {
                        sb.insert( i + 1, "ya");
                        preMsg = sb.toString();
                    }
                }
            }

            //Double some random chosen consonants.
            for (char consonant : consonants) {
                if (random.nextInt(2) != 1) {
                    if ((preMsg.charAt(i) == consonant) && (preMsg.charAt(i-1) == ' ')) {
                        sb.insert(i + 1, '-');
                        sb.insert(i + 2, preMsg.charAt(i));
                        preMsg = sb.toString();
                    }
                }
            }
        }

        //Substitute chars 'l' and 'r' with 'w'.
        preMsg = preMsg.replace('L', 'W').replace('l', 'w');
        preMsg = preMsg.replace('R', 'W').replace('r', 'w');

        //Randomly add end phrases at the end from endPhrases massive.
        char end = preMsg.charAt(preMsg.length() - 1);
        if(random.nextInt(2) == 1) {
            switch (end) {
                case '!', '?' -> preMsg = preMsg + " " + endPhrases[random.nextInt(endPhrases.length)];
                case '.' -> preMsg = preMsg.substring(0, preMsg.length() - 1);
                default -> preMsg = preMsg + ", " + endPhrases[random.nextInt(endPhrases.length)];
            }
        }
        finalMessage = preMsg;
    }

    public void rusOwOAlgorithm () {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        char[] vowels = new char[] {'??', '??', '??', '??'};
        char[] consonants = new char[] {'??', '??', '??', '??', '??', '??', '??', '??', '??', '??'};
        String[] endPhrases = new String[] {"??????~", "owo~", "-w-"};

        //Converts String array in String.
        for (int i = 1; i < msg.length; i++) {
            sb.append(" ").append(msg[i]);
        }
        String preMsg = sb.toString();


        for (int i = 0; i < preMsg.length(); i++) {
            //Put char 'y' after char 'n' and add string "ya" if char 'n' stands as last letter.
            for (char vowel : vowels) {
                if ((preMsg.charAt(i) == '??') || (preMsg.charAt(i) == '??')) {
                    try {
                        if (preMsg.charAt(i+1) == vowel) {
                            sb.insert(i + 1, '??');
                            preMsg = sb.toString();
                        }
                    } catch (StringIndexOutOfBoundsException err) {
                        sb.insert( i + 1, "????");
                        preMsg = sb.toString();
                    }
                }
            }

            //Double some random chosen consonants.
            for (char consonant : consonants) {
                if (random.nextInt(2) != 1) {
                    if ((preMsg.charAt(i) == consonant) && (preMsg.charAt(i-1) == ' ')) {
                        sb.insert(i + 1, '-');
                        sb.insert(i + 2, preMsg.charAt(i));
                        preMsg = sb.toString();
                    }
                }
            }

            //Randomly replace '??' with "????".
            if((preMsg.charAt(i) == '??') || (preMsg.charAt(i) == '??')) {
                if(preMsg.charAt(i-1) != ' ') {
                    if(random.nextInt(2) == 1) {
                        sb.replace(i, i+1, "????");
                        preMsg = sb.toString();
                    }
                }
            }

        }

        //Substitute chars 'l' and 'r' with 'w'.
        preMsg = preMsg.replace('??', '??').replace('??', '??');
        preMsg = preMsg.replace('??', '??').replace('??', '??');

        //Randomly add end phrases at the end from endPhrases array.
        char end = preMsg.charAt(preMsg.length() - 1);
        if(random.nextInt(2) == 1) {
            switch (end) {
                case '!', '?' -> preMsg = preMsg + " " + endPhrases[random.nextInt(endPhrases.length)];
                case '.' -> preMsg = preMsg.substring(0, preMsg.length() - 1);
                default -> preMsg = preMsg + ", " + endPhrases[random.nextInt(endPhrases.length)];
            }
        }
        finalMessage = preMsg;
    }

    public String getFinalMessage(){
        return finalMessage;
    }
}
