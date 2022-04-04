import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ResortDao {
    private static BasicDataSource dataSource;
    private static String RESORT_TABLE = "Resort";

    public ResortDao() {
        dataSource = DBCPDataSource.getDataSource();
    }

    public void createResort(Resort newResort) {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String insertQueryStatement = "INSERT INTO " + RESORT_TABLE + " " +
                                      "(LiftId, LiftTime, ResortId, DayId, SkierId) " +
                                      "VALUES (?,?,?,?,?)";
        try {
            conn = dataSource.getConnection();
            preparedStatement = conn.prepareStatement(insertQueryStatement);
            preparedStatement.setInt(1, newResort.getLiftId());
            preparedStatement.setInt(2, newResort.getLiftTime());
            preparedStatement.setInt(3, newResort.getResortId());
            preparedStatement.setInt(4, newResort.getDayId());
            preparedStatement.setInt(5, newResort.getSkierId());

            // execute insert SQL statement
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    // How many unique skiers visited resort X on day N?
    public void getDayCountForSkierInSeason(int resortId, int dayId) {
        String queryStmt = "SELECT COUNT(SkierId) AS SKIER_COUNT " +
                           "FROM Resort " +
                           "WHERE ResortId = ? AND DayId = ?;";
        try {
            Connection conn = dataSource.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(queryStmt);
            preparedStatement.setInt(1, resortId);
            preparedStatement.setInt(2, dayId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Integer skierCount = Integer.parseInt(resultSet.getString("SKIER_COUNT"));
                    System.out.println("SUCCESS: GET SKIER COUNT FOR RESORT (" + resortId + ") ON DAY (" + dayId + "): " + skierCount);
                }
            } catch (Exception e) {
                System.out.println("FAILURE: GET SKIER COUNT FOR RESORT (" + resortId + ") ON DAY (" + dayId + "): ");
            }
        } catch (SQLException e) {
//            e.printStackTrace();
            System.out.println("FAILURE: GET SKIER COUNT FOR RESORT (" + resortId + ") ON DAY (" + dayId + "): ");
        }
    }

    // How many rides on lift N happened on day N?
    public void getRideCountForLiftOnDay(int liftId, int dayId) {
        String queryStmt = "SELECT COUNT(LiftTime) AS RIDE_COUNT " +
                           "FROM Resort " +
                           "WHERE LiftId = ? AND DayId = ?;";
        try {
            Connection conn = dataSource.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(queryStmt);
            preparedStatement.setInt(1, liftId);
            preparedStatement.setInt(2, dayId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Integer rideCount = Integer.parseInt(resultSet.getString("RIDE_COUNT"));
                    System.out.println("SUCCESS: GET RIDE COUNT FOR LIFT (" + liftId + ") ON DAY (" + dayId + "): " + rideCount);
                }
            } catch (Exception e) {
                System.out.println("FAILURE: GET RIDE COUNT FOR LIFT (" + liftId + ") ON DAY (" + dayId + ")");
            }
        } catch (SQLException e) {
//            e.printStackTrace();
            System.out.println("FAILURE: GET RIDE COUNT FOR LIFT (" + liftId + ") ON DAY (" + dayId + ")");
        }
    }

    // On day N, show me how many lift rides took place in each hour of the ski day
    // each ski day is of length 420 minutes (7 hours - 9am-4pm)
    public void getLiftCountForTheDay(int dayId) {
        String queryStmt = "SELECT LiftTime " +
                           "FROM LiftRide " +
                           "WHERE DayId = ?;";
        try {
            Connection conn = dataSource.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(queryStmt);
            preparedStatement.setInt(1, dayId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                int[] countArray = {0, 0, 0, 0, 0, 0, 0};
                while (resultSet.next()) {
                    Integer liftTime = Integer.parseInt(resultSet.getString("LiftTime"));
                    if (liftTime != null) {
                        int liftHour = liftTime / 60;
                        countArray[liftHour]++;
                    }
                }
                System.out.println("SUCCESS: GET LIFT COUNT ON DAY (" + dayId + "): " + Arrays.toString(countArray));
            } catch (Exception e) {
                System.out.println("FAILURE: GET LIFT COUNT ON DAY (" + dayId + ")");
            }
        } catch (SQLException e) {
//            e.printStackTrace();
            System.out.println("FAILURE: GET LIFT COUNT ON DAY (" + dayId + ")");
        }
    }

    public void createTable() {
        String createStmt = "CREATE TABLE " + RESORT_TABLE + "(" +
                "LiftId int, " +
                "LiftTime int, " +
                "ResortId int, " +
                "DayId int, " +
                "SkierId int);";
        try {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(createStmt);
            System.out.println("SUCCESS: CREATE TABLE " + RESORT_TABLE);
        } catch (SQLException e) {
//            e.printStackTrace();
            System.out.println("FAILUE: CANNOT CREATE TABLE " + RESORT_TABLE);
        }
    }

    public void deleteTable() {
        System.out.println("DELETE TABLE " + RESORT_TABLE);
        String deleteStmt = "DROP TABLE " + RESORT_TABLE;
        try {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(deleteStmt);
            System.out.println("SUCCESS: DELETE TABLE " + RESORT_TABLE);
        } catch (SQLException e) {
//            e.printStackTrace();
            System.out.println("FAILUE: CANNOT DELETE TABLE " + RESORT_TABLE);
        }
    }
}
