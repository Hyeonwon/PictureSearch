import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Collections;
import java.util.Comparator;

public class PictureList {
    private List<Picture> pictures;
    private StuffList stuffList;

    private static final String IMAGE_DIRECTORY = "C:\\Java_assignment\\assginment_06\\pract_03\\src\\images\\";

    // Constructor to initialize PictureList with a StuffList
    public PictureList(StuffList stuffList) {
        this.pictures = new ArrayList<>();
        this.stuffList = stuffList;
    }

    // Constructor to initialize PictureList from a file
    public PictureList(String fileName, StuffList stuffList) {
        this(stuffList);
        initializeFromFile(fileName);
    }

    // Method to initialize PictureList from a file
    private void initializeFromFile(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            Pattern pattern = Pattern.compile(";\\s*images/(\\S+\\.jpg);");

            while ((line = br.readLine()) != null) {
                int startIndex = line.indexOf("<") + 1;
                int endIndex = line.lastIndexOf(">");
                if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                    String content = line.substring(startIndex, endIndex).trim();
                    String[] parts = content.split("> <");
                    if (parts.length >= 4) {
                        String pictureID = parts[0].trim();
                        String imageTime = parts[1].trim();
                        String images = parts[2].trim();
                        String stuff = parts[3].trim();
                        String tags = parts.length > 4 ? parts[4].trim() : "";
                        String comments = parts.length > 5 ? parts[5].trim() : "";

                        Matcher matcher = pattern.matcher(images);
                        if (matcher.find()) {
                            images = matcher.group(1);
                        }

                        Picture picture = new Picture(pictureID);
                        picture.setImageTime(imageTime);
                        picture.setImages(images);
                        picture.setComments(comments);
                        picture.setTags(tags);

                        String[] stuffItems = stuff.split("\\]\\s*\\[");
                        for (String item : stuffItems) {
                            item = item.replace("[", "").replace("]", "").trim();
                            if (!item.isEmpty()) {
                                String[] stuffParts = item.split(";\\s*");
                                if (stuffParts.length >= 3) {
                                    String stuffId = stuffParts[0].trim();
                                    String type = stuffParts.length > 1 ? stuffParts[1].trim() : "";
                                    String name = stuffParts.length > 2 ? stuffParts[2].trim() : "";
                                    String tagsField = stuffParts.length > 3 ? stuffParts[3].trim() : "";

                                    Stuff matchedStuff = new Stuff(stuffId, type, name, tagsField);
                                    picture.addStuff(matchedStuff);
                                } else {
                                    System.err.println("Skipping invalid stuff item: " + item);
                                }
                            }
                        }

                        pictures.add(picture);
                    } else {
                        System.err.println("Skipping invalid line: " + line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Sort pictures by time
        Collections.sort(pictures, Comparator.comparing(Picture::getCompareImageTime));
    }

    // Method to print the details of all pictures
    public void print() {
        for (Picture picture : pictures) {
            picture.print();
            System.out.println();
        }
    }

    // Method to get the size of the pictures list
    public int size() {
        return pictures.size();
    }

    // Method to get a picture by index
    public Picture get(int i) {
        if (i >= 0 && i < size()) {
            return pictures.get(i);
        }
        return null;
    }

    // Method to display all pictures using a GUI
    public void showAllPictures(PictureSearchGUI gui) {
        gui.updatePicturePanel(pictures);
    }

    // Method to get the image directory path
    public static String getImageDirectory() {
        return IMAGE_DIRECTORY;
    }

    // Method to save the list of pictures to a file
    public void saveToFile(String fileName) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            for (Picture picture : pictures) {
                pw.print("< " + picture.getPictureID() + " > ");
                pw.print("< " + picture.getImageTime() + " > ");
                pw.print("< " + new File(picture.getImages()).getName() + " > ");
                pw.print("< ");
                for (Stuff stuff : picture.getStuffList()) {
                    pw.print("[ " + stuff.getId() + "; " + stuff.getType() + "; " + stuff.getName() + "; " + stuff.getTags() + " ] ");
                }
                pw.print("> < " + picture.getTags() + " > ");
                pw.print("< " + picture.getComments() + " > ");
                pw.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to load the list of pictures from a file
    public void loadFromFile(String fileName) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            pictures = (List<Picture>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Sort pictures by time
        Collections.sort(pictures, Comparator.comparing(Picture::getCompareImageTime));
    }

    // Method to add a picture to the list
    public void addPicture(Picture picture) {
        pictures.add(picture);

        // Sort pictures by time
        Collections.sort(pictures, Comparator.comparing(Picture::getCompareImageTime));
    }

    // Method to delete a picture from the list
    public void deletePicture(Picture picture) {
        pictures.remove(picture);
    }

    // Method to get a list of all pictures
    public List<Picture> getAllPictures() {
        return new ArrayList<>(pictures);
    }
}
