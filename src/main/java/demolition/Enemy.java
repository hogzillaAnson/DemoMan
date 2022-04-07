package demolition;

/**
 * Abstract class representing enemy characters
 */
public abstract class Enemy extends Character {

    /** The number of seconds between enemy movements */
    public static final int SECONDSBETWEENMOVES = 1;

    /** Keeps track of how long has elapsed since the enemy last moved */
    public int timer;

    /**
     * Constructor for enemy
     * @param x x coordinate of enemy (column)
     * @param y y coordinate of enemy (row)
     * @param app the app that created the enemy
     */
    public Enemy(int x, int y, App app) {
        super(x, y);
        timer = 0;
        dead = false;
    }

    /**
     * Calculates whether the enemy is on the same square as an explosion, in which case the enmy dies
     * @param app the app calling the method
     */
    public void calculateEnemyCollision(App app) {
        if (inExplosion(app)) {
            dead = true;
        }
    }

    /**
     * Abstract method whose implementations update the Enemy according to particular 'AI' movements
     * @param app the app calling the method
     */
    public abstract void tick(App app);
}
