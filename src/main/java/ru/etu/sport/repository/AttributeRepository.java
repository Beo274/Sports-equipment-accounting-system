package ru.etu.sport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.etu.sport.model.entity.Attribute;

public interface AttributeRepository extends JpaRepository<Attribute, Integer> {

    public Attribute getAttributeById(Integer id);

    @Modifying
    @Query("UPDATE Attribute a SET a.intValue = NULL WHERE a.id =:id")
    void deleteIntValueById(Integer id);

    @Modifying
    @Query("UPDATE Attribute a SET a.stringValue = NULL WHERE a.id =:id")
    void deleteStringValueById(Integer id);

    @Modifying
    @Query("UPDATE Attribute a SET a.imageValue = NULL WHERE a.id =:id")
    void deleteImageValueById(Integer id);

    @Modifying
    @Query("UPDATE Attribute a SET a.intValue = :val WHERE a.id =:id")
    void updateValue(Integer val, Integer id);

    @Modifying
    @Query("UPDATE Attribute a SET a.stringValue = :val WHERE a.id =:id")
    void updateValue(String val, Integer id);
}
