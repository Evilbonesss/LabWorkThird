package view;

import controller.MonsterController;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Monster;


public class MonsterView extends JFrame {
    
    private final MonsterController controller;
    private JTree monsterTree;
    private JTextArea monsterDetails;
    private JTextArea descriptionArea;
    private JButton saveButton;
    private JLabel descriptionLabel;


    public MonsterView(MonsterController controller) {
        this.controller = controller;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Bestiarum");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(320);
        
        monsterTree = new JTree();
        monsterTree.setOpaque(true);

        JScrollPane treeScroll = new JScrollPane(monsterTree);
        splitPane.setLeftComponent(treeScroll);

        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        monsterDetails = new JTextArea();
        monsterDetails.setEditable(false);
        monsterDetails.setLineWrap(true);
        monsterDetails.setWrapStyleWord(true);
        JScrollPane detailsScroll = new JScrollPane(monsterDetails);
        detailsScroll.setPreferredSize(new Dimension(520, 320));
        rightPanel.add(detailsScroll, BorderLayout.CENTER);

        JPanel descriptionPanel = new JPanel(new BorderLayout(5,5));
        descriptionLabel = new JLabel("Отредактируйте поле:");
        descriptionArea = new JTextArea(4, 40);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionPanel.add(descriptionLabel, BorderLayout.NORTH);
        descriptionPanel.add(descriptionScroll, BorderLayout.CENTER);

        saveButton = new JButton("Сохранить изменения");
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(e -> saveChanges());
        descriptionPanel.add(saveButton, BorderLayout.SOUTH);

        rightPanel.add(descriptionPanel, BorderLayout.SOUTH);
        splitPane.setRightComponent(rightPanel);
        
        JPanel buttonPanel = new JPanel();
        JButton importButton = new JButton("Импорт");

        importButton.setFocusPainted(false);
        importButton .addActionListener(e -> importFiles());
        buttonPanel.add(importButton);

        JButton exportButton = new JButton("Экспорт");
        exportButton.setFocusPainted(false);
        exportButton.addActionListener(e -> exportAll());
        buttonPanel.add(exportButton);

        getContentPane().setLayout(new BorderLayout(10,10));
        getContentPane().add(splitPane, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        updateTree();
    }

    private void importFiles() {
        controller.importFiles(this, monsters -> {
            updateTree();
            JOptionPane.showMessageDialog(this,
                    "Импортировано " + monsters.size() + " чудовищ",
                    "Импорт завершен", JOptionPane.INFORMATION_MESSAGE);
        });
    }    
    
    private void exportAll() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) monsterTree.getLastSelectedPathComponent();
        if (selectedNode != null && selectedNode.getUserObject() instanceof Monster) {
            Monster selectedMonster = (Monster) selectedNode.getUserObject();
            List<Monster> monsters = controller.getMonstersBySource(selectedMonster.getSource());
            controller.exportMonsters(this, monsters); 
        } else {
            JOptionPane.showMessageDialog(this, "Пожалуйста, выберите файл для экспорта", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Чудовища");

        Map<String, List<Monster>> monstersBySource = new HashMap<>();
        for (Monster monster : controller.getAllMonsters()) {
            monstersBySource.computeIfAbsent(monster.getSource(), k -> new ArrayList<>()).add(monster);
        }

        for (Map.Entry<String, List<Monster>> entry : monstersBySource.entrySet()) {
            DefaultMutableTreeNode sourceNode = new DefaultMutableTreeNode(entry.getKey());
            for (Monster monster : entry.getValue()) {
                sourceNode.add(new DefaultMutableTreeNode(monster));
            }
            root.add(sourceNode);
        }

        monsterTree.setModel(new DefaultTreeModel(root));
 

        monsterTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) monsterTree.getLastSelectedPathComponent();
                if (node != null && node.getUserObject() instanceof Monster) {
                    showMonsterDetails((Monster) node.getUserObject());
                }
            }
        });
    }

    private void showMonsterDetails(Monster monster) {
        StringBuilder sb = new StringBuilder();
        sb.append("Имя: ").append(monster.getName()).append("\n");
        sb.append("Описание: ").append(monster.getDescription()).append("\n\n");
        sb.append("Уровень опасности: ").append(monster.getDangerLevel()).append("\n");
        sb.append("Источник: ").append(nonNullOrDefault(monster.getSource(), "не указано")).append("\n");

        sb.append("Места обитания: ");
        List<String> habitats = monster.getHabitats();
        sb.append(habitats != null && !habitats.isEmpty() ? String.join(", ", habitats) : "не указано").append("\n");

        sb.append("Впервые упомянут: ").append(formatDate(monster.getFirstMentioned())).append("\n");

        sb.append("Чувствительность: ");
        List<String> vulnerabilities = monster.getVulnerabilities();
        sb.append(vulnerabilities != null && !vulnerabilities.isEmpty() ? String.join(", ", vulnerabilities) : "не указано").append("\n");

        sb.append("Иммунитет: ");
        List<String> immunities = monster.getImmunities();
        sb.append(immunities != null && !immunities.isEmpty() ? String.join(", ", immunities) : "не указано").append("\n");

        sb.append("Активность: ").append(nonNullOrDefault(monster.getActivity(), "не указано")).append("\n\n");

        sb.append("Параметры:\n");
            Map<String, String> params = monster.getParameters();
            if (params != null && !params.isEmpty()) {
            String height = params.get("height"); 
            String weight = params.get("weight");

            if (height != null) {
                sb.append("  Рост: ").append(height).append("\n");
            } else {
                sb.append("  Рост: не указано\n");
            }

            if (weight != null) {
                sb.append("  Вес: ").append(weight).append("\n");
            } else {
                sb.append("  Вес: не указано\n");
            }
        } else {
            sb.append("  не указано\n");
        }
        sb.append("\n");

        sb.append("Рецепт масла:\n");
        List<Map<String, Object>> ingredients = monster.getRecipe();
        if (ingredients != null && !ingredients.isEmpty()) {
            for (Map<String, Object> ingredient : ingredients) {
                String name = (String) ingredient.get("name");
                Object quantityObj = ingredient.get("quantity");
                String quantity = quantityObj != null ? quantityObj.toString() : "0";
                sb.append("  - ").append(name).append(": ").append(quantity).append("\n");
            }
        } else {
            sb.append("  не указано\n");
        }
        sb.append("  Эффективность: ").append(nonNullOrDefault(monster.getParameter("effectiveness"), "не указано")).append("\n");
        sb.append("  Время приготовления: ").append(nonNullOrDefault(monster.getParameter("prep_time"), "не указано")).append("\n");
        monsterDetails.setText(sb.toString());
        monsterDetails.setCaretPosition(0);
        descriptionArea.setText(monster.getDescription());
    }

    private void saveChanges() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) monsterTree.getLastSelectedPathComponent();
        if (node != null && node.getUserObject() instanceof Monster) {
            Monster monster = (Monster) node.getUserObject();
            monster.setDescription(descriptionArea.getText());
            JOptionPane.showMessageDialog(this, "Изменения сохранены", "Сохранение", JOptionPane.INFORMATION_MESSAGE);
            showMonsterDetails(monster);
        }
    }

    private String formatDate(Date date) {
        if (date == null) return "N/A";
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy");
        return outputFormat.format(date);
    }

    private String nonNullOrDefault(String str, String def) {
        return (str != null && !str.isEmpty()) ? str : def;
    }
}