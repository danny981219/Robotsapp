package bg.tu.robot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Measurement {

    public enum Status { ON, OFF }
    private Date date;
    private List<Integer> data;
    private Integer currentMin;
    private Integer currentMax;
    private Integer currentSum;
    private Status status;
        private Integer timeBeforeOff;

    public Measurement(Date date) {
        this.date = date;
        this.data = new ArrayList<>();
        this.currentMin = Integer.MAX_VALUE;
        this.currentMax = Integer.MIN_VALUE;
        this.currentSum = 0;
        this.status = Status.OFF;
        this.timeBeforeOff = 0;
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

    public void addData(Integer value) {
        getData().add(value);
        setCurrentSum(getCurrentSum() + value);
        setCurrentMin(Math.min(getCurrentMin(), value));
        setCurrentMax(Math.max(getCurrentMax(), value));
    }

    public Integer getCurrentAvg() {
        return getData().size() > 0 ? getCurrentSum() / getData().size() : 0;
    }

    public boolean isPumpOn() {
        return getStatus() == Status.ON;
    }

}
