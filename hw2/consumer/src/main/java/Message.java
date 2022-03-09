public class Message {
    private Integer time = null;
    private Integer liftID = null;
    private Integer waitTime = null;
    private Integer resortId = null;
    private Integer seasonID = null;
    private Integer dayID = null;
    private Integer skierID = null;
    private String season = null;
    private String type = null;

    public Message(Integer time, Integer liftID, Integer waitTime, Integer resortId, Integer seasonID,
                   Integer dayID, Integer skierID, String type) {
        this.time = time;
        this.liftID = liftID;
        this.waitTime = waitTime;
        this.resortId = resortId;
        this.seasonID = seasonID;
        this.dayID = dayID;
        this.skierID = skierID;
        this.type = type;
    }

    public Message(String season, Integer resortId, String type) {
        this.season = season;
        this.resortId = resortId;
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

    public Integer getResortId() {
        return resortId;
    }

    public Integer getSeasonID() {
        return seasonID;
    }

    public Integer getDayID() {
        return dayID;
    }

    public Integer getSkierID() {
        return skierID;
    }

    public String getSeason() {
        return season;
    }

    public String getType() {
        return type;
    }
}
