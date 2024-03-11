package com.hy.project.demo.flink.task.common;

public class Event {
    private String user;
    private String url;
    private String timestamp;

    public Event() {
    }

    public Event(String user, String url, String timestamp) {
        this.user = user;
        this.url = url;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Event{" +
                "user='" + user + '\'' +
                ", url='" + url + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
