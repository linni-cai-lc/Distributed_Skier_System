public class LiftRide {
    private String seasonID;
    private String skierID;
    private Integer liftID;

    public LiftRide(Integer liftID, String seasonID, String skierID) {
        this.seasonID = seasonID;
        this.skierID = skierID;
        this.liftID = liftID;
    }

    public String getSeasonID() {
        return seasonID;
    }

    public String getSkierID() {
        return skierID;
    }

    public Integer getVertical() {
        return this.liftID * 10;
    }
}
