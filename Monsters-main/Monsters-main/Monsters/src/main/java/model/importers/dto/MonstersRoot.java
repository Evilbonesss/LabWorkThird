package model.importers.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MonstersRoot {
    @JsonProperty("creatures")
    public List<MonsterDTO> creatures;
}
