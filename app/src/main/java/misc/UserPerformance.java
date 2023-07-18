package misc;

public class UserPerformance {
    int event_id;
    String event_result_key;
    String event_result_value;
    String event_name;
    String event_date;
    String event_location;

    public UserPerformance(int event_id, String event_result_key, String event_result_value,
                           String event_name, String event_date, String event_location) {
        this.event_id = event_id;
        this.event_result_key = event_result_key;
        this.event_result_value = event_result_value;
        this.event_name = event_name;
        this.event_date = event_date;
        this.event_location = event_location;
    }

    public int getEvent_id() {
        return event_id;
    }

    public void setEvent_id(int event_id) {
        this.event_id = event_id;
    }

    public String getEvent_result_key() {
        return event_result_key;
    }

    public void setEvent_result_key(String event_result_key) {
        this.event_result_key = event_result_key;
    }

    public String getEvent_result_value() {
        return event_result_value;
    }

    public void setEvent_result_value(String event_result_value) {
        this.event_result_value = event_result_value;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getEvent_date() {
        return event_date;
    }

    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }

    public String getEvent_location() {
        return event_location;
    }

    public void setEvent_location(String event_location) {
        this.event_location = event_location;
    }

    @Override
    public String toString() {
        return "UserPerformance{" +
                "event_id=" + event_id +
                ", event_result_key='" + event_result_key + '\'' +
                ", event_result_value='" + event_result_value + '\'' +
                ", event_name='" + event_name + '\'' +
                ", event_date='" + event_date + '\'' +
                ", event_location='" + event_location + '\'' +
                '}';
    }
}
