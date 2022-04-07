package demolition;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestClass {

    public static final int DELAY = 1500;
    public static boolean draw = false;
    
    /**
     * Ensures bomb guy is initialised correctly
     */
    @Test
    public void bombGuyInitTest() {
        App app = new App();
        app.noLoop();
        app.setConfig("src/test/resources/config.json");
        App.runSketch(new String[] {"App"}, app);
        app.delay(DELAY);
        assertTrue(app.bombGuy != null);
    }

    /**
     * Ensures character movement in all four cardinal directions is correct
     * Ensures that holding down a key does not move the player more than once
     */
    @Test
    public void movementTest() {
        App app = new App();

        app.noLoop();
        app.setConfig("src/test/resources/config.json");
        App.runSketch(new String[] {"App"}, app);
        app.delay(DELAY);

        BombGuy player = app.bombGuy;

        // record initial player position
        int playerX = player.x;
        int playerY = player.y;

        // Press right arrow key down without releasing
        app.keyCode = 39;
        app.keyPressed();
        app.keyPressed();
        app.keyPressed();
        app.keyPressed();

        app.keyReleased();

        // The player should only have moved once to the right
        assertTrue(player.x == playerX + 1);
        assertTrue(player.y == playerY);

        // Press left arrow key
        app.keyCode = 37;
        app.keyPressed();

        assertTrue(player.x == playerX);
        assertTrue(player.y == playerY);

        // Press the dorn arrow key
        app.keyCode = 40;
        app.keyPressed();

        assertTrue(player.x == playerX);
        assertTrue(player.y == playerY + 1);

        // Press up arrow key
        app.keyCode = 38;
        app.keyPressed();

        assertTrue(player.x == playerX);
        assertTrue(player.y == playerY);
    }

    /** Ensures that characters cannot get past solid or broken tiles */
    @Test
    public void noMovementTest() {

        App app = new App();
        app.noLoop();
        app.setConfig("src/test/resources/brokenTilesConfig.json");
        App.runSketch(new String[] {"App"}, app);
        app.setup();
        app.delay(DELAY);

        BombGuy player = app.bombGuy;
        int playerX = player.x;
        int playerY = player.y;
        Map map = app.map;

        player.pressDown(map);
        player.draw(app);
        assertEquals(playerX, player.x);
        assertEquals(playerY, player.y);

        player.pressUp(map);
        player.draw(app);
        assertEquals(playerX, player.x);
        assertEquals(playerY, player.y);

        player.pressLeft(map);
        player.draw(app);
        assertEquals(playerX, player.x);
        assertEquals(playerY, player.y);

        player.pressRight(map);
        player.draw(app);
        assertEquals(playerX, player.x);
        assertEquals(playerY, player.y);

        // Do the same for enemies

        Enemy enemy1 = app.enemies.get(0);
        Enemy enemy2 = app.enemies.get(1);
        int e1_x = enemy1.x;
        int e1_y = enemy1.y;
        int e2_x = enemy2.x;
        int e2_y = enemy2.y;

        for (int i = 0; i < 2*App.FPS; i ++){
            app.draw();
        }

        assertEquals(e1_x, enemy1.x);
        assertEquals(e1_y, enemy1.y);
        assertEquals(e2_x, enemy2.x);
        assertEquals(e2_y, enemy2.y);

        // Make sure when the player is ON the boundary (for some reason) e.g (x,y)=(0,0)
        // they can't go off the map

        Map.readMapFile("src/test/resources/justWalls.txt", 3, app);
        player.x = 0;
        player.y = 0;

        playerX = 0;
        playerY = 0;

        player.pressLeft(map);
        player.draw(app);
        assertEquals(playerX, player.x);
        assertEquals(playerY, player.y);

        player.pressUp(map);
        player.draw(app);
        assertEquals(playerX, player.x);
        assertEquals(playerY, player.y);

        player.x = 14;
        player.y = 12;
        playerX = 14;
        playerY = 12;

        player.pressRight(map);
        player.draw(app);
        assertEquals(playerX, player.x);
        assertEquals(playerY, player.y);

        player.pressDown(map);
        player.draw(app);
        assertEquals(playerX, player.x);
        assertEquals(playerY, player.y);

        // Make sure Solid tiles (walls) block the player in all directions
        player.x = 1;
        player.y = 1;
        playerX = 1;
        playerY = 1;

        player.pressLeft(map);
        player.draw(app);
        assertEquals(playerX, player.x);
        assertEquals(playerY, player.y);

        player.pressUp(map);
        player.draw(app);
        assertEquals(playerX, player.x);
        assertEquals(playerY, player.y);

        player.x = 13;
        player.y = 11;
        playerX = 13;
        playerY = 11;

        player.pressRight(map);
        player.draw(app);
        assertEquals(playerX, player.x);
        assertEquals(playerY, player.y);

        player.pressDown(map);
        player.draw(app);
        assertEquals(playerX, player.x);
        assertEquals(playerY, player.y);

        // Make sure enemies can't go off the edge of the map

        Enemy red = new RedEnemy(0,0,app);
        Enemy yellow = new YellowEnemy(0,0,app);

        red.direction = Direction.LEFT;
        yellow.direction = Direction.LEFT;
        app.enemies.add(red);
        app.enemies.add(yellow);

        for (int i = 0; i < App.FPS; i++) {
            app.draw();
        }

        assertEquals(0, red.x);
        assertEquals(0, red.y);
        assertEquals(0, yellow.x);
        assertEquals(0, yellow.y);

        red.direction = Direction.UP;
        yellow.direction = Direction.UP;

        for (int i = 0; i < App.FPS; i++) {
            app.draw();
        }

        assertEquals(0, red.x);
        assertEquals(0, red.y);
        assertEquals(0, yellow.x);
        assertEquals(0, yellow.y);

        red.x = 14;
        red.y= 12;
        yellow.x = 14;
        yellow.y = 12;
        red.direction = Direction.RIGHT;
        yellow.direction = Direction.RIGHT;

        for (int i = 0; i < App.FPS; i++) {
            app.draw();
        }

        assertEquals(14, red.x);
        assertEquals(12, red.y);
        assertEquals(14, yellow.x);
        assertEquals(12, yellow.y);

        red.direction = Direction.DOWN;
        yellow.direction = Direction.DOWN;

        for (int i = 0; i < App.FPS; i++) {
            app.draw();
        }

        assertEquals(14, red.x);
        assertEquals(12, red.y);
        assertEquals(14, yellow.x);
        assertEquals(12, yellow.y);

        // Make sure walls block enemies

        red.x = 1;
        red.y = 1;
        yellow.x = 1;
        yellow.y = 1;

        red.direction = Direction.LEFT;
        yellow.direction = Direction.LEFT;

        for (int i = 0; i < App.FPS; i++) {
            app.draw();
        }

        assertEquals(1, red.x);
        assertEquals(1, red.y);
        assertEquals(1, yellow.x);
        assertEquals(1, yellow.y);

        red.direction = Direction.UP;
        yellow.direction = Direction.UP;

        for (int i = 0; i < App.FPS; i++) {
            app.draw();
        }

        assertEquals(1, red.x);
        assertEquals(1, red.y);
        assertEquals(1, yellow.x);
        assertEquals(1, yellow.y);

        red.x = 13;
        red.y= 11;
        yellow.x = 13;
        yellow.y = 11;

        red.direction = Direction.RIGHT;
        yellow.direction = Direction.RIGHT;

        for (int i = 0; i < App.FPS; i++) {
            app.draw();
        }

        assertEquals(13, red.x);
        assertEquals(11, red.y);
        assertEquals(13, yellow.x);
        assertEquals(11, yellow.y);

        red.direction = Direction.DOWN;
        yellow.direction = Direction.DOWN;

        for (int i = 0; i < App.FPS; i++) {
            app.draw();
        }

        assertEquals(13, red.x);
        assertEquals(11, red.y);
        assertEquals(13, yellow.x);
        assertEquals(11, yellow.y);

    }

    /**
     * Ensures bombs are placed correctly
     * Ensures holding down space does not place more than one bomb
     */
    @Test
    public void bombTest() {
        
        App app = new App();
        app.noLoop();

        App.runSketch(new String[] {"App"}, app);
        //app.setup();
        app.delay(DELAY);

        BombGuy player = app.bombGuy;
        if (draw) {
            app.draw();
        }

        // record initial player position
        int playerX = player.x;
        int playerY = player.y;

        // Press right arrow key
        app.keyCode = 32;
        app.keyPressed();
        app.keyPressed();
        app.keyReleased();

        assertTrue(app.bombs.size() == 1);
        assertTrue(app.bombs.get(0).x == playerX && app.bombs.get(0).y == playerY);

        // Call draw again to move onto the next frame
        if (draw) {
            app.draw();
        }

    }

    /**
     * Assuming bombs work well, ensures explosions are spawned correctly
     */
    @Test
    public void explosionSpawnTest() {

        App app = new App();
        app.noLoop();
        App.runSketch(new String[] {"App"}, app);
        app.delay(DELAY);

        if (draw) {
            app.draw();
        }

        // Press space bar
        app.keyCode = 32;
        app.keyPressed();
        app.keyReleased();

        // go right by 3
        app.keyCode = 39;
        app.keyPressed();
        app.keyReleased();
        app.keyPressed();
        app.keyReleased();
        app.keyPressed();
        app.keyReleased();

        for (int i = 0; i <= 2 * App.FPS; i ++) {
            app.tick();
        }

        // there should be an active explosion now
        assertEquals(1, app.explosions.size());
        assertTrue(!app.explosions.get(0).finished);

        for (int i = 0; i <= 0.5 * App.FPS; i ++) {
            app.tick();
        }

        // the explosion should be done now
        assertTrue(app.explosions.get(0).finished);
    }

    /**
     * Ensures explosions are displayed correctly
     */
    @Test
    public void explosionGraphicsTest1() {
        App app = new App();
        app.noLoop();

        App.runSketch(new String[] {"App"}, app);
        //app.setup();
        app.delay(DELAY);

        if (draw) {
            app.draw();
        }

        // Press space bar
        app.keyCode = 32;
        app.keyPressed();
        app.keyReleased();

        app.keyCode = 39;
        app.keyPressed();
        app.keyReleased();
        app.keyPressed();
        app.keyReleased();
        app.keyPressed();
        app.keyReleased();

        for (int i = 0; i <= 2 * App.FPS; i ++) {
            app.draw();
        }

        assertEquals(1, app.explosions.size());

        app.tick();

        assertTrue(app.explosions.get(0).x == 1 && app.explosions.get(0).y == 1);
        app.draw();

        for (int i = 0; i <= 0.5 * App.FPS; i ++) {
            app.draw();
        }

        app.keyCode = 39;
        app.keyPressed();
        app.keyReleased();

        app.keyCode = 32;
        app.keyPressed();
        app.keyReleased();

        app.keyCode = 37;
        app.keyPressed();
        app.keyReleased();
        app.keyPressed();
        app.keyReleased();
        app.keyPressed();
        app.keyReleased();

        for (int i = 0; i <= 2 * App.FPS; i ++) {
            app.draw();
        }

        assertEquals(2, app.explosions.size());
    }

    /**
     * Ensures explosions have the correct effect on broken squares
     */
    @Test
    public void explosionGraphicsTest2() {
        App app = new App();
        app.noLoop();
        
        App.runSketch(new String[] {"App"}, app);
        
        app.setConfig("src/test/resources/explosionGraphicsConfig.json");
        
        app.setup();
        app.delay(DELAY);

        app.addExplosion(7, 5);
       
        app.draw();
        Explosion explosion = app.explosions.get(0);

        assertTrue(explosion.squares[1][2] != null);
        assertTrue(explosion.squares[0][2] == null); // should be blocked by broken tile

        assertTrue(explosion.squares[2][1] != null);
        assertTrue(explosion.squares[2][0] != null);

        assertTrue(explosion.squares[2][3] != null);
        assertTrue(explosion.squares[2][4] != null);

        assertTrue(explosion.squares[3][2] != null);
        assertTrue(explosion.squares[4][2] != null);

        for (int i = 0; i < 0.5 * App.FPS; i ++) {
            app.draw();
        }

        // The explosion should have turned all broken squares into empty ones
        assertTrue(app.map.mapTiles[4][7].tileType == TileType.Empty);
        assertTrue(app.map.mapTiles[5][5].tileType == TileType.Empty);
        assertTrue(app.map.mapTiles[5][9].tileType == TileType.Empty);
        assertTrue(app.map.mapTiles[7][7].tileType == TileType.Empty);
    }


    /**
     * Tests various problems with the map file. The Map object should have null tiles showing
     * the map was not imported at all because of the error 
     */
    @Test
    public void invalidMapFileTest() {
        App app = new App();
        app.noLoop();
        App.runSketch(new String[] {"App"}, app);

        // Missing map file
        app.setConfig("src/test/resources/missingMapFileConfig.json");

        boolean nullTile = true;

        for (int i = 0; i < app.map.mapTiles.length; i ++) {
            for (int j = 0; j < app.map.mapTiles[i].length; j ++) {
                nullTile = nullTile && (app.map.mapTiles[i][j] == null);
            }
        }
        
        assertTrue(nullTile);


        // Map file defines player (P) more than once
        app.map = new Map();
        Map.readMapFile("src/test/resources/playerDefinedTwiceLevel.txt", 3, app);
        nullTile = true;

        for (int i = 0; i < app.map.mapTiles.length; i ++) {
            for (int j = 0; j < app.map.mapTiles[i].length; j ++) {
                nullTile = nullTile && (app.map.mapTiles[i][j] == null);
            }
        }
        
        assertTrue(nullTile);

        // Map doesn't have 15 columns
        app.map = new Map();
        Map.readMapFile("src/test/resources/notEnoughColumns.txt", 3, app);
        nullTile = true;

        for (int i = 0; i < app.map.mapTiles.length; i ++) {
            for (int j = 0; j < app.map.mapTiles[i].length; j ++) {
                nullTile = nullTile && (app.map.mapTiles[i][j] == null);
            }
        }

        assertTrue(nullTile);

        // Map doesn't have enough rows
        app.map = new Map();
        Map.readMapFile("src/test/resources/notEnoughRows.txt", 3, app);
        nullTile = true;

        for (int i = 0; i < app.map.mapTiles.length; i ++) {
            for (int j = 0; j < app.map.mapTiles[i].length; j ++) {
                nullTile = nullTile && (app.map.mapTiles[i][j] == null);
            }
        }

        assertTrue(nullTile);
        
        // Map has too many rows
        app.map = new Map();
        Map.readMapFile("src/test/resources/tooManyRows.txt", 3, app);
        nullTile = true;

        for (int i = 0; i < app.map.mapTiles.length; i ++) {
            for (int j = 0; j < app.map.mapTiles[i].length; j ++) {
                nullTile = nullTile && (app.map.mapTiles[i][j] == null);
            }
        }

        assertTrue(nullTile);

        // Map doesn't define a player (P)
        app.map = new Map();
        Map.readMapFile("src/test/resources/noPlayerDefined.txt", 3, app);
        nullTile = true;

        for (int i = 0; i < app.map.mapTiles.length; i ++) {
            for (int j = 0; j < app.map.mapTiles[i].length; j ++) {
                nullTile = nullTile && (app.map.mapTiles[i][j] == null);
            }
        }

        assertTrue(nullTile);

        // Map doesn't have a solid (horizontal) bounding border,
        // which I have interpreted to mean a rectangular wall perimeter
        // It is permitted for maps to have empty tiles outside the wall,
        // but a wall there must be!
        app.map = new Map();

        Map.readMapFile("src/test/resources/noHorizontalMapBoundary.txt", 3, app);

        nullTile = true;

        for (int i = 0; i < app.map.mapTiles.length; i ++) {
            for (int j = 0; j < app.map.mapTiles[i].length; j ++) {
                nullTile = nullTile && (app.map.mapTiles[i][j] == null);
            }
        }

        assertTrue(nullTile);

        // Map doesn't have a solid (vertical) bounding border
        app.map = new Map();
        Map.readMapFile("src/test/resources/noVerticalMapBoundary.txt", 3, app);

        nullTile = true;

        for (int i = 0; i < app.map.mapTiles.length; i ++) {
            for (int j = 0; j < app.map.mapTiles[i].length; j ++) {
                nullTile = nullTile && (app.map.mapTiles[i][j] == null);
            }
        }
        assertTrue(nullTile);
    }
    

    /**
     * Ensures that the levels advance correctly and result in the player winning the game
     */
    @Test
    public void winTest() {

        App app = new App();
        app.noLoop();
        app.setConfig("src/test/resources/winTestConfig.json");
        App.runSketch(new String[] {"App"}, app);
        app.delay(DELAY);

        Map.readMapFile("src/test/resources/winLevel.txt", 3, app);
        assertTrue(app.bombGuy != null);

        // go right
        app.keyCode = 39;
        app.keyPressed();
        app.keyReleased();
        app.draw();

        // should have advanced one level
        assertEquals(1, app.levelIndex);

        // go right
        app.keyCode = 39;
        app.keyPressed();
        app.keyReleased();
        app.draw();

        // should have advanced again
        assertEquals(2, app.levelIndex);
        assertEquals(2, app.levels.size());

        app.tick();
        app.tick();
        app.draw();

        boolean allHidden = app.map.hidden && app.bombGuy.hidden && !app.showUI;
        
        for (Bomb bomb : app.bombs) {
            allHidden = allHidden && bomb.exploded;
        }

        for (Enemy enemy : app.enemies) {
           allHidden = allHidden && enemy.dead;
        }

        // all elements should be hidden to show the win screen
        assertTrue(allHidden);

    }

    /**
     * Ensures that when the player loses all lives, they lose the game
     */
    @Test
    public void gameOverTest() {

        App app = new App();
        app.noLoop();
        app.setConfig("src/test/resources/gameOverTestConfig.json");
        App.runSketch(new String[] {"App"}, app);
        app.setup();
        app.delay(DELAY);

        int initialLives = app.bombGuy.lives;
        assertEquals(2, initialLives);

        // place bomb
        app.keyCode = 32;
        app.keyPressed();
        app.keyReleased();

        // wait for the bomb to explode
        for (int j = 0; j < 2 *App.FPS; j ++) {
            app.draw();
        }

        assertEquals(0, app.explosions.size());
        app.draw();
        assertEquals(1, app.explosions.size());

        assertEquals(app.bombGuy.x, app.explosions.get(0).x);
        assertEquals(app.bombGuy.y, app.explosions.get(0).y);

        assertTrue(!app.bombGuy.dead);
        app.draw();

        // The player should be dead
        assertTrue(app.bombGuy.dead);
        app.draw();
        // The player should have one less life
        assertEquals(1, app.bombGuy.lives);
        assertTrue(!app.bombGuy.dead);

        // repeat the above
        assertEquals(1, app.bombGuy.lives);
        initialLives = app.bombGuy.lives;

        // place bomb
        app.keyCode = 32;
        app.keyPressed();
        app.keyReleased();

        for (int j = 0; j < 2 *App.FPS; j ++) {
            app.draw();
        }

        assertEquals(0, app.explosions.size());
        app.draw();
        assertEquals(1, app.explosions.size());
        assertTrue(!app.bombGuy.dead);
        app.draw();
        assertTrue(app.bombGuy.dead);
        app.draw();


        // the player should have no lives now
        assertEquals(0, app.bombGuy.lives);

        /** Ensure the game hides game elements correctly to display the game over screen */
        boolean allHidden = app.map.hidden && app.bombGuy.hidden && !app.showUI;

        for (Bomb bomb : app.bombs) {
            allHidden = allHidden && bomb.exploded;
        }

        for (Enemy enemy : app.enemies) {
           allHidden = allHidden && enemy.dead;
        }

        assertTrue(allHidden);
    }

    /**
     * Ensures the yellow enemy moves correctly when encountering obstacles
     */
    @Test
    public void yellowEnemyTest() {
        App app = new App();
        app.noLoop();
        app.setConfig("src/test/resources/yellowEnemyTestConfig.json");

        App.runSketch(new String[] {"App"}, app);        
        app.delay(DELAY);

        assertEquals(1, app.enemies.size());
        assertEquals(6, app.enemies.get(0).x);
        assertEquals(2, app.enemies.get(0).y);
        app.enemies.get(0).direction = Direction.DOWN;

        for (int i = 0; i < 4 * App.FPS; i ++) {
            app.draw();
        }

        assertTrue(app.enemies.get(0).x == 6 && app.enemies.get(0).y == 4);

        // The direction should have changed clockwise to LEFT
        assertEquals(Direction.LEFT, app.enemies.get(0).direction);

        for (int i = 0; i < 5 * App.FPS; i ++) {
            app.draw();
        }

        assertTrue(app.enemies.get(0).x == 2 && app.enemies.get(0).y == 4);
        assertEquals(Direction.UP, app.enemies.get(0).direction);

        for (int i = 0; i < 3 * App.FPS; i ++) {
            app.draw();
        }
        assertEquals(2, app.enemies.get(0).x);
        assertEquals(2, app.enemies.get(0).y);
        assertEquals(Direction.RIGHT, app.enemies.get(0).direction);

        for (int i = 0; i < 5 * App.FPS; i ++) {
            app.draw();
        }

        assertTrue(app.enemies.get(0).x == 6 && app.enemies.get(0).y == 2);
        assertEquals(Direction.DOWN, app.enemies.get(0).direction);

    }

    /**
     * Ensures the red enemy moves correctly when encountering obstacles
     */
    @Test
    public void redEnemyTest() {

        App app = new App();
        app.noLoop();
        App.runSketch(new String[] {"App"}, app);
        app.setConfig("src/test/resources/redEnemyTestConfig.json");
        app.setup();
        app.delay(DELAY);
        assertEquals(1, app.enemies.size());
        Enemy redEnemy = app.enemies.get(0);

        for (Direction d : Direction.values()) {
            redEnemy.x = 3;
            redEnemy.y = 3;
            redEnemy.direction = d;

            for (int i = 0; i <= 3 * App.FPS; i ++) {
                app.draw();
            }

            if (d == Direction.LEFT) {
                assertTrue((redEnemy.x == 1 && redEnemy.y == 3)); 
            } else if (d == Direction.RIGHT) {
                assertTrue((redEnemy.x == 5 && redEnemy.y == 3)); 
            } else if (d == Direction.UP) {
                assertTrue((redEnemy.x == 3 && redEnemy.y == 1)); 
            } else if (d == Direction.DOWN) {
                assertTrue((redEnemy.x == 3 && redEnemy.y == 5)); 
            }

            for (int i = 0; i <= App.FPS; i ++) {
                app.draw();
            }
            
            // The random choice should be different from the previous direction
            assertTrue(d != redEnemy.direction);
        }
    }

    /**
     * Ensures enemies die when encountering an explosion
     */
    @Test
    public void enemyDeathTest() {
        App app = new App();
        app.noLoop();
        
        App.runSketch(new String[] {"App"}, app);
        
        app.setConfig("src/test/resources/enemyDeathConfig.json");
        app.setup();
        app.delay(DELAY);

        assertEquals(1, app.enemies.size());
        Enemy enemy = app.enemies.get(0);
        enemy.direction = Direction.RIGHT;

        app.addBomb(12, 6);

        assertEquals(1, app.bombs.size());
        assertEquals(0, app.bombs.get(0).timer);

        assertEquals(12, enemy.x);
        assertEquals(6, enemy.y);
        
        for (int i = 0; i < 2 * App.FPS; i ++) {
            app.draw();
        }
        assertTrue(!app.bombs.get(0).exploded);
        assertEquals(0, app.explosions.size());
        
        app.draw();

        assertTrue(app.bombs.get(0).exploded);
        assertEquals(1, app.explosions.size());
        
        app.draw();
        
        assertTrue(app.explosions.get(0).squares[2][2] != null);
        assertTrue(app.explosions.get(0).squares[2][3] != null);
        assertTrue(app.explosions.get(0).squares[2][1] != null);
        assertTrue(enemy.dead);    
    }

    /**
     * Ensures that bomb guy loses a life when he touches an enemy
     */
    @Test
    public void enemyTouchingTest() {

        App app = new App();
        app.noLoop();
        app.setConfig("src/test/resources/enemyTouchingConfig.json");

        App.runSketch(new String[] {"App"}, app);        
        
        ////app.setup();
        app.delay(DELAY);

        // reduce lives by 1
        int initialLives = app.bombGuy.lives;
        assertEquals(2, initialLives);

        app.enemies.get(0).direction = Direction.RIGHT;

        for (int j = 0; j < 2 * App.FPS; j ++) {
            app.draw();
        }
        
        app.draw();
        app.draw();
        assertEquals(app.enemies.get(0).x, app.bombGuy.x);
        assertEquals(app.enemies.get(0).y, app.bombGuy.y);

        assertTrue(app.bombGuy.dead);
        app.draw();
        assertEquals(1, app.bombGuy.lives);
        assertTrue(!app.bombGuy.dead);
        app.enemies.get(0).direction = Direction.RIGHT;

        for (int j = 0; j < 2 * App.FPS; j ++) {
            app.draw();
        }

        app.draw();
        app.draw();
        app.draw();    

        assertEquals(app.enemies.get(0).x, app.bombGuy.x);
        assertEquals(app.enemies.get(0).y, app.bombGuy.y);
        assertTrue(app.bombGuy.dead);
        assertEquals(0, app.bombGuy.lives);
    }
}
