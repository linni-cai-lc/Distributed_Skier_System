public class LiftRide {
    private String seasonID;
    private String dayID;
    private String resortID;

    public LiftRide(String seasonID, String dayID, String resortID) {
        this.seasonID = seasonID;
        this.dayID = dayID;
        this.resortID = resortID;
    }

    public String getSeasonID() {
        return seasonID;
    }

    public String getDayID() {
        return dayID;
    }

    public String getResortID() { return resortID; }
}
