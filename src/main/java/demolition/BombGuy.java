package demolition;

/**
 * Represents the player character, Bomb Guy.
 */
public class BombGuy extends Character {

    /** The number of lives the player has */
    public int lives;

    /**
     * Constructor for bomb guy
     * @param x player x coordinate (column)
     * @param y player y coordinate (row)
     * @param lives number of lives the player has
     * @param app the app calling the constructor
     */
    public BombGuy(int x, int y, int lives, App app) {
        
        super(x, y);
        this.lives = lives;
        this.dead = false;
        this.direction = Direction.DOWN;
        
        animationCycles.put(Direction.LEFT, new AnimationCycle("player", "player_left", FRAMES, SECONDSBETWEENFRAMES, app));
        animationCycles.put(Direction.RIGHT, new AnimationCycle("player", "player_right", FRAMES, SECONDSBETWEENFRAMES, app));
        animationCycles.put(Direction.UP, new AnimationCycle("player", "player_up", FRAMES, SECONDSBETWEENFRAMES, app));
        animationCycles.put(Direction.DOWN, new AnimationCycle("player", "player_down", FRAMES, SECONDSBETWEENFRAMES, app));
    }

    /**
     * Called whenever the left arrow key is pressed. Moves the player left if possible, and sets the direction to left for animation purposes.
     * @param map the map on which the player is situated.
     */
    public void pressLeft(Map map) {
        if (x >= 1 && !map.mapTiles[y][x-1].tileType.equals(TileType.Solid) && !map.mapTiles[y][x-1].tileType.equals(TileType.Broken)) {
            x --;
        }
        this.direction = Direction.LEFT;
    }

    /**
     * Called whenever the right arrow key is pressed. Moves the player right if possible, and sets the direction to right for animation purposes.
     * @param map the map on which the player is situated.
     */
    public void pressRight(Map map) {
        if (x <= Map.COLS - 2 && !map.mapTiles[y][x+1].tileType.equals(TileType.Solid) && !map.mapTiles[y][x+1].tileType.equals(TileType.Broken)) {
            x ++;
        }
        this.direction = Direction.RIGHT;
    }

    /**
     * Called whenever the up arrow key is pressed. Moves the player up if possible, and sets the direction to up for animation purposes.
     * @param map the map on which the player is situated.
     */
    public void pressUp(Map map) {
        if (y >= 1 && !map.mapTiles[y-1][x].tileType.equals(TileType.Solid) && !map.mapTiles[y-1][x].tileType.equals(TileType.Broken)) {
            y --;
        }
        this.direction = Direction.UP;
    }

    /**
     * Called whenever the down arrow key is pressed. Moves the player down if possible, and sets the direction to down for animation purposes.
     * @param map the map on which the player is situated.
     */
    public void pressDown(Map map) {
        if (y <= Map.ROWS - 2 && !map.mapTiles[y+1][x].tileType.equals(TileType.Solid) && !map.mapTiles[y+1][x].tileType.equals(TileType.Broken)) {
            y ++;
        }
        this.direction = Direction.DOWN;
    }

    /**
     * Updates the bomb guy object every frame. Updates the animation cycle and determines collisions with explosions or enemies
     * @param app the app calling the method.
     */
    public void tick(App app) {

        updateAnimation();

        if (inExplosion(app)) {
            this.dead = true;
            return;
        }

        for (Enemy enemy : app.enemies) {
            if (!enemy.dead && enemy.x == x && enemy.y == y){
                this.dead = true;
                return;
            }
        }
    }
}