import java.io.Serializable;

public class Stuff implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String type;
    private String name;
    private String tags;

    // Constructor to initialize Stuff object with provided details
    public Stuff(String id, String type, String name, String tags) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.tags = tags;
    }

    // Getter for id
    public String getId() {
        return id;
    }

    // Getter for type
    public String getType() {
        return type;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Getter for tags
    public String getTags() {
        return tags;
    }

    // Setter for id
    public void setId(String id) {
        this.id = id;
    }

    // Setter for type
    public void setType(String type) {
        this.type = type;
    }

    // Setter for name
    public void setName(String name) {
        this.name = name;
    }

    // Setter for tags
    public void setTags(String tags) {
        this.tags = tags;
    }

    // Method to get a string representation of all Stuff details
    public String getAllStuff() {
        return "[" + id + "; " + type + "; " + name + "; " + tags + "]";
    }

    // Override toString method to return a detailed string representation of Stuff
    @Override
    public String toString() {
        return "Stuff{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", tags='" + tags + '\'' +
                '}';
    }
}
