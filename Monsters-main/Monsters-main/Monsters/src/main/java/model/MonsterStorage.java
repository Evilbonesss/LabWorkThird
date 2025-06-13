package model;

import java.io.File;
import java.util.*;

public class MonsterStorage {
    private final Map<String, Monster> monsterByKey = new LinkedHashMap<>();
    private final Map<String, List<Monster>> monstersBySource = new HashMap<>();
    private final Map<File, List<Monster>> monstersByFile = new HashMap<>();

    private String buildKey(String name, String source) {
        return (name == null ? "" : name.toLowerCase()) + "|" + (source == null ? "" : source);
    }

    public boolean addMonster(Monster monster, File sourceFile) {
        if (monster == null || sourceFile == null) return false;
        String key = buildKey(monster.getName(), monster.getSource());
        if (monsterByKey.containsKey(key)) return false; // duplicate – skip

        monsterByKey.put(key, monster);

        monstersBySource.computeIfAbsent(monster.getSource(), k -> new ArrayList<>()).add(monster);
        monstersByFile.computeIfAbsent(sourceFile, k -> new ArrayList<>()).add(monster);
        return true;
    }

    public List<Monster> getMonsters() {
        return Collections.unmodifiableList(new ArrayList<>(monsterByKey.values()));
    }

    public Optional<Monster> getMonsterById(UUID id) {
        return monsterByKey.values().stream().filter(m -> m.getId().equals(id)).findFirst();
    }

    public List<Monster> getMonstersBySource(String source) {
        return monstersBySource.getOrDefault(source, Collections.emptyList());
    }

    public boolean updateMonster(UUID id, Monster newData) {
        Optional<Monster> optExisting = getMonsterById(id);
        if (optExisting.isEmpty()) return false;

        Monster monster = optExisting.get();
        String oldKey = buildKey(monster.getName(), monster.getSource());
        String oldSource = monster.getSource();

        monster.setName(newData.getName());
        monster.setDescription(newData.getDescription());
        monster.setDangerLevel(newData.getDangerLevel());
        monster.setHabitats(newData.getHabitats());
        monster.setFirstMentioned(newData.getFirstMentioned());
        monster.setVulnerabilities(newData.getVulnerabilities());
        monster.setImmunities(newData.getImmunities());
        monster.setActivity(newData.getActivity());

        monster.getParameters().clear();
        monster.getParameters().putAll(newData.getParameters());

        monster.getRecipe().clear();
        monster.getRecipe().addAll(newData.getRecipe());

        monster.setSource(newData.getSource());
        String newSource = monster.getSource();

        if (!Objects.equals(oldSource, newSource)) {
            monstersBySource.getOrDefault(oldSource, Collections.emptyList()).remove(monster);
            monstersBySource.computeIfAbsent(newSource, k -> new ArrayList<>()).add(monster);
        }

        String newKey = buildKey(monster.getName(), monster.getSource());
        if (!oldKey.equals(newKey)) {
            monsterByKey.remove(oldKey);
            monsterByKey.put(newKey, monster);
        }
        return true;
    }
}