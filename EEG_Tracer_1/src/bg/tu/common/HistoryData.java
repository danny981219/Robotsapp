package bg.tu.common;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryData {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    public static List<Integer> readHistory(Date date, String storage, String dataType, String suffix) {
        List<Integer> data = new ArrayList<>();
        String line = "";
        try {
            String fileName = "storage/" + storage + "/mes" + sdf.format(date) + suffix + ".txt";
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            int rowNum = 0;
            if (dataType == "Temperature") {
                rowNum = 1;
            } else if (dataType == "Humidity") {
                rowNum = 2;
            } else if (dataType == "Pump") {
                rowNum = 3;
            }
            if (rowNum > 0) {
                for (int r = 1; r <= rowNum; r++) {
                    line = in.readLine();
                    if (line == null) {
                        break;
                    }
                    if (r == rowNum) {
                        String[] values = line.split(",");
                        if (values.length > 0) {
                            for (String value : values) {
                                data.add(Integer.valueOf(value));
                            }
                        }
                    }
                }
            }
            in.close();
        } catch (NumberFormatException e) {
            System.out.println("NumberFormatException : " + dataType + " : " + line);
        } catch (IOException e) {
        }
        return data;
    }

    public static void writeHistory(Date date, List<Integer> temperature, List<Integer> humidity,
                                    List<Integer> pump, String storage, String suffix) {
        try {
            String fileName = "storage/" + storage + "/mes" + sdf.format(date) + suffix + ".txt";
            (new File(fileName)).delete();
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName, false));

            for (Integer val : temperature) {
                out.write(val.toString() + ",");
            }
            out.write("\n");
            for (Integer val : humidity) {
                out.write(val.toString() + ",");
            }
            out.write("\n");
            for (Integer val : pump) {
                out.write(val.toString() + ",");
            }
            out.write("\n");

            out.close();
        } catch (IOException e) {
            // todo
            System.out.println(e.getMessage());
        }
    }
}
