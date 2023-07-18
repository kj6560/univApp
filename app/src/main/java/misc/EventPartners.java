package misc;

public class EventPartners {
    int id;
    String partner_name;
    String partner_logo;

    public EventPartners(int id, String partner_name, String partner_logo) {
        this.id = id;
        this.partner_name = partner_name;
        this.partner_logo = partner_logo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPartner_name() {
        return partner_name;
    }

    public void setPartner_name(String partner_name) {
        this.partner_name = partner_name;
    }

    public String getPartner_logo() {
        return partner_logo;
    }

    public void setPartner_logo(String partner_logo) {
        this.partner_logo = partner_logo;
    }

    @Override
    public String toString() {
        return "EventPartners{" +
                "id=" + id +
                ", partner_name='" + partner_name + '\'' +
                ", partner_logo='" + partner_logo + '\'' +
                '}';
    }
}
