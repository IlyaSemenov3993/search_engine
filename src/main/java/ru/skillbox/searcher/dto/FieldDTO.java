package ru.skillbox.searcher.dto;

import ru.skillbox.searcher.model.entity.FieldEntity;

import java.util.Objects;

public class FieldDTO {
    private String name;
    private String selector;
    private float weight;

    public FieldDTO() {
    }

    public FieldDTO(FieldEntity fieldEntity) {
        this.name = fieldEntity.getName();
        this.selector = fieldEntity.getSelector();
        this.weight = fieldEntity.getWeight();
    }

    public String getName() {
        return name;
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

    public static FieldDTO.Builder newBuilder() {
        return new FieldDTO().new Builder();
    }

    public class Builder {

        private Builder() {
            // private constructor
        }

        public FieldDTO.Builder setName(String name) {
            FieldDTO.this.name = name;
            return this;
        }

        public FieldDTO.Builder setSelector(String selector) {
            FieldDTO.this.selector = selector;
            return this;
        }

        public FieldDTO.Builder setWeight(float weight) {
            FieldDTO.this.weight = weight;
            return this;
        }


        public FieldDTO build() {
            return FieldDTO.this;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldDTO fieldDTO = (FieldDTO) o;
        return Float.compare(fieldDTO.weight, weight) == 0 && name.equals(fieldDTO.name) && selector.equals(fieldDTO.selector);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, selector, weight);
    }
}
