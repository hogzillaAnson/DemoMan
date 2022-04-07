package demolition;

/**
* Represents the different tile types (Solid, Broken, Empty, Goal) that make up the map.
*/
public enum TileType {
    /** Solid / wall tile type */
    Solid("solid"),

    /** Broken tile type */
    Broken("broken"),

    /** Empty tile type */
    Empty("empty"),

    /** Goal tile */
    Goal("goal");

    /** Represents a simple string description of the tiletype e.g Solid -> "solid" */
    String string;

    /** Constructor for TileType */
    TileType(String string) {
        this.string = string;
    }
}