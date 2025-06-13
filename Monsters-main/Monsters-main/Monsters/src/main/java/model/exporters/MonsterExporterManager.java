package model.exporters;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import javax.swing.*;
import model.Monster;
import model.MonsterStorage;

public class MonsterExporterManager {

    public MonsterExporterManager(MonsterStorage storage) {
    }

    public void exportData(JFrame parent, List<Monster> monsters) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Сохранить как");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        javax.swing.filechooser.FileNameExtensionFilter jsonFilter = new javax.swing.filechooser.FileNameExtensionFilter("JSON Files", "json");
        javax.swing.filechooser.FileNameExtensionFilter xmlFilter = new javax.swing.filechooser.FileNameExtensionFilter("XML Files", "xml");
        javax.swing.filechooser.FileNameExtensionFilter yamlFilter = new javax.swing.filechooser.FileNameExtensionFilter("YAML Files", "yaml", "yml");
        fileChooser.addChoosableFileFilter(jsonFilter);
        fileChooser.addChoosableFileFilter(xmlFilter);
        fileChooser.addChoosableFileFilter(yamlFilter);

        int returnValue = fileChooser.showSaveDialog(parent);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            javax.swing.filechooser.FileFilter selectedFilter = fileChooser.getFileFilter();

            String extension = getFileExtension(selectedFile.getName());
            String expectedExtension = "";

            if (selectedFilter == jsonFilter) {
                expectedExtension = "json";
            } else if (selectedFilter == xmlFilter) {
                expectedExtension = "xml";
            } else if (selectedFilter == yamlFilter) {
                expectedExtension = "yaml";
            }

            if (!extension.equalsIgnoreCase(expectedExtension)) {
                selectedFile = new File(selectedFile.getAbsolutePath() + "." + expectedExtension);
            }

            try {
                switch (expectedExtension) {
                    case "json":
                        new JSONExporter().export(monsters, selectedFile);
                        break;
                    case "xml":
                        new XMLExporter().export(monsters, selectedFile);
                        break;
                    case "yaml":
                        try (FileWriter writer = new FileWriter(selectedFile)) {
                            new YAMLExporter().export(monsters, writer);
                        }
                        break;
                    default:
                        JOptionPane.showMessageDialog(parent, "Неподдерживаемый формат файла.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                }
                JOptionPane.showMessageDialog(parent, "Экспорт завершён.", "Успех", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(parent, "Ошибка экспорта: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String getFileExtension(String fileName) {
        int lastIndexOfDot = fileName.lastIndexOf('.');
        if (lastIndexOfDot == -1) {
            return "";
        }
        return fileName.substring(lastIndexOfDot + 1).toLowerCase();
    }
}
