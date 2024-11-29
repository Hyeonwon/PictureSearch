import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StuffList {
    private static int maxStuffNum = 0;
    private static List<Stuff> sList = new ArrayList<>();

    // Default constructor initializing StuffList
    public StuffList() {
        initialize();
    }

    // Constructor initializing StuffList from a file
    public StuffList(String fileName) {
        this();
        readFile(fileName);
    }

    // Method to initialize the list
    private void initialize() {
        sList = new ArrayList<>();
    }

    // Method to read stuff data from a file
    public void readFile(String fileName) {
        if (!new File(fileName).exists()) {
            System.err.println("File not found: " + fileName);
            return;
        }

        initialize();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            Set<String> idSet = new HashSet<>();
            while ((line = br.readLine()) != null) {
                Matcher matcher = Pattern.compile("\\[(.*?)\\]").matcher(line);
                while (matcher.find()) {
                    String info = matcher.group(1).trim();
                    String[] infoParts = info.split(";");
                    if (infoParts.length >= 3) {
                        String id = infoParts[0].trim();
                        if (!idSet.add(id)) {
                            continue;
                        }
                        String type = infoParts.length > 1 ? infoParts[1].trim() : "";
                        String name = infoParts.length > 2 ? infoParts[2].trim() : "";
                        String tags = infoParts.length > 3 ? infoParts[3].trim() : "";

                        Stuff stuff = new Stuff(id, type, name, tags);
                        addStuff(stuff);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to add a Stuff object to the list
    private static void addStuff(Stuff stuff) {
        sList.add(stuff);
    }

    // Method to get the size of the list
    public int size() {
        return sList.size();
    }

    // Method to get a Stuff object by index
    public Stuff get(int i) {
        if (i >= 0 && i < size()) {
            return sList.get(i);
        }
        return null;
    }

    // Method to get a list of Stuff objects by ID
    public List<Stuff> getStuffById(String id) {
        List<Stuff> result = new ArrayList<>();
        for (Stuff stuff : sList) {
            if (stuff.getId().equals(id)) {
                result.add(stuff);
            }
        }
        return result;
    }

    // Method to print the details of all Stuff objects in the list
    public void print() {
        for (Stuff stuff : sList) {
            System.out.println(stuff.getName() + " " + stuff.getType() + " " + stuff.getTags());
        }
    }

    // Method to get a list of all Stuff objects
    public List<Stuff> getAllStuffs() {
        return new ArrayList<>(sList);
    }

    // Method to get the ID of a Stuff object by type and name, creating a new one if necessary
    public static String getStuffID(String type, String name) {
        for (Stuff stuff : sList) {
            if (stuff.getType().equals(type) && stuff.getName().equals(name)) {
                return stuff.getId();
            }
        }
        String newID = newStuffID();
        addStuff(new Stuff(newID, type, name, ""));
        return newID;
    }

    // Method to generate a new Stuff ID
    private static String newStuffID() {
        return String.format("%08d", ++maxStuffNum);
    }

    // Method to add a Stuff object to the list by specifying its attributes
    private static void addStuff(String id, String type, String name) {
        sList.add(new Stuff(id, type, name, ""));
    }
}
