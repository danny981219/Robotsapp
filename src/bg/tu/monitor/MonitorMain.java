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
                Map<Integer, String> config = readConfiguration();
                MonitorIrrigate ex = new MonitorIrrigate(config);
                ex.setVisible(true);
                ex.setResizable(false);
            }
        });
    }

    private static Map<Integer, String> readConfiguration() {
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

        Map<Integer, String> config = new HashMap<Integer, String>();
        if (! configProperties.isEmpty()) {
            int maxRobots = Integer.valueOf((String) configProperties.getOrDefault("max.robot", "4"));
            if (maxRobots < 1 || maxRobots > 4) {
                System.out.println("Invalid argument for robots count : " + maxRobots +
                        ". Default value 4 will be used");
                maxRobots = 4;
            }
            for (int r = 1; r <= maxRobots; r++) {
                config.put(r, (String) configProperties.getOrDefault("robot.name." + r, "Robot " + r));
            }
        } else {
            System.out.println("Missing configuration. Default configuration will be used.");
            for (int r = 1; r <= 4; r++) {
                config.put(r, "Robot " + r);
            }
        }

        return config;
    }
}
