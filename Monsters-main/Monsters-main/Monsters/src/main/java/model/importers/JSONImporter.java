package model.importers;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import model.Monster;
import model.importers.dto.MonsterDTO;
import model.importers.dto.MonstersRoot;

public class JSONImporter implements FileImporter {
    private FileImporter next;

    @Override
    public void setNext(FileImporter next) {
        this.next = next;
    }

    @Override
    public List<Monster> importFile(File file) throws Exception {
        if (canHandle(file)) {
            ObjectMapper mapper = new ObjectMapper();
            MonstersRoot root = mapper.readValue(file, MonstersRoot.class);
            List<Monster> monsters = new ArrayList<>();
            if (root.creatures != null) {
                for (MonsterDTO dto : root.creatures) {
                    monsters.add(dto.toMonster());
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
        return name.endsWith(".json");
    }

    // No additional parsing logic required with DTO approach
}

