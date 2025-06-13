package model;

import java.io.File;
import java.util.*;


public class MonsterStorage {
    private List<Monster> monsters = new ArrayList<>();
    private Map<String, List<Monster>> monstersBySource = new HashMap<>();
    private Map<File, List<Monster>> monstersByFile = new HashMap<>();

    public void addMonster(Monster monster, File sourceFile) {
        if (monster != null && sourceFile != null) {
            monsters.add(monster);
            if (!monstersBySource.containsKey(monster.getSource())) {
                monstersBySource.put(monster.getSource(), new ArrayList<>());
            }
            monstersBySource.get(monster.getSource()).add(monster);
            if (!monstersByFile.containsKey(sourceFile)) {
                monstersByFile.put(sourceFile, new ArrayList<>());
            }
            monstersByFile.get(sourceFile).add(monster);
        }
    }

    public List<Monster> getMonsters() {
        return Collections.unmodifiableList(monsters);
    }

    public Optional<Monster> getMonsterById(UUID id) {
        return monsters.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst();
    }
    
    public List<Monster> getMonstersBySource(String source) {
        List<Monster> filteredMonsters = new ArrayList<>();
        for (Monster monster : monsters) {
            if (monster.getSource() != null && monster.getSource().equals(source)) {
                filteredMonsters.add(monster);
            }
        }
        return filteredMonsters;
    }
    
    public boolean updateMonster(UUID id, Monster newData) {
        Optional<Monster> existing = getMonsterById(id);

        if (existing.isPresent()) {
            Monster monster = existing.get();
            String originalSource = monster.getSource();

            monster.setName(newData.getName());
            monster.setDescription(newData.getDescription());
            monster.setDangerLevel(newData.getDangerLevel());
            monster.setHabitats(newData.getHabitats());
            monster.setFirstMentioned(newData.getFirstMentioned());
            monster.setVulnerabilities(newData.getVulnerabilities());
            monster.setImmunities(newData.getImmunities());
            monster.setActivity(newData.getActivity());

            monster.getParameters().clear();
            for (Map.Entry<String, String> entry : newData.getParameters().entrySet()) {
                monster.getParameters().put(entry.getKey(), entry.getValue());
            }

            monster.getRecipe().clear();
            for (Map<String, Object> ingredient : newData.getRecipe()) {
                monster.getRecipe().add(ingredient);
            }
            String newSource = newData.getSource();
            if (!originalSource.equals(newSource)) {
                List<Monster> originalList = monstersBySource.get(originalSource);
                if (originalList != null) {
                    originalList.remove(monster);
                }

                if (!monstersBySource.containsKey(newSource)) {
                    monstersBySource.put(newSource, new ArrayList<>());
                }
                monstersBySource.get(newSource).add(monster);
                monster.setSource(newSource);
            }

            return true;
        }

        return false;
    }
}