package model.exporters;

import model.Monster; 

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONExporter {
    private final ObjectMapper mapper;

    public JSONExporter() {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void export(List<Monster> monsters, File outputFile) throws IOException {
        Map<String, Object> root = new HashMap<>();
        root.put("creatures", convertMonstersToMapList(monsters));
        
        mapper.writeValue(outputFile, root);
    }

    private List<Map<String, Object>> convertMonstersToMapList(List<Monster> monsters) {
        List<Map<String, Object>> exportData = new ArrayList<>();
        
        for (Monster monster : monsters) {
            Map<String, Object> monsterData = new HashMap<>();
            monsterData.put("name", monster.getName());
            monsterData.put("description", monster.getDescription());
            monsterData.put("danger_level", monster.getDangerLevel());
            monsterData.put("source", monster.getSource());
            monsterData.put("habitats", monster.getHabitats());
            monsterData.put("first_mentioned", monster.getFirstMentionedAsString());
            monsterData.put("vulnerabilities", monster.getVulnerabilities());
            monsterData.put("immunities", monster.getImmunities());
            monsterData.put("activity", monster.getActivity());

            if (monster.getRecipe() != null) {
                Map<String, Object> recipeData = new HashMap<>();
                recipeData.put("ingredients", monster.getRecipe());
                recipeData.put("prep_time", monster.getParameter("prep_time"));
                recipeData.put("effectiveness", monster.getParameter("effectiveness"));
                monsterData.put("recipe", recipeData);
            }

            exportData.add(monsterData);
        }
        
        return exportData;
    }
}
