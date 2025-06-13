package model.exporters;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;
import java.io.File;
import java.util.List;
import java.util.Map;
import model.Monster;


public class XMLExporter {
    public void export(List<Monster> monsters, File outputFile) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("creatures");
        doc.appendChild(rootElement);

        for (Monster monster : monsters) {
            Element monsterElement = doc.createElement("monster");
            rootElement.appendChild(monsterElement);
            appendTextElement(doc, monsterElement, "name", monster.getName());
            appendTextElement(doc, monsterElement, "description", monster.getDescription());
            appendTextElement(doc, monsterElement, "danger_level", String.valueOf(monster.getDangerLevel()));
            appendTextElement(doc, monsterElement, "source", monster.getSource());
            Element habitatsElement = doc.createElement("habitats");
            monsterElement.appendChild(habitatsElement);
            for (String habitat : monster.getHabitats()) {
                appendTextElement(doc, habitatsElement, "habitat", habitat);
            }
            String dateStr = (monster.getFirstMentioned() != null)
                    ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(monster.getFirstMentioned()) : "";
            appendTextElement(doc, monsterElement, "first_mentioned", dateStr);

            Element vulnerabilitiesElement = doc.createElement("vulnerabilities");
            monsterElement.appendChild(vulnerabilitiesElement);
            for (String vuln : monster.getVulnerabilities()) {
                appendTextElement(doc, vulnerabilitiesElement, "vulnerability", vuln);
            }

            Element recipeElement = doc.createElement("recipe");
            monsterElement.appendChild(recipeElement);
            for (Map<String, Object> ing : monster.getRecipe()) {
                Element ingElement = doc.createElement("ingredient");
                recipeElement.appendChild(ingElement);

                Object nameObj = ing.get("name");
                String name = nameObj != null ? nameObj.toString() : "";
                appendTextElement(doc, ingElement, "name", name);

                Object quantityObj = ing.get("quantity");
                String quantity = quantityObj != null ? quantityObj.toString() : "";
                appendTextElement(doc, ingElement, "quantity", quantity);            }
            appendTextElement(doc, recipeElement, "prep_time", monster.getParameter("prep_time"));
            appendTextElement(doc, recipeElement, "effectiveness", monster.getParameter("effectiveness"));
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(outputFile);

        transformer.transform(source, result);
    }

    private void appendTextElement(Document doc, Element parent, String tagName, String textContent) {
        Element elem = doc.createElement(tagName);
        if (textContent != null) {
            elem.appendChild(doc.createTextNode(textContent));
        } else {
            elem.appendChild(doc.createTextNode(""));
        }
        parent.appendChild(elem);
    }
}