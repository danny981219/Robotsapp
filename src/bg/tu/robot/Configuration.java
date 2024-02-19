package bg.tu.robot;

import java.util.HashMap;
import java.util.Map;

public class Configuration {

    private Map<String, Integer> thresholds;
    private Map<String, Integer> limits;

    private Integer step;

    public Configuration() {
        this.thresholds = new HashMap<>();
        this.limits = new HashMap<>();
        this.step = 20;
    }

    public Map<String, Integer> getThresholds() {
        return thresholds;
    }

    public void setThresholds(Map<String, Integer> thresholds) {
        this.thresholds = thresholds;
    }

    public Map<String, Integer> getLimits() {
        return limits;
    }

    public void setLimits(Map<String, Integer> limits) {
        this.limits = limits;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

}
