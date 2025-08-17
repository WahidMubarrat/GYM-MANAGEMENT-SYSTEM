
--feature 3


-- Each exercise record
CREATE OR REPLACE TYPE t_exercise AS OBJECT (
  exercise_name     VARCHAR2(100),
  duration_minutes  NUMBER,
  repetitions       NUMBER
);
/

-- A table (array) of exercises
CREATE OR REPLACE TYPE t_exercise_tab AS TABLE OF t_exercise;
/
CREATE OR REPLACE FUNCTION get_member_workoutplan_id (
  p_member_id IN NUMBER
) RETURN NUMBER
IS
  v_plan_id NUMBER;
BEGIN
  SELECT workoutplan_id
    INTO v_plan_id
    FROM workout_plan
   WHERE member_id = p_member_id;
  RETURN v_plan_id;
EXCEPTION
  WHEN NO_DATA_FOUND THEN
    RETURN NULL;
END;
/

CREATE OR REPLACE PROCEDURE create_workout_plan_with_exercises (
  p_member_id         IN  NUMBER,
  p_description       IN  VARCHAR2,
  p_days_per_week     IN  NUMBER,
  p_exercises         IN  t_exercise_tab,  -- pass multiple exercises
  p_replace_if_exists IN  VARCHAR2 DEFAULT 'N', -- 'Y' to replace existing plan
  p_workoutplan_id    OUT NUMBER            -- returns the plan id
) AS
  v_existing_plan_id NUMBER;
BEGIN
  -- Validate member exists
  DECLARE v_cnt NUMBER; BEGIN
    SELECT COUNT(*) INTO v_cnt FROM member WHERE member_id = p_member_id;
    IF v_cnt = 0 THEN
      RAISE_APPLICATION_ERROR(-20100, 'Member not found.');
    END IF;
  END;

  -- Check if member already has a plan
  v_existing_plan_id := get_member_workoutplan_id(p_member_id);

  IF v_existing_plan_id IS NOT NULL THEN
    IF UPPER(p_replace_if_exists) = 'Y' THEN
      -- delete children first, then plan
      DELETE FROM work_out WHERE workoutplan_id = v_existing_plan_id;
      DELETE FROM workout_plan WHERE workoutplan_id = v_existing_plan_id;
    ELSE
      RAISE_APPLICATION_ERROR(-20101, 'Member already has a workout plan. Use replace flag or edit existing.');
    END IF;
  END IF;

  -- Create new plan; trigger will populate ID, we RETURN it
  INSERT INTO workout_plan (member_id, description, days_per_week)
  VALUES (p_member_id, p_description, p_days_per_week)
  RETURNING workoutplan_id INTO p_workoutplan_id;

  -- Insert exercises (if any provided)
  IF p_exercises IS NOT NULL AND p_exercises.COUNT > 0 THEN
    FOR i IN 1 .. p_exercises.COUNT LOOP
      -- Require at least duration or reps
      IF p_exercises(i).duration_minutes IS NULL AND p_exercises(i).repetitions IS NULL THEN
        RAISE_APPLICATION_ERROR(-20102, 'Exercise "'||p_exercises(i).exercise_name||'" must have duration or repetitions.');
      END IF;

      INSERT INTO work_out (workoutplan_id, exercise_name, duration_minutes, repetitions)
      VALUES (
        p_workoutplan_id,
        p_exercises(i).exercise_name,
        p_exercises(i).duration_minutes,
        p_exercises(i).repetitions
      );
    END LOOP;
  END IF;
END;
/

-- Add one exercise to an existing plan
CREATE OR REPLACE PROCEDURE add_exercise_to_plan (
  p_workoutplan_id  IN NUMBER,
  p_exercise_name   IN VARCHAR2,
  p_duration_min    IN NUMBER,
  p_repetitions     IN NUMBER
) AS
  v_cnt NUMBER;
BEGIN
  -- plan exists?
  SELECT COUNT(*) INTO v_cnt FROM workout_plan WHERE workoutplan_id = p_workoutplan_id;
  IF v_cnt = 0 THEN
    RAISE_APPLICATION_ERROR(-20110, 'Workout plan not found.');
  END IF;

  IF p_duration_min IS NULL AND p_repetitions IS NULL THEN
    RAISE_APPLICATION_ERROR(-20111, 'Provide duration or repetitions.');
  END IF;

  INSERT INTO work_out (workoutplan_id, exercise_name, duration_minutes, repetitions)
  VALUES (p_workoutplan_id, p_exercise_name, p_duration_min, p_repetitions);
END;
/

-- Update an exercise row (by Work_Out_ID)
CREATE OR REPLACE PROCEDURE update_exercise (
  p_workout_id     IN NUMBER,
  p_exercise_name  IN VARCHAR2,
  p_duration_min   IN NUMBER,
  p_repetitions    IN NUMBER
) AS
  v_cnt NUMBER;
BEGIN
  SELECT COUNT(*) INTO v_cnt FROM work_out WHERE workout_id = p_workout_id;
  IF v_cnt = 0 THEN
    RAISE_APPLICATION_ERROR(-20112, 'Exercise row not found.');
  END IF;

  IF p_duration_min IS NULL AND p_repetitions IS NULL THEN
    RAISE_APPLICATION_ERROR(-20113, 'Provide duration or repetitions.');
  END IF;

  UPDATE work_out
     SET exercise_name    = p_exercise_name,
         duration_minutes = p_duration_min,
         repetitions      = p_repetitions
   WHERE workout_id = p_workout_id;
END;
/

-- Delete an exercise row (by Work_Out_ID)
CREATE OR REPLACE PROCEDURE delete_exercise (
  p_workout_id IN NUMBER
) AS
BEGIN
  DELETE FROM work_out WHERE workout_id = p_workout_id;
END;
/

CREATE OR REPLACE PROCEDURE delete_workout_plan (
  p_workoutplan_id IN NUMBER
) AS
BEGIN
  DELETE FROM work_out WHERE workoutplan_id = p_workoutplan_id;
  DELETE FROM workout_plan WHERE workoutplan_id = p_workoutplan_id;
END;
/

CREATE OR REPLACE VIEW vw_member_workout_detail AS
SELECT
  m.member_id,
  m.name               AS member_name,
  wp.workoutplan_id,
  wp.description       AS plan_description,
  wp.days_per_week,
  wo.workout_id,
  wo.exercise_name,
  wo.duration_minutes,
  wo.repetitions
FROM member m
JOIN workout_plan wp ON wp.member_id = m.member_id
LEFT JOIN work_out wo ON wo.workoutplan_id = wp.workoutplan_id;
/
