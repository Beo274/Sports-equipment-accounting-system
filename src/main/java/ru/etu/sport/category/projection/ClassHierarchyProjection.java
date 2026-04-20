package ru.etu.sport.category.projection;

public interface ClassHierarchyProjection {
    Integer getId();
    String getName();
    String getShortName();
    Integer getBaseClassId();
    Integer getLevel();
    Integer getMUnitId();
}