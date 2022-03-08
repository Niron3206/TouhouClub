package ru.niron3206.cmds.OwO;

import java.util.Random;

public class OwOAlgorithm {

    public String[] msg;
    public String finalMessage;

    public OwOAlgorithm(String[] msg) {
        this.msg = msg;
    }

    public String engOwOAlgorithm() {

        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        char[] vowels = new char[] {'a', 'e', 'i', 'o', 'u'};
        char[] consonants = new char[] {'c', 'h', 'g', 'b', 'k', 'm', 'r', 't', 'w', 'd'};
        String[] endPhrases = new String[] {", nya~", ", owo~", ", -w-"};

        //Converts String array in String.
        for (int i = 1; i < msg.length; i++) {
            sb.append(" " + msg[i]);
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
        if(random.nextInt(2) != 1) {
            finalMessage = preMsg;
        } else {
            finalMessage = preMsg + endPhrases[random.nextInt(endPhrases.length)];
        }

        return finalMessage;
    }

    public String rusOwOAlgorithm () {
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        char[] vowels = new char[] {'е', 'и', 'о', 'у'};
        char[] consonants = new char[] {'с', 'х', 'г', 'б', 'к', 'м', 'р', 'т', 'в', 'д'};
        String[] endPhrases = new String[] {", нья~", ", owo~", ", -w-"};

        //Converts String array in String.
        for (int i = 1; i < msg.length; i++) {
            sb.append(" " + msg[i]);
        }
        String preMsg = sb.toString();


        for (int i = 0; i < preMsg.length(); i++) {
            //Put char 'y' after char 'n' and add string "ya" if char 'n' stands as last letter.
            for (char vowel : vowels) {
                if ((preMsg.charAt(i) == 'н') || (preMsg.charAt(i) == 'Н')) {
                    try {
                        if (preMsg.charAt(i+1) == vowel) {
                            sb.insert(i + 1, 'ь');
                            preMsg = sb.toString();
                        }
                    } catch (StringIndexOutOfBoundsException err) {
                        sb.insert( i + 1, "ья");
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

            //Randomly replace 'ч' with "ть".
            if((preMsg.charAt(i) == 'ч') || (preMsg.charAt(i) == 'Ч')) {
                if(preMsg.charAt(i-1) != ' ') {
                    if(random.nextInt(2) == 1) {
                        sb.replace(i, i+1, "ть");
                        preMsg = sb.toString();
                    }
                }
            }

        }

        //Substitute chars 'l' and 'r' with 'w'.
        preMsg = preMsg.replace('Л', 'В').replace('л', 'в');
        preMsg = preMsg.replace('Р', 'В').replace('р', 'в');

        //Randomly add end phrases at the end from endPhrases array.
        if(random.nextInt(2) != 1) {
            finalMessage = preMsg;
        } else {
            finalMessage = preMsg + endPhrases[random.nextInt(endPhrases.length)];
        }

        return finalMessage;
    }
}
