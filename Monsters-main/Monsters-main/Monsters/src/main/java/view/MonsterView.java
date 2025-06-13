package view;

import controller.MonsterController;
import model.Monster;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class MonsterView extends JFrame {
    private final MonsterController controller;
    private DefaultListModel<String> sourcesModel;
    private JList<String> sourcesList;
    private DefaultListModel<Monster> monsterListModel;
    private JList<Monster> monsterList;
    private JTextArea infoArea;
    private JTextArea descriptionArea;
    private JButton saveButton;

    public MonsterView(MonsterController controller) {
        this.controller = controller;
        setTitle("Bestiarum");
        setSize(1050, 680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10,10));
        initializeUI();
    }

    private void initializeUI() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        JButton importButton = new JButton("Импорт");
        JButton exportButton = new JButton("Экспорт");
        topPanel.add(importButton);
        topPanel.add(exportButton);
        add(topPanel, BorderLayout.NORTH);

        JPanel leftPanel = new JPanel(new BorderLayout(8, 8));
        sourcesModel = new DefaultListModel<>();
        sourcesList = new JList<>(sourcesModel);
        sourcesList.setBorder(BorderFactory.createTitledBorder("Файлы-источники"));
        sourcesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        monsterListModel = new DefaultListModel<>();
        monsterList = new JList<>(monsterListModel);
        monsterList.setBorder(BorderFactory.createTitledBorder("Чудовища"));
        monsterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JSplitPane listsSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(sourcesList),
                new JScrollPane(monsterList)
        );
        listsSplit.setDividerLocation(180);

        leftPanel.add(listsSplit, BorderLayout.CENTER);
        add(leftPanel, BorderLayout.WEST);

        JTabbedPane tabbedPane = new JTabbedPane();

        infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        JScrollPane infoScroll = new JScrollPane(infoArea);
        infoScroll.setBorder(new EmptyBorder(10,10,10,10));
        tabbedPane.addTab("Информация", infoScroll);

        JPanel editPanel = new JPanel(new BorderLayout(8,8));
        descriptionArea = new JTextArea(6, 40);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setBorder(BorderFactory.createTitledBorder("Описание"));
        saveButton = new JButton("Сохранить описание");
        editPanel.add(descScroll, BorderLayout.CENTER);
        editPanel.add(saveButton, BorderLayout.SOUTH);

        tabbedPane.addTab("Редактировать", editPanel);

        add(tabbedPane, BorderLayout.CENTER);

        importButton.addActionListener(e -> controller.importFiles(this, monsters -> {
            updateSourcesList();
            JOptionPane.showMessageDialog(this,
                    "Импортировано " + monsters.size() + " чудовищ",
                    "Импорт завершен", JOptionPane.INFORMATION_MESSAGE);
        }));
        exportButton.addActionListener(e -> exportSelectedSource());

        sourcesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateMonsterList();
            }
        });
        monsterList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Monster m = monsterList.getSelectedValue();
                if (m != null) {
                    showMonsterInfo(m);
                    descriptionArea.setText(m.getDescription() == null ? "" : m.getDescription());
                }
            }
        });

        saveButton.addActionListener(e -> {
            Monster m = monsterList.getSelectedValue();
            if (m != null) {
                m.setDescription(descriptionArea.getText());
                showMonsterInfo(m);
                JOptionPane.showMessageDialog(this, "Описание сохранено", "Сохранение", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        updateSourcesList();
    }

    private void updateSourcesList() {
        sourcesModel.clear();
        List<Monster> all = controller.getAllMonsters();
        java.util.Set<String> uniqueSources = new java.util.TreeSet<>();
        for (Monster m : all) {
            if (m.getSource() != null) uniqueSources.add(m.getSource());
        }
        for (String src : uniqueSources) sourcesModel.addElement(src);
        if (!uniqueSources.isEmpty()) sourcesList.setSelectedIndex(0);
        updateMonsterList();
    }

    private void updateMonsterList() {
        monsterListModel.clear();
        String selectedSource = sourcesList.getSelectedValue();
        if (selectedSource == null) return;
        List<Monster> monsters = controller.getMonstersBySource(selectedSource);
        for (Monster m : monsters) monsterListModel.addElement(m);
        if (!monsters.isEmpty()) monsterList.setSelectedIndex(0);
    }

    private void showMonsterInfo(Monster m) {
        StringBuilder sb = new StringBuilder();
        sb.append("Имя: ").append(m.getName()).append('\n');
        sb.append("Описание: ").append(m.getDescription()).append("\n\n");
        sb.append("Уровень опасности: ").append(m.getDangerLevel()).append('\n');
        sb.append("Источник: ").append(nonNullOr(m.getSource())).append('\n');
        sb.append("Места обитания: ").append(listOrEmpty(m.getHabitats())).append('\n');
        sb.append("Впервые упомянут: ").append(m.getFirstMentionedAsString()).append('\n');
        sb.append("Чувствительность: ").append(listOrEmpty(m.getVulnerabilities())).append('\n');
        sb.append("Иммунитет: ").append(listOrEmpty(m.getImmunities())).append('\n');
        sb.append("Активность: ").append(nonNullOr(m.getActivity())).append("\n\n");
        sb.append("Параметры:\n");
        Map<String, String> params = m.getParameters();
        if (params.isEmpty()) sb.append("  — нет —\n");
        else for (Map.Entry<String, String> en : params.entrySet())
            sb.append("  ").append(en.getKey()).append(": ").append(en.getValue()).append('\n');
        sb.append('\n');
        sb.append("Рецепт масла:\n");
        List<Map<String,Object>> ings = m.getRecipe();
        if (ings.isEmpty()) sb.append("  — нет —\n");
        else for (Map<String,Object> ing : ings)
            sb.append("  - ").append(ing.get("name"))
                    .append(": ").append(ing.get("quantity"))
                    .append('\n');
        sb.append("  Эффективность: ").append(nonNullOr(m.getParameter("effectiveness"))).append('\n');
        sb.append("  Время приготовления: ").append(nonNullOr(m.getParameter("prep_time"))).append('\n');
        infoArea.setText(sb.toString());
        infoArea.setCaretPosition(0);
    }

    private void exportSelectedSource() {
        String src = sourcesList.getSelectedValue();
        if (src == null) {
            JOptionPane.showMessageDialog(this, "Выберите файл-источник", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        List<Monster> monsters = controller.getMonstersBySource(src);
        controller.exportMonsters(this, monsters);
    }

    private String nonNullOr(String s) { return (s == null || s.isEmpty()) ? "не указано" : s; }

    private String listOrEmpty(List<String> l) {
        return (l == null || l.isEmpty()) ? "не указано" : String.join(", ", l);
    }
}
