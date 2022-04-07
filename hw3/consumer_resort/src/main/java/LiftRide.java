import com.google.gson.annotations.SerializedName;
public class LiftRide {
    private Integer liftID;
    private Integer liftTime;
    private String seasonID;
    private String dayID;
    private String skierID;
    private String resortID;
    private Integer vertical;

    public LiftRide(Integer liftID, Integer liftTime, String seasonID, String dayID, String skierID, String resortID) {
        this.liftID = liftID;
        this.liftTime = liftTime;
        this.seasonID = seasonID;
        this.dayID = dayID;
        this.skierID = skierID;
        this.resortID = resortID;
        this.vertical = this.liftID * 10;
    }

    public Integer getLiftID() {
        return liftID;
    }

    public Integer getLiftTime() {
        return liftTime;
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

    public String getResortID() { return resortID; }

    public Integer getVertical() {
        return vertical;
    }
}
