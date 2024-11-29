import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class PictureSearchGUI extends JFrame {
    private PictureList pictureList;
    private StuffList stuffList;
    private JPanel mainPanel;
    private JFileChooser fileChooser;
    private Picture selectedPicture;
    private JLabel deleteLabel; // Label to show "delete 선택" text

    // Constructor to initialize the GUI
    public PictureSearchGUI() {
        String stuffFilePath = "C:\\Java_assignment\\assginment_06\\pract_03\\src\\picture-normal.data";
        stuffList = new StuffList(stuffFilePath);

        String pictureFilePath = "C:\\Java_assignment\\assginment_06\\pract_03\\src\\picture-normal.data";
        pictureList = new PictureList(pictureFilePath, stuffList);

        setTitle("Simple Picture Search");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        JButton showAllButton = new JButton("Show All Pictures");
        topPanel.add(showAllButton, BorderLayout.CENTER);

        showAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pictureList.showAllPictures(PictureSearchGUI.this);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 1, 0, 10));

        String[] buttonLabels = {"ADD", "DELETE", "LOAD", "SAVE", "SEARCH"};
        Dimension buttonSize = new Dimension(100, 40);
        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setPreferredSize(buttonSize);
            button.setMaximumSize(buttonSize);
            button.setMinimumSize(buttonSize);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    switch (label) {
                        case "ADD":
                            new AddPictureFrame();
                            break;
                        case "DELETE":
                            deleteSelectedPicture();
                            break;
                        case "LOAD":
                            loadPictures();
                            break;
                        case "SAVE":
                            savePictures();
                            break;
                        case "SEARCH":
                            new SearchPictureFrame();
                            break;
                    }
                }
            });
            buttonPanel.add(button);
        }

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        JScrollPane mainScrollPane = new JScrollPane(mainPanel);
        mainScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(mainScrollPane, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.EAST);

        fileChooser = new JFileChooser();

        // Display all pictures initially
        pictureList.showAllPictures(this);

        // Initialize deleteLabel
        deleteLabel = new JLabel("delete 선택");
        deleteLabel.setForeground(Color.RED);
        deleteLabel.setVisible(false);
        getLayeredPane().add(deleteLabel, JLayeredPane.POPUP_LAYER);
    }

    // Method to delete the selected picture
    private void deleteSelectedPicture() {
        if (selectedPicture != null) {
            pictureList.deletePicture(selectedPicture);
            selectedPicture = null;
            pictureList.showAllPictures(this);
        } else {
            JOptionPane.showMessageDialog(this, "삭제할 사진을 선택하세요.", "선택 오류", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Method to load pictures from a file
    private void loadPictures() {
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile.exists()) {
                pictureList = new PictureList(selectedFile.getPath(), stuffList);
                pictureList.showAllPictures(this);
            } else {
                JOptionPane.showMessageDialog(this, "선택한 파일을 찾을 수 없습니다: " + selectedFile.getPath(), "파일 오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Method to save pictures to a file
    private void savePictures() {
        int returnValue = fileChooser.showSaveDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile.exists()) {
                int response = JOptionPane.showConfirmDialog(this, "파일이 이미 존재합니다. 덮어쓰시겠습니까?", "파일 덮어쓰기", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (response == JOptionPane.NO_OPTION) {
                    return;
                }
            }
            pictureList.saveToFile(selectedFile.getPath());
        }
    }

    // Method to update the picture panel with the given list of pictures
    public void updatePicturePanel(List<Picture> pictures) {
        mainPanel.removeAll();

        // Sort pictures by time
        pictures.sort((p1, p2) -> p1.getCompareImageTime().compareTo(p2.getCompareImageTime()));

        for (Picture picture : pictures) {
            if (picture != null && !picture.getImages().isEmpty()) {
                JPanel picturePanel = new JPanel();
                picturePanel.setLayout(new BorderLayout());
                picturePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

                picturePanel.addMouseMotionListener(new MouseMotionAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent e) {
                        if (picturePanel.getCursor().getType() == Cursor.HAND_CURSOR) {
                            Point p = e.getPoint();
                            SwingUtilities.convertPointToScreen(p, picturePanel);
                            Point panelLocation = getLocationOnScreen();
                            deleteLabel.setLocation(p.x - panelLocation.x + 10, p.y - panelLocation.y + 10);
                            deleteLabel.setVisible(true);
                        }
                    }
                });

                picturePanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        selectedPicture = picture;
                        for (Component component : mainPanel.getComponents()) {
                            component.setBackground(null);
                        }
                        picturePanel.setBackground(Color.LIGHT_GRAY);
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        picturePanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        picturePanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        deleteLabel.setVisible(false);
                    }
                });

                JPanel pictureGroupPanel = new JPanel(new BorderLayout());
                pictureGroupPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

                JLabel timeLabel = new JLabel(picture.getImageTime(), SwingConstants.CENTER);
                pictureGroupPanel.add(timeLabel, BorderLayout.NORTH);

                try {
                    File imgFile = new File(PictureList.getImageDirectory() + picture.getImages());
                    if (imgFile.exists()) {
                        BufferedImage img = ImageIO.read(imgFile);
                        Image scaledImg = img.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                        JLabel picLabel = new JLabel(new ImageIcon(scaledImg));
                        pictureGroupPanel.add(picLabel, BorderLayout.CENTER);
                    } else {
                        JLabel picLabel = new JLabel("Image not found", SwingConstants.CENTER);
                        pictureGroupPanel.add(picLabel, BorderLayout.CENTER);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                JLabel commentsLabel = new JLabel(picture.getComments(), SwingConstants.CENTER);
                pictureGroupPanel.add(commentsLabel, BorderLayout.SOUTH);

                picturePanel.add(pictureGroupPanel, BorderLayout.WEST);

                JPanel infoGroupPanel = new JPanel(new BorderLayout());
                infoGroupPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

                JPanel tagsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                tagsPanel.add(new JLabel(picture.getTags()));
                infoGroupPanel.add(tagsPanel, BorderLayout.NORTH);

                JPanel stuffGroupPanel = new JPanel();
                stuffGroupPanel.setLayout(new BoxLayout(stuffGroupPanel, BoxLayout.Y_AXIS));
                stuffGroupPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

                JScrollPane stuffScrollPane = new JScrollPane(stuffGroupPanel);
                stuffScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                stuffScrollPane.setPreferredSize(new Dimension(600, 200));

                List<Stuff> pictureStuffList = picture.getStuffList();
                if (pictureStuffList.isEmpty()) {
                    stuffGroupPanel.add(new JLabel("No Stuff Information"));
                } else {
                    for (Stuff stuff : pictureStuffList) {
                        JPanel singleStuffPanel = new JPanel();
                        singleStuffPanel.setLayout(new BoxLayout(singleStuffPanel, BoxLayout.Y_AXIS));
                        singleStuffPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

                        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                        typePanel.add(new JLabel("Type  "));
                        JTextField typeField = new JTextField(stuff.getType());
                        typeField.setEditable(false);
                        typeField.setColumns(71);
                        typePanel.add(typeField);
                        singleStuffPanel.add(typePanel);

                        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                        namePanel.add(new JLabel("Name"));
                        JTextField nameField = new JTextField(stuff.getName());
                        nameField.setEditable(false);
                        nameField.setColumns(71);
                        namePanel.add(nameField);
                        singleStuffPanel.add(namePanel);

                        JPanel tagsPanelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
                        tagsPanelInfo.add(new JLabel("Tags  "));
                        JTextField tagsField = new JTextField(stuff.getTags());
                        tagsField.setEditable(false);
                        tagsField.setColumns(71);
                        tagsPanelInfo.add(tagsField);
                        singleStuffPanel.add(tagsPanelInfo);

                        stuffGroupPanel.add(singleStuffPanel);
                    }
                }

                infoGroupPanel.add(stuffScrollPane, BorderLayout.CENTER);
                picturePanel.add(infoGroupPanel, BorderLayout.CENTER);

                mainPanel.add(picturePanel);
                mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    // Method to get all pictures from the picture list
    public List<Picture> getAllPictures() {
        return pictureList.getAllPictures();
    }

    // Main method to launch the GUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                PictureSearchGUI gui = new PictureSearchGUI();
                gui.setVisible(true);
            }
        });
    }

    // Inner class for the frame to add a new picture
    class AddPictureFrame extends JFrame {
        private JTextField imagePathField;
        private JTextField timeField;
        private JTextField pictureTagsField;
        private JTextField commentsField;
        private List<StuffPanel> stuffPanels;
        private JPanel stuffContainer;

        // Constructor to initialize the frame
        public AddPictureFrame() {
            setTitle("Add a Picture");
            setSize(500, 300);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLayout(new BorderLayout());

            JPanel mainPanel = new JPanel(new BorderLayout());

            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            topPanel.add(new JLabel("Time"));
            timeField = new JTextField(15);
            topPanel.add(timeField);
            topPanel.add(new JLabel("(Picture) Tags"));
            pictureTagsField = new JTextField(15);
            topPanel.add(pictureTagsField);
            mainPanel.add(topPanel, BorderLayout.NORTH);

            JPanel centerPanel = new JPanel(new BorderLayout());

            JPanel leftCenterPanel = new JPanel(new BorderLayout());
            JButton selectImageButton = new JButton("Select Image File");
            imagePathField = new JTextField();
            imagePathField.setVisible(false);
            leftCenterPanel.add(imagePathField, BorderLayout.NORTH); // hidden field to store the image path
            selectImageButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int returnValue = fileChooser.showOpenDialog(null);
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        imagePathField.setText(selectedFile.getAbsolutePath());
                    }
                }
            });
            leftCenterPanel.add(selectImageButton, BorderLayout.CENTER);
            centerPanel.add(leftCenterPanel, BorderLayout.WEST);

            JPanel rightCenterPanel = new JPanel(new BorderLayout());
            rightCenterPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            stuffContainer = new JPanel();
            stuffContainer.setLayout(new BoxLayout(stuffContainer, BoxLayout.Y_AXIS));
            rightCenterPanel.add(new JScrollPane(stuffContainer), BorderLayout.CENTER);

            centerPanel.add(rightCenterPanel, BorderLayout.CENTER);

            mainPanel.add(centerPanel, BorderLayout.CENTER);

            JPanel bottomPanel = new JPanel(new BorderLayout());

            JPanel commentsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            commentsPanel.add(new JLabel("Comments"));
            commentsField = new JTextField(35);
            commentsPanel.add(commentsField);
            bottomPanel.add(commentsPanel, BorderLayout.NORTH);

            JPanel buttonPanel = new JPanel(new BorderLayout());
            JButton moreStuffButton = new JButton("More Stuff");
            moreStuffButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addStuffPanel();
                }
            });
            buttonPanel.add(moreStuffButton, BorderLayout.WEST);

            JButton okButton = new JButton("OK - INPUT END");
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addPicture();
                    dispose();
                }
            });
            buttonPanel.add(okButton, BorderLayout.EAST);
            bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

            mainPanel.add(bottomPanel, BorderLayout.SOUTH);

            add(mainPanel, BorderLayout.CENTER);

            setVisible(true);

            stuffPanels = new ArrayList<>();
            addStuffPanel(); // 기본적으로 하나의 StuffPanel 추가
        }

        // Method to add a new StuffPanel to the frame
        private void addStuffPanel() {
            StuffPanel stuffPanel = new StuffPanel();
            stuffPanels.add(stuffPanel);
            stuffContainer.add(stuffPanel);
            stuffContainer.revalidate();
            stuffContainer.repaint();
        }

        // Method to add a new picture
        private void addPicture() {
            String imagePath = imagePathField.getText();
            String time = timeField.getText().isEmpty() ? LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss:SSS")) : timeField.getText();
            String tags = pictureTagsField.getText();
            String comments = commentsField.getText();

            Picture newPicture = new Picture("m_" + time);
            newPicture.setImageTime(time);
            newPicture.setTags(tags);
            newPicture.setComments(comments);
            newPicture.setImages(new File(imagePath).getName());

            for (StuffPanel stuffPanel : stuffPanels) {
                Stuff stuff = new Stuff("", stuffPanel.getType(), stuffPanel.getName(), stuffPanel.getTags());
                newPicture.addStuff(stuff);
            }

            pictureList.addPicture(newPicture);
            pictureList.showAllPictures(PictureSearchGUI.this);
        }

    }

    // Inner class for the panel to add stuff information
    class StuffPanel extends JPanel {
        private JTextField typeField;
        private JTextField nameField;
        private JTextField tagsField;

        // Constructor to initialize the panel
        public StuffPanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY)));

            JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            typePanel.add(new JLabel("Type"));
            typeField = new JTextField(25);
            typePanel.add(typeField);
            add(typePanel);

            JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            namePanel.add(new JLabel("Name"));
            nameField = new JTextField(25);
            namePanel.add(nameField);
            add(namePanel);

            JPanel tagsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            tagsPanel.add(new JLabel("Tags"));
            tagsField = new JTextField(25);
            tagsPanel.add(tagsField);
            add(tagsPanel);
        }

        // Method to get the type of stuff
        public String getType() {
            return typeField.getText();
        }

        // Method to get the name of stuff
        public String getName() {
            return nameField.getText();
        }

        // Method to get the tags of stuff
        public String getTags() {
            return tagsField.getText();
        }
    }

    // Inner class for the frame to search pictures
    class SearchPictureFrame extends JFrame {
        private JTextField fromTimeField;
        private JTextField toTimeField;
        private JTextField tagsField;
        private JTextField commentsField;
        private JTextField typeField;
        private JTextField nameField;
        private JTextField rightTagsField;
        private JTextField generalSearchField;

        // Constructor to initialize the frame
        public SearchPictureFrame() {
            setTitle("Search Picture");
            setSize(500, 300);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLayout(new BorderLayout());

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

            JPanel timeSearchPanel = new JPanel(new BorderLayout());
            timeSearchPanel.setBorder(BorderFactory.createTitledBorder("Time Search"));

            JPanel timeLabelsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
            timeLabelsPanel.add(new JLabel("From"));
            timeLabelsPanel.add(new JLabel("To"));
            timeSearchPanel.add(timeLabelsPanel, BorderLayout.WEST);

            JPanel timeFieldsPanel = new JPanel(new GridLayout(2, 1, 0, 0));
            JPanel fromPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            fromTimeField = new JTextField(20);
            fromPanel.add(fromTimeField);
            timeFieldsPanel.add(fromPanel);
            JPanel toPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            toTimeField = new JTextField(20);
            toPanel.add(toTimeField);
            timeFieldsPanel.add(toPanel);
            timeSearchPanel.add(timeFieldsPanel, BorderLayout.CENTER);

            JPanel timeFormatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            timeFormatPanel.add(new JLabel("(yyyy-MM-dd_HH:mm:ss)"));
            timeSearchPanel.add(timeFormatPanel, BorderLayout.EAST);

            mainPanel.add(timeSearchPanel);

            JPanel keywordSearchPanel = new JPanel(new BorderLayout());
            keywordSearchPanel.setBorder(BorderFactory.createTitledBorder("Keyword Search"));

            JPanel leftKeywordPanel = new JPanel(new BorderLayout());
            JPanel leftFieldsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
            leftFieldsPanel.add(new JLabel("Tags"));
            tagsField = new JTextField(10);
            leftFieldsPanel.add(tagsField);
            leftFieldsPanel.add(new JLabel("Comments"));
            commentsField = new JTextField(10);
            leftFieldsPanel.add(commentsField);
            leftKeywordPanel.add(leftFieldsPanel, BorderLayout.CENTER);

            keywordSearchPanel.add(leftKeywordPanel, BorderLayout.WEST);

            JPanel rightKeywordPanel = new JPanel(new GridLayout(3, 1, 0, 0));

            JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            typePanel.add(new JLabel("Type"));
            typeField = new JTextField(18);
            typePanel.add(typeField);
            rightKeywordPanel.add(typePanel);

            JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            namePanel.add(new JLabel("Name"));
            nameField = new JTextField(18);
            namePanel.add(nameField);
            rightKeywordPanel.add(namePanel);

            JPanel rightTagsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            rightTagsPanel.add(new JLabel("Tags"));
            rightTagsField = new JTextField(18);
            rightTagsPanel.add(rightTagsField);
            rightKeywordPanel.add(rightTagsPanel);

            keywordSearchPanel.add(rightKeywordPanel, BorderLayout.CENTER);

            JPanel generalSearchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            generalSearchPanel.add(new JLabel("General Search"));
            generalSearchField = new JTextField(10);
            generalSearchPanel.add(generalSearchField);

            keywordSearchPanel.add(generalSearchPanel, BorderLayout.SOUTH);

            mainPanel.add(keywordSearchPanel);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton andSearchButton = new JButton("AND SEARCH");
            andSearchButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    performSearch(true);
                }
            });
            JButton orSearchButton = new JButton("OR SEARCH");
            orSearchButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    performSearch(false);
                }
            });
            JButton closeButton = new JButton("CLOSE");
            closeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });

            buttonPanel.add(andSearchButton);
            buttonPanel.add(orSearchButton);
            buttonPanel.add(closeButton);

            add(mainPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);

            setVisible(true);
        }

        // Method to perform search based on given criteria
        private void performSearch(boolean isAndSearch) {
            String fromTime = fromTimeField.getText().trim();
            String toTime = toTimeField.getText().trim();
            String tags = tagsField.getText().trim();
            String comments = commentsField.getText().trim();
            String type = typeField.getText().trim();
            String name = nameField.getText().trim();
            String rightTags = rightTagsField.getText().trim();
            String generalSearch = generalSearchField.getText().trim();

            LocalDateTime fromDateTime = null;
            LocalDateTime toDateTime = null;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");

            try {
                if (!fromTime.isEmpty()) {
                    fromDateTime = LocalDateTime.parse(fromTime, formatter);
                }

                if (!toTime.isEmpty()) {
                    toDateTime = LocalDateTime.parse(toTime, formatter);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Please enter the time in the format yyyy-MM-dd_HH:mm:ss", "Invalid Time Format", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if only time fields are filled for OR search
            if (!isAndSearch && fromDateTime != null && toDateTime != null && tags.isEmpty() && comments.isEmpty() && type.isEmpty() && name.isEmpty() && rightTags.isEmpty() && generalSearch.isEmpty()) {
                JOptionPane.showMessageDialog(this, "OR search is not supported with only time search fields.", "Invalid Search Criteria", JOptionPane.WARNING_MESSAGE);
                return;
            }

            List<Picture> resultPictures = new ArrayList<>();

            for (Picture picture : pictureList.getAllPictures()) {
                boolean matches = true;

                if (fromDateTime != null || toDateTime != null) {
                    LocalDateTime imageDateTime = LocalDateTime.parse(picture.getCompareImageTime(), formatter);

                    if (fromDateTime != null) {
                        if (imageDateTime.isBefore(fromDateTime)) {
                            matches = false;
                        }
                    }

                    if (toDateTime != null) {
                        if (imageDateTime.isAfter(toDateTime)) {
                            matches = false;
                        }
                    }
                }

                if (isAndSearch) {
                    if (!tags.isEmpty() && !picture.getTags().contains(tags)) {
                        matches = false;
                    }
                    if (!comments.isEmpty() && !picture.getComments().contains(comments)) {
                        matches = false;
                    }
                    if (!type.isEmpty()) {
                        boolean typeMatches = false;
                        for (Stuff stuff : picture.getStuffList()) {
                            if (stuff.getType().contains(type)) {
                                typeMatches = true;
                                break;
                            }
                        }
                        if (!typeMatches) {
                            matches = false;
                        }
                    }
                    if (!name.isEmpty()) {
                        boolean nameMatches = false;
                        for (Stuff stuff : picture.getStuffList()) {
                            if (stuff.getName().contains(name)) {
                                nameMatches = true;
                                break;
                            }
                        }
                        if (!nameMatches) {
                            matches = false;
                        }
                    }
                    if (!rightTags.isEmpty()) {
                        boolean tagsMatches = false;
                        for (Stuff stuff : picture.getStuffList()) {
                            if (stuff.getTags().contains(rightTags)) {
                                tagsMatches = true;
                                break;
                            }
                        }
                        if (!tagsMatches) {
                            matches = false;
                        }
                    }
                    if (!generalSearch.isEmpty()) {
                        boolean generalMatches = picture.getTags().contains(generalSearch) || picture.getComments().contains(generalSearch);
                        for (Stuff stuff : picture.getStuffList()) {
                            if (stuff.getType().contains(generalSearch) || stuff.getName().contains(generalSearch) || stuff.getTags().contains(generalSearch)) {
                                generalMatches = true;
                                break;
                            }
                        }
                        if (!generalMatches) {
                            matches = false;
                        }
                    }
                } else {
                    matches = false;
                    if (!tags.isEmpty() && picture.getTags().contains(tags)) {
                        matches = true;
                    }
                    if (!comments.isEmpty() && picture.getComments().contains(comments)) {
                        matches = true;
                    }
                    if (!type.isEmpty()) {
                        for (Stuff stuff : picture.getStuffList()) {
                            if (stuff.getType().contains(type)) {
                                matches = true;
                                break;
                            }
                        }
                    }
                    if (!name.isEmpty()) {
                        for (Stuff stuff : picture.getStuffList()) {
                            if (stuff.getName().contains(name)) {
                                matches = true;
                                break;
                            }
                        }
                    }
                    if (!rightTags.isEmpty()) {
                        for (Stuff stuff : picture.getStuffList()) {
                            if (stuff.getTags().contains(rightTags)) {
                                matches = true;
                                break;
                            }
                        }
                    }
                    if (!generalSearch.isEmpty()) {
                        if (picture.getTags().contains(generalSearch) || picture.getComments().contains(generalSearch)) {
                            matches = true;
                        }
                        for (Stuff stuff : picture.getStuffList()) {
                            if (stuff.getType().contains(generalSearch) || stuff.getName().contains(generalSearch) || stuff.getTags().contains(generalSearch)) {
                                matches = true;
                                break;
                            }
                        }
                    }
                }

                if (matches) {
                    resultPictures.add(picture);
                }
            }

            updatePicturePanel(resultPictures);
        }
    }
}
