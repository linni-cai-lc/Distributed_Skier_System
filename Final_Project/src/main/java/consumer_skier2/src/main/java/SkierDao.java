import com.google.gson.Gson;
import redis.clients.jedis.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SkierDao {
    private Jedis jedis;

    public SkierDao(Jedis jedis) {
        this.jedis = jedis;
    }

    public void createSkier(LiftRide newLiftRide) {
        String seasonID = newLiftRide.getSeasonID();
        String skierID = newLiftRide.getSkierID();
        int vertical = newLiftRide.getVertical();

//        jedis.watch(skierID, seasonID);
        Pipeline pip = jedis.pipelined();
        pip.hincrBy(skierID, seasonID, vertical);
        pip.sync();
//        System.out.println("key: " + skierID + ", field: " + seasonID + ", value: " + jedis.hget(skierID, seasonID));
//        List<Object> result = pip.syncAndReturnAll();
//        result

//        String result = jedis.hget(skierID, seasonID);
//
//        Transaction transaction = jedis.multi();
//        if (result != null && result.isEmpty()) {
//            totalVertical += Integer.parseInt(result);
//        }
//        transaction.hset(skierID, seasonID, String.valueOf(totalVertical));
//
//        transaction.exec();

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