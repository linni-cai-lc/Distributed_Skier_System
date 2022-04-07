public class Message {
    private Integer time;
    private Integer liftID;
    private Integer waitTime;
    private String resortId;
    private String seasonID;
    private String dayID;
    private String skierID;
    private String type;

    public Message(Integer time, Integer liftID, Integer waitTime, String resortId, String seasonID, String dayID, String skierID, String type) {
        this.time = time;
        this.liftID = liftID;
        this.waitTime = waitTime;
        this.resortId = resortId;
        this.seasonID = seasonID;
        this.dayID = dayID;
        this.skierID = skierID;
        this.type = type;
    }

    public Integer getTime() {
        return time;
    }

    public Integer getLiftID() {
        return liftID;
    }

    public Integer getWaitTime() {
        return waitTime;
    }

    public String getResortId() {
        return resortId;
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

    public String getType() {
        return type;
    }
}
