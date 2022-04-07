package demolition;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.data.JSONObject;
import processing.data.JSONArray;

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Represents the game executable. Aggregates from other classes
 */
public class App extends PApplet {

    /** Width of the game window in pixels */
    public static final int WIDTH = 480;

    /** Height of the game window in pixels */
    public static final int HEIGHT = 480;

    /** The height of the UI bar at the top */
    public static final int VERTICALOFFSET = 64;

    /** The font size */
    public static final int FONTSIZE = 20;

    /** The framerate */
    public static final int FPS = 60;

    /** The map being played */
    public Map map;

    /** The player object */
    public BombGuy bombGuy;
    
    /** The path to the config file being read from */
    public String configFile;
    
    /** The index of the current level */
    public int levelIndex = 0;

    /** The current level being played */
    public Level currentLevel;

    /** True when the UI should be shown, false otherwise */
    public boolean showUI = true;

    /** The enemies (red + yellow) present in the level */
    public ArrayList<Enemy> enemies;

    /** The bombs present in the level */
    public ArrayList<Bomb> bombs;
    
    /** The explosions present in the level */
    public ArrayList<Explosion> explosions;

    /** The levels that will be played */
    public ArrayList<Level> levels = new ArrayList<Level>();

    /** HashMap of keyCodes marking whether they are being held down at a given moment */
    public HashMap<Integer, Boolean> keysHeldDown = new HashMap<Integer, Boolean>();
    
    /** All the images loaded during setup */
    public HashMap<String, PImage> images = new HashMap<String, PImage>();

    /**
     * Sets the config file for the app to read from.
     * @param configFile The path of the config file
     * @throws NullPointerException when the path is null
     */
    public void setConfig(String configFile) {
        if (configFile != null) {
            this.configFile = configFile;
        } else {
            throw new NullPointerException("Can't have null config path");
        }
    }

    /**
     * Constructor for App. Initialises the map, list of enemies, bombs and explosions
     */
    public App() {
        this.map = new Map();
        this.enemies = new ArrayList<Enemy>();
        this.bombs = new ArrayList<Bomb>();
        this.explosions = new ArrayList<Explosion>();
        this.configFile = "config.json"; // default config, can be changed later (but before setup())
    }

    /**
     * Adds a new Explosion object to the list of explosions
     * @param x the x coordinate of the centre of the explosion
     * @param y the y coordinate of the centre of the explosion
     */
    public void addExplosion(int x, int y) {
        explosions.add(new Explosion(x, y));
    }

    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Called when the player reaches the final goal tile. Hides all UI elements and displays * the YOU WIN text
     * against an orange background.
     */
    public void win() {
        hideAll();
        background(216, 128, 0);
        text("YOU WIN", 169, 250);
        noLoop();
    }

    /**
     * Returns the JSONObject associated with the specified json file path.
     * @param path the path of the json file to be read
     * @return the JSONObject associated with path
     */
    public JSONObject readJSON(String path) {
        File jsonFile = new File(path);
        String jsonString = "";

        try {
            Scanner scan = new Scanner(jsonFile);

            while (scan.hasNext()) {
                jsonString += scan.next();
            }

            scan.close();
            return JSONObject.parse(jsonString);

        } catch (FileNotFoundException e) {

            System.out.println("No such config file!");
            return null;
        }
    }

    /**
     * Loads all images, fonts, levels. Sets the framerate of the game
     */
    public void setup() {

        frameRate(FPS);

        // Load bomb sprites
        for (int i = 1; i < 1 + Bomb.FRAMES; i ++) {
            images.put(String.format("bomb%d", i), loadImage(String.format("src/main/resources/bomb/bomb%d.png", i)));
        }

        // Load tile sprites
        for (TileType tileType : TileType.values()) {
            images.put(tileType.string, loadImage(String.format("src/main/resources/%s/%s.png", tileType.string, tileType.string)));
        }

        // Load explosion sprites
        for (String string : new String[] {"centre", "end_bottom", "end_left", "end_right", "end_top", "horizontal", "vertical"}) {
            images.put(string, loadImage(String.format("src/main/resources/explosion/%s.png", string)));
        }

        // Load UI icons
        images.put("clockIcon", loadImage("src/main/resources/icons/clock.png"));
        images.put("playerIcon", loadImage("src/main/resources/icons/player.png"));

        // Load character sprites
        for (String characterName : new String[]{"player", "red", "yellow"}) {
            
            String directory = characterName;

            if (!characterName.equals("player")) {
                directory += "_enemy";
            }
            
            for (Direction direction : Direction.values()) {
                for (int i = 1; i < 1 + Character.FRAMES; i ++) {
                    images.put(characterName, loadImage(String.format("src/main/resources/%s/%s_%s%d.png", directory, characterName, direction.string, i)));
                }
            }
        }

        // load config.json
        JSONObject config = readJSON(this.configFile);
        JSONArray levelsJSON = config.getJSONArray("levels");

        for (int i = 0; i < levelsJSON.size(); i ++) {
            
            JSONObject levelData = levelsJSON.getJSONObject(i);
            levels.add(new Level(levelData.getString("path"), levelData.getInt("time")));

        }
        
        // Set font
        PFont font = createFont("src/main/resources/PressStart2P-Regular.ttf", FONTSIZE);
        textFont(font);

        //System.out.println("there are " + levels.size() + "levels here");

        // load the first level
        setLevel(levelIndex, config.getInt("lives"));
    }

    /**
     * Sets the current level to the (levelIndex + 1)-th level, and sets the player's lives.
     * If the levelIndex is >= to the number of levels there are, the player is considered to * have won the game by reaching the goal tile of the final level.
     * @param levelIndex the index of the level within levels
     * @param lives the number of lives to give the player
     */
    public void setLevel(int levelIndex, int lives) {

        if (levelIndex < levels.size()) {

            this.enemies = new ArrayList<Enemy>();
            this.bombs = new ArrayList<Bomb>();
            this.explosions = new ArrayList<Explosion>();
            currentLevel = levels.get(levelIndex);
            Map.readMapFile(currentLevel.path, lives, this);

        } else {
            win();
        }
    }

    /**
     * Draws the game over screen. Hides UI elements and displays the GAME OVER text against
     * an orange background. Stops the game from looping.
     */
    public void gameOver() {
        
        hideAll();
        background(216, 128, 0);
        text("GAME OVER", 152, 250);
        noLoop();
    }

    /**
     * Updates all game objects: Map, Level, BombGuy, enemies, bombs, explosions
     */
    public void tick() {

        this.currentLevel.tick();

        if (!bombGuy.dead) {
            this.bombGuy.tick(this);

        } else {
            
            bombGuy.lives--;

            if (bombGuy.lives <= 0) {
                gameOver();
            } else {
                setLevel(levelIndex, bombGuy.lives); // respawn the current level
                bombGuy.dead = false;
            }
        }
        
        for (Enemy enemy : enemies) {
            if (!enemy.dead) {
                enemy.tick(this);
            }
        }

        for (Bomb bomb : bombs) {
            if (!bomb.exploded) {
                bomb.tick(this);
            }
        }

        for (Explosion explosion : explosions) {
            if (!explosion.finished) {
                explosion.tick(this);
            }
        }

        if (currentLevel.timer > currentLevel.timeLimit * FPS || bombGuy.lives == 0) {
            gameOver();
        }

        if (map.mapTiles[bombGuy.y][bombGuy.x].tileType == TileType.Goal) {
            //noLoop();
            levelIndex ++;
            setLevel(levelIndex, bombGuy.lives);
            //loop();
        }
    }

    /**
     * Adds a new bomb to the list of bombs at a particular grid coordinate
     * @param x the x coordinate of the bomb
     * @param y the y coordinate of the bomb
     */
    public void addBomb(int x, int y) {
        this.bombs.add(new Bomb(x, y, this));
    }
    
    /**
     * Draws each bomb in the bombs list if they have not exploded already
     */
    public void drawBombs() {
        for (Bomb bomb : bombs) {
            if (!bomb.exploded) {
                bomb.draw(this);
            }
        }
    }

    /**
     * Draws each explosion in the explosions list if they have not been marked finished
     */
    public void drawExplosions() {
        for (Explosion explosion : explosions) {
            if (!explosion.finished) {
                explosion.draw(this);
            }
        }
    }

    /**
     * Draws each enemy (red and yellow) in the enemies list if they are not dead
     */
    public void drawEnemies() {
        for (Enemy enemy : enemies) {
            if (!enemy.dead) {
                enemy.draw(this);
            }   
        }
    }

    /**
     * Hides all entities (map, player, enemies) by marking enemies as dead and bombs as 
     * exploded.
     */
    public void hideAll() {

        showUI = false;
        map.hidden = true;
        bombGuy.hidden = true;

        for (Enemy enemy : enemies) {
            enemy.dead = true;
        }
        for (Bomb bomb : bombs) {
            bomb.exploded = true;
        }
        for (Explosion explosion : explosions) {
            explosion.finished = true;
        }
    }

    /**
     * Draws the UI elements at the top of the game window if the App attribute showUI is true.
     */
    public void drawUI() {

        if (showUI) {
            background(216, 128, 0); // orange background colour
            fill(0, 0, 0);
            text(String.format("%d", bombGuy.lives), 170, 43); // number of lives
            text(String.format("%d", currentLevel.timeLimit - currentLevel.timer/60), 298, 43); // time left
            
            //text(String.format("%d %d", ui_x,ui_y), 10, 40);

            this.image(images.get("playerIcon"), 129, 15);
            this.image(images.get("clockIcon"), 257, 15);
            
            //this.image(loadImage("src/main/resources/icons/player.png"), 129, 15);
            //this.image(loadImage("src/main/resources/icons/clock.png"), 257, 15);
        }
    }

    /**
     * Draws the map if it is not hidden
     */
    public void drawMap() {
        if (!map.hidden) {
            this.map.draw(this);
        }
    }

    /**
     * Draws the player if it is not hidden
     */
    public void drawBombGuy() {
        if (!bombGuy.hidden) {
            bombGuy.draw(this);
        }
    }

    /**
     * Draws all game objects by calling tick(), then all subsidiary draw functions e.g drawMap(), drawBombGuy().
     * The order of rendering is UI, map, bombs, enemies, explosions, player
     */
    public void draw() {
        tick();
        drawUI();
        drawMap();
        drawBombs();
        drawEnemies();
        drawExplosions();
        drawBombGuy();
    }

    /**
     * Called every time a key is pressed. Handles the arrow keys for player movement, and the space key to place bombs. Holding down a key does not execute an action multiple times
     */
    public void keyPressed() {

        if (keysHeldDown.get(keyCode) == null || !keysHeldDown.get(keyCode)) {
            
            keysHeldDown.put(keyCode, true);

            if (this.keyCode == 37) {
                this.bombGuy.pressLeft(map);

            } else if (this.keyCode == 39) {
                this.bombGuy.pressRight(map);

            } else if (this.keyCode == 38) {
                this.bombGuy.pressUp(map);

            } else if (this.keyCode == 40) {
                this.bombGuy.pressDown(map);

            } else if (this.keyCode == 32) {
                addBomb(bombGuy.x, bombGuy.y);
            }
        }
    }

    /**
     * Called whenenver a key is released. Used to ensure that actions only execute twice when keys are pressed, let go, and pressed again.
     */
    public void keyReleased() {
        keysHeldDown.put(keyCode, false);
    }

    /**
     * Runs the game
     */
    public static void main(String[] args) {
        PApplet.main("demolition.App");
    }
}
