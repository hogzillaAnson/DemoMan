package demolition;

import processing.core.PImage;

/**
 * Implemented by all drawable objects: map, player, bomb, etc.
 */
public interface Drawable {

    /**
     * An abstract method whose implementations draw the Drawable object to the screen
     * @param app the app calling the method
     */
    public abstract void draw(App app);
    
    /**
     * Default method that places an image at a particular grid (rather than pixel) coordinate
     * @param image the image to place
     * @param x the x coordinate (grid column) to draw to
     * @param y the y coordinate (grid row) to draw to
     * @param alignment fine tuning factor to adjust where the image sits vertically in the grid space
     * @param app the app to draw to
     */
    public default void drawToGrid(PImage image, int x, int y, int alignment, App app) {
        try{
            app.image(image, x * Tile.TILESIZE, App.VERTICALOFFSET + y * Tile.TILESIZE + alignment);   
        } catch (NullPointerException e) {
            return;
        }
    }
}
