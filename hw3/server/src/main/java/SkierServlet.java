import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rabbitmq.client.Channel;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@WebServlet(name = "SkierServlet", value = "/SkierServlet")
public class SkierServlet extends HttpServlet {
    private String RESORTS = "resorts";
    private String SEASONS = "seasons";
    private String DAY = "day";
    private String DAYS = "days";
    private String SKIERS = "skiers";
    private String VERTICAL = "vertical";
    private String STATISTICS = "statistics";
    private String SERVER_QUEUE = "server_queue";
    private ObjectPool<Channel> pool;
    private static final String EXCHANGE_NAME = "logs";
    private static final String EXCHANGE_TYPE = "fanout";

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            pool = new GenericObjectPool<>(new ChannelFactory());
        } catch (IOException e) {
            System.out.println("ERROR: Fail to initialize.");
            e.printStackTrace();
        } catch (TimeoutException e) {
            System.out.println("ERROR: Fail to initialize.");
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        String urlPath = req.getPathInfo();
        if (urlPath == null || urlPath.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("URL is null or empty");
            return;
        }
        // urlPath = "/a/b/c"
        // urlParts = ["", "a", "b", "c"]
        String[] urlParts = urlPath.split("/");
        int urlPartsSize = urlParts.length;

        // resorts GET: get a list of ski resorts in the database
        //  0    1
        // ["", "resorts"]
        if (urlPartsSize == 2 && urlParts[1].equalsIgnoreCase(RESORTS)) {
            res.setStatus(HttpServletResponse.SC_OK);
            res.getWriter().write("{\n" +
                    "  \"resorts\": [\n" +
                    "    {\n" +
                    "      \"resortName\": \"Creek Lake\",\n" +
                    "      \"resortID\": 0\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}");
        }
        // resorts GET: get number of unique skiers at resort/season/day
        //  0    1         2           3       4           5      6       7
        // ["", "resorts", resortID, "seasons", seasonID, "day", dayID, "skiers"]
        else if (urlPartsSize == 8 &&
                 urlParts[1].equalsIgnoreCase(RESORTS) &&
                 urlParts[3].equalsIgnoreCase(SEASONS) &&
                 urlParts[5].equalsIgnoreCase(DAY) &&
                 urlParts[7].equalsIgnoreCase(SKIERS)) {
            String resortId = urlParts[2];
            String seasonID = urlParts[4];
            String dayID = urlParts[6];
            res.setStatus(HttpServletResponse.SC_OK);
            res.getWriter().write("{\n" +
                    "  \"time\": \"Mission Ridge\",\n" +
                    "  \"numSkiers\": 78999\n" +
                    "}");
        }
        // resorts GET: get a list of seasons for the specified resort
        //  0    1         2           3
        // ["", "resorts", resortID, "seasons"]
        else if (urlPartsSize == 4 &&
                 urlParts[1].equalsIgnoreCase(RESORTS) &&
                 urlParts[3].equalsIgnoreCase(SEASONS)) {
            String resortId = urlParts[2];
            res.setStatus(HttpServletResponse.SC_OK);
            res.getWriter().write("{\n" +
                    "  \"seasons\": [\n" +
                    "    \"winter\"\n" +
                    "  ]\n" +
                    "}");
        }
        // skiers GET: get the total vertical for the skier for the specified ski day
        //  0    1         2           3       4           5      6       7        8
        // ["", "skiers", resortID, "seasons", seasonID, "days", dayID, "skiers", skierID]
        else if (urlPartsSize == 9 &&
                 urlParts[1].equalsIgnoreCase(SKIERS) &&
                 urlParts[3].equalsIgnoreCase(SEASONS) &&
                 urlParts[5].equalsIgnoreCase(DAYS) &&
                 urlParts[7].equalsIgnoreCase(SKIERS)) {
            String resortId = urlParts[2];
            String seasonID = urlParts[4];
            String dayID = urlParts[6];
            String skierID = urlParts[8];
            res.setStatus(HttpServletResponse.SC_OK);
            res.getWriter().write("34507");
        }
        // skiers GET: get the total vertical for the skier the specified resort. If no season is specified, return all seasons
        //  0    1         2           3
        // ["", "skiers", skierID, "vertical"]
        else if (urlPartsSize == 4 && urlParts[1].equalsIgnoreCase(SKIERS) && urlParts[3].equalsIgnoreCase(VERTICAL)) {
            String skierID = urlParts[2];
            res.setStatus(HttpServletResponse.SC_OK);
            res.getWriter().write("{\n" +
                    "  \"resorts\": [\n" +
                    "    {\n" +
                    "      \"seasonID\": \"Creek Lake\",\n" +
                    "      \"totalVert\": 100\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}");
        }
        // skiers GET: get the API performance stats
        //  0    1
        // ["", "statistics"]
        else if (urlPartsSize == 2 && urlParts[1].equalsIgnoreCase(STATISTICS)) {
            res.setStatus(HttpServletResponse.SC_OK);
            res.getWriter().write("{\n" +
                    "  \"endpointStats\": [\n" +
                    "    {\n" +
                    "      \"URL\": \"/resorts\",\n" +
                    "      \"operation\": \"GET\",\n" +
                    "      \"mean\": 11,\n" +
                    "      \"max\": 198\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}");
        } else {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("URL format or parameters are invalid");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        String urlPath = req.getPathInfo();
        if (urlPath == null || urlPath.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("URL is null or empty");
            return;
        }
        // urlPath = "/a/b/c"
        // urlParts = ["", "a", "b", "c"]
        String[] urlParts = urlPath.split("/");
        int urlPartsSize = urlParts.length;

        // resorts POST: Add a new season for a resort
        //  0    1          2          3
        // ["", "resorts", resortID, "seasons"]
        if (urlPartsSize == 4 && urlParts[1].equalsIgnoreCase(RESORTS) && urlParts[3].equalsIgnoreCase(SEASONS)) {
            String resortId = urlParts[2];
            try {
                String postBodyStr = req.getReader().lines().collect(Collectors.joining());
                JsonObject postBodyJson = new JsonParser().parse(postBodyStr).getAsJsonObject();
                postBodyJson.addProperty("resortId", resortId);
                postBodyJson.addProperty("type", "resorts");
                sendDataToQueue(postBodyJson);
                res.setStatus(HttpServletResponse.SC_CREATED);
                res.getWriter().write("new season created");
            } catch (Exception e) {
                e.printStackTrace();
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().write("POST body is invalid");
            }
        }
        // skiers POST: write a new lift ride for the skier
        //  0    1         2           3       4           5      6       7        8
        // ["", "skiers", resortID, "seasons", seasonID, "days", dayID, "skiers", skierID]
        else if (urlPartsSize == 9 &&
                 urlParts[1].equalsIgnoreCase(SKIERS) &&
                 urlParts[3].equalsIgnoreCase(SEASONS) &&
                 urlParts[5].equalsIgnoreCase(DAYS) &&
                 urlParts[7].equalsIgnoreCase(SKIERS)) {
            String resortId = urlParts[2];
            String seasonID = urlParts[4];
            String dayID = urlParts[6];
            String skierID = urlParts[8];
            try {
                String postBodyStr = req.getReader().lines().collect(Collectors.joining());
                JsonObject postBodyJson = new JsonParser().parse(postBodyStr).getAsJsonObject();
                postBodyJson.addProperty("resortId", resortId);
                postBodyJson.addProperty("seasonID", seasonID);
                postBodyJson.addProperty("dayID", dayID);
                postBodyJson.addProperty("skierID", skierID);
                postBodyJson.addProperty("type", "lift_ride");
                sendDataToQueue(postBodyJson);
                res.setStatus(HttpServletResponse.SC_CREATED);
                res.getWriter().write("Write successful");
            } catch (Exception e) {
                e.printStackTrace();
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().write("POST body is invalid");
            }
        } else {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("URL format or parameters are invalid");
        }
    }

    private boolean sendDataToQueue(JsonObject bodyJson) {
         try {
             Channel channel = pool.borrowObject();
             channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE);
             channel.queueDeclare(SERVER_QUEUE, true, false, false, null);
             channel.basicPublish(EXCHANGE_NAME, SERVER_QUEUE, null, bodyJson.toString().getBytes(StandardCharsets.UTF_8));
             pool.returnObject(channel);
             System.out.println(String.format(" [x] Sent '%s'", bodyJson));
             return true;
         } catch (IOException e) {
             System.out.println("ERROR: Fail to send data to queue.");
             e.printStackTrace();
             return false;
         } catch (Exception e) {
             System.out.println("ERROR: Fail to send data to queue.");
             e.printStackTrace();
             return false;
         }
    }
}
