package demolition;

import processing.core.PImage;

/**
 * This class represents an animation cycle of multiple PImage sprites that updates a set number of times per second
 */
public class AnimationCycle {

    /** The array of sprites to loop through */
    public PImage[] sprites;

    /** The index of the current sprite */
    public int currentSpriteIndex;
    
    /** The current sprite being displayed */
    public PImage currentSprite;
    
    /** The number of seconds between frames */
    public double increment;

    /** The number of sprites in the animation cycle */
    public int frames;

    /** Keeps track of the time that has elapsed since the previous sprite */
    public int timer;

    /**
     * Constructor for the animation cycle, reading in sprites from the resources folder
     * @param entity The thing whose animation cycle it is i.e the sub-directory name in main
     * @param filePrefix The part of the filename common to all of the sprite files e.g bomb for bomb1,...,bomb8
     * @param frames How many frames are in the animation cycle
     * @param increment How many seconds are between frames
     * @param app The app
     */
    public AnimationCycle(String entity, String filePrefix, int frames, double increment, App app) {
        
        this.sprites = new PImage[frames];
        this.increment = increment;

        for (int spriteNumber = 1; spriteNumber < frames + 1; spriteNumber++) {
            sprites[spriteNumber-1] = app.loadImage(String.format("src/main/resources/%s/%s%d.png", entity, filePrefix, spriteNumber));
        }

        this.currentSpriteIndex = 0;
        this.currentSprite = sprites[currentSpriteIndex];
        this.frames = sprites.length;
    }

    /**
     * Updates the current sprite that is being displayed, depending on whether the time increment has elapsed since the last sprite
     */
    public void tick() {

        timer ++;
        if (timer > increment * App.FPS) {
            // Moves to the next sprite in the list modulo the number of frames
            currentSprite = sprites[currentSpriteIndex++ % frames];
            timer = 0;
        }
    }
}