
-- feature 4

CREATE OR REPLACE FUNCTION compute_next_due (p_member_id IN NUMBER)
RETURN DATE
IS
    v_start_date     DATE;
    v_duration       NUMBER;
    v_pay_count      NUMBER;
BEGIN
    SELECT m.start_date, p.duration_months
      INTO v_start_date, v_duration
      FROM member m
      JOIN plan   p ON p.plan_id = m.plan_id
     WHERE m.member_id = p_member_id;

    SELECT COUNT(*)
      INTO v_pay_count
      FROM payment
     WHERE member_id = p_member_id;

    RETURN ADD_MONTHS(v_start_date, (v_pay_count + 1) * v_duration);
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
END;
/

CREATE OR REPLACE PROCEDURE record_payment (
    p_member_id     IN NUMBER,
    p_amount        IN NUMBER,
    p_method        IN VARCHAR2,
    p_notes         IN VARCHAR2 DEFAULT NULL
) AS
    v_exists        NUMBER;
    v_price         NUMBER;
    v_duration      NUMBER;
    v_end_date      DATE;
    v_next_due      DATE;
BEGIN
    -- Member & plan info
    SELECT COUNT(*) INTO v_exists FROM member WHERE member_id = p_member_id;
    IF v_exists = 0 THEN
        RAISE_APPLICATION_ERROR(-20200, 'Member not found.');
    END IF;

    SELECT p.price, p.duration_months, m.end_date
      INTO v_price, v_duration, v_end_date
      FROM member m
      JOIN plan p ON p.plan_id = m.plan_id
     WHERE m.member_id = p_member_id;

    -- Optional: enforce minimum plan price
    IF p_amount < v_price THEN
        RAISE_APPLICATION_ERROR(-20201, 'Payment amount is less than plan price.');
    END IF;

    -- Compute next due date AFTER this payment
    -- (i.e., this payment covers the coming plan cycle)
    v_next_due := compute_next_due(p_member_id);

    INSERT INTO payment (
        payment_id, member_id, amount, date_paid, next_due_date, payment_method, notes
    ) VALUES (
        seq_payment.NEXTVAL, p_member_id, p_amount, SYSDATE, v_next_due, p_method, p_notes
    );

    -- If not already expired, mark Active after payment
    UPDATE member
       SET status = CASE 
                      WHEN SYSDATE > v_end_date THEN 'Expired'
                      ELSE 'Active'
                    END
     WHERE member_id = p_member_id;
END;
/


CREATE OR REPLACE PROCEDURE sync_member_billing_status AS
BEGIN
    -- Expired: past member's plan end date
    UPDATE member m
       SET m.status = 'Expired'
     WHERE SYSDATE > m.end_date
       AND m.status <> 'Expired';

    -- Overdue: not expired, but next due reached/passed
    UPDATE member m
       SET m.status = 'Overdue'
     WHERE SYSDATE <= m.end_date
       AND compute_next_due(m.member_id) <= SYSDATE
       AND m.status <> 'Overdue';

    -- Active: not expired and not overdue
    UPDATE member m
       SET m.status = 'Active'
     WHERE SYSDATE <= m.end_date
       AND compute_next_due(m.member_id) > SYSDATE
       AND m.status <> 'Active';
END;
/

CREATE OR REPLACE VIEW vw_member_billing AS
SELECT
    m.member_id,
    m.name            AS member_name,
    m.status,
    p.plan_name,
    p.price           AS plan_price,
    -- next due *if paid count + 1; NULL if compute_next_due returns null
    compute_next_due(m.member_id) AS next_due_date,
    -- last payment date
    (SELECT MAX(date_paid) FROM payment py WHERE py.member_id = m.member_id) AS last_paid_on,
    -- days overdue (0 if not overdue)
    GREATEST(
        CASE 
          WHEN compute_next_due(m.member_id) IS NULL THEN 0
          WHEN compute_next_due(m.member_id) > SYSDATE THEN 0
          ELSE TRUNC(SYSDATE) - TRUNC(compute_next_due(m.member_id))
        END, 0
    ) AS days_overdue
FROM member m
JOIN plan   p ON p.plan_id = m.plan_id;
/
