package ru.skillbox.searcher.model.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.searcher.model.entity.FieldEntity;

import java.util.UUID;

@Repository
public interface FieldRepository extends CrudRepository<FieldEntity, UUID> {

}
