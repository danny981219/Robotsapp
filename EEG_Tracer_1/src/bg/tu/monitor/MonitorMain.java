package bg.tu.monitor;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MonitorMain {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Map<Integer, MonitorIrrigate.RobotConfiguration> config = readConfiguration();
                MonitorIrrigate ex = new MonitorIrrigate(config);
                ex.setVisible(true);
                ex.setResizable(false);
            }
        });
    }

    private static Map<Integer, MonitorIrrigate.RobotConfiguration> readConfiguration() {
        Properties configProperties = new Properties();
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream stream = loader.getResourceAsStream("config.properties");
            if (stream != null) {
                configProperties.load(stream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Map<Integer, MonitorIrrigate.RobotConfiguration> config = new HashMap<>();
        if (! configProperties.isEmpty()) {
            int maxRobots = Integer.valueOf((String) configProperties.getOrDefault("max.robot", "4"));
            if (maxRobots < 1 || maxRobots > 4) {
                System.out.println("Invalid argument for robots count : " + maxRobots +
                        ". Default value 4 will be used");
                maxRobots = 4;
            }
            for (int i = 1, r = 1; i <= maxRobots; i++) {
                if (configProperties.get("robot.name." + i) != null) {
                    MonitorIrrigate.RobotConfiguration robotConfiguration = new MonitorIrrigate.RobotConfiguration();
                    robotConfiguration.setName((String) configProperties.get("robot.name." + i));
                    robotConfiguration.setCanConnect(Boolean.valueOf((String) configProperties.get("robot.connect." + i)));
                    config.put(r, robotConfiguration);
                    r++;
                }
            }
        } else {
            System.out.println("Missing configuration. Default configuration will be used.");
            for (int r = 1; r <= 4; r++) {
                config.put(r, new MonitorIrrigate.RobotConfiguration("Robot " + r, true));
            }
        }

        return config;
    }
}
