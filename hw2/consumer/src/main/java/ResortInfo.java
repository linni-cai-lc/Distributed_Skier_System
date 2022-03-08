public class ResortInfo {
    private int resortId;
    private int seasonId;
    private int dayId;
    private String resortName;

    public ResortInfo(int resortId, int seasonId, int dayId, String resortName) {
        this.resortId = resortId;
        this.seasonId = seasonId;
        this.dayId = dayId;
        this.resortName = resortName;
    }

    public int getResortId() {
        return resortId;
    }

    public int getSeasonId() {
        return seasonId;
    }

    public int getDayId() {
        return dayId;
    }

    public String getResortName() {
        return resortName;
    }
}
