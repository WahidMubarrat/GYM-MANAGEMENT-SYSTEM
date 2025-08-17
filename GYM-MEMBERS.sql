
--feature 1
CREATE OR REPLACE PROCEDURE ADD_MEMBER (
    p_name        IN VARCHAR2,
    p_age         IN NUMBER,
    p_gender      IN VARCHAR2,
    p_phone       IN VARCHAR2,
    p_address     IN VARCHAR2,
    p_plan_id     IN NUMBER,
    p_trainer_id  IN NUMBER,
    p_start_date  IN DATE
) AS
BEGIN
    INSERT INTO Member (Name, Age, Gender, Phone, Address, Plan_ID, Trainer_ID, Start_Date)
    VALUES (p_name, p_age, p_gender, p_phone, p_address, p_plan_id, p_trainer_id, p_start_date);
END;
/
CREATE OR REPLACE PROCEDURE EDIT_MEMBER (
    p_member_id   IN NUMBER,
    p_name        IN VARCHAR2,
    p_age         IN NUMBER,
    p_gender      IN VARCHAR2,
    p_phone       IN VARCHAR2,
    p_address     IN VARCHAR2,
    p_plan_id     IN NUMBER,
    p_trainer_id  IN NUMBER
) AS
BEGIN
    UPDATE Member
    SET Name       = p_name,
        Age        = p_age,
        Gender     = p_gender,
        Phone      = p_phone,
        Address    = p_address,
        Plan_ID    = p_plan_id,
        Trainer_ID = p_trainer_id
    WHERE Member_ID = p_member_id;
END;
/
CREATE OR REPLACE PROCEDURE DELETE_MEMBER (
    p_member_id IN NUMBER
) AS
BEGIN
    DELETE FROM Member
    WHERE Member_ID = p_member_id;
END;
/
CREATE OR REPLACE TRIGGER trg_member_end_date
BEFORE INSERT ON Member
FOR EACH ROW
DECLARE
    v_duration NUMBER;
BEGIN
    SELECT Duration_Months
    INTO v_duration
    FROM Plan
    WHERE Plan_ID = :NEW.Plan_ID;

    :NEW.End_Date := ADD_MONTHS(:NEW.Start_Date, v_duration);
    :NEW.Status := 'Active';
END;
/
