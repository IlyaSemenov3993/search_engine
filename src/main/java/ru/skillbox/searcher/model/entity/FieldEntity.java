package ru.skillbox.searcher.model.entity;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "field")
public class FieldEntity {

    @Id
    @Column
    private UUID id;

    @Column
    private String name;

    @Column
    private String selector;

    @Column
    private float weight;

    public FieldEntity() {
    }

    public FieldEntity(UUID id, String name, String selector, float weight) {
        this.id = id;
        this.name = name;
        this.selector = selector;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

}
