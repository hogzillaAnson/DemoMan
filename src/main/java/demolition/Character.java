package demolition;

import processing.core.PImage;
import java.util.*;

/**
 * Abstract class representing a character in the game, meaning player or enemy.
 */
public abstract class Character implements Drawable {

    /** Number of seconds between animation frames */
    public static final double SECONDSBETWEENFRAMES = 0.2;

    /** Number of sprites in a character animation cycle, by default 4 */
    public static final int FRAMES = 4;

    /** Number of pixels by which to vertically offset the sprite by to align with the grid properly */
    public static final int CHARACTEROFFSET = -15;

    /** x coordinate */
    public int x;

    /** y coordinate */
    public int y;

    /** whether the character is dead */
    public boolean dead = false;
    
    /** whether the character is hidden */
    public boolean hidden = false;

    /** the current character sprite */
    public PImage sprite;

    /** the direction in which the character is travelling */
    public Direction direction;

    /** Contains animation cycles for each cardinal direction */
    public HashMap<Direction, AnimationCycle> animationCycles = new HashMap<Direction, AnimationCycle>();

    /**
     * Constructor for Character.
     * @param x x coordinate of the character
     * @param y y coordinate of the character
     */    
    public Character(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Sets the character's direction to a random direction other than the specified one.
     * Useful for enemy AI
     * @param except the direction that is excluded from being picked. If null, then no direction is excluded
     */
    protected void pickRandomDirection(Direction except) {
        List<Direction> directions = new ArrayList<Direction>();
        Random rand = new Random();
        int randomNumber = rand.nextInt();

        if (except == null) {
            direction = Direction.values()[Math.abs(randomNumber) % Direction.values().length];
        }

        for (Direction d : Direction.values()) {
            if (!d.equals(except)) {
                directions.add(d);
            }
        }

        direction = directions.get(Math.abs(randomNumber) % directions.size());
    }

    /**
     * Updates the animation cycle for the character depending on their direction
     * Sets the character's sprite to the current sprite of the animation cycle
     */
    public void updateAnimation() {
        animationCycles.get(direction).tick();
        sprite = animationCycles.get(direction).currentSprite;
    }

    /**
     * Draws the sprite to the character's grid coordinates
     * @param app the app calling the method
     */
    public void draw(App app) {
        drawToGrid(sprite, x, y, CHARACTEROFFSET, app);
    }

    /**
     * Determines whether the character is colliding with an ongoing explosion object
     * @param app the app calling the method
     * @return true if the character is in an explosion, false otherwise
     */
    public boolean inExplosion(App app) {
        for (Explosion explosion : app.explosions) {
            try {
                if (!explosion.finished && explosion.squares[Explosion.EXPLOSIONRADIUS + y - explosion.y][Explosion.EXPLOSIONRADIUS + x - explosion.x] != null) {
                    return true;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                return false;
            }
        }
        return false;
    }
}