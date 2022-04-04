import java.sql.*;
import org.apache.commons.dbcp2.*;

public class LiftRideDao {
    private static BasicDataSource dataSource;
    private static String LIFT_RIDE_TABLE = "LiftRide";

    public LiftRideDao() {
        dataSource = DBCPDataSource.getDataSource();
    }

    public void createLiftRide(LiftRide newLiftRide) {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String insertQueryStatement = "INSERT INTO " + LIFT_RIDE_TABLE + " " +
                                      "(LiftId, SeasonId, DayId, SkierId, Vertical) " +
                                      "VALUES (?,?,?,?,?)";
        try {
            conn = dataSource.getConnection();
            preparedStatement = conn.prepareStatement(insertQueryStatement);
            preparedStatement.setInt(1, newLiftRide.getLiftId());
            preparedStatement.setInt(2, newLiftRide.getSeasonId());
            preparedStatement.setInt(3, newLiftRide.getDayId());
            preparedStatement.setInt(4, newLiftRide.getSkierId());
            preparedStatement.setInt(5, newLiftRide.getVertical());

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

    // For skier N, how many days have they skied this season?
    public void getDayCountForSkierInSeason(int skierId, int seasonId) {
        String queryStmt = "SELECT COUNT(DayId) AS DAY_COUNT " +
                           "FROM LiftRide " +
                           "WHERE SkierId = ? AND SeasonId = ?;";
        try {
            Connection conn = dataSource.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(queryStmt);
            preparedStatement.setInt(1, skierId);
            preparedStatement.setInt(2, seasonId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Integer dayCount = Integer.parseInt(resultSet.getString("DAY_COUNT"));
                    System.out.println("SUCCESS: GET DAY COUNT FOR SKIER (" + skierId + ") IN SEASON (" + seasonId + "): " + dayCount);
                }
            } catch (Exception e) {
                System.out.println("FAILURE: GET DAY COUNT FOR SKIER (" + skierId + ") IN SEASON (" + seasonId + ")");
            }
        } catch (SQLException e) {
//            e.printStackTrace();
            System.out.println("FAILURE: GET DAY COUNT FOR SKIER (" + skierId + ") IN SEASON (" + seasonId + ")");
        }
    }

    // For skier N, what are the vertical totals for each ski day?
    public void getVerticalForSkierEachDay(int skierId) {
        String queryStmt = "SELECT DayId, Vertical " +
                           "FROM LiftRide " +
                           "WHERE SkierId = ?;";
        try {
            Connection conn = dataSource.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(queryStmt);
            preparedStatement.setInt(1, skierId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Integer dayId = Integer.parseInt(resultSet.getString("DayId"));
                    Integer vertical = Integer.parseInt(resultSet.getString("Vertical"));
                    System.out.println("SUCCESS: GET VERTICAL FOR SKIER (" + skierId + ") ON DAY (" + dayId + "): " + vertical);
                }
            } catch (Exception e) {
                System.out.println("FAILURE: GET VERTICAL FOR SKIER (" + skierId + ")");
            }
        } catch (SQLException e) {
//            e.printStackTrace();
            System.out.println("FAILURE: GET VERTICAL FOR SKIER (" + skierId + ")");
        }
    }

    // For skier N, show me the lifts they rode on each ski day
    public void getLiftCountForSkierEachDay(int skierId) {
        String queryStmt = "SELECT DayId, COUNT(LiftId) AS LIFT_COUNT " +
                           "FROM LiftRide " +
                           "WHERE SkierId = ? " +
                           "GROUP BY DayId";
        try {
            Connection conn = dataSource.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(queryStmt);
            preparedStatement.setInt(1, skierId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Integer dayId = Integer.parseInt(resultSet.getString("DayId"));
                    Integer liftCount = Integer.parseInt(resultSet.getString("LIFT_COUNT"));
                    System.out.println("SUCCESS: GET LIFT COUNT FOR SKIER (" + skierId + ") ON DAY (" + dayId + "): " + liftCount);
                }
            } catch (Exception e) {
                System.out.println("FAILURE: GET LIFT COUNT FOR SKIER (" + skierId + ")");
            }
        } catch (SQLException e) {
//            e.printStackTrace();
            System.out.println("FAILURE: GET LIFT COUNT FOR SKIER (" + skierId + ")");
        }
    }

    public void createTable() {
        String createStmt = "CREATE TABLE " + LIFT_RIDE_TABLE + "(" +
                            "LiftId int, " +
                            "SeasonId int, " +
                            "DayId int, " +
                            "SkierId int, " +
                            "Vertical int);";
        try {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(createStmt);
            System.out.println("SUCCESS: CREATE TABLE " + LIFT_RIDE_TABLE);
        } catch (SQLException e) {
//            e.printStackTrace();
            System.out.println("FAILUE: CANNOT CREATE TABLE " + LIFT_RIDE_TABLE);
        }
    }

    public void deleteTable() {
        System.out.println("DELETE TABLE " + LIFT_RIDE_TABLE);
        String deleteStmt = "DROP TABLE " + LIFT_RIDE_TABLE;
        try {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(deleteStmt);
            System.out.println("SUCCESS: DELETE TABLE " + LIFT_RIDE_TABLE);
        } catch (SQLException e) {
//            e.printStackTrace();
            System.out.println("FAILUE: CANNOT DELETE TABLE " + LIFT_RIDE_TABLE);
        }
    }
}