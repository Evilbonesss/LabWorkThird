package model.importers;

import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.Monster;
import org.yaml.snakeyaml.Yaml;

public class YAMLImporter implements FileImporter {
    private FileImporter next;

    @Override
    public void setNext(FileImporter next) {
        this.next = next;
    }

    @Override
    public List<Monster> importFile(File file) throws Exception {
        if (canHandle(file)) {
            Yaml yaml = new Yaml();
            try (FileInputStream inputStream = new FileInputStream(file)) {
                Map<String, List<Map<String, Object>>> data = yaml.load(inputStream);

                List<Monster> monsters = new ArrayList<>();
                List<Map<String, Object>> monstersData = data.get("creatures");
                for (Map<String, Object> monsterData : monstersData) {
                    Monster monster = parseMonsterFromMap(monsterData);
                    monsters.add(monster);
                }
                return monsters;
            }
        } else if (next != null) {
            return next.importFile(file);
        } else {
            throw new UnsupportedOperationException("Unsupported file format: " + file.getName());
        }
    }

    @Override
    public boolean canHandle(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".yaml") || name.endsWith(".yml");
    }

    private Monster parseMonsterFromMap(Map<String, Object> data) throws ParseException {
        Monster monster = new Monster();
        monster.setName((String) data.get("name"));
        monster.setDescription((String) data.get("description"));
        monster.setDangerLevel((int) data.get("danger_level"));

        if (data.containsKey("habitats")) {
            List<String> habitats = (List<String>) data.get("habitats");
            monster.setHabitats(habitats);
        }

        if (data.containsKey("first_mentioned")) {
            Object firstMentionedObj = data.get("first_mentioned");
            if (firstMentionedObj instanceof java.util.Date) {
                monster.setFirstMentioned((java.util.Date) firstMentionedObj);
            } else if (firstMentionedObj != null) {
                monster.setFirstMentioned(firstMentionedObj.toString());
            }
        }

        if (data.containsKey("vulnerabilities")) {
            List<String> vulnerabilities = (List<String>) data.get("vulnerabilities");
            monster.setVulnerabilities(vulnerabilities);
        }

        if (data.containsKey("parameters")) {
            Map<String, String> parameters = (Map<String, String>) data.get("parameters");
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                monster.setParameter(entry.getKey(), entry.getValue());
            }
        }

        if (data.containsKey("immunities")) {
            List<String> immunities = (List<String>) data.get("immunities");
            monster.setImmunities(immunities);
        }

        if (data.containsKey("activity")) {
            monster.setActivity((String) data.get("activity"));
        }

        if (data.containsKey("recipe")) {
            Map<String, Object> recipeData = (Map<String, Object>) data.get("recipe");
            if (recipeData.containsKey("ingredients")) {
                List<Map<String, Object>> ingredients = (List<Map<String, Object>>) recipeData.get("ingredients");                for (Map<String, Object> ingredient : ingredients) {
                    String name = (String) ingredient.get("name");
                    int quantity = (int) ingredient.get("quantity");
                    monster.addIngredient(name, quantity);
                }
            }
            monster.setRecipeParams(
                    (String) recipeData.get("prep_time"),
                    (String) recipeData.get("effectiveness")
            );
        }
        return monster;
    }
}