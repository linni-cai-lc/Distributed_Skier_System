import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

import org.json.JSONObject;
import redis.clients.jedis.JedisPooled;

public class ResortServlet extends HttpServlet {
  private JedisPooled jedis;
  private static final String RESORT_NAME = "Mission Ridge";
  private static final String NUM_SKIERS = "NUM_SKIERS";
  private static final String RESORT_NOT_FOUND = "{\"message\": \"Resort not found\"}";
  private static final String INVALID_RESORT_ID = "{\"message\": \"Invalid Resort ID supplied\"}";
  private static final String INVALID_SEASON_ID = "{\"message\": \"Invalid Season ID supplied\"}";
  private static final String INVALID_DAY_ID = "{\"message\": \"Invalid Day ID supplied\"}";
  private static final String INVALID_INPUT = "{\"message\": \"Invalid inputs supplied\"}";
  private static final String SEASONS = "seasons";
  private static final String DAY = "day";
  private static final String SKIERS = "skiers";
  private static final int MIN_DAY_ID = 1;
  private static final int MAX_DAY_ID = 366;

  @Override
  public void init() throws ServletException {
    super.init();
    jedis = new JedisPooled(IPAddress.RESORT_REDIS_IP, 6379);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
    String urlPath = req.getPathInfo();
    PrintWriter writer = res.getWriter();

    if (urlPath == null || urlPath.isEmpty()) {
      res.setContentType("text/plain");
      res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
      writer.write("Please provide url");
      return;
    }

    String[] urlParts = urlPath.substring(1).split("/");

    if (urlParts.length == 6 &&
            urlParts[1].equals(SEASONS) &&
            urlParts[3].equals(DAY) &&
            urlParts[5].equals(SKIERS)) {
      String resortIDString = urlParts[0];
      String seasonIDString = urlParts[2];
      String dayIDString = urlParts[4];

      int resortID = parseID(resortIDString, res, writer, INVALID_RESORT_ID);
      int seasonID = parseID(seasonIDString, res, writer, INVALID_SEASON_ID);
      int dayID = parseID(dayIDString, res, writer, INVALID_DAY_ID);
      if (resortID == -1 || seasonID == -1 || dayID == -1) {
        // do nothing
      } else if (dayID < MIN_DAY_ID || dayID > MAX_DAY_ID) {
        invalidInput(res, writer, INVALID_DAY_ID);
      } else {
        // get number of unique skiers at resort/season/day
        // resort GET: get the number of unique skiers at resort/season/day
        //   0          1          2         3      4     5
        // [resortID, "seasons", seasonID, "day", dayID, skiers]
        try {
          String numSkiersField = resortIDString + "-" + seasonIDString + "-" + dayIDString;
          String result = jedis.hget(NUM_SKIERS, numSkiersField);

          int numSkiers = Integer.parseInt(result);
          if (numSkiers == 0) {
            resortNotFound(res, writer);
          } else {
            JSONObject resultObject = new JSONObject();
            resultObject.put("resortName", RESORT_NAME);
            resultObject.put("numSkiers", numSkiers);
            res.setStatus(HttpServletResponse.SC_OK);
            writer.write(resultObject.toString());
          }
        } catch(Exception e) {
          resortNotFound(res, writer);
        }
      }
    } else {
      invalidInput(res, writer, INVALID_INPUT);
    }
  }

  private int parseID(String idString, HttpServletResponse res, PrintWriter writer, String errorMessage) {
    try {
      return Integer.parseInt(idString);
    } catch (NumberFormatException e) {
      invalidInput(res, writer, errorMessage);
      return -1;
    }
  }

  private void resortNotFound(HttpServletResponse res, PrintWriter writer) {
    res.setStatus(HttpServletResponse.SC_NOT_FOUND);
    writer.write(RESORT_NOT_FOUND);
  }

  private void invalidInput(HttpServletResponse res, PrintWriter writer, String errorMessage) {
    res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    writer.write(errorMessage);
  }


//  @Override
//  protected void doPost(HttpServletRequest req, HttpServletResponse res)
//      throws ServletException, IOException {
//    String urlPath = req.getPathInfo();
//    // check we have a URL!
//    if (urlPath == null || urlPath.isEmpty()) {
//      res.setContentType("text/plain");
//      res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
//      res.getWriter().write("Please provide url");
//      return;
//    }
//    String[] urlParts = urlPath.substring(1).split("/");
//    if (isUrlValidPost(urlParts) != 0) {
//      res.setContentType("text/plain");
//      res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid format: "+ urlPath + "Reason: " + isUrlValidPost(urlParts) );
//      res.getWriter().write("Please provide follow the correct format");
//    }  else {
//      res.setStatus(HttpServletResponse.SC_CREATED);
//      // do any sophisticated processing with urlParts which contains all the url params
//      DummyObject dummyObject = new DummyObject("Name");
//      String objJsonString = this.gson.toJson(dummyObject);
//
//      PrintWriter out = res.getWriter();
//      res.setContentType("application/json");
//      res.setCharacterEncoding("UTF-8");
//      out.print(objJsonString);
//      out.flush();
//    }
//  }
}
