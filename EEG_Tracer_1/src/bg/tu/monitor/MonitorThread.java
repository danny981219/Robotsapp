package bg.tu.monitor;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MonitorThread extends Thread {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    private String hostname;

    private int port;

    private Map<String, Measurement> robotData;

    private Date systemDate;

    private boolean sendDate = false;

    private boolean sendConfiguration = false;

    private boolean scanData = false;

    private boolean pumpOff = false;

    private boolean pumpOn = false;

    private boolean exit = false;

    private boolean connected = false;

    public MonitorThread(String hostname, int port, Map<String, Measurement> robotData, Date systemDate) {
        this.systemDate = systemDate;
        this.hostname = hostname;
        this.port = port;
        this.robotData = robotData;
    }

    public void run() {
        try {
            Socket socket = new Socket(hostname, port);

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            String response;

            writer.println("Hello");
            response = reader.readLine();
            log("Hello >> " + response);
            connected = true;

            writer.println("Set Date: " + sdf.format(systemDate));
            response = reader.readLine();
            log("Set Date: " + sdf.format(systemDate) + " >> " + response);

            writer.println("Get Configuration");
            response = reader.readLine();
            log("Get Configuration >> " + response);
            refreshConfigurations(response);

            startDataScan();

            do {
                if (sendDate) {
                    writer.println("Set Date: " + sdf.format(systemDate));
                    response = reader.readLine();
                    sendDate = false;
                    log("Set Date: " + sdf.format(systemDate) + " >> " + response);
                } else if (sendConfiguration) {
                    writer.println("Set Configuration: " + getConfigurations());
                    response = reader.readLine();
                    sendConfiguration = false;
                    log("Set Configuration: " + getConfigurations() + " >> " + response);
                } else if (scanData) {
                    writer.println("Get Measurements: " + sdf.format(systemDate));
                    response = reader.readLine();
                    refreshData(response);
                    scanData = false;
                    log("Get Measurements: " + sdf.format(systemDate)); // + " >> " + response);
                } else if (pumpOff) {
                    writer.println("Pump OFF");
                    response = reader.readLine();
                    pumpOff = false;
                    log("Pump OFF >> " + response);
                } else if (pumpOn) {
                    writer.println("Pump ON");
                    response = reader.readLine();
                    pumpOn = false;
                    log("Pump ON >> " + response);
                } else if (exit) {
                    writer.println("Bye");
                    response = reader.readLine();
                    log("Bye >> " + response);
                } else {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            } while (! response.equals("Bye"));

            socket.close();
        } catch (UnknownHostException ex) {
            log("Client: server not found: " + ex.getMessage());
        } catch (IOException ex) {
            log("Client: I/O error: " + ex.getMessage());
        }
    }

    private Timer timer = new Timer(50, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            scanData();
        }
    });

    private void startDataScan() {
        if (! timer.isRunning()) {
            timer.start();
        }
    }

    private void stopDataScan() {
        timer.stop();
    }

    private String getConfigurations() {
        StringBuilder configList = new StringBuilder();
        configList.append(robotData.get("Temperature").getThresholds().get("Min").toString() + ",");
        configList.append(robotData.get("Temperature").getThresholds().get("Max").toString() + ",");
        configList.append(robotData.get("Temperature").getLimits().get("Min").toString() + ",");
        configList.append(robotData.get("Temperature").getLimits().get("Max").toString() + ",");
        configList.append(robotData.get("Temperature").getStep().toString() + ",");
        configList.append(robotData.get("Humidity").getThresholds().get("Min").toString() + ",");
        configList.append(robotData.get("Humidity").getThresholds().get("Max").toString() + ",");
        configList.append(robotData.get("Humidity").getLimits().get("Min").toString() + ",");
        configList.append(robotData.get("Humidity").getLimits().get("Max").toString() + ",");
        configList.append(robotData.get("Humidity").getStep().toString());
        return configList.toString();
    }

    private void refreshConfigurations(String configuration) {
        String[] values = configuration.split(",");
        if (values.length == 10) {
            robotData.get("Temperature").getThresholds().put("Min", Integer.parseInt(values[0]));
            robotData.get("Temperature").getThresholds().put("Max", Integer.parseInt(values[1]));
            robotData.get("Temperature").getLimits().put("Min", Integer.parseInt(values[2]));
            robotData.get("Temperature").getLimits().put("Max", Integer.parseInt(values[3]));
            robotData.get("Temperature").setStep(Integer.parseInt(values[4]));

            robotData.get("Humidity").getThresholds().put("Min", Integer.parseInt(values[5]));
            robotData.get("Humidity").getThresholds().put("Max", Integer.parseInt(values[6]));
            robotData.get("Humidity").getLimits().put("Min", Integer.parseInt(values[7]));
            robotData.get("Humidity").getLimits().put("Max", Integer.parseInt(values[8]));
            robotData.get("Humidity").setStep(Integer.parseInt(values[9]));
        }
    }

    private void refreshData(String data) { // todo for date
        String[] values = data.split(";");
        if (values.length == 4) {
            String[] temperature = values[1].split(",");
            for (int i = 0; i < temperature.length; i++) {
                Integer intValue = Integer.parseInt(temperature[i]);
                Measurement measurement = robotData.get("Temperature");
                measurement.getData().add(intValue);
                measurement.setCurrentSum(measurement.getCurrentSum() + intValue);
                measurement.setCurrentMin(Math.min(measurement.getCurrentMin(), intValue));
                measurement.setCurrentMax(Math.max(measurement.getCurrentMax(), intValue));
            }
            String[] humidity = values[2].split(",");
            for (int i = 0; i < humidity.length; i++) {
                Integer intValue = Integer.parseInt(humidity[i]);
                Measurement measurement = robotData.get("Humidity");
                measurement.getData().add(intValue);
                measurement.setCurrentSum(measurement.getCurrentSum() + intValue);
                measurement.setCurrentMin(Math.min(measurement.getCurrentMin(), intValue));
                measurement.setCurrentMax(Math.max(measurement.getCurrentMax(), intValue));
            }
            String[] pump = values[3].split(",");
            Integer lastPumpValue = 0;
            for (int i = 0; i < pump.length; i++) {
                lastPumpValue = Integer.parseInt(pump[i]);
                Measurement measurement = robotData.get("Pump");
                measurement.getData().add(lastPumpValue);
                measurement.setCurrentSum(measurement.getCurrentSum() + lastPumpValue);
            }
            robotData.get("Pump").setStatus(lastPumpValue == 0 ?
                    Measurement.Status.OFF : Measurement.Status.ON);
        }
    }

    public void sendDate(Date date) {
        this.systemDate = date;
        this.sendDate = true;
    }

    public void sendConfiguration() {
        this.sendConfiguration = true;
    }

    public void scanData() {
        this.scanData = true;
    }

    public void pumpOff() {
        this.pumpOff = true;
    }

    public void pumpOn() {
        this.pumpOn = true;
    }

    public void exit() {
        this.exit = true;
    }

    public boolean isConnected() {
        return connected;
    }

    private void log(String message) {
        System.out.println("(" + port + ") " + message);
    }
}
