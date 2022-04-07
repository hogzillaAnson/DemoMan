package demolition;

/**
 * Represents a level of the game
 */
public class Level {

    /** path specifying the map file */
    public String path; 

    /** time limit for the level in seconds*/
    public int timeLimit; 

    /** keeps track of how many frames have elapsed since the start of the level for timing purposes */
    public int timer;

    /**
     * Constructor for level
     * @param path the path of the map file to load
     * @param timeLimit the time limit of the level in seconds
     */
    public Level(String path, int timeLimit) {
        this.path = path;
        this.timeLimit = timeLimit;
        this.timer = 0;
    }

    /**
     * Updates the level object by increasing the timer every frame, 
     */
    public void tick() {
        timer ++;
    }
}