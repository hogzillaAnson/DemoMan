package demolition;

/**
 * Class representing the red enemy in the game
 */
public class RedEnemy extends Enemy {

    /**
     * Red enemy constructor
     * @param x x coordinate
     * @param y y coordinate
     * @param app the app
     */
    public RedEnemy(int x, int y, App app) {
        
        super(x, y, app);
        pickRandomDirection(null);
        
        animationCycles.put(Direction.LEFT, new AnimationCycle("red_enemy", "red_left", FRAMES, SECONDSBETWEENFRAMES, app));
        animationCycles.put(Direction.RIGHT, new AnimationCycle("red_enemy", "red_right", FRAMES, SECONDSBETWEENFRAMES, app));
        animationCycles.put(Direction.UP, new AnimationCycle("red_enemy", "red_up", FRAMES, SECONDSBETWEENFRAMES, app));
        animationCycles.put(Direction.DOWN, new AnimationCycle("red_enemy", "red_down", FRAMES, SECONDSBETWEENFRAMES, app));

        sprite = animationCycles.get(direction).currentSprite;
    }

    /**
     * Updates the red enemy object according to its random direction AI.
     * Determines collisions with explosions and marks the red enemy dead if that is the case
     * @param app the app
     */
    public void tick(App app) {

        timer ++;
        calculateEnemyCollision(app);
        updateAnimation();

        if (!dead && timer > SECONDSBETWEENMOVES * App.FPS) {
            
            if (direction.equals(Direction.LEFT)) {
                
                if (x >= 1 && !app.map.mapTiles[y][x-1].tileType.equals(TileType.Solid) && !app.map.mapTiles[y][x-1].tileType.equals(TileType.Broken)) {
                    x --;
                } else {
                    pickRandomDirection(direction);
                }

            } else if (direction.equals(Direction.RIGHT)) {
                
                if (x <= Map.COLS - 2 && !app.map.mapTiles[y][x+1].tileType.equals(TileType.Solid) && !app.map.mapTiles[y][x+1].tileType.equals(TileType.Broken)) {
                    x ++;
                } else {
                    pickRandomDirection(direction);
                }

            } else if (direction.equals(Direction.UP)) {

                if (y >= 1 && !app.map.mapTiles[y-1][x].tileType.equals(TileType.Solid) && !app.map.mapTiles[y-1][x].tileType.equals(TileType.Broken)) {
                    y --;
                } else {
                    pickRandomDirection(direction);
                }
                
            } else if (direction.equals(Direction.DOWN)) {

                if (y <= Map.ROWS - 2 && !app.map.mapTiles[y+1][x].tileType.equals(TileType.Solid) && !app.map.mapTiles[y+1][x].tileType.equals(TileType.Broken)) {
                    y ++;
                } else {
                    pickRandomDirection(direction);
                }
            }
            timer = 0;
        }
    }
}