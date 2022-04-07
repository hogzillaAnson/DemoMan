package demolition;

/**
 * Class representing the yellow enemy in the game
 */
public class YellowEnemy extends Enemy {

    /**
     * Yellow enemy constructor
     * @param x x coordinate
     * @param y y coordinate
     * @param app the app
     */
    public YellowEnemy(int x, int y, App app) {
        super(x, y, app);
        pickRandomDirection(null);
        animationCycles.put(Direction.LEFT, new AnimationCycle("yellow_enemy", "yellow_left", FRAMES, SECONDSBETWEENFRAMES, app));
        animationCycles.put(Direction.RIGHT, new AnimationCycle("yellow_enemy", "yellow_right", FRAMES, SECONDSBETWEENFRAMES, app));
        animationCycles.put(Direction.UP, new AnimationCycle("yellow_enemy", "yellow_up", FRAMES, SECONDSBETWEENFRAMES, app));
        animationCycles.put(Direction.DOWN, new AnimationCycle("yellow_enemy", "yellow_down", FRAMES, SECONDSBETWEENFRAMES, app));
        
        sprite = animationCycles.get(direction).currentSprite;
    }
    
    /**
     * Updates the yellow enemy object according to its clockwise direction AI.
     * Determines collisions with explosions and marks the yellow enemy dead if that is the case
     * @param app the app
     */
    public void tick(App app) {
        
        timer ++;
        Map map = app.map;

        calculateEnemyCollision(app);
        updateAnimation();

        // Yellow enemy 'AI'
        if (!dead && timer > SECONDSBETWEENMOVES * App.FPS) {
            
            if (direction.equals(Direction.LEFT)) {
                
                if (x >= 1 && !map.mapTiles[y][x-1].tileType.equals(TileType.Solid) && !map.mapTiles[y][x-1].tileType.equals(TileType.Broken)) {
                    x --;
                } else {
                    direction = Direction.values()[(direction.ordinal() + 1) % FRAMES];
                }

            } else if (direction.equals(Direction.RIGHT)) {
                
                if (x <= Map.COLS - 2 && !map.mapTiles[y][x+1].tileType.equals(TileType.Solid) && !map.mapTiles[y][x+1].tileType.equals(TileType.Broken)) {
                    x ++;
                } else {
                    direction = Direction.values()[(direction.ordinal() + 1) % FRAMES];
                }

            } else if (direction.equals(Direction.UP)) {

                if (y >= 1 && !map.mapTiles[y-1][x].tileType.equals(TileType.Solid) && !map.mapTiles[y-1][x].tileType.equals(TileType.Broken)) {
                    y --;
                } else {
                    direction = Direction.values()[(direction.ordinal() + 1) % FRAMES];
                }
                
            } else if (direction.equals(Direction.DOWN)) {

                if (y <= Map.ROWS - 2 && !map.mapTiles[y+1][x].tileType.equals(TileType.Solid) && !map.mapTiles[y+1][x].tileType.equals(TileType.Broken)) {
                    y ++;
                } else {
                    direction = Direction.values()[(direction.ordinal() + 1) % FRAMES];
                }
            }

            timer = 0;
        }
    }
}
