package part2;

import com.opencsv.CSVWriter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CSVProcessor {
    private static final String RESULT_FILE_PATH = "/Users/linni/Documents/CS6650/HW/hw2/hw2.csv";
    private static final String BONUS_FILE_PATH = "/Users/linni/Documents/CS6650/HW/hw1/bonus.csv";
    public List<String[]> records;
    public long start;

    public CSVProcessor(List<String[]> records, long start) {
        this.records = records;
        this.start = start;
    }
    public void generateCSV() {
        Map<Long, Long> latencyIntervalRecords = new TreeMap<>();
        Map<Long, Integer> countIntervalRecords = new TreeMap<>();
        try (
            Writer writer = Files.newBufferedWriter(Paths.get(RESULT_FILE_PATH));
            CSVWriter csvWriter = new CSVWriter(writer,
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
        ) {
            String[] header = {"start time", "request type", "latency", "response code"};
            csvWriter.writeNext(header);
            for (String[] record : records) {
                long croppedStart = Long.parseLong(record[0]) - start;
                long croppedStartInSecond = croppedStart / 1000;
                long latency = Long.parseLong(record[2]);
                record[0] = String.valueOf(croppedStart);
                if (!latencyIntervalRecords.containsKey(croppedStartInSecond)) {
                    latencyIntervalRecords.put(croppedStartInSecond, 0L);
                    countIntervalRecords.put(croppedStartInSecond, 0);
                }
                latencyIntervalRecords.put(croppedStartInSecond, latencyIntervalRecords.get(croppedStartInSecond) + latency);
                countIntervalRecords.put(croppedStartInSecond, countIntervalRecords.get(croppedStartInSecond) + 1);
                csvWriter.writeNext(record);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
            Writer writer = Files.newBufferedWriter(Paths.get(BONUS_FILE_PATH));
            CSVWriter csvWriter = new CSVWriter(writer,
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
        ) {
            String[] header = {"second interval", "request count", "mean response"};
            csvWriter.writeNext(header);
            for (Long secondInterval : latencyIntervalRecords.keySet()) {
                int count = countIntervalRecords.get(secondInterval);
                long mean = latencyIntervalRecords.get(secondInterval) / count;
                String[] record = {String.valueOf(secondInterval), String.valueOf(count), String.valueOf(mean)};
                csvWriter.writeNext(record);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
