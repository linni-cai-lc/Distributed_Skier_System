import java.util.Objects;
import com.google.gson.annotations.SerializedName;
public class LiftRide {
    @SerializedName("liftID")
    private Integer liftId = null;
    private Integer time = null;
    private Integer waitTime = null;

    public LiftRide(Integer liftId, Integer time, Integer waitTime) {
        this.liftId = liftId;
        this.time = time;
        this.waitTime = waitTime;
    }

    public Integer getLiftId() {
        return liftId;
    }

    public Integer getTime() {
        return time;
    }

    public Integer getWaitTime() {
        return waitTime;
    }
}
