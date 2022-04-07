package demolition;

import processing.core.PImage;

/**
 * Class representing an explosion in the game
 */
public class Explosion implements Drawable {

    /** How many squares away from the centre the explosion extends to */
    public static final int EXPLOSIONRADIUS = 2;

    /** How long the explosion lasts on screen */
    public static final double DURATION = 0.5;

    /** x coordinate of explosion centre */
    public int x;

    /** y coordinate of explosion centre */
    public int y;

    /** Keeps track of how long has elapsed since the explosion started */
    public int timer;

    /** True if the explosion is finished. If true, the explosion won't be drawn or kill the player */
    public boolean finished; 

    /** 2D array of sprites defining the explosion graphics */
    public PImage[][] squares = new PImage[2*EXPLOSIONRADIUS + 1][2*EXPLOSIONRADIUS + 1];

    /** true if the explosion graphic has been calculated already, in which case draw() will simply render the squares */
    public boolean drawn; 

    /**
     * Constructor for Explosion
     * @param x x coordinate of the centre of the explosion
     * @param y y coordinate of the centre of the explosion
     */
    public Explosion(int x, int y) {

        this.x = x;
        this.y = y;
        this.timer = 0;
        this.finished = false;
        this.drawn = false;

    }


    /**
     * Updates the explosion object. If the duration of the explosion has elapsed, marks the explosion as finished
     * @param app the app
     */
    public void tick(App app) {

        timer ++;

        if (timer > DURATION * App.FPS) {
            finished = true;
            timer = 0;
        }

    }

    /**
     * Draws the vertical elements of the explosion (up and down) by recursively considering squares
     * of increasing distance from the centre
     * @param radius how many squares away from the centre to consider
     * @param app the app
     * @param direction which direction to look in (UP or DOWN).
     */
    public void drawVertical(int radius, App app, Direction direction) {

        int sign;

        if (direction == Direction.UP) {
            sign = -1;
        } else {
            sign = 1;
        }

        if (app.map.mapTiles[y + sign * radius][x].tileType == TileType.Empty || app.map.mapTiles[y + sign * radius][x].tileType == TileType.Goal) {

            if (radius == EXPLOSIONRADIUS || app.map.mapTiles[y + sign * (radius + 1)][x].tileType == TileType.Solid) {

                if (sign == 1) {
                    squares[EXPLOSIONRADIUS + radius][EXPLOSIONRADIUS] = app.loadImage("src/main/resources/explosion/end_bottom.png");
                } else {
                    squares[EXPLOSIONRADIUS - radius][EXPLOSIONRADIUS] = app.loadImage("src/main/resources/explosion/end_top.png");
                }

            } else {

                squares[EXPLOSIONRADIUS + sign * radius][EXPLOSIONRADIUS] = app.loadImage("src/main/resources/explosion/vertical.png");
                drawVertical(radius + 1, app, direction);

            }

        } else if (app.map.mapTiles[y + sign * radius][x].tileType == TileType.Broken) {

            app.map.mapTiles[y + sign * radius][x].tileType = TileType.Empty;

            if (sign == 1) {
                squares[EXPLOSIONRADIUS + radius][EXPLOSIONRADIUS] = app.loadImage("src/main/resources/explosion/end_bottom.png");
            } else {
                squares[EXPLOSIONRADIUS - radius][EXPLOSIONRADIUS] = app.loadImage("src/main/resources/explosion/end_top.png");
            }
        }
    }

    /**
     * Draws the horizontal elements of the explosion (left and right) by recursively considering squares
     * of increasing distance from the centre
     * @param radius how many squares away from the centre to consider
     * @param app the app
     * @param direction which direction to look in (LEFT or RIGHT).
     */
    public void drawHorizontal(int radius, App app, Direction direction) {

        int sign;

        if (direction == Direction.LEFT) {
            sign = -1;
        } else {
            sign = 1;
        }

        if (app.map.mapTiles[y][x + sign * radius].tileType == TileType.Empty || app.map.mapTiles[y][x + sign * radius].tileType == TileType.Goal) {

            if (radius == EXPLOSIONRADIUS || app.map.mapTiles[y][x + sign * (radius + 1)].tileType == TileType.Solid) {                

                if (sign == 1) {
                    squares[EXPLOSIONRADIUS][EXPLOSIONRADIUS + radius] = app.loadImage("src/main/resources/explosion/end_right.png");
                } else {
                    squares[EXPLOSIONRADIUS][EXPLOSIONRADIUS - radius] = app.loadImage("src/main/resources/explosion/end_left.png");
                }

            } else {

                squares[EXPLOSIONRADIUS][EXPLOSIONRADIUS + sign * radius] = app.loadImage("src/main/resources/explosion/horizontal.png");
                drawHorizontal(radius + 1, app, direction);

            }

        } else if (app.map.mapTiles[y][x + sign * radius].tileType == TileType.Broken) {

            app.map.mapTiles[y][x + sign * radius].tileType = TileType.Empty;

            if (sign == 1) {
                squares[EXPLOSIONRADIUS][EXPLOSIONRADIUS + radius] = app.loadImage("src/main/resources/explosion/end_right.png");
            } else {
                squares[EXPLOSIONRADIUS][EXPLOSIONRADIUS - radius] = app.loadImage("src/main/resources/explosion/end_left.png");
            }
        }
    }

    /**
     * Draws the explosion to the screen by calling the drawVertical() and drawHorizontal() methods.
     * 
     * @param app the app
     */
    public void draw(App app) {
        
        if (!drawn) {
            squares[EXPLOSIONRADIUS][EXPLOSIONRADIUS] = app.loadImage("src/main/resources/explosion/centre.png");
            drawVertical(1, app, Direction.UP);
            drawVertical(1, app, Direction.DOWN);
            drawHorizontal(1, app, Direction.LEFT);
            drawHorizontal(1, app, Direction.RIGHT);
            drawn = true;
        } 

        for (int i = 0; i <= 2*EXPLOSIONRADIUS; i ++) {
            if (squares[i][2] != null) {
                drawToGrid(squares[i][2], x + 2 - EXPLOSIONRADIUS, y + i - EXPLOSIONRADIUS, 0, app);
            }
            if (squares[2][i] != null) {
                drawToGrid(squares[2][i], x + i - EXPLOSIONRADIUS, y + 2 - EXPLOSIONRADIUS, 0, app);
            }
        }
    }

}
