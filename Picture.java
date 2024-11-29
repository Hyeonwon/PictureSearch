import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Picture implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int nextId = 1;
    private int id;
    private LocalDateTime timestamp;
    private String pictureID;
    private String imageTime;
    private String compareImageTime;
    private String images;
    private String comments;
    private String tags;
    private List<Stuff> stuffList;

    // Constructor to initialize a new Picture object with the provided imageName
    public Picture(String imageName) {
        this.id = nextId++;
        this.timestamp = LocalDateTime.now();
        this.pictureID = imageName;
        this.imageTime = "";
        this.compareImageTime = "";
        this.images = "";
        this.comments = "";
        this.tags = "";
        this.stuffList = new ArrayList<>();
    }

    // Helper method to format the timestamp in "yyyy-MM-dd_HH:mm:ss:SSS" format
    private String timeStamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss:SSS");
        return timestamp.format(formatter);
    }

    // Method to print the details of the Picture object in a formatted manner
    public void print() {
        System.out.print("< " + pictureID + " >  ");
        System.out.print("< " + imageTime + " >  ");
        System.out.print("< " + images + " >  ");
        System.out.print("< " + stuffList + " >  ");
        System.out.print("< " + tags + " >  ");
        if (!comments.isEmpty()) {
            System.out.print("< " + comments + " >  ");
        }
    }

    // Getters and setters for the fields
    public int getId() {
        return id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getPictureID() {
        return pictureID;
    }

    public String getImageTime() {
        return imageTime;
    }

    // Sets the imageTime and compareImageTime fields with the provided imageTime value
    public void setImageTime(String imageTime) {
        this.imageTime = imageTime.substring(0, 23); // Store in yyyy-MM-dd_HH:mm:ss:SSS format
        this.compareImageTime = imageTime.substring(0, 19); // Store in yyyy-MM-dd_HH:mm:ss format
    }

    public String getCompareImageTime() {
        return compareImageTime;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public List<Stuff> getStuffList() {
        return stuffList;
    }

    // Adds a Stuff object to the stuffList
    public void addStuff(Stuff stuff) {
        this.stuffList.add(stuff);
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
