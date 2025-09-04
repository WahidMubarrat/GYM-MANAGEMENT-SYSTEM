

CREATE TABLE Plan (
    Plan_ID           NUMBER PRIMARY KEY,
    Plan_Name         VARCHAR2(50) NOT NULL UNIQUE,
    Duration_Months   NUMBER(3)    NOT NULL CHECK (Duration_Months > 0),
    Price             NUMBER(10,2) NOT NULL CHECK (Price >= 0)
);

CREATE TABLE Trainer (
    Trainer_ID        NUMBER PRIMARY KEY,
    Name              VARCHAR2(100) NOT NULL,
    Specialization    VARCHAR2(50)  NOT NULL,
    Phone             VARCHAR2(20) UNIQUE,
    Max_Members       NUMBER(4)     DEFAULT 20 CHECK (Max_Members > 0)
);

CREATE TABLE Member (
    Member_ID         NUMBER PRIMARY KEY,
    Name              VARCHAR2(100) NOT NULL,
    Age               NUMBER(3)     CHECK (Age BETWEEN 12 AND 100),
    Gender            VARCHAR2(10)  CHECK (Gender IN ('Male','Female','Other')),
    Phone             VARCHAR2(20) UNIQUE,
    Address           VARCHAR2(200),
    Plan_ID           NUMBER NOT NULL,
    Trainer_ID        NUMBER,
    Start_Date        DATE   NOT NULL,
    End_Date          DATE,
    Status            VARCHAR2(20) DEFAULT 'Active' CHECK (Status IN ('Active','Overdue','Expired')),
    CONSTRAINT fk_member_plan    FOREIGN KEY (Plan_ID)    REFERENCES Plan(Plan_ID),
    CONSTRAINT fk_member_trainer FOREIGN KEY (Trainer_ID) REFERENCES Trainer(Trainer_ID)
);

-- One active plan per Member (unique Member_ID here enforces 1:1)
CREATE TABLE Workout_Plan (
    WorkoutPlan_ID    NUMBER PRIMARY KEY,
    Member_ID         NUMBER NOT NULL UNIQUE,
    Description       VARCHAR2(200),
    Days_Per_Week     NUMBER(1) CHECK (Days_Per_Week BETWEEN 1 AND 7),
    CONSTRAINT fk_workoutplan_member FOREIGN KEY (Member_ID) REFERENCES Member(Member_ID)
);

-- Multiple exercises per Workout_Plan (1:N)
CREATE TABLE Work_Out (
    WorkOut_ID        NUMBER PRIMARY KEY,
    WorkoutPlan_ID    NUMBER NOT NULL,
    Exercise_Name     VARCHAR2(100) NOT NULL,
    Duration_Minutes  NUMBER(4),       -- for cardio / time-based
    Repetitions       NUMBER(4),       -- for reps-based
    CONSTRAINT fk_workout_plan FOREIGN KEY (WorkoutPlan_ID) REFERENCES Workout_Plan(WorkoutPlan_ID)
);

CREATE TABLE Payment (
    Payment_ID       NUMBER PRIMARY KEY,
    Member_ID        NUMBER NOT NULL,
    Amount           NUMBER(10,2) NOT NULL CHECK (Amount >= 0),
    Date_Paid        DATE DEFAULT SYSDATE NOT NULL,
    Next_Due_Date    DATE,
    Payment_Method   VARCHAR2(20), -- Cash, Card, MobileBanking
    Notes            VARCHAR2(200),
    CONSTRAINT fk_payment_member FOREIGN KEY (Member_ID) REFERENCES Member(Member_ID)
);

-- Create sequences
CREATE SEQUENCE seq_plan START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_trainer START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_member START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_workoutplan START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_workout START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_payment START WITH 1 INCREMENT BY 1;

-- Create triggers for auto-increment IDs
CREATE OR REPLACE TRIGGER trg_plan_bi
BEFORE INSERT ON Plan
FOR EACH ROW
BEGIN
  IF :NEW.Plan_ID IS NULL THEN
    :NEW.Plan_ID := seq_plan.NEXTVAL;
  END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_trainer_bi
BEFORE INSERT ON Trainer
FOR EACH ROW
BEGIN
  IF :NEW.Trainer_ID IS NULL THEN
    :NEW.Trainer_ID := seq_trainer.NEXTVAL;
  END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_member_bi
BEFORE INSERT ON Member
FOR EACH ROW
BEGIN
  IF :NEW.Member_ID IS NULL THEN
    :NEW.Member_ID := seq_member.NEXTVAL;
  END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_workoutplan_bi
BEFORE INSERT ON Workout_Plan
FOR EACH ROW
BEGIN
  IF :NEW.WorkoutPlan_ID IS NULL THEN
    :NEW.WorkoutPlan_ID := seq_workoutplan.NEXTVAL;
  END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_workout_bi
BEFORE INSERT ON Work_Out
FOR EACH ROW
BEGIN
  IF :NEW.WorkOut_ID IS NULL THEN
    :NEW.WorkOut_ID := seq_workout.NEXTVAL;
  END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_payment_bi
BEFORE INSERT ON Payment
FOR EACH ROW
BEGIN
  IF :NEW.Payment_ID IS NULL THEN
    :NEW.Payment_ID := seq_payment.NEXTVAL;
  END IF;
END;
/


CREATE INDEX idx_member_status     ON member(status);
CREATE INDEX idx_member_plan       ON member(plan_id);
CREATE INDEX idx_member_trainer    ON member(trainer_id);

CREATE INDEX idx_payment_date      ON payment(date_paid);
CREATE INDEX idx_payment_member    ON payment(member_id);


INSERT INTO Plan (Plan_Name, Duration_Months, Price)
VALUES ('Basic Monthly', 1, 2000);
INSERT INTO Plan (Plan_Name, Duration_Months, Price)
VALUES ('Quarterly Fitness', 3, 5000);
INSERT INTO Plan (Plan_Name, Duration_Months, Price)
VALUES ('Annual Pro', 12, 18000);

-- Insert some Trainers
INSERT INTO Trainer (Name, Specialization, Phone, Max_Members)
VALUES ('Arif Hossain', 'Cardio', '01710000001', 20);
INSERT INTO Trainer (Name, Specialization, Phone, Max_Members)
VALUES ('Sadia Rahman', 'Strength', '01710000002', 15);
INSERT INTO Trainer (Name, Specialization, Phone, Max_Members)
VALUES ('Mahmudul Karim', 'Yoga', '01710000003', 25);

