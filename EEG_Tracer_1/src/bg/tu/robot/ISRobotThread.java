package bg.tu.robot;

import bg.tu.common.HistoryData;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static bg.tu.common.Constants.*;

public class ISRobotThread extends Thread {

    private Socket socket;

    private Date systemDate = new Date();

    private int robot;

    private Map<String, Configuration> configurationMap;

    private Map<String, Measurement> measurements;

    private Integer sendTemperaturePosition;

    private Integer sendHumidityPosition;

    private Integer sendPumpPosition;

    public ISRobotThread(Socket socket, int robot) {
        this.socket = socket;
        this.robot = robot;
        readConfiguration();
    }

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            String text;
            do {
                text = reader.readLine();
                if ("Hello".equals(text)) {
                    writer.println("Hello");
                    log(text + " >> Hello");
                } else if (text != null && text.startsWith("Set Date: ")) {
                    setSystemDate(text.split(":")[1].trim());
                    startDataScan();
                    writer.println("Done");
                    log(text + " >> Done");
                } else if ("Get Configuration".equals(text)) {
                    writer.println(getConfigurations());
                    log(text + " >> Done");
                } else if (text != null && text.startsWith("Set Configuration: ")) {
                    setConfigurations(text.split(":")[1].trim());
                    writer.println("Done");
                    log(text + " >> Done");
                } else if (text != null && text.startsWith("Get Measurements: ")) {
                    // todo for date
                    writer.println(getMeasurements());
                    log(text + " >> Done");
                } else if ("Pump OFF".equals(text)) {
                    pumpOff();
                    writer.println("Done");
                    log(text + " >> Done");
                } else if ("Pump ON".equals(text)) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(systemDate);
                    pumpOn(pumpMaxTimeManual[cal.get(Calendar.MONTH)]);
                    writer.println("Done");
                    log(text + " >> Done");
                } else if ("Bye".equals(text)) {
                    stopDataScan();
                    writer.println("Bye");
                    log(text + " >> Bye");
                } else if (text != null) {
                    writer.println("Wrong command");
                }
            } while (text != null && ! text.equals("Bye"));

            socket.close();
        } catch (IOException ex) {
            System.out.println("Server exception (" + socket.getPort() + "): " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            saveConfiguration();
        }
    }

    private Timer timer = new Timer(50, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            timerEvent();
        }
    });

    private synchronized void startDataScan() {
        if (measurements != null && measurements.get("Temperature") != null &&
                ! measurements.get("Temperature").getData().isEmpty()) {
            saveData();
        }

        measurements = new HashMap<>();
        measurements.put("Temperature", new Measurement(systemDate));
        measurements.put("Humidity", new Measurement(systemDate));
        measurements.put("Pump", new Measurement(systemDate));
        sendTemperaturePosition = 0;
        sendHumidityPosition = 0;
        sendPumpPosition = 0;

        readData();

        if (! timer.isRunning()) {
            timer.start();
        }
    }

    private void stopDataScan() {
        saveData();
        timer.stop();
    }

    private int getRandomNumber(float min, float max) {
        return (int) ((Math.random() * (max - min + 1)) + min);
    }

    public synchronized void timerEvent() {
        if (measurements == null || measurements.get("Temperature") == null ||
                measurements.get("Humidity") == null || measurements.get("Pump") == null) {
            return;
        }
        int count = measurements.get("Temperature").getData().size();
        if (count >= 240) {
            stopDataScan();
            return;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(systemDate);

        float tempMin = temperatureMin[cal.get(Calendar.MONTH)][count / 10];
        float tempMax = temperatureMax[cal.get(Calendar.MONTH)][count / 10];
        float humMin = humidityMin[cal.get(Calendar.MONTH)][count / 10];
        float humMax = humidityMax[cal.get(Calendar.MONTH)][count / 10];
        float humMinLimit = configurationMap.get("Humidity").getLimits().get("Min");
        float humMaxLimit = configurationMap.get("Humidity").getLimits().get("Max");

        if (count == 0) {
            measurements.get("Temperature").addData(getRandomNumber(tempMin, tempMax));
            measurements.get("Humidity").addData(getRandomNumber(humMin, humMax));
        } else {
            float tempMinPrev = temperatureMin[cal.get(Calendar.MONTH)][(count - 1) / 10];
            float tempMaxPrev = temperatureMax[cal.get(Calendar.MONTH)][(count - 1) / 10];
            float tempCoeff = (tempMax - tempMin) / (tempMaxPrev - tempMinPrev);
            int prevTempValue = measurements.get("Temperature").getData().get(count - 1);
            int newTempValue = (int) tempMin + (int) (tempCoeff * (prevTempValue - tempMinPrev));
            //float tMinusDelta = count * 10 < 120 ? 1.2f : 1.2f;
            //float tPlusDelta = count * 10 < 120 ? 1.2f : 1.2f;
            newTempValue = Math.max(Math.min(getRandomNumber(newTempValue - 1.2f, newTempValue + 1.2f),
                    (int) tempMax), (int) tempMin);
            measurements.get("Temperature").addData(newTempValue);

            float humMinPrev = humidityMin[cal.get(Calendar.MONTH)][(count - 1) / 10];
            float humMaxPrev = humidityMax[cal.get(Calendar.MONTH)][(count - 1) / 10];
            float humCoeff = (humMax - humMin) / (humMaxPrev - humMinPrev);
            int prevHumValue = measurements.get("Humidity").getData().get(count - 1);
            int newHumValue = (int) humMin + (int) (humCoeff * (prevHumValue - humMinPrev));
            if (measurements.get("Pump").isPumpOn()) {
                newHumValue = Math.max(Math.min(getRandomNumber(newHumValue, newHumValue + 2.0f),
                        100), (int) humMin);
            } else {
                float hMinusDelta = count * 10 < 60 || count > 180 ? 1.0f : 1.2f;
                float hPlusDelta = count * 10 < 60 || count > 180 ? 1.0f : 0.8f;
                newHumValue = Math.max(Math.min(getRandomNumber(newHumValue - hMinusDelta, newHumValue + hPlusDelta),
                        (int) humMax), (int) humMin);
                if (prevHumValue - newHumValue > 0.05f * (humMaxLimit - humMinLimit)) {
                    newHumValue = prevHumValue - (int) (0.02f * (humMaxLimit - humMinLimit));
                }
            }
            measurements.get("Humidity").addData(newHumValue);
        }

        float averageTemperature = measurements.get("Temperature").getCurrentAvg();
        float averageHumidity = measurements.get("Humidity").getCurrentAvg();
        if ((count == 60 || count == 200) && ! measurements.get("Pump").isPumpOn() &&
                averageTemperature > configurationMap.get("Temperature").getThresholds().get("Min") &&
                // averageTemperature < configurationMap.get("Temperature").getThresholds().get("Max") &&
                averageHumidity < configurationMap.get("Humidity").getThresholds().get("Max")) {
            int timeOn = pumpMaxTimeAuto[cal.get(Calendar.MONTH)];
            timeOn *= ((averageTemperature - configurationMap.get("Temperature").getThresholds().get("Min")) /
                    (configurationMap.get("Temperature").getThresholds().get("Max") -
                            configurationMap.get("Temperature").getThresholds().get("Min"))) *
                    ((configurationMap.get("Humidity").getThresholds().get("Max") - averageHumidity) /
                            configurationMap.get("Humidity").getThresholds().get("Max"));
            if (averageHumidity < configurationMap.get("Humidity").getThresholds().get("Min")) {
                timeOn *= 1.2;
            }
            if (averageTemperature > configurationMap.get("Temperature").getThresholds().get("Max")) {
                timeOn *= 1.2;
            }
            pumpOn(timeOn);
        }

        setPump(getRandomNumber(9, 10));
    }

    public synchronized void pumpOff() {
        measurements.get("Pump").setStatus(Measurement.Status.OFF);
        measurements.get("Pump").setTimeBeforeOff(0);
    }

    public synchronized void pumpOn(int timeBeforeOff) {
        measurements.get("Pump").setStatus(Measurement.Status.ON);
        measurements.get("Pump").setTimeBeforeOff(timeBeforeOff);
    }

    public synchronized void setPump(int status) {
        boolean pumpIsOn = measurements.get("Pump").getStatus() == Measurement.Status.ON;
        measurements.get("Pump").getData().add(pumpIsOn ? status : 0);
        measurements.get("Pump").setTimeBeforeOff(measurements.get("Pump").getTimeBeforeOff() - 1);
        if (pumpIsOn) {
            if (measurements.get("Pump").getTimeBeforeOff() <= 0) {
                pumpOff();
            }
        } else {
            measurements.get("Pump").setTimeBeforeOff(0);
        }
    }

    void setSystemDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        try {
            systemDate = sdf.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void readConfiguration() {
        Properties config = new Properties();
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream stream = loader.getResourceAsStream("config/robot/config" + (robot + 1) + ".properties");
            if (stream != null) {
                config.load(stream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        configurationMap = new HashMap<>();
        configurationMap.put("Temperature", new Configuration());
        configurationMap.put("Humidity", new Configuration());

        configurationMap.get("Temperature").getThresholds().put("Min",
                Integer.valueOf((String) config.
                        getOrDefault("temperature.threshold.min", "10")));
        configurationMap.get("Temperature").getThresholds().put("Max",
                Integer.valueOf((String) config.
                        getOrDefault("temperature.threshold.max", "30")));
        configurationMap.get("Temperature").getLimits().put("Min",
                Integer.valueOf((String) config.
                        getOrDefault("temperature.limit.min", "-20")));
        configurationMap.get("Temperature").getLimits().put("Max",
                Integer.valueOf((String) config.
                        getOrDefault("temperature.limit.max", "40")));
        configurationMap.get("Temperature").
                setStep(Integer.valueOf((String) config.
                        getOrDefault("temperature.step", "20")));

        configurationMap.get("Humidity").getThresholds().put("Min",
                Integer.valueOf((String) config.getOrDefault("humidity.threshold.min", "50")));
        configurationMap.get("Humidity").getThresholds().put("Max",
                Integer.valueOf((String) config.getOrDefault("humidity.threshold.max", "80")));
        configurationMap.get("Humidity").getLimits().put("Min",
                Integer.valueOf((String) config.getOrDefault("humidity.limit.min", "0")));
        configurationMap.get("Humidity").getLimits().put("Max",
                Integer.valueOf((String) config.getOrDefault("humidity.limit.max", "100")));
        configurationMap.get("Humidity").
                setStep(Integer.valueOf((String) config.getOrDefault("humidity.step", "50")));
    }

    private void saveConfiguration() {
        try {
            Properties config = new Properties();
            config.setProperty("temperature.threshold.min",
                    configurationMap.get("Temperature").getThresholds().get("Min").toString());
            config.setProperty("temperature.threshold.max",
                    configurationMap.get("Temperature").getThresholds().get("Max").toString());
            config.setProperty("temperature.limit.min",
                    configurationMap.get("Temperature").getLimits().get("Min").toString());
            config.setProperty("temperature.limit.max",
                    configurationMap.get("Temperature").getLimits().get("Max").toString());
            config.setProperty("temperature.step",
                    configurationMap.get("Temperature").getStep().toString());

            config.setProperty("humidity.threshold.min",
                    configurationMap.get("Humidity").getThresholds().get("Min").toString());
            config.setProperty("humidity.threshold.max",
                    configurationMap.get("Humidity").getThresholds().get("Max").toString());
            config.setProperty("humidity.limit.min",
                    configurationMap.get("Humidity").getLimits().get("Min").toString());
            config.setProperty("humidity.limit.max",
                    configurationMap.get("Humidity").getLimits().get("Max").toString());
            config.setProperty("humidity.step",
                    configurationMap.get("Humidity").getStep().toString());

            config.store(new FileOutputStream("config/robot/config" + (robot + 1) + ".properties"), null);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private String getConfigurations() {
        StringBuilder configList = new StringBuilder();
        configList.append(configurationMap.get("Temperature").getThresholds().get("Min").toString() + ",");
        configList.append(configurationMap.get("Temperature").getThresholds().get("Max").toString() + ",");
        configList.append(configurationMap.get("Temperature").getLimits().get("Min").toString() + ",");
        configList.append(configurationMap.get("Temperature").getLimits().get("Max").toString() + ",");
        configList.append(configurationMap.get("Temperature").getStep().toString() + ",");
        configList.append(configurationMap.get("Humidity").getThresholds().get("Min").toString() + ",");
        configList.append(configurationMap.get("Humidity").getThresholds().get("Max").toString() + ",");
        configList.append(configurationMap.get("Humidity").getLimits().get("Min").toString() + ",");
        configList.append(configurationMap.get("Humidity").getLimits().get("Max").toString() + ",");
        configList.append(configurationMap.get("Humidity").getStep().toString());
        return configList.toString();
    }

    private void setConfigurations(String configuration) {
        if (configuration == null) {
            return;
        }
        String[] values = configuration.split(",");
        if (values.length == 10) {
            configurationMap.get("Temperature").getThresholds().put("Min", Integer.parseInt(values[0]));
            configurationMap.get("Temperature").getThresholds().put("Max", Integer.parseInt(values[1]));
            configurationMap.get("Temperature").getLimits().put("Min", Integer.parseInt(values[2]));
            configurationMap.get("Temperature").getLimits().put("Max", Integer.parseInt(values[3]));
            configurationMap.get("Temperature").setStep(Integer.parseInt(values[4]));

            configurationMap.get("Humidity").getThresholds().put("Min", Integer.parseInt(values[5]));
            configurationMap.get("Humidity").getThresholds().put("Max", Integer.parseInt(values[6]));
            configurationMap.get("Humidity").getLimits().put("Min", Integer.parseInt(values[7]));
            configurationMap.get("Humidity").getLimits().put("Max", Integer.parseInt(values[8]));
            configurationMap.get("Humidity").setStep(Integer.parseInt(values[9]));

            saveConfiguration();
        }
    }

    public synchronized void readData() {
        readData(systemDate, "Temperature");
        readData(systemDate, "Humidity");
        readData(systemDate, "Pump");
    }

    public void readData(Date date, String dataType) {
        measurements.get(dataType).setData(HistoryData.readHistory(date, "robot", dataType,
                "_" + (robot + 1)));
        if (measurements.get(dataType).getData().size() > 0) {
            int minValue = Integer.MAX_VALUE;
            int maxValue = Integer.MIN_VALUE;
            int sumValue = 0;
            for (int j = 0; j < measurements.get(dataType).getData().size(); j++) {
                minValue = Math.min(minValue, measurements.get(dataType).getData().get(j));
                maxValue = Math.max(maxValue, measurements.get(dataType).getData().get(j));
                sumValue += measurements.get(dataType).getData().get(j);
                measurements.get(dataType).setCurrentMin(minValue);
                measurements.get(dataType).setCurrentMax(maxValue);
                measurements.get(dataType).setCurrentSum(sumValue);
            }
        } else {
            measurements.get(dataType).setCurrentMin(Integer.MAX_VALUE);
            measurements.get(dataType).setCurrentMax(Integer.MIN_VALUE);
            measurements.get(dataType).setCurrentSum(0);
        }
        measurements.get(dataType).setDate(date);
    }

    public synchronized void saveData() {
        for (int i = 0; i < 4; i++) {
            HistoryData.writeHistory(measurements.get("Temperature").getDate(),
                    measurements.get("Temperature").getData(),
                    measurements.get("Humidity").getData(),
                    measurements.get("Pump").getData(), "robot", "_" + (robot + 1));
        }
    }

    private synchronized String getMeasurements() {
        if (measurements == null || measurements.get("Temperature") == null ||
                measurements.get("Humidity") == null || measurements.get("Pump") == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        StringBuilder data = new StringBuilder();
        data.append(sdf.format(measurements.get("Temperature").getDate()) + ";");

        int count = measurements.get("Temperature").getData().size();
        for (int i = sendTemperaturePosition; i < count; i++) {
            data.append(measurements.get("Temperature").getData().get(i).toString() + ",");
        }
        sendTemperaturePosition = count;
        data.append(";");

        count = measurements.get("Humidity").getData().size();
        for (int i = sendHumidityPosition; i < count; i++) {
            data.append(measurements.get("Humidity").getData().get(i).toString() + ",");
        }
        sendHumidityPosition = count;
        data.append(";");

        count = measurements.get("Pump").getData().size();
        for (int i = sendPumpPosition; i < count; i++) {
            data.append(measurements.get("Pump").getData().get(i).toString() + ",");
        }
        sendPumpPosition = count;

        log(data.toString());
        return data.toString();
    }

    private void log(String message) {
        System.out.println(message);
    }
}
