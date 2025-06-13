package model.importers.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import model.Monster;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MonsterDTO {
    public String name;
    public String description;
    @JsonProperty("danger_level")
    public int dangerLevel;
    public List<String> habitats;
    @JsonProperty("first_mentioned")
    public String firstMentioned;
    public List<String> vulnerabilities;
    public Map<String, String> parameters;
    public List<String> immunities;
    public String activity;
    public RecipeDTO recipe;

    public Monster toMonster() throws ParseException {
        Monster m = new Monster();
        m.setName(name);
        m.setDescription(description);
        m.setDangerLevel(dangerLevel);
        if (habitats != null) m.setHabitats(habitats);
        if (firstMentioned != null) m.setFirstMentioned(firstMentioned);
        if (vulnerabilities != null) m.setVulnerabilities(vulnerabilities);
        if (parameters != null) {
            for (Map.Entry<String,String> e : parameters.entrySet()) {
                m.setParameter(e.getKey(), e.getValue());
            }
        }
        if (immunities != null) m.setImmunities(immunities);
        if (activity != null) m.setActivity(activity);
        if (recipe != null) {
            if (recipe.ingredients != null) {
                for (IngredientDTO ing : recipe.ingredients) {
                    m.addIngredient(ing.name, ing.quantity);
                }
            }
            m.setRecipeParams(recipe.prep_time, recipe.effectiveness);
        }
        return m;
    }
}
