import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rabbitmq.client.Channel;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.json.JSONArray;
import org.json.JSONObject;
import redis.clients.jedis.JedisPooled;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet(name = "SkierServlet", value = "/SkierServlet")
public class SkierServlet extends HttpServlet {
    private static final String SEASONS = "seasons";
    private static final String DAYS = "days";
    private static final String SKIERS = "skiers";
    private static final String VERTICAL = "vertical";
    private static final String SERVER_QUEUE = "server_queue";
    private ObjectPool<Channel> pool;
    private static final String EXCHANGE_NAME = "logs";
    private static final String EXCHANGE_TYPE = "fanout";
    private static final String TOTAL_VERTICAL = "TOTAL_VERTICAL";
    private static final String DATA_NOT_FOUND = "{\"message\": \"Data not found\"}";
    private static final String INVALID_INPUT = "{\"message\": \"Invalid inputs supplied\"}";
    private static final String INVALID_RESORT_ID = "{\"message\": \"Invalid Resort ID supplied\"}";
    private static final String INVALID_SEASON_ID = "{\"message\": \"Invalid Season ID supplied\"}";
    private static final String INVALID_DAY_ID = "{\"message\": \"Invalid Day ID supplied\"}";
    private static final String INVALID_SKIER_ID = "{\"message\": \"Invalid Skier ID supplied\"}";
    private static final int MIN_DAY_ID = 1;
    private static final int MAX_DAY_ID = 366;
    private static final int PORT_NUM = 6379;
    private JedisPooled jedisTotalVertical;
    private JedisPooled jedisTotalVerticalList;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            pool = new GenericObjectPool<>(new ChannelFactory());
            jedisTotalVertical = new JedisPooled(IPAddress.TOTAL_VERTICAL_IP, PORT_NUM);
            jedisTotalVerticalList = new JedisPooled(IPAddress.TOTAL_VERTICAL_LIST_IP, PORT_NUM);
        } catch (Exception e) {
            System.out.println("ERROR: Fail to initialize.");
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        String urlPath = req.getPathInfo();

        PrintWriter writer = res.getWriter();
        if (urlPath == null || urlPath.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            writer.write(DATA_NOT_FOUND);
            return;
        }

        // urlPath = "/a/b/c"
        // urlParts = ["", "a", "b", "c"]
        String[] urlParts = urlPath.split("/");
        int urlPartsSize = urlParts.length;

        // skiers GET: get the total vertical for the skier for the specified ski day
        //  0    1         2           3       4           5      6       7
        // ["", resortID, "seasons", seasonID, "days", dayID, "skiers", skierID]
        if (urlPartsSize == 8 &&
                urlParts[2].equalsIgnoreCase(SEASONS) &&
                urlParts[4].equalsIgnoreCase(DAYS) &&
                urlParts[6].equalsIgnoreCase(SKIERS)) {
            String resortIDString = urlParts[1];
            String seasonIDString = urlParts[3];
            String dayIDString = urlParts[5];
            String skierIDString = urlParts[7];
            int resortID = parseID(resortIDString, res, writer, INVALID_RESORT_ID);
            int seasonID = parseID(seasonIDString, res, writer, INVALID_SEASON_ID);
            int dayID = parseID(dayIDString, res, writer, INVALID_DAY_ID);
            int skierID = parseID(skierIDString, res, writer, INVALID_SKIER_ID);
            if (resortID == -1 || seasonID == -1 || dayID == -1 || skierID == -1) {
                // do nothing
            } else if (dayID < MIN_DAY_ID || dayID > MAX_DAY_ID) {
                invalidInput(res, writer, INVALID_DAY_ID);
            } else {
                try {
                    String field = resortIDString + "-" + seasonIDString + "-" + dayIDString + "-" + skierIDString;
                    String totalVerticalString = jedisTotalVertical.hget(TOTAL_VERTICAL, field);
                    int totalVertical = Integer.parseInt(totalVerticalString);
                    if (totalVertical > 0) {
                        res.setStatus(HttpServletResponse.SC_OK);
                        writer.write(totalVerticalString);
                    } else {
                        dataNotFound(res, writer);
                    }
                } catch (Exception e) {
                    dataNotFound(res, writer);
                }
            }
        }
        // skiers GET: get the total vertical for the skier the specified resort. If no season is specified, return all seasons
        //  0    1         2
        // ["", skierID, "vertical"]
        else if (urlPartsSize == 3 &&
                    urlParts[2].equalsIgnoreCase(VERTICAL)) {
            String skierIDString = urlParts[1];
            int skierID = parseID(skierIDString, res, writer, INVALID_SKIER_ID);
            if (skierID == -1) {
                // do nothing
            } else {
                try {
                    Map<String, String> recordMap = jedisTotalVerticalList.hgetAll(skierIDString);
                    JSONObject resultObject = new JSONObject();
                    JSONArray resultArray = new JSONArray();
                    if (recordMap == null || recordMap.isEmpty()) {
                        dataNotFound(res, writer);
                    } else {
                        for (String season : recordMap.keySet()) {
                            JSONObject item = new JSONObject();
                            int totalVert = Integer.parseInt(recordMap.get(season));
                            item.put("seasonID", season);
                            item.put("totalVert", totalVert);
                            resultArray.put(item);
                        }
                        resultObject.put("resorts", resultArray);
                        res.setStatus(HttpServletResponse.SC_OK);
                        writer.write(resultObject.toString());
                    }
                } catch (Exception e) {
                    dataNotFound(res, writer);
                }
            }
        } else {
            invalidInput(res, writer, INVALID_INPUT);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        String urlPath = req.getPathInfo();

        PrintWriter writer = res.getWriter();
        if (urlPath == null || urlPath.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            writer.write(DATA_NOT_FOUND);
            return;
        }
        // urlPath = "/a/b/c"
        // urlParts = ["", "a", "b", "c"]
        String[] urlParts = urlPath.substring(1).split("/");
//        System.out.println(Arrays.toString(urlParts));
        int urlPartsSize = urlParts.length;

        // skiers POST: write a new lift ride for the skier
        //      0        1         2         3       4       5         6
        // [resortID, "seasons", seasonID, "days", dayID, "skiers", skierID]
        if (urlPartsSize == 7 &&
                urlParts[1].equalsIgnoreCase(SEASONS) &&
                urlParts[3].equalsIgnoreCase(DAYS) &&
                urlParts[5].equalsIgnoreCase(SKIERS)) {
            String resortId = urlParts[0];
            String seasonID = urlParts[2];
            String dayID = urlParts[4];
            String skierID = urlParts[6];
            try {
                String postBodyStr = req.getReader().lines().collect(Collectors.joining());
                JsonObject postBodyJson = new JsonParser().parse(postBodyStr).getAsJsonObject();
                postBodyJson.addProperty("resortID", resortId);
                postBodyJson.addProperty("seasonID", seasonID);
                postBodyJson.addProperty("dayID", dayID);
                postBodyJson.addProperty("skierID", skierID);
                sendDataToQueue(postBodyJson);
                res.setStatus(HttpServletResponse.SC_CREATED);
                writer.write("Write successful");
            } catch (Exception e) {
                invalidInput(res, writer, INVALID_INPUT);
            }
        } else {
            dataNotFound(res, writer);
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

    private void dataNotFound(HttpServletResponse res, PrintWriter writer) {
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        writer.write(DATA_NOT_FOUND);
    }

    private void invalidInput(HttpServletResponse res, PrintWriter writer, String errorMessage) {
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        writer.write(errorMessage);
    }

    private boolean sendDataToQueue(JsonObject bodyJson) {
         try {
             Channel channel = pool.borrowObject();
             channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE);
//             channel.queueDeclare(SERVER_QUEUE, true, false, false, null);
             channel.basicPublish(EXCHANGE_NAME, SERVER_QUEUE, null, bodyJson.toString().getBytes(StandardCharsets.UTF_8));
             pool.returnObject(channel);
//             System.out.println(String.format(" [x] Sent '%s'", bodyJson));
             return true;
         } catch (Exception e) {
             System.out.println("ERROR: Fail to send data to queue.");
             e.printStackTrace();
             return false;
         }
    }
}
