package misc;

public class Sliders {
    String image;

    public Sliders(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Sliders{" +
                "image='" + image + '\'' +
                '}';
    }
}
