public class LiftRide {
    private Integer liftID;
    private String resortID;
    private String seasonID;
    private String dayID;
    private String skierID;

    public LiftRide(Integer liftID, String resortID, String seasonID, String dayID, String skierID) {
        this.liftID = liftID;
        this.resortID = resortID;
        this.seasonID = seasonID;
        this.dayID = dayID;
        this.skierID = skierID;
    }

    public Integer getVertical() {
        return this.liftID * 10;
    }

    public String getResortID() {
        return resortID;
    }

    public String getSeasonID() {
        return seasonID;
    }

    public String getDayID() {
        return dayID;
    }

    public String getSkierID() {
        return skierID;
    }
}
