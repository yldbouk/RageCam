import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.FutureTask;
import java.util.stream.Stream;

public class SettingsManager {

    public static final String[] SupportedGames = {"GGST.exe"};
    public String currentGame = new String();
    private static final File settingsFile = new File(System.getProperty("user.home") + (System.getProperty("os.name").startsWith("Windows") ? "\\" : "/") + ".ragecam");
    private static final JFrame jFrame = new JFrame();
    private static JTextField pathField = new JTextField();
    private static JCheckBox showAgain = new JCheckBox("Don't Show Again");
    static final FutureTask<Object> ft = new FutureTask<Object>(() -> {}, new Object());
    private boolean haveSettings = false;
    private String[] settings = {"", ""};

    public SettingsManager() {
        System.out.println("Loading Settings...");
        try {
            if (!settingsFile.exists()) {
                System.out.println("No Settings File Exists! Creating...");
                showSettingsPopup();
                ft.get();
            } else {
                Scanner settingsReader = new Scanner(settingsFile);
                settings[0] = settingsReader.nextLine();
                settings[1] = settingsReader.nextLine();
                settingsReader.close();
                haveSettings = true;

                if (settings[1].equals("false")) {
                    showSettingsPopup();
                    ft.get();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("Waiting For Game...");
        getCurrentGame();

    }

    public String getSetting(Integer settingID){
        // only one allowable setting here
        // otherwise a switch would be used
        return settings[0];
    }

    private void showSettingsPopup() {
        jFrame.setSize(400,65);
        jFrame.setUndecorated(true);
        jFrame.setLocationRelativeTo(null);
        jFrame.setLayout(new GridLayout(2,1));

        JPanel fileSelectorPanel = new JPanel();
        SpringLayout fileSelectorPanelLayout = new SpringLayout();
        fileSelectorPanel.setLayout(fileSelectorPanelLayout);

        JLabel locationLabel = new JLabel();
        locationLabel.setText("Save Captures At:");

        if(haveSettings) pathField.setText(settings[0]);

        fileSelectorPanel.add(locationLabel);
        fileSelectorPanel.add(pathField);

        JButton fileChooseButton = new JButton();
        fileChooseButton.setActionCommand("chooseFile");
        fileChooseButton.setText("...");
        fileChooseButton.addActionListener(new DialogControlAction());
        fileSelectorPanel.add(fileChooseButton);

        {
            fileSelectorPanelLayout.putConstraint("West", locationLabel, 6, "West", fileSelectorPanel);
            fileSelectorPanelLayout.putConstraint("North", locationLabel, 6, "North", fileSelectorPanel);
            fileSelectorPanelLayout.putConstraint("West", pathField, 6, "East", locationLabel);
            fileSelectorPanelLayout.putConstraint("North", pathField, 6, "North", fileChooseButton);
            fileSelectorPanelLayout.putConstraint("West", fileChooseButton, 6, "East", pathField);
            fileSelectorPanelLayout.putConstraint("North", fileChooseButton, 6, "North", fileSelectorPanel);
            fileSelectorPanelLayout.putConstraint("East", fileSelectorPanel, 6, "East", fileChooseButton);
            fileSelectorPanelLayout.putConstraint("South", fileSelectorPanel, 6, "South", fileChooseButton);
        }

        jFrame.add(fileSelectorPanel);


        JPanel buttonsPanel = new JPanel();

        JButton saveButton = new JButton();
        saveButton.setActionCommand("save");
        saveButton.setText("Save");

        JButton cancelButton = new JButton();
        cancelButton.setText("Cancel");

        cancelButton.addActionListener(new DialogControlAction());
        saveButton.addActionListener(new DialogControlAction());
        buttonsPanel.add(showAgain);
        buttonsPanel.add(cancelButton);
        buttonsPanel.add(saveButton);
        jFrame.add(buttonsPanel);


        jFrame.setVisible(true);
    }

    class DialogControlAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand() == "save") {
                // ensure file location is valid
                if(!new File(pathField.getText()).exists()) 
                jFrame.setVisible(false);
                System.out.println("Saving Settings...");
                saveSettings(pathField.getText(), showAgain.isSelected());
            } else if(e.getActionCommand() == "chooseFile"){
                JFileChooser j = new JFileChooser(System.getProperty("user.home"));
                j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if(j.showDialog(jFrame, "Select") == JFileChooser.APPROVE_OPTION)
                    pathField.setText(j.getSelectedFile().getAbsolutePath());
            } else {

                if(haveSettings) jFrame.setVisible(false);
                else JOptionPane.showMessageDialog(jFrame,
                        "Cannot start without any settings!",
                        "Error", JOptionPane.WARNING_MESSAGE);
            }

        }
    }

    private static boolean saveSettings(String text, boolean selected){

        try {
            FileWriter writer = new FileWriter(settingsFile);
            BufferedWriter out = new BufferedWriter(writer);
            out.write(text + "\n" + selected); // awful but not my problem for now
            out.close();
        } catch (Exception e){//Catch exception if any
            throw new RuntimeException("Error saving settings file");
        }
        ft.run();
        return true;
    }

    public void getCurrentGame() {
       while(true) {
           ProcessHandle.allProcesses().forEach(process -> {
           String game = Arrays.stream(SupportedGames).filter(process.info().command().get()::contains).findAny().get();
               if (!game.isEmpty()) {
                   System.out.println("Found "+game);
                   currentGame = game;
               }
           });
           if(!currentGame.isEmpty()) break;
           try {Thread.sleep(2000);} catch (InterruptedException e) { throw new RuntimeException(e); }
       }
    }
}

