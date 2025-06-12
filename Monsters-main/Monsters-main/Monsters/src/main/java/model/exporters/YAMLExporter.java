/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.exporters;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.Monster;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class YAMLExporter {
    public void export(List<Monster> monsters, FileWriter fileWriter) throws IOException {
        Map<String, Object> data = new LinkedHashMap<>();
        List<Object> creaturesList = new ArrayList<>();

        for (Monster monster : monsters) {
            Map<String, Object> monMap = new LinkedHashMap<>();
            monMap.put("name", monster.getName());
            monMap.put("description", monster.getDescription());
            monMap.put("danger_level", monster.getDangerLevel());
            monMap.put("source", monster.getSource());
            monMap.put("habitats", monster.getHabitats());

            String firstMentioned = monster.getFirstMentioned() != null
                    ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(monster.getFirstMentioned()) : "";
            monMap.put("first_mentioned", firstMentioned);
            monMap.put("vulnerabilities", monster.getVulnerabilities());
            monMap.put("immunities", monster.getImmunities());
            monMap.put("activity", monster.getActivity());

            Map<String, Object> recipeData = new LinkedHashMap<>();
            recipeData.put("ingredients", monster.getRecipe());
            recipeData.put("prep_time", monster.getParameter("prep_time"));
            recipeData.put("effectiveness", monster.getParameter("effectiveness"));
            monMap.put("recipe", recipeData);

            creaturesList.add(monMap);
        }

        data.put("creatures", creaturesList);

        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Yaml yaml = new Yaml(options);
        yaml.dump(data, fileWriter);
        fileWriter.flush();
    }
}
