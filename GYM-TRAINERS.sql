
--feature 2
-- Shows current active member load and remaining capacity per trainer
CREATE OR REPLACE VIEW vw_trainer_load AS
SELECT
    t.trainer_id,
    t.name,
    t.specialization,
    t.max_members,
    COUNT(m.member_id) AS active_members,
    (t.max_members - COUNT(m.member_id)) AS remaining_capacity
FROM trainer t
LEFT JOIN member m
  ON m.trainer_id = t.trainer_id
 AND m.status = 'Active'
GROUP BY
    t.trainer_id, t.name, t.specialization, t.max_members;
/

CREATE OR REPLACE FUNCTION suggest_trainer_by_spec (
    p_specialization IN VARCHAR2
) RETURN NUMBER
IS
    v_trainer_id NUMBER;
BEGIN
    SELECT trainer_id
    INTO v_trainer_id
    FROM (
        SELECT tl.trainer_id
        FROM vw_trainer_load tl
        WHERE tl.specialization = p_specialization
          AND tl.remaining_capacity > 0
        ORDER BY tl.active_members ASC, tl.trainer_id ASC
    )
    WHERE ROWNUM = 1;

    RETURN v_trainer_id;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL; -- caller decides what to do if no trainer available
END;
/

CREATE OR REPLACE PROCEDURE auto_assign_trainer_for_member (
    p_member_id      IN NUMBER,
    p_specialization IN VARCHAR2
) AS
    v_trainer_id NUMBER;
    v_exists     NUMBER;
BEGIN
    -- Ensure the member exists
    SELECT COUNT(*) INTO v_exists FROM member WHERE member_id = p_member_id;
    IF v_exists = 0 THEN
        RAISE_APPLICATION_ERROR(-20010, 'Member not found.');
    END IF;

    -- Find a trainer with least load & capacity
    v_trainer_id := suggest_trainer_by_spec(p_specialization);

    IF v_trainer_id IS NULL THEN
        RAISE_APPLICATION_ERROR(-20011, 'No trainer available for specialization: ' || p_specialization);
    END IF;

    -- Assign
    UPDATE member
       SET trainer_id = v_trainer_id
     WHERE member_id = p_member_id;

END;
/


CREATE OR REPLACE PROCEDURE reassign_trainer (
    p_member_id   IN NUMBER,
    p_trainer_id  IN NUMBER,
    p_enforce_spec IN VARCHAR2 DEFAULT 'N' -- 'Y' to enforce specialization match, else 'N'
) AS
    v_exists_member  NUMBER;
    v_exists_trainer NUMBER;
    v_active_count   NUMBER;
    v_max_members    NUMBER;
    v_trainer_spec   VARCHAR2(50);
    v_member_status  VARCHAR2(20);
BEGIN
    -- Validate member
    SELECT COUNT(*)
      INTO v_exists_member
      FROM member
     WHERE member_id = p_member_id;
    IF v_exists_member = 0 THEN
        RAISE_APPLICATION_ERROR(-20020, 'Member not found.');
    END IF;

    -- Validate trainer
    SELECT COUNT(*)
      INTO v_exists_trainer
      FROM trainer
     WHERE trainer_id = p_trainer_id;
    IF v_exists_trainer = 0 THEN
        RAISE_APPLICATION_ERROR(-20021, 'Trainer not found.');
    END IF;

    -- Optional specialization enforcement
    IF UPPER(p_enforce_spec) = 'Y' THEN
        SELECT t.specialization INTO v_trainer_spec FROM trainer t WHERE t.trainer_id = p_trainer_id;
        -- (If you want to enforce a *member preference*, add a column or pass it in and compare here.)
        -- For now this just ensures the trainer *has* a specialization; no mismatch rule applied.
        IF v_trainer_spec IS NULL THEN
            RAISE_APPLICATION_ERROR(-20022, 'Trainer has no specialization configured.');
        END IF;
    END IF;

    -- Capacity check (only counts ACTIVE members)
    SELECT COUNT(*)
      INTO v_active_count
      FROM member
     WHERE trainer_id = p_trainer_id
       AND status = 'Active';

    SELECT max_members
      INTO v_max_members
      FROM trainer
     WHERE trainer_id = p_trainer_id;

    IF v_active_count >= v_max_members THEN
        RAISE_APPLICATION_ERROR(-20023, 'Trainer at capacity.');
    END IF;

    -- Optional: only allow active members to be counted; still allow reassignment of any member
    SELECT status INTO v_member_status FROM member WHERE member_id = p_member_id;

    -- Reassign
    UPDATE member
       SET trainer_id = p_trainer_id
     WHERE member_id = p_member_id;
END;
/


CREATE OR REPLACE PROCEDURE add_member_auto (
    p_name           IN VARCHAR2,
    p_age            IN NUMBER,
    p_gender         IN VARCHAR2,
    p_phone          IN VARCHAR2,
    p_address        IN VARCHAR2,
    p_plan_id        IN NUMBER,
    p_specialization IN VARCHAR2,
    p_start_date     IN DATE
) AS
    v_trainer_id NUMBER;
BEGIN
    -- Pick trainer first
    v_trainer_id := suggest_trainer_by_spec(p_specialization);
    IF v_trainer_id IS NULL THEN
        RAISE_APPLICATION_ERROR(-20030, 'No trainer available for specialization: ' || p_specialization);
    END IF;

    -- Insert member (your existing trigger will set End_Date & Status)
    INSERT INTO member (name, age, gender, phone, address, plan_id, trainer_id, start_date)
    VALUES (p_name, p_age, p_gender, p_phone, p_address, p_plan_id, v_trainer_id, p_start_date);
END;
/


CREATE OR REPLACE PROCEDURE list_trainers_by_spec (
    p_specialization IN VARCHAR2,
    p_cursor         OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN p_cursor FOR
        SELECT
            t.trainer_id,
            t.name,
            t.specialization,
            t.max_members,
            tl.active_members,
            tl.remaining_capacity
        FROM trainer t
        JOIN vw_trainer_load tl
          ON tl.trainer_id = t.trainer_id
       WHERE t.specialization = p_specialization
       ORDER BY tl.active_members ASC, t.trainer_id ASC;
END;
/
