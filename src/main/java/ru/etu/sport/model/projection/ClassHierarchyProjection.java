package ru.etu.sport.model.projection;

public interface ClassHierarchyProjection {
    Integer getId();
    String getName();
    String getShortName();
    Integer getBaseClassId();
    Integer getLevel();
    Integer getMUnitId();
}