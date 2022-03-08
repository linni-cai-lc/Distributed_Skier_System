public class SkierInfo {
    private int resortId;
    private String seasonId;
    private String dayId;
    private int skierId;

    public SkierInfo(int resortId, String seasonId, String dayId, int skierId) {
        this.resortId = resortId;
        this.seasonId = seasonId;
        this.dayId = dayId;
        this.skierId = skierId;
    }

    public int getResortId() {
        return resortId;
    }

    public String getSeasonId() {
        return seasonId;
    }

    public String getDayId() {
        return dayId;
    }

    public int getSkierId() {
        return skierId;
    }
}
