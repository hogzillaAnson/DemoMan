package demolition;

/**
 * represents each of the four cardinal directions, each with a simple string representation.
 * instances are ordered such that they correspond to a clockwise motion as the ordinals increase
 */
public enum Direction {
    /** Left */
    LEFT("left"),

    /** Up */
    UP("up"),

    /** Right */
    RIGHT("right"),

    /** Down */
    DOWN("down");

    /** Simple string description of the direction e.g DOWN -> "down" */
    String string;

    /** Constructor for Direction */
    Direction(String string) {
        this.string = string;
    }
}