package bg.tu.monitor;

import bg.tu.common.HistoryData;

import javax.swing.*;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PanelRobot extends JPanel {

    private static String HOSTNAME = "127.0.0.1";

    private static int BASE_PORT = 6000;

    private Stroke solid = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
    private Stroke solid2 = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
    private Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND,
            0, new float[]{1,3}, 0);

    private boolean visibleRobots[] = {true, true, true, true};

    private Map<String, Measurement>[] graphs = new HashMap[4];

    private String[] robotNames = {"Robot 1", "Robot 2", "Robot 3", "Robot 4"};

    private MonitorThread[] monitorThreads = {null, null, null, null};

    Date systemDate;

    int maxRobots;

    public PanelRobot(Date systemDate, int maxRobots, Map<Integer,
                        MonitorIrrigate.RobotConfiguration> robotConfiguration) {
        this.systemDate = systemDate;
        this.maxRobots = maxRobots;
        for (int r = 0; r < maxRobots; r++) {
            robotNames[r] = robotConfiguration.get(r+1).getName();
        }
        readConfiguration();

        readData(systemDate);
        for (int i = 0; i < maxRobots; i++) {
            if (graphs[i].get("Pump").getData().size() > 0 &&
                    graphs[i].get("Pump").getData().get(graphs[i].get("Pump").getData().size() - 1) != 0) {
                graphs[i].get("Pump").setStatus(Measurement.Status.ON);
            }
        }
    }

    public String getRoobotName(int robot) {
        return robotNames[robot];
    }

    public boolean isRobotVisible(int robot) {
        if (robot == -1) {
            return visibleRobotsCount() == maxRobots;
        }
        return visibleRobots[robot];
    }

    public void setRobotVisible(int robot) {
        for (int i = 0; i < maxRobots; i++) {
            this.visibleRobots[i] = robot == -1;
        }
        if (robot == -1) {
            return;
        }
        this.visibleRobots[robot] = true;
    }

    public Integer getTemperatureLimit(String name, int robot) {
        return graphs[robot].get("Temperature").getLimits().get(name);
    }

    public Integer getTemperatureStep(int robot) {
        return graphs[robot].get("Temperature").getStep();
    }

    public int getTemperaturesCount(int robot) {
        return graphs[robot].get("Temperature").getData().size();
    }

    public int getTemperature(int index, int robot) {
        return graphs[robot].get("Temperature").getData().get(index);
    }

    public void setTemperature(int newData, int robot) {
        graphs[robot].get("Temperature").getData().add(newData);
        graphs[robot].get("Temperature").setCurrentSum(graphs[robot].get("Temperature").getCurrentSum() + newData);
        graphs[robot].get("Temperature").setCurrentMin(Math.min(graphs[robot].get("Temperature").getCurrentMin(), newData));
        graphs[robot].get("Temperature").setCurrentMax(Math.max(graphs[robot].get("Temperature").getCurrentMax(), newData));
    }

    public Integer getAverageTemperature(int robot) {
        return graphs[robot].get("Temperature").getCurrentAvg();
    }

    public Integer getMinTemperature(int robot) {
        return graphs[robot].get("Temperature").getCurrentMin();
    }

    public Integer getMaxTemperature(int robot) {
        return graphs[robot].get("Temperature").getCurrentMax();
    }

    public Integer getTemperatureThreshold(String name, int robot) {
        return graphs[robot].get("Temperature").getThresholds().get(name);
    }

    public void setTemperatureThreshold(String name, int newData, int robot) {
        graphs[robot].get("Temperature").getThresholds().put(name, newData);
    }

    public Integer getHumidityLimit(String name, int robot) {
        return graphs[robot].get("Humidity").getLimits().get(name);
    }

    public Integer getHumidityStep(int robot) {
        return graphs[robot].get("Humidity").getStep();
    }

    public int getHumidity(int index, int robot) {
        return graphs[robot].get("Humidity").getData().get(index);
    }

    public void setHumidity(int newData, int robot) {
        graphs[robot].get("Humidity").getData().add(newData);
        graphs[robot].get("Humidity").setCurrentSum(graphs[robot].get("Humidity").getCurrentSum() + newData);
        graphs[robot].get("Humidity").setCurrentMin(Math.min(graphs[robot].get("Humidity").getCurrentMin(), newData));
        graphs[robot].get("Humidity").setCurrentMax(Math.max(graphs[robot].get("Humidity").getCurrentMax(), newData));
    }

    public Integer getAverageHumidity(int robot) {
        return graphs[robot].get("Humidity").getData().size() > 0 ?
                graphs[robot].get("Humidity").getCurrentSum() / graphs[robot].get("Humidity").getData().size() : 0;
    }

    public Integer getHumidityThreshold(String name, int robot) {
        return graphs[robot].get("Humidity").getThresholds().get(name);
    }

    public void setHumidityThreshold(String name, int newData, int robot) {
        graphs[robot].get("Humidity").getThresholds().put(name, newData);
    }

    public void togglePump(int robot, int timeBeforeOff) {
        if (graphs[robot].get("Pump").getStatus() == Measurement.Status.ON) {
            graphs[robot].get("Pump").setStatus(Measurement.Status.OFF);
        } else {
            graphs[robot].get("Pump").setStatus(Measurement.Status.ON);
        }
        if (graphs[robot].get("Pump").getStatus() == Measurement.Status.ON) {
            graphs[robot].get("Pump").setTimeBeforeOff(timeBeforeOff);
        } else {
            graphs[robot].get("Pump").setTimeBeforeOff(0);
        }
    }

    public boolean isPumpOn(int robot) {
        return graphs[robot].get("Pump").getStatus() == Measurement.Status.ON;
    }

    public void setPump(int newData, int robot) {
        boolean pumpIsOn = graphs[robot].get("Pump").getStatus() == Measurement.Status.ON;
        graphs[robot].get("Pump").getData().add(pumpIsOn ? newData : 0);
        graphs[robot].get("Pump").setTimeBeforeOff(graphs[robot].get("Pump").getTimeBeforeOff() - 1);
        if (pumpIsOn) {
            if (graphs[robot].get("Pump").getTimeBeforeOff() <= 0) {
                graphs[robot].get("Pump").setStatus(Measurement.Status.OFF);
                graphs[robot].get("Pump").setTimeBeforeOff(0);
            }
        } else {
            graphs[robot].get("Pump").setTimeBeforeOff(0);
        }
    }

    public int visibleRobotsCount() {
        int count = 0;
        for (int i = 0; i < maxRobots; i++) {
            if (isRobotVisible(i)) {
                count++;
            }
        }
        return  count;
    }

    public int getFirstVisibleRobot() {
        for (int i = 0; i < maxRobots; i++) {
            if (isRobotVisible(i)) {
                return i;
            }
        }
        return -1;
    }

    private void doDrawing(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        Dimension size = getSize();
        Insets insets = getInsets();

        int w = size.width - insets.left - insets.right;
        int h = size.height - insets.top - insets.bottom;

        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, w, h);

        int count = visibleRobotsCount();
        if (count == 1) {
            drawGraphic(g2d, graphs[getFirstVisibleRobot()].get("Temperature"), 160, 10, 2, 2, true);
            drawGraphic(g2d, graphs[getFirstVisibleRobot()].get("Humidity"), 160, 250, 2, 2, false);
            drawGraphic(g2d, graphs[getFirstVisibleRobot()].get("Pump"), 160, 500, 2, 1, true);
        } else if (count > 1) {
            for (int i = 0; i < maxRobots; i++) {
                if (isRobotVisible(i)) {
                    drawGraphic(g2d, graphs[i].get("Temperature"), 20 + 600 * (i % 2), 10 + 320 * (i / 2), 1, 1, true);
                    drawGraphic(g2d, graphs[i].get("Humidity"), 20 + 600 * (i % 2), 160 + 320 * (i / 2), 1, 1, false);
                }
            }
        }
    }

    private void drawGraphic(Graphics2D g2d, Measurement data, int gx0, int gy0, int xScale, int yScale,
                             boolean colorFactor) {

        Color minColor = colorFactor ? Color.GREEN: Color.RED;
        Color maxColor = colorFactor ? Color.RED: Color.GREEN;

        int x1 = 40, xStep = 20 * xScale;
        int y1 = 20, y2 = 100 * yScale;

        Font font = new Font("Arial", Font.PLAIN, 12);

        g2d.setFont(font);
        g2d.setColor(Color.lightGray);
        g2d.setStroke(dashed);

        // vertical grid
        for (int j = 2; j <= 24; j += 2) {
            g2d.drawLine(x1 + xStep * j + gx0, y1 + gy0, x1 + xStep * j + gx0, y2 + gy0);
            g2d.drawString(Integer.toString(j), x1 - 5 + xStep * j + gx0, y2 + 20 + gy0);
        }

        // horizontal grid
        int delta = (y2 - y1) / ((data.getLimits().get("Max") - data.getLimits().get("Min")) / data.getStep());
        for (int j = data.getLimits().get("Min"), y = 0; j <= data.getLimits().get("Max"); j += data.getStep(), y += delta) {
            g2d.drawString(Integer.toString( j), x1 - 30 + gx0, y2 + 5 - y + gy0);
        }

        // thresholds
        g2d.setStroke(dashed);
        for (Map.Entry<String, Integer> threshold : data.getThresholds().entrySet()) {
            int pos = (int) ((y2 - y1) * (data.getLimits().get("Max") - (float) threshold.getValue()) /
                                         (data.getLimits().get("Max") - data.getLimits().get("Min")));
            g2d.setColor(threshold.getKey().equals("Max") ? maxColor :  minColor);
            g2d.drawLine(x1 + gx0, y1 + pos + gy0, 24 * xStep + 60 + gx0, y1 + pos + gy0);
        }

        String currentValue = data.getData().size() > 0 ?
                Integer.toString(data.getData().get(data.getData().size() - 1)) : "";
        g2d.setColor(Color.GREEN);
        g2d.drawString(data.getName() + ": " + currentValue, x1 - 5 + gx0, y1 - 10 + gy0);
        if (!data.getCurrentMin().equals(Integer.MAX_VALUE)) {
            g2d.drawString("Min: " + data.getCurrentMin(), x1 - 5 + 80 + gx0, y1 - 10 + gy0);
        }
        if (!data.getCurrentMax().equals(Integer.MIN_VALUE)) {
            g2d.drawString("Max: " + data.getCurrentMax(), x1 - 5 + 160 + gx0, y1 - 10 + gy0);
        }
        g2d.drawString("Avg: " + data.getCurrentAvg(), x1 - 5 + 240 + gx0, y1 - 10 + gy0);

        g2d.setStroke(solid2);
        g2d.setColor(Color.white);

        g2d.drawLine(x1 + gx0, y1 + gy0, x1 + gx0, y2 + gy0);
        g2d.drawLine(x1 + gx0, y2 + gy0, 24 * xStep + 60 + gx0, y2 + gy0);

        int x_axis = x1 + 5;

        if (data.getData().size() == 0) {
            return;
        }

        Integer minThreshold = data.getThresholds().get("Min");
        if (minThreshold == null) {
            minThreshold = Integer.MIN_VALUE;
        }
        Integer maxThreshold = data.getThresholds().get("Max");
        if (maxThreshold == null) {
            maxThreshold = Integer.MAX_VALUE;
        }
        for(int j = data.getData().size() - 1; j > 0; j--){
            g2d.setStroke(solid);
            int avg = (data.getData().get(data.getData().size() - j - 1) + data.getData().get(data.getData().size() - j)) / 2;
            if (minThreshold == Integer.MAX_VALUE && maxThreshold == Integer.MAX_VALUE) {
                g2d.setColor(Color.CYAN);
            } else {
                g2d.setColor(avg < minThreshold ? minColor : (avg < maxThreshold ? Color.YELLOW : maxColor));
            }
            int pos1 = (int) ((y2 - y1) * (data.getLimits().get("Max") - (float) data.getData().get(data.getData().size() - j - 1)) /
                    (data.getLimits().get("Max") - data.getLimits().get("Min")));
            int pos2 = (int) ((y2 - y1) * (data.getLimits().get("Max") - (float) data.getData().get(data.getData().size() - j)) /
                    (data.getLimits().get("Max") - data.getLimits().get("Min")));
            g2d.drawLine(x_axis - xStep / 10 + gx0, y1 + pos1 + gy0, x_axis + gx0, y1 + pos2 + gy0);

            x_axis += xStep / 10;
        }
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        doDrawing(g);
    }

    public void saveConfiguration() {
        try {
            for (int i = 0; i < maxRobots ;i++) {
                Properties config = new Properties();
                config.setProperty("temperature.threshold.min",
                        getTemperatureThreshold("Min", i).toString());
                config.setProperty("temperature.threshold.max",
                        getTemperatureThreshold("Max", i).toString());
                config.setProperty("temperature.limit.min",
                        getTemperatureLimit("Min", i).toString());
                config.setProperty("temperature.limit.max",
                        getTemperatureLimit("Max", i).toString());
                config.setProperty("temperature.step",
                        getTemperatureStep(i).toString());

                config.setProperty("humidity.threshold.min",
                        getHumidityThreshold("Min", i).toString());
                config.setProperty("humidity.threshold.max",
                        getHumidityThreshold("Max", i).toString());
                config.setProperty("humidity.limit.min",
                        getHumidityLimit("Min", i).toString());
                config.setProperty("humidity.limit.max",
                        getHumidityLimit("Max", i).toString());
                config.setProperty("humidity.step",
                        getHumidityStep(i).toString());

                config.store(new FileOutputStream("config/monitor/config" + (i + 1) + ".properties"), null);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void saveData() {
        for (int i = 0; i < maxRobots; i++) {
            HistoryData.writeHistory(graphs[i].get("Temperature").getDate(),
                    graphs[i].get("Temperature").getData(),
                    graphs[i].get("Humidity").getData(),
                    graphs[i].get("Pump").getData(), "monitor", "_" + (i + 1));
        }
    }

    public void readConfiguration() {
        for (int i = 0; i < maxRobots; i++) {
            Properties config = new Properties();
            try {
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                InputStream stream = loader.getResourceAsStream("config/monitor/config" + (i + 1) + ".properties");
                if (stream != null) {
                    config.load(stream);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            graphs[i] = new HashMap<>();
            graphs[i].put("Temperature", new Measurement("Temp", systemDate));
            graphs[i].put("Humidity", new Measurement("Hum", systemDate));
            graphs[i].put("Pump", new Measurement("Pump", systemDate));

            graphs[i].get("Temperature").getThresholds().put("Min",
                    Integer.valueOf((String) config.
                            getOrDefault("temperature.threshold.min", "10")));
            graphs[i].get("Temperature").getThresholds().put("Max",
                    Integer.valueOf((String) config.
                            getOrDefault("temperature.threshold.max", "30")));
            graphs[i].get("Temperature").getLimits().put("Min",
                    Integer.valueOf((String) config.
                            getOrDefault("temperature.limit.min", "-20")));
            graphs[i].get("Temperature").getLimits().put("Max",
                    Integer.valueOf((String) config.
                            getOrDefault("temperature.limit.max", "40")));
            graphs[i].get("Temperature").
                    setStep(Integer.valueOf((String) config.
                            getOrDefault("temperature.step", "20")));

            graphs[i].get("Humidity").getThresholds().put("Min",
                    Integer.valueOf((String) config.
                            getOrDefault("humidity.threshold.min", "50")));
            graphs[i].get("Humidity").getThresholds().put("Max",
                    Integer.valueOf((String) config.
                            getOrDefault("humidity.threshold.max", "80")));
            graphs[i].get("Humidity").getLimits().put("Min",
                    Integer.valueOf((String) config.
                            getOrDefault("humidity.limit.min", "0")));
            graphs[i].get("Humidity").getLimits().put("Max",
                    Integer.valueOf((String) config.
                            getOrDefault("humidity.limit.max", "100")));
            graphs[i].get("Humidity").
                    setStep(Integer.valueOf((String) config.
                            getOrDefault("humidity.step", "50")));

            graphs[i].get("Pump").getThresholds().put("Min", Integer.MAX_VALUE);
            graphs[i].get("Pump").getThresholds().put("Max", Integer.MAX_VALUE);
            graphs[i].get("Pump").getLimits().put("Min", 0);
            graphs[i].get("Pump").getLimits().put("Max", 10);
            graphs[i].get("Pump").setStep(10);
        }
    }

    public void initData(Date date) {
        systemDate = date;
        initData(systemDate, "Temperature");
        initData(systemDate, "Humidity");
        initData(systemDate, "Pump");
    }

    public void initData(Date date, String dataType) {
        for (int i = 0; i < maxRobots; i++) {
            graphs[i].get(dataType).getData().clear();
            graphs[i].get(dataType).setCurrentMin(Integer.MAX_VALUE);
            graphs[i].get(dataType).setCurrentMax(Integer.MIN_VALUE);
            graphs[i].get(dataType).setCurrentSum(0);
            graphs[i].get(dataType).setDate(date);
        }
    }

    public void readData(Date date) {
        systemDate = date;
        readData(systemDate, "Temperature");
        readData(systemDate, "Humidity");
        readData(systemDate, "Pump");
    }

    public void readData(Date date, String dataType) {
        for (int i = 0; i < maxRobots; i++) {
            graphs[i].get(dataType).setData(HistoryData.readHistory(date, "monitor", dataType, "_" + (i + 1)));
            if (graphs[i].get(dataType).getData().size() > 0) {
                int minValue = Integer.MAX_VALUE;
                int maxValue = Integer.MIN_VALUE;
                int sumValue = 0;
                for (int j = 0; j < graphs[i].get(dataType).getData().size(); j++) {
                    minValue = Math.min(minValue, graphs[i].get(dataType).getData().get(j));
                    maxValue = Math.max(maxValue, graphs[i].get(dataType).getData().get(j));
                    sumValue += graphs[i].get(dataType).getData().get(j);
                    graphs[i].get(dataType).setCurrentMin(minValue);
                    graphs[i].get(dataType).setCurrentMax(maxValue);
                    graphs[i].get(dataType).setCurrentSum(sumValue);
                }
            } else {
                graphs[i].get(dataType).setCurrentMin(Integer.MAX_VALUE);
                graphs[i].get(dataType).setCurrentMax(Integer.MIN_VALUE);
                graphs[i].get(dataType).setCurrentSum(0);
            }
            graphs[i].get(dataType).setDate(date);
        }
    }

    public void connect(int robot) {
        monitorThreads[robot] = new MonitorThread(HOSTNAME, BASE_PORT + robot, graphs[robot], systemDate);
        monitorThreads[robot].start();
    }

    public void sendDate() {
        for (int i = 0; i < maxRobots; i++) {
            if (monitorThreads[i] != null) {
                monitorThreads[i].sendDate(systemDate);
            }
        }
    }

    public void sendConfiguration(int robot) {
        if (monitorThreads[robot] != null) {
            monitorThreads[robot].sendConfiguration();
        }
    }

    public void pumpOff(int robot) {
        if (monitorThreads[robot] != null) {
            monitorThreads[robot].pumpOff();
        }
    }

    public void pumpOn(int robot) {
        if (monitorThreads[robot] != null) {
            monitorThreads[robot].pumpOn();
        }
    }

    public void disconnect(int robot) {
        if (monitorThreads[robot] != null) {
            monitorThreads[robot].exit();
            while (monitorThreads[robot].isAlive());
        }
        monitorThreads[robot] = null;
    }

    public boolean comunicationIsRunning(int robot) {
        /*if (monitorThreads[robot] != null && ! monitorThreads[robot].isAlive()) {
            monitorThreads[robot] = null;
        }*/
        return monitorThreads[robot] != null && monitorThreads[robot].isConnected();
    }
}
