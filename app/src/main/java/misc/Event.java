package misc;

public class Event {
    int id;
    String event_name;
    String event_date;
    String event_bio;
    String event_location;
    String event_image;

    String name;

    String event_live_link;

    int event_category;

    public Event(int id, String event_name, String event_date,
                 String event_bio, String event_location,
                 String event_image,int event_category,
                 String name,String event_live_link) {
        this.id = id;
        this.event_name = event_name;
        this.event_date = event_date;
        this.event_bio = event_bio;
        this.event_location = event_location;
        this.event_image = event_image;
        this.event_category = event_category;
        this.name = name;
        this.event_live_link = event_live_link;
    }

    public String getEvent_live_link() {
        return event_live_link;
    }

    public void setEvent_live_link(String event_live_link) {
        this.event_live_link = event_live_link;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getEvent_bio() {
        return event_bio;
    }

    public void setEvent_bio(String event_bio) {
        this.event_bio = event_bio;
    }

    public String getEvent_location() {
        return event_location;
    }

    public void setEvent_location(String event_location) {
        this.event_location = event_location;
    }

    public String getEvent_image() {
        return event_image;
    }

    public void setEvent_image(String event_image) {
        this.event_image = event_image;
    }

    public int getEvent_category() {
        return event_category;
    }

    public void setEvent_category(int event_category) {
        this.event_category = event_category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", event_name='" + event_name + '\'' +
                ", event_date='" + event_date + '\'' +
                ", event_bio='" + event_bio + '\'' +
                ", event_location='" + event_location + '\'' +
                ", event_image='" + event_image + '\'' +
                ", name='" + name + '\'' +
                ", event_category=" + event_category +
                '}';
    }
}
