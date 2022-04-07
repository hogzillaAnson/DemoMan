package demolition;
import processing.core.PImage;

/**
* Represents the individual tiles (squares) that make up the map.
*/
public class Tile {

    /** TileType of a particular tile e.g Solid, Broken */
    public TileType tileType;

    /** How big in pixels an individual tile is */
    public static final int TILESIZE = 32;

    /** 
     * Tile constructor 
     * @param tileType the tileType of a particular square
    */
    public Tile(TileType tileType) {
        this.tileType = tileType;
    }

    /**
     * Returns the sprite associated with the tile type
     * @param app the app
     * @return the PImage graphic
     */
    public PImage tileGraphic(App app) {        
        return app.images.get(tileType.string);
    }

    /**
     * Returns the tile type associated with the inputted character from a map file
     * e.g 'W' becomes a Solid tile while ' ' becomes an empty one
     * @param c character to convert into a tile type
     * @return the tile type (Solid, Broken, Empty, Goal) associated with the character
     */
    public static TileType charToTile(char c) {

        if (c == 'W') {
            return TileType.Solid;
        } else if (c == 'B') {
            return TileType.Broken;
        } else if (c == 'G') {
            return TileType.Goal;
        }

        return TileType.Empty;
    }

}

