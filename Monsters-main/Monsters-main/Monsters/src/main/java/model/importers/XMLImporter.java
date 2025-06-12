
package model.importers;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.util.*;
import model.Monster;

public class XMLImporter implements FileImporter {
    private FileImporter next;

    @Override
    public void setNext(FileImporter next) {
        this.next = next;
    }

    @Override
    public List<Monster> importFile(File file) throws Exception {
        if (canHandle(file)) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);

            NodeList creatureNodes = document.getElementsByTagName("creature");
            List<Monster> monsters = new ArrayList<>();

            for (int i = 0; i < creatureNodes.getLength(); i++) {
                Node creatureNode = creatureNodes.item(i);
                if (creatureNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element creatureElement = (Element) creatureNode;
                    Monster monster = parseCreatureElement(creatureElement);
                    monsters.add(monster);
                }
            }
            return monsters;
        } else if (next != null) {
            return next.importFile(file);
        } else {
            throw new UnsupportedOperationException("Unsupported file format: " + file.getName());
        }
    }

    @Override
    public boolean canHandle(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".xml");
    }

    private Monster parseCreatureElement(Element creatureElement) {
        Monster monster = new Monster();

        monster.setName(getElementText(creatureElement, "name"));
        monster.setDescription(getElementText(creatureElement, "description"));
        monster.setDangerLevel(Integer.parseInt(getElementText(creatureElement, "danger_level")));

        NodeList habitatNodes = creatureElement.getElementsByTagName("region");
        for (int i = 0; i < habitatNodes.getLength(); i++) {
            monster.addHabitat(habitatNodes.item(i).getTextContent());
        }

        String firstMentioned = getElementText(creatureElement, "first_mentioned");
        try {
            monster.setFirstMentioned(firstMentioned);
        } catch (Exception e) {
        }

        NodeList vulnerabilityNodes = creatureElement.getElementsByTagName("vulnerability");
        for (int i = 0; i < vulnerabilityNodes.getLength(); i++) {
            monster.addVulnerability(vulnerabilityNodes.item(i).getTextContent());
        }

        NodeList parameterNodes = creatureElement.getElementsByTagName("parameters");
        if (parameterNodes.getLength() > 0) {
            Element parametersElement = (Element) parameterNodes.item(0);
            String height = getElementText(parametersElement, "height");
            String weight = getElementText(parametersElement, "weight");
            if (height != null) {
                monster.setParameter("height", height);
            }
            if (weight != null) {
                monster.setParameter("weight", weight);
            }
        }

        NodeList immunityNodes = creatureElement.getElementsByTagName("immunity");
        for (int i = 0; i < immunityNodes.getLength(); i++) {
            monster.addImmunity(immunityNodes.item(i).getTextContent());
        }

        monster.setActivity(getElementText(creatureElement, "activity"));

        NodeList recipeNodes = creatureElement.getElementsByTagName("recipe");
        if (recipeNodes.getLength() > 0) {
            Element recipeElement = (Element) recipeNodes.item(0);
            NodeList ingredientNodes = recipeElement.getElementsByTagName("ingredient");
            for (int i = 0; i < ingredientNodes.getLength(); i++) {
                Element ingredientElement = (Element) ingredientNodes.item(i);
                String name = ingredientElement.getTextContent();
                String quantity = ingredientElement.getAttribute("quantity");
                monster.addIngredient(name, Integer.parseInt(quantity));
            }

            String prepTime = getElementText(recipeElement, "prep_time");
            String effectiveness = getElementText(recipeElement, "effectiveness");
            monster.setRecipeParams(prepTime, effectiveness);
        }

        return monster;
    }

    private String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return null;
    }
}
