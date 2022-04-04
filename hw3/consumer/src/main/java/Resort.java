import com.google.gson.annotations.SerializedName;

public class Resort {
    @SerializedName("liftID")
    private Integer liftId;
    private Integer liftTime;
    private Integer dayId;
    private Integer skierId;
    private Integer resortId;

    public Resort(Integer liftId, Integer liftTime, Integer dayId, Integer skierId, Integer resortId) {
        this.liftId = liftId;
        this.liftTime = liftTime;
        this.dayId = dayId;
        this.skierId = skierId;
        this.resortId = resortId;
    }

    public Integer getLiftId() {
        return liftId;
    }

    public Integer getLiftTime() {
        return liftTime;
    }

    public Integer getDayId() {
        return dayId;
    }

    public Integer getSkierId() {
        return skierId;
    }

    public Integer getResortId() {
        return resortId;
    }
}
