import com.google.gson.annotations.SerializedName;
public class LiftRide {
    @SerializedName("liftID")
    private Integer liftId;
    private Integer liftTime;
    private Integer waitTime;
    private Integer seasonId;
    private Integer dayId;
    private Integer skierId;
    private Integer vertical;

    public LiftRide(Integer liftId, Integer liftTime, Integer waitTime, Integer seasonId, Integer dayId, Integer skierId) {
        this.liftId = liftId;
        this.liftTime = liftTime;
        this.waitTime = waitTime;
        this.seasonId = seasonId;
        this.dayId = dayId;
        this.skierId = skierId;
        this.vertical = this.liftId * 10;
    }

    public Integer getLiftId() {
        return liftId;
    }

    public Integer getLiftTime() {
        return liftTime;
    }

    public Integer getWaitTime() {
        return waitTime;
    }

    public Integer getSeasonId() {
        return seasonId;
    }

    public Integer getDayId() {
        return dayId;
    }

    public Integer getSkierId() {
        return skierId;
    }

    public Integer getVertical() {
        return vertical;
    }
}
