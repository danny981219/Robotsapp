package bg.tu.monitor;

import java.util.*;

public class Measurement {

    public enum Status { ON, OFF }
    private String name;
    private Date date;
    private List<Integer> data;

    private Integer currentMin;
    private Integer currentMax;
    private Integer currentSum;
    private Map<String, Integer> thresholds;
    private Map<String, Integer> limits;
    private Status status;
    private Integer timeBeforeOff;

    private Integer step;

    public Measurement(String name, Date date) {
        this.name = name;
        this.date = date;
        this.data = new ArrayList<>();
        this.currentMin = Integer.MAX_VALUE;
        this.currentMax = Integer.MIN_VALUE;
        this.currentSum = 0;
        this.thresholds = new HashMap<>();
        this.limits = new HashMap<>();
        this.status = Status.OFF;
        this.timeBeforeOff = 0;
        this.step = 20;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Integer> getData() {
        return data;
    }

    public void setData(List<Integer> data) {
        this.data = data;
    }

    public Integer getCurrentMin() {
        return currentMin;
    }

    public void setCurrentMin(Integer currentMin) {
        this.currentMin = currentMin;
    }

    public Integer getCurrentMax() {
        return currentMax;
    }

    public void setCurrentMax(Integer currentMax) {
        this.currentMax = currentMax;
    }

    public Integer getCurrentSum() {
        return currentSum;
    }

    public void setCurrentSum(Integer currentSum) {
        this.currentSum = currentSum;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getTimeBeforeOff() {
        return timeBeforeOff;
    }

    public void setTimeBeforeOff(Integer timeBeforeOff) {
        this.timeBeforeOff = timeBeforeOff;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public Integer getCurrentAvg() {
        return getData().size() > 0 ? getCurrentSum() / getData().size() : 0;
    }
}
