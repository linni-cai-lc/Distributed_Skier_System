package part2;

import com.opencsv.CSVWriter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CSVProcessor {
    private static final String FILE_PATH = "/Users/linni/Documents/CS6650/HW/hw1/hw1.csv";
    public List<String[]> records;
    public long start;

    public CSVProcessor(List<String[]> records, long start) {
        this.records = records;
        this.start = start;
    }
    public void generateCSV() {
        try (
            Writer writer = Files.newBufferedWriter(Paths.get(FILE_PATH));
            CSVWriter csvWriter = new CSVWriter(writer,
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
        ) {
            String[] header = {"start time", "request type", "latency", "response code"};
            csvWriter.writeNext(header);
            for (String[] record : records) {
                record[0] = String.valueOf(Long.parseLong(record[0]) - start);
                csvWriter.writeNext(record);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
