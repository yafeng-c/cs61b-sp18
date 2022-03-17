package byog.lab6;

import edu.princeton.cs.introcs.In;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    private int width;
    private int height;
    private int round;
    private Random rand;
    private boolean gameOver;
    private boolean playerTurn;
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        int seed = Integer.parseInt(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, int seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.white);
        StdDraw.enableDoubleBuffering();

        //todo: Initialize random number generator
        rand = new Random(seed);
    }

    public String generateRandomString(int n) {
        //todo: Generate random string of letters of length n
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            char c = CHARACTERS[rand.nextInt(CHARACTERS.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    public void drawFrame(String s) {
        //todo: Take the string and display it in the center of the screen
        //todo: If game is not over, display relevant game information at the top of the screen
        StdDraw.clear();
        StdDraw.clear(Color.black);
        Font font = new Font("Arial", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.text(width / 2, height / 2, s);
        StdDraw.show();
    }

    public void flashSequence(String letters) {
        //todo: Display each character in letters, making sure to blank the screen between letters
        char[] charArray = letters.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            drawFrame(Character.toString(charArray[i]));
            StdDraw.pause(1000);
            drawFrame(" ");
            StdDraw.pause(500);
        }
    }

    public String solicitNCharsInput(int n) {
        //todo: Read n letters of player input
        StringBuilder sb = new StringBuilder();
        while (sb.length() < n) {
            if (StdDraw.hasNextKeyTyped()) {
                sb.append(StdDraw.nextKeyTyped());
            }
            drawFrame(sb.toString());
        }
        StdDraw.pause(500);
        return sb.toString();
    }

    public void startGame() {
        //todo: Set any relevant variables before the game starts
        round = 1;
        //todo: Establish Game loop
        while (!gameOver) {
            drawFrame("Round " + Integer.toString(round));
            String target = generateRandomString(round);
            flashSequence(target);
            String userInput = solicitNCharsInput(round);
            if (!userInput.equals(target)) {
                gameOver = true;
                drawFrame("Game Over! Final Round: " + Integer.toString(round));
            } else {
                round++;
                drawFrame("Well Done!");
            }
        }
    }

}
