import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public class ResortDao {
    private Jedis jedis;
    private static String NUM_SKIERS = "NUM_SKIERS";

    public ResortDao(Jedis jedis) {
        this.jedis = jedis;
    }

    public void createResort(LiftRide newLiftRide) {
        String resortID = newLiftRide.getResortID();
        String field = resortID + "-" + newLiftRide.getSeasonID() + "-" + newLiftRide.getDayID();
        Pipeline pip = jedis.pipelined();
        jedis.hincrBy(NUM_SKIERS, field, 1);
        pip.sync();
    }
//
//    // For skier N, how many days have they skied this season?
//    public void getDayCountForSkierInSeason(int skierId, int seasonId) {
//        String queryStmt = "SELECT COUNT(DayId) AS DAY_COUNT " +
//                           "FROM LiftRide " +
//                           "WHERE SkierId = ? AND SeasonId = ?;";
//        try {
//            Connection conn = dataSource.getConnection();
//            PreparedStatement preparedStatement = conn.prepareStatement(queryStmt);
//            preparedStatement.setInt(1, skierId);
//            preparedStatement.setInt(2, seasonId);
//            try (ResultSet resultSet = preparedStatement.executeQuery()) {
//                while (resultSet.next()) {
//                    Integer dayCount = Integer.parseInt(resultSet.getString("DAY_COUNT"));
//                    System.out.println("SUCCESS: GET DAY COUNT FOR SKIER (" + skierId + ") IN SEASON (" + seasonId + "): " + dayCount);
//                }
//            } catch (Exception e) {
//                System.out.println("FAILURE: GET DAY COUNT FOR SKIER (" + skierId + ") IN SEASON (" + seasonId + ")");
//            }
//        } catch (SQLException e) {
////            e.printStackTrace();
//            System.out.println("FAILURE: GET DAY COUNT FOR SKIER (" + skierId + ") IN SEASON (" + seasonId + ")");
//        }
//    }
//
//    // For skier N, what are the vertical totals for each ski day?
//    public void getVerticalForSkierEachDay(int skierId) {
//        String queryStmt = "SELECT DayId, Vertical " +
//                           "FROM LiftRide " +
//                           "WHERE SkierId = ?;";
//        try {
//            Connection conn = dataSource.getConnection();
//            PreparedStatement preparedStatement = conn.prepareStatement(queryStmt);
//            preparedStatement.setInt(1, skierId);
//            try (ResultSet resultSet = preparedStatement.executeQuery()) {
//                while (resultSet.next()) {
//                    Integer dayId = Integer.parseInt(resultSet.getString("DayId"));
//                    Integer vertical = Integer.parseInt(resultSet.getString("Vertical"));
//                    System.out.println("SUCCESS: GET VERTICAL FOR SKIER (" + skierId + ") ON DAY (" + dayId + "): " + vertical);
//                }
//            } catch (Exception e) {
//                System.out.println("FAILURE: GET VERTICAL FOR SKIER (" + skierId + ")");
//            }
//        } catch (SQLException e) {
////            e.printStackTrace();
//            System.out.println("FAILURE: GET VERTICAL FOR SKIER (" + skierId + ")");
//        }
//    }
//
//    // skierID_seasonId_day_count = int
//    // resortID_dayID_skier_count = int
//
//    // For skier N, show me the lifts they rode on each ski day
//    public void getLiftCountForSkierEachDay(int skierId) {
//        String queryStmt = "SELECT DayId, COUNT(LiftId) AS LIFT_COUNT " +
//                           "FROM LiftRide " +
//                           "WHERE SkierId = ? " +
//                           "GROUP BY DayId";
//        try {
//            Connection conn = dataSource.getConnection();
//            PreparedStatement preparedStatement = conn.prepareStatement(queryStmt);
//            preparedStatement.setInt(1, skierId);
//            try (ResultSet resultSet = preparedStatement.executeQuery()) {
//                while (resultSet.next()) {
//                    Integer dayId = Integer.parseInt(resultSet.getString("DayId"));
//                    Integer liftCount = Integer.parseInt(resultSet.getString("LIFT_COUNT"));
//                    System.out.println("SUCCESS: GET LIFT COUNT FOR SKIER (" + skierId + ") ON DAY (" + dayId + "): " + liftCount);
//                }
//            } catch (Exception e) {
//                System.out.println("FAILURE: GET LIFT COUNT FOR SKIER (" + skierId + ")");
//            }
//        } catch (SQLException e) {
////            e.printStackTrace();
//            System.out.println("FAILURE: GET LIFT COUNT FOR SKIER (" + skierId + ")");
//        }
//    }
}