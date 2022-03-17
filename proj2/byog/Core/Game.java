package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Game {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private final int MAXROOMHEIGHT = 8;
    private final int MAXROOMWIDTH = 10;
    private final  int MINROOMNUM = 10;
    private final int MAXROOMNUM = 20;
    private boolean gameOver = false;
    private Position playerPosition;
    private int playerX;
    private int playerY;
    private int doorX;
    private int doorY;

    /**
     * Method used for playing a fresh game. The game should start from the main menu.
     */
    public void playWithKeyboard() {
        startUI();
        TETile[][] finalWorld = null;
        char c;
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                c = Character.toLowerCase(StdDraw.nextKeyTyped());
                if (c == 'n' || c == 'l' || c == 'q') {
                    break;
                }
            }
        }
        switch (c) {
            case 'l' :
                finalWorld = loadWorld();
                break;
            case 'n':
                finalWorld = newWorld();
                break;
            case 'q':
                System.exit(0);
                break;
            default:
                break;
        }
        renderWorld(finalWorld);
    }

    private void startUI() {
        StdDraw.clear(Color.black);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(WIDTH / 2, 3 * HEIGHT / 4, "CS61B: THE GAME");
        Font smallFont = new Font("Monaco", Font.PLAIN, 30);
        StdDraw.setFont(smallFont);
        StdDraw.text(WIDTH / 2, HEIGHT / 4 + 2, "New Game (N)");
        StdDraw.text(WIDTH / 2, HEIGHT / 4, "Load Game (L)");
        StdDraw.text(WIDTH / 2, HEIGHT / 4 - 2, "Quit (Q)");
        StdDraw.show();
    }

    /**
     * Method used for autograding and testing the game code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The game should
     * behave exactly as if the user typed these characters into the game after playing
     * playWithKeyboard. If the string ends in ":q", the same world should be returned as if the
     * string did not end with q. For example "n123sss" and "n123sss:q" should return the same
     * world. However, the behavior is slightly different. After playing with "n123sss:q", the game
     * should save, and thus if we then called playWithInputString with the string "l", we'd expect
     * to get the exact same world back again, since this corresponds to loading the saved game.
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] playWithInputString(String input) {
        // todo: Fill out this method to run the game using the input passed in,
        // and return a 2D tile representation of the world that would have been
        // drawn if the same inputs had been given to playWithKeyboard().
        if (input == null) {
            return null;
        }
        input = input.toLowerCase();
        TETile[][] finalWorld = null;
        Character start = input.charAt(0);
        switch (start) {
            case 'l' :
                finalWorld = loadWorld(input);
                break;
            case 'n':
                finalWorld = newWorld(input);
                break;
            case 'q':
                System.exit(0);
                break;
            default:
                break;
        }
        return finalWorld;
    }

    private void renderWorld(TETile[][] finalWorld) {
        ter.initialize(WIDTH, HEIGHT);
        ter.renderFrame(finalWorld);
        while (!gameOver) {
            if (StdDraw.isMousePressed()) {
                ter.renderFrame(finalWorld);
                int mouseX = (int) StdDraw.mouseX();
                int mouseY = (int) StdDraw.mouseY();
                String hud = finalWorld[mouseX][mouseY].description();
                // draw as text
                Font font = new Font("Monaco", Font.BOLD, 14);
                StdDraw.setFont(font);
                StdDraw.setPenColor(StdDraw.WHITE);
                StdDraw.textLeft(1, HEIGHT - 1, hud);
                StdDraw.show();
            }
            while (StdDraw.hasNextKeyTyped()) {
                char op = StdDraw.nextKeyTyped();
                int prevPlayerX = playerX;
                int prevPlayerY = playerY;
                switch (op) {
                    case 'w':
                        playerY++;
                        break;
                    case 'a':
                        playerX--;
                        break;
                    case 's':
                        playerY--;
                        break;
                    case 'd':
                        playerX++;
                        break;
                    case ':':
                        if (StdDraw.hasNextKeyTyped()) {
                            if (Character.toLowerCase(StdDraw.nextKeyTyped()) == 'q') {
                                saveGame(finalWorld);
                            }
                        }
                        break;
                    default: break;
                }
                updatePlayer(finalWorld, prevPlayerX, prevPlayerY);
                ter.renderFrame(finalWorld);
            }
        }
    }

    private void updatePlayer(TETile[][] finalWorld, int prevPlayerX, int prevPlayerY) {
        if (finalWorld[playerX][playerY].equals(Tileset.WALL)) {
            playerX = prevPlayerX;
            playerY = prevPlayerY;
            return;
        }
        finalWorld[prevPlayerX][prevPlayerY] = Tileset.FLOOR;
        finalWorld[playerX][playerY] = Tileset.PLAYER;
    }

    private void saveGame(TETile[][] finalWorld) {
        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try {
            fos = new FileOutputStream("savedfile.txt");
            out = new ObjectOutputStream(fos);
            out.writeObject(finalWorld);
            playerPosition = new Position(playerX, playerY);
            out.writeObject(playerPosition);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private TETile[][] loadWorld() {
        TETile[][] finalWorld = null;
        FileInputStream fos;
        try {
            fos = new FileInputStream("savedfile.txt");
            ObjectInputStream oos = new ObjectInputStream(fos);
            finalWorld = (TETile[][]) oos.readObject();
            playerPosition = (Position) oos.readObject();
            playerX = playerPosition.x;
            playerY = playerPosition.y;
            fos.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return finalWorld;
    }

    private TETile[][] loadWorld(String input) {
        TETile[][] finalWorld = null;
        FileInputStream fos;
        try {
            fos = new FileInputStream("savedfile.txt");
            ObjectInputStream oos = new ObjectInputStream(fos);
            finalWorld = (TETile[][]) oos.readObject();
            playerPosition = (Position) oos.readObject();
            playerX = playerPosition.x;
            playerY = playerPosition.y;
            fos.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        parseInputOps(finalWorld, input);
        return finalWorld;
    }

    private void parseInputOps(TETile[][] finalWorld, String input) {
        int idxOfN = input.indexOf('n');
        int idxOfS = input.indexOf('s');
        if (idxOfS == input.length() - 1) {
            return;
        }
        if (idxOfN == -1 || idxOfS == -1) {
            idxOfS = 0;
        }
        char[] ops = input.substring(idxOfS + 1).toCharArray();
        for (int i = 0; i < ops.length; i++) {
            char op = ops[i];
            int prevPlayerX = playerX;
            int prevPlayerY = playerY;
            switch (op) {
                case 'w':
                    playerY++;
                    break;
                case 'a':
                    playerX--;
                    break;
                case 's':
                    playerY--;
                    break;
                case 'd':
                    playerX++;
                    break;
                case ':':
                    if (i + 1 < ops.length && Character.toLowerCase(ops[i + 1]) == 'q') {
                        saveGame(finalWorld);
                    }
                    break;
                default: break;
            }
            updatePlayer(finalWorld, prevPlayerX, prevPlayerY);
        }
    }

    public TETile[][] newWorld() {
        TETile[][] world = null;
        long seed = getUserSeed();
        Random random = new Random(seed);
        world = generateWorld(random);
        return world;
    }

    private long getUserSeed() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setFont(new Font("Monaco", Font.PLAIN, 20));
        StdDraw.text(WIDTH / 2, 3 * HEIGHT / 4, "Please enter a random seed:");
        long seed = 0;
        while (true) {
            char digit;
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            digit = Character.toLowerCase(StdDraw.nextKeyTyped());
            if (digit != 's') {
                if (!Character.isDigit(digit)) {
                    continue;
                }
                seed = seed * 10 + Character.getNumericValue(digit);
                StdDraw.clear(Color.black);
                StdDraw.text(WIDTH / 2, HEIGHT / 2, Long.toString(seed));
                StdDraw.show();
            } else {
                break;
            }
        }
        return seed;
    }

    public TETile[][] newWorld(String input) {
        TETile[][] world = null;
        int idxOfS = input.indexOf('s');
        // seed up to 9,223,372,036,854,775,807
        long seed = getSeed(input, idxOfS);
        Random random = new Random(seed);
        world = generateWorld(random);
        parseInputOps(world, input);
        return world;
    }

    private TETile[][] generateWorld(Random random) {
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                world[i][j] = Tileset.NOTHING;
            }
        }
        List<Room> rooms = generateRooms(random);
        fillRoomsWithWallAndFloor(world, rooms);
        connectRooms(world, rooms, random);
        initializeDoor(world, random);
        initializePlayer(world, random);
        return world;
    }

    private void initializeDoor(TETile[][] world, Random random) {
        doorX = RandomUtils.uniform(random, 1, WIDTH - 1);
        doorY = RandomUtils.uniform(random, 1, HEIGHT - 1);
        while (true) {
            if (world[doorX][doorY].equals(Tileset.WALL)
                    && ((world[doorX - 1][doorY].equals(Tileset.NOTHING)
                    && world[doorX + 1][doorY].equals(Tileset.FLOOR))
                    || (world[doorX - 1][doorY].equals(Tileset.FLOOR)
                    && world[doorX + 1][doorY].equals(Tileset.NOTHING))
                    || (world[doorX][doorY - 1].equals(Tileset.FLOOR)
                    && world[doorX][doorY + 1].equals(Tileset.NOTHING))
                    || (world[doorX][doorY - 1].equals(Tileset.NOTHING)
                        && world[doorX][doorY + 1].equals(Tileset.FLOOR))
                )) {
                break;
            }
            doorX = RandomUtils.uniform(random, 1, WIDTH - 1);
            doorY = RandomUtils.uniform(random, 1, HEIGHT - 1);
        }
        world[doorX][doorY] = Tileset.LOCKED_DOOR;
    }

    private void initializePlayer(TETile[][] world, Random random) {
        playerX = RandomUtils.uniform(random, 1, WIDTH - 1);
        playerY = RandomUtils.uniform(random, 1, HEIGHT - 1);
        while (true) {
            if (world[playerX][playerY].equals(Tileset.FLOOR)) {
                break;
            }
            playerX = RandomUtils.uniform(random, 1, WIDTH - 1);
            playerY = RandomUtils.uniform(random, 1, HEIGHT - 1);
        }
        world[playerX][playerY] = Tileset.PLAYER;
    }

    private void fillRoomsWithWallAndFloor(TETile[][] world, List<Room> rooms) {
        for (Room room : rooms) {
            for (int i = room.leftBtm.x; i <= room.upRight.x; i++) {
                for (int j = room.leftBtm.y; j <= room.upRight.y; j++) {
                    if (i == room.leftBtm.x || i == room.upRight.x || j == room.leftBtm.y
                        || j == room.upRight.y) {
                        world[i][j] = Tileset.WALL;
                    } else {
                        world[i][j] = Tileset.FLOOR;
                    }
                }
            }
        }
    }

    private void connectRooms(TETile[][] world, List<Room> rooms, Random random) {
        for (int i = 0; i < rooms.size() - 1; i++) {
            Room r1 = rooms.get(i);
            Room r2 = rooms.get(i + 1);
            Position p1 = new Position(RandomUtils.uniform(random, r1.leftBtm.x + 1,
                    r1.upRight.x),
                    RandomUtils.uniform(random, r1.leftBtm.y + 1, r1.upRight.y));
            Position p2 = new Position(RandomUtils.uniform(random, r2.leftBtm.x + 1,
                    r2.upRight.x),
                    RandomUtils.uniform(random, r2.leftBtm.y + 1, r2.upRight.y));
            createLHallway(world, p1, p2, random);
        }
    }

    private void createLHallway(TETile[][] world, Position p1, Position p2, Random random) {
        // L or up-side-down L
        int startX = Math.min(p1.x, p2.x);
        int endX = Math.max(p1.x, p2.x);
        int startY = Math.min(p1.y, p2.y);
        int endY = Math.max(p1.y, p2.y);
        if (RandomUtils.bernoulli(random)) {
            // L hallway
            for (int i = startX; i <= endX; i++) {
                if (!world[i][p2.y - 1].equals(Tileset.FLOOR)) {
                    world[i][p2.y - 1] = Tileset.WALL;
                }
                world[i][p2.y] = Tileset.FLOOR;
                if (!world[i][p2.y + 1].equals(Tileset.FLOOR)) {
                    world[i][p2.y + 1] = Tileset.WALL;
                }
            }
            for (int i = startY; i <= endY; i++) {
                if (!world[p1.x - 1][i].equals(Tileset.FLOOR)) {
                    world[p1.x - 1][i] = Tileset.WALL;
                }
                world[p1.x][i] = Tileset.FLOOR;
                if (!world[p1.x + 1][i].equals(Tileset.FLOOR)) {
                    world[p1.x + 1][i] = Tileset.WALL;
                }
            }
            world[p1.x - 1][p2.y + 1] = Tileset.WALL;
        } else {
            // up-side-down L hallway
            for (int i = startX; i <= endX; i++) {
                if (!world[i][p1.y - 1].equals(Tileset.FLOOR)) {
                    world[i][p1.y - 1] = Tileset.WALL;
                }
                world[i][p1.y] = Tileset.FLOOR;
                if (!world[i][p1.y + 1].equals(Tileset.FLOOR)) {
                    world[i][p1.y + 1] = Tileset.WALL;
                }
            }
            for (int i = startY; i <= endY; i++) {
                if (!world[p2.x - 1][i].equals(Tileset.FLOOR)) {
                    world[p2.x - 1][i] = Tileset.WALL;
                }
                world[p2.x][i] = Tileset.FLOOR;
                if (!world[p2.x + 1][i].equals(Tileset.FLOOR)) {
                    world[p2.x + 1][i] = Tileset.WALL;
                }
            }
            world[p2.x + 1][p1.y - 1] = Tileset.WALL;
        }
    }

    private List<Room> generateRooms(Random random) {
        List<Room> rooms = new ArrayList<>();
        int roomNum = RandomUtils.uniform(random, MINROOMNUM, MAXROOMNUM);
        int i = 0;
        while (i < roomNum) {
            Room room = createOneRoom(random);
            if (room.isValid(rooms)) {
                rooms.add(room);
                i++;
            }
        }
        rooms.sort(new Comparator<Room>() {
            @Override
            public int compare(Room o1, Room o2) {
                if (o1.leftBtm.x != o2.leftBtm.x) {
                    return o1.leftBtm.x - o2.leftBtm.x;
                } else {
                    return o1.leftBtm.y - o2.leftBtm.y;
                }
            }
        });
        return rooms;
    }

    private Room createOneRoom(Random random) {
        int x = RandomUtils.uniform(random, 1, WIDTH - 2);
        int y = RandomUtils.uniform(random, 1, HEIGHT - 2);
        // the minimum value of height and width should be 3 for
        // a rooms consists of two walls and a floor in each direction
        // width and height include the border (wall) position so 3 - 1 = 2
        int width;
        int height;
        do {
            width = RandomUtils.uniform(random, 2, MAXROOMWIDTH);
        } while (x + width >= WIDTH);
        do {
            height = RandomUtils.uniform(random, 2, MAXROOMHEIGHT);
        } while (y + height >= HEIGHT);
        Room room = new Room(new Position(x, y), new Position(x + width, y + height));
        return room;
    }

    private long getSeed(String input, int idxOfS) {
        return Long.parseLong(input.substring(1, idxOfS));
    }
}
