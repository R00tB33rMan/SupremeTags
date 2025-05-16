package net.noscape.project.supremetags.handlers;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Setter
@Getter
public class Category {

    private String category;
    private List<String> tags;

    public Category(String category, List<String> tags) {
        this.category = category;
        this.tags = tags;
    }
}
