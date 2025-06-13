package controller;

import model.Monster;
import model.MonsterStorage;
import model.importers.FileImporter;
import model.importers.JSONImporter;
import model.importers.XMLImporter;
import model.importers.YAMLImporter;
import model.exporters.MonsterExporterManager;

import javax.swing.*;
import java.io.File;
import java.util.*;
import java.util.function.Consumer;

public class MonsterController {
    private final MonsterStorage storage = new MonsterStorage();
    private final FileImporter fileImporter = createImporterChain();
    private final Map<File, List<UUID>> fileMonsterMap = new HashMap<>();

    public void importFiles(JFrame parent, Consumer<List<Monster>> onSuccess) {
        JFileChooser fileChooser = createFileChooser();
        if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            for (File file : fileChooser.getSelectedFiles()) {
                importFile(file, parent, onSuccess);
            }
        }
    }

    public void importFile(File file, JFrame parent, Consumer<List<Monster>> onSuccess) {
        try {
            List<Monster> parsed = fileImporter.importFile(file);
            List<Monster> added = new ArrayList<>();
            List<UUID> ids = new ArrayList<>();

            for (Monster monster : parsed) {
                monster.setSource(file.getName());
                if (storage.addMonster(monster, file)) { // true if not a duplicate
                    added.add(monster);
                    ids.add(monster.getId());
                }
            }

            if (!ids.isEmpty()) {
                fileMonsterMap.put(file, ids);
            }

            if (!added.isEmpty()) {
                onSuccess.accept(added);
            } else {
                JOptionPane.showMessageDialog(parent,
                        "Все существа из '" + file.getName() + "' уже существуют.",
                        "Дубликаты", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent,
                    "Failed to import " + file.getName() + ": " + e.getMessage(),
                    "Import Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void exportMonsters(JFrame parent, List<Monster> monsters) {
        new MonsterExporterManager(storage).exportData(parent, monsters);
    }

    public List<Monster> getAllMonsters() { return storage.getMonsters(); }
    public List<Monster> getMonstersBySource(String source) { return storage.getMonstersBySource(source); }

    private FileImporter createImporterChain() {
        JSONImporter json = new JSONImporter();
        XMLImporter xml = new XMLImporter();
        YAMLImporter yaml = new YAMLImporter();
        json.setNext(xml);
        xml.setNext(yaml);
        return json;
    }

    private JFileChooser createFileChooser() {
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fc.setMultiSelectionEnabled(true);
        fc.setDialogTitle("Select Files to Import");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JSON, XML, YAML Files", "json", "xml", "yaml", "yml"));
        return fc;
    }
}
