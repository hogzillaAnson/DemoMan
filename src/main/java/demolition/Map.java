package demolition;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

/**
 * Represents the map of the game. Includes attributes and methods to draw the map each frame
 */
public class Map implements Drawable {

    /** Number of columns in the map, by default 15*/
    public static final int COLS = 15;

    /** Number of rows in the map, by default 13*/
    public static final int ROWS = 13;
    
    /** 2D array of tiles that make up the map */
    public Tile[][] mapTiles;

    /** True if the map is hidden to display the win or game over screens. */
    public boolean hidden;

    /**
     * Map constructor, initialising the 2D array of tiles.
     */
    public Map() {
        this.mapTiles = new Tile[ROWS][COLS];
        this.hidden = false;
    }

    /**
     * Static method that reads the data from a map file into the map object in an existing app
     * Handles all errors relating to the map file validity
     * @param path map file path
     * @param lives number of lives for bomb guy to have
     * @param app the app whose map is being read into
     */
    public static void readMapFile(String path, int lives, App app) {

        if (path == null) {
            return;
        }

        File mapFile = new File(path);
        boolean playerDefined = false;

        int topBoundary = -1;
        int bottomBoundary = -1;

        boolean hasTopBoundary = false;
        boolean hasBottomBoundary = false;
        boolean hasLeftBoundary = false;
        boolean hasRightBoundary = false;

        Tile[][] mapTiles = new Tile[ROWS][COLS];
        ArrayList<Enemy> enemies = new ArrayList<Enemy>();
        BombGuy bombGuy = null;

        try {
            Scanner s = new Scanner(mapFile);
            String line = null;
            int i = 0;

            while (i < Map.ROWS && s.hasNextLine()) {
                line = s.nextLine();
                
                if (line.length() != Map.COLS) {
                    System.out.println("Map file doesn't have 15 columns.");
                    s.close();
                    return;
                }

                boolean lineIsBoundary = true;

                for (int j = 0; j < Map.COLS; j ++) {

                    lineIsBoundary = lineIsBoundary && line.charAt(j) == 'W';
                    mapTiles[i][j] = new Tile(Tile.charToTile(line.charAt(j)));
                    // consider keeping track of which entities have been defined with booleans
                    
                    if (line.charAt(j) == 'P') {
                        
                        if (!playerDefined) {
                            bombGuy = new BombGuy(j, i, lives, app); // need to set lives
                            playerDefined = true;

                        } else {

                            System.out.println("Cannot define player more than once.");
                            s.close();
                            return;
                        }

                    } else if (line.charAt(j) == 'R') {
                        enemies.add(new RedEnemy(j, i, app));
                    } else if (line.charAt(j) == 'Y') {
                        enemies.add(new YellowEnemy(j, i, app));
                    }
                }

                if (lineIsBoundary) {
                    if (hasTopBoundary) {
                        hasBottomBoundary = true;
                        bottomBoundary = i;
                    } else {
                        hasTopBoundary = true;
                        topBoundary = i;
                    }
                }
                i ++;
            }            
            
            if (i != Map.ROWS) {
                System.out.println("Map file doesn't have enough rows.");
                s.close();
                return;
            } else if (s.hasNext()) {
                System.out.println("Map file has too many rows.");
                s.close();
                return;
            } else if (!playerDefined) {
                System.out.println("No player is defined in level.");
                s.close();
                return;
            }

            if (!(hasTopBoundary && hasBottomBoundary)) {
                System.out.println("Map has no horizontal boundary walls.");
                s.close();
                return;
            }
            // check whether vertical boundaries exist
            boolean lineIsVerticalBoundary;

            for (int j = 0; j < COLS; j++) {
                lineIsVerticalBoundary = true;
                for (int k = topBoundary; k <= bottomBoundary; k++) {
                    lineIsVerticalBoundary = lineIsVerticalBoundary && (mapTiles[k][j].tileType == TileType.Solid);
                }

                if (lineIsVerticalBoundary) {
                    if (hasLeftBoundary) {
                        hasRightBoundary = true;
                    } else {
                        hasLeftBoundary = true;
                    }
                }
            }

            if (!(hasLeftBoundary && hasRightBoundary)) {
                System.out.println("Map has no vertical boundary walls.");
            } else {
                app.map.mapTiles = mapTiles;
                app.bombGuy = bombGuy;
                app.enemies = enemies;
            }

            s.close();
            return;

        } catch (FileNotFoundException e) {
            System.out.println("No such map file.");
            return;
        }
    }

    /**
    * Draws each tile of the map to the screen, accounting for the vertical offset at the top
    * @param app the app calling the method
    */
    public void draw(App app) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                drawToGrid(mapTiles[i][j].tileGraphic(app), j, i, 0, app);
            }
        }
    }
}