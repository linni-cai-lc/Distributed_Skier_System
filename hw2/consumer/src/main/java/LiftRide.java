import java.util.Objects;
import java.util.Arrays;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;
/**
 * LiftRide
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-02-13T19:56:22.938Z[GMT]")
public class LiftRide {
    @SerializedName("time")
    private Integer time = null;

    @SerializedName("liftID")
    private Integer liftID = null;

    @SerializedName("waitTime")
    private Integer waitTime = null;

    public LiftRide time(Integer time) {
        this.time = time;
        return this;
    }

    /**
     * Get time
     * @return time
     **/
    @Schema(example = "217", description = "")
    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public LiftRide liftID(Integer liftID) {
        this.liftID = liftID;
        return this;
    }

    /**
     * Get liftID
     * @return liftID
     **/
    @Schema(example = "21", description = "")
    public Integer getLiftID() {
        return liftID;
    }

    public void setLiftID(Integer liftID) {
        this.liftID = liftID;
    }

    public LiftRide waitTime(Integer waitTime) {
        this.waitTime = waitTime;
        return this;
    }

    /**
     * Get waitTime
     * @return waitTime
     **/
    @Schema(example = "3", description = "")
    public Integer getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(Integer waitTime) {
        this.waitTime = waitTime;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LiftRide liftRide = (LiftRide) o;
        return Objects.equals(this.time, liftRide.time) &&
                Objects.equals(this.liftID, liftRide.liftID) &&
                Objects.equals(this.waitTime, liftRide.waitTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, liftID, waitTime);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class LiftRide {\n");

        sb.append("    time: ").append(toIndentedString(time)).append("\n");
        sb.append("    liftID: ").append(toIndentedString(liftID)).append("\n");
        sb.append("    waitTime: ").append(toIndentedString(waitTime)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}
