package demolition;

import processing.core.PImage;

/**
 * Represents a bomb in the game that ticks and explodes at a particular grid space
 */
public class Bomb implements Drawable {

    /** The number of seconds between frames */
    public static final double SECONDSBETWEENFRAMES = 0.25;

    /** The number of frames in the bomb animation cycle */
    public static final int FRAMES = 8;

    /** Duration in seconds of the bomb ticking sequence */
    public static final int SECONDS = 2;

    public int x;
    public int y;

    /** Keeps track of the time since the bomb was placed */
    public int timer;

    /** The current sprite of the bomb */
    public PImage sprite;

    /** True if the bomb has exploded. Stops the exploded bomb from being drawn or killing the player */
    public boolean exploded;

    /** The animation cycle for the bomb */
    public AnimationCycle animationCycle;
    
    /**
     * Constructor for bomb.
     * @param x the x coordinate (column) of the bomb
     * @param y the y coordinate (row) of the bomb
     * @param app the app from which the method is being called
     */
    public Bomb(int x, int y, App app) {

        this.x = x;
        this.y = y;
        this.timer = 0;
        this.exploded = false;
        
        PImage[] sprites = new PImage[FRAMES];

        for (int i = 1; i < FRAMES + 1; i ++) {
            sprites[i-1] = app.loadImage(String.format("src/main/resources/bomb/bomb%d.png", i));
        }

        //animationCycle = new AnimationCycle(sprites, SECONDSBETWEENFRAMES);
        animationCycle = new AnimationCycle("bomb", "bomb", FRAMES, SECONDSBETWEENFRAMES, app);
        sprite = animationCycle.currentSprite;
    }

    /**
     * Updates the bomb object every frame. If more than 2 seconds worth of frames have elapsed, the bomb explodes,
     * triggering an explosion to be added at the same coordinates.
     * @param app the app calling the method
     */
    public void tick(App app) {

        timer ++;
        if (timer > SECONDS * App.FPS) {
            exploded = true;
            app.addExplosion(x, y);
        } else {
            animationCycle.tick();
            sprite = animationCycle.currentSprite;
        }
    }

    /**
     * Draws the bomb object to the screen
     * @param app the app calling the method
     */
    public void draw(App app) {
        drawToGrid(sprite, x, y, 0, app);
    }

}
