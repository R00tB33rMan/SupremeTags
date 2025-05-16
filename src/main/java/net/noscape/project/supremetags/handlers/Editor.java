package net.noscape.project.supremetags.handlers;

import lombok.Getter;
import lombok.Setter;
import net.noscape.project.supremetags.enums.EditingType;

@Setter
@Getter
public class Editor {

    private String identifier;
    private EditingType type;

    public Editor(String identifier, EditingType type) {
        this.identifier = identifier;
        this.type = type;
    }
}
