package org.app.noteproject;

public class Note {

    private int number;
    private int velocity;
    private int startTime;
    private int endTime;

    public Note() {}

    public Note(int number, int velocity, int startTime, int endTime) {
        this.number = number;
        this.velocity = velocity;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getNumber() {
        return  this.number;
    }

    public int getVelocity() {
        return velocity;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }
}
