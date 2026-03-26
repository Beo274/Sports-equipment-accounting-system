package ru.etu.sport.dao;

import java.sql.ResultSet;

import org.springframework.jdbc.core.RowMapper;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.etu.sport.model.dto.response.ClassHierarchyResponse;
import ru.etu.sport.model.dto.response.ClassResponse;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ClassTreeDao {
    private final JdbcTemplate jdbcTemplate;

    private static class ClassHierarchyRowMapper implements RowMapper<ClassHierarchyResponse> {
        @Override
        public ClassHierarchyResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
            return ClassHierarchyResponse.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .shortName(rs.getString("short_name"))
                .baseClassId(rs.getInt("base_class_id"))
                .level(rs.getInt("level"))
                .mUnitId(rs.getInt("m_unit_id"))
                .build();
        }
    }

    private static class ClassRowMapper implements RowMapper<ClassResponse> {
        @Override
        public ClassResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
            return ClassResponse.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .shortName(rs.getString("short_name"))
                .baseClassId(rs.getInt("base_class_id"))
                .mUnitId(rs.getInt("m_unit_id"))
                .build();
        }
    }

    public List<ClassHierarchyResponse> findChildren(Integer classId) {
        String sql = "SELECT * FROM find_children(?)";

        try {
            return jdbcTemplate.query(sql, new ClassHierarchyRowMapper(), classId);
        } catch (Exception e) {
            log.error("Error calling find_children: {}", e);
            throw new RuntimeException("Failed to find children to class: " + classId, e);
        }
    }

    public List<ClassHierarchyResponse> findParents(Integer classId) {
        String sql = "SELECT * FROM find_parents(?)";

        try {
            return jdbcTemplate.query(sql, new ClassHierarchyRowMapper(), classId);
        } catch (Exception e) {
            log.error("Error calling find_parents: {}", e);
            throw new RuntimeException("Failed to find parents to class: " + classId, e);
        }
    }

    public List<ClassResponse> findLeaves() {
        String sql = "SELECT * FROM find_leaves()";

        try {
            return jdbcTemplate.query(sql, new ClassRowMapper());
        } catch (Exception e) {
            log.error("Error calling find_leaves: {}", e);
            throw new RuntimeException("Failed to find leaves: {}", e);
        }
    }
}
