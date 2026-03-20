-- Managing
CREATE OR REPLACE FUNCTION insert_class(
    p_name VARCHAR(256),
    p_m_unit_id INTEGER,
    p_short_name VARCHAR(128) DEFAULT NULL,
    p_base_class_id INTEGER DEFAULT NULL
)
RETURNS INTEGER AS 
$$
DECLARE
    new_class_id INTEGER;
BEGIN
    INSERT INTO class (name, short_name, base_class_id, m_unit_id) VALUES 
    (p_name, p_short_name, p_base_class_id, p_m_unit_id)
    RETURNING id INTO new_class_id;

    RETURN new_class_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION swap_base_class(
    p_class_id INTEGER,
    p_new_base_class INTEGER DEFAULT NULL
)
RETURNS BOOLEAN AS
$$
DECLARE
    updated_count INTEGER;
BEGIN
    UPDATE class SET base_class_id = p_new_base_class
    WHERE id = p_class_id;
    
    GET DIAGNOSTICS updated_count = ROW_COUNT;

    IF updated_count > 0 THEN 
        RETURN TRUE;
    ELSE
        RETURN FALSE;
    END IF;
EXCEPTION
    WHEN foreign_key_violation THEN
        RAISE EXCEPTION 'Base class % not found', p_new_base_class;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION delete_class(
    p_class_id INTEGER
)
RETURNS BOOLEAN AS
$$
DECLARE
    deleted INTEGER;
BEGIN
    DELETE FROM class WHERE id = p_class_id;

    GET DIAGNOSTICS deleted = ROW_COUNT;

    if deleted > 0 THEN
        RETURN TRUE;
    ELSE 
        RETURN FALSE;
    END IF;
END;
$$ LANGUAGE plpgsql;

-- Searching
CREATE OR REPLACE FUNCTION find_children(
    p_class_id INTEGER
)
RETURNS TABLE (
    id INTEGER, 
    name VARCHAR(256), 
    short_name VARCHAR(128), 
    base_class_id INTEGER, 
    m_unit_id INTEGER,
    level INTEGER
) AS
$$
BEGIN
    RETURN QUERY
    WITH RECURSIVE descendants AS (
        SELECT c.id, c.name, c.short_name, c.base_class_id, c.m_unit_id, 1 AS level FROM class c
        WHERE c.id = p_class_id

        UNION ALL

        SELECT c.id, c.name, c.short_name, c.base_class_id, c.m_unit_id, d.level + 1
        FROM descendants d
        JOIN class c ON c.base_class_id = d.id
    )
    SELECT d.id, d.name, d.short_name, d.base_class_id, d.m_unit_id, d.level
    FROM descendants d
    ORDER BY d.level;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION find_parents(
    p_class_id INTEGER
)
RETURNS TABLE (
    id INTEGER,
    name VARCHAR(256),
    short_name VARCHAR(128),
    base_class_id INTEGER,
    m_unit_id INTEGER,
    level INTEGER
) AS
$$
BEGIN
    RETURN QUERY
    WITH RECURSIVE ancestors AS (
        SELECT p.id, p.name, p.short_name, p.base_class_id, p.m_unit_id, 1 AS level
        FROM class p
        WHERE p.id = p_class_id

        UNION ALL

        SELECT p.id, p.name, p.short_name, p.base_class_id, p.m_unit_id, a.level + 1
        FROM ancestors a
        JOIN class p ON a.base_class_id = p.id
    )
    SELECT a.id, a.name, a.short_name, a.base_class_id, a.m_unit_id, a.level
    FROM ancestors a
    ORDER BY level;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION find_leaves()
RETURNS TABLE (
    id INTEGER,
    name VARCHAR(256),
    short_name VARCHAR(128),
    base_class_id INTEGER,
    m_unit_id INTEGER
) AS 
$$
BEGIN
    RETURN QUERY
    SELECT c.id, c.name, c.short_name, c.base_class_id, c.m_unit_id
    FROM class c
    LEFT JOIN class parent ON parent.id = c.base_class_id
    WHERE NOT EXISTS (
        SELECT 1 FROM class child
        WHERE child.base_class_id = c.id
    );
END;
$$ LANGUAGE plpgsql;

-- Сycle prevention
CREATE OR REPLACE FUNCTION prevent_class_cycle()
RETURNS TRIGGER AS 
$$
DECLARE
    v_current_id INTEGER;
    v_parent_id INTEGER;
    v_cycle_detected BOOLEAN := FALSE;
BEGIN
    IF NEW.base_class_id IS NOT NULL THEN
        v_current_id := NEW.id;
        v_parent_id := NEW.base_class_id;
        
        WHILE v_parent_id IS NOT NULL LOOP
            IF v_parent_id = v_current_id THEN
                v_cycle_detected := TRUE;
                EXIT;
            END IF;

            SELECT base_class_id INTO v_parent_id
            FROM class
            WHERE id = v_parent_id;
        END LOOP;

        IF v_cycle_detected THEN 
            RAISE EXCEPTION 'Cycle detected';
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER check_class_cycle_trigger
    BEFORE INSERT OR UPDATE OF base_class_id ON class
    FOR EACH ROW
    EXECUTE FUNCTION prevent_class_cycle();