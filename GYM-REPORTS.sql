
---feature 5

/* Current trainer utilization (Active = members with status 'Active') */
CREATE OR REPLACE VIEW vw_trainer_stats AS
SELECT
  t.trainer_id,
  t.name                AS trainer_name,
  t.specialization,
  t.max_members,
  COUNT(CASE WHEN m.status = 'Active' THEN 1 END) AS active_members,
  COUNT(m.member_id)                                AS total_members_assigned,
  (t.max_members - COUNT(CASE WHEN m.status = 'Active' THEN 1 END)) AS remaining_capacity
FROM trainer t
LEFT JOIN member m
  ON m.trainer_id = t.trainer_id
GROUP BY t.trainer_id, t.name, t.specialization, t.max_members;
/

/* Plan popularity (both total and active members) */
CREATE OR REPLACE VIEW vw_plan_popularity AS
SELECT
  p.plan_id,
  p.plan_name,
  p.duration_months,
  p.price,
  COUNT(m.member_id) AS total_members,
  COUNT(CASE WHEN m.status = 'Active' THEN 1 END) AS active_members
FROM plan p
LEFT JOIN member m
  ON m.plan_id = p.plan_id
GROUP BY p.plan_id, p.plan_name, p.duration_months, p.price;
/

/* Overdue members (leverages Feature 4 view) */
CREATE OR REPLACE VIEW vw_overdue_members AS
SELECT *
FROM vw_member_billing
WHERE status = 'Overdue' AND days_overdue > 0;
/

/* Revenue aggregates */
-- Daily
CREATE OR REPLACE VIEW vw_revenue_daily AS
SELECT
  TRUNC(date_paid) AS pay_date,
  COUNT(*)         AS payments_count,
  SUM(amount)      AS total_amount
FROM payment
GROUP BY TRUNC(date_paid);
/

-- Monthly
CREATE OR REPLACE VIEW vw_revenue_monthly AS
SELECT
  TRUNC(date_paid, 'MM') AS month_start,
  COUNT(*)               AS payments_count,
  SUM(amount)            AS total_amount
FROM payment
GROUP BY TRUNC(date_paid, 'MM');
/


CREATE OR REPLACE PROCEDURE report_top_trainers (
    p_limit     IN NUMBER DEFAULT 10,
    p_cursor    OUT SYS_REFCURSOR
) AS
BEGIN
  OPEN p_cursor FOR
    SELECT *
    FROM (
      SELECT
        ts.trainer_id,
        ts.trainer_name,
        ts.specialization,
        ts.active_members,
        ts.total_members_assigned,
        ts.max_members,
        ts.remaining_capacity
      FROM vw_trainer_stats ts
      ORDER BY ts.active_members DESC, ts.trainer_id ASC
    )
    WHERE ROWNUM <= NVL(p_limit, 10);
END;
/

CREATE OR REPLACE PROCEDURE report_popular_plans (
    p_limit     IN NUMBER DEFAULT 10,
    p_cursor    OUT SYS_REFCURSOR
) AS
BEGIN
  OPEN p_cursor FOR
    SELECT *
    FROM (
      SELECT
        pp.plan_id,
        pp.plan_name,
        pp.duration_months,
        pp.price,
        pp.total_members,
        pp.active_members
      FROM vw_plan_popularity pp
      ORDER BY pp.total_members DESC, pp.plan_id ASC
    )
    WHERE ROWNUM <= NVL(p_limit, 10);
END;
/

CREATE OR REPLACE PROCEDURE report_overdue_members (
    p_min_days_overdue IN NUMBER DEFAULT 1,
    p_cursor           OUT SYS_REFCURSOR
) AS
BEGIN
  OPEN p_cursor FOR
    SELECT
      member_id,
      member_name,
      status,
      plan_name,
      plan_price,
      next_due_date,
      last_paid_on,
      days_overdue
    FROM vw_overdue_members
    WHERE days_overdue >= NVL(p_min_days_overdue, 1)
    ORDER BY days_overdue DESC, member_id ASC;
END;
/

CREATE OR REPLACE PROCEDURE report_revenue (
    p_from_date IN DATE,
    p_to_date   IN DATE,
    p_group_by  IN VARCHAR2,          -- 'DAY' | 'MONTH' | 'YEAR'
    p_cursor    OUT SYS_REFCURSOR
) AS
BEGIN
  IF UPPER(p_group_by) = 'DAY' THEN
    OPEN p_cursor FOR
      SELECT TRUNC(date_paid) AS period_start,
             COUNT(*)         AS payments_count,
             SUM(amount)      AS total_amount
      FROM payment
      WHERE date_paid >= p_from_date
        AND date_paid <  p_to_date + 1
      GROUP BY TRUNC(date_paid)
      ORDER BY period_start;
  ELSIF UPPER(p_group_by) = 'MONTH' THEN
    OPEN p_cursor FOR
      SELECT TRUNC(date_paid, 'MM') AS period_start,
             COUNT(*)               AS payments_count,
             SUM(amount)            AS total_amount
      FROM payment
      WHERE date_paid >= p_from_date
        AND date_paid <  ADD_MONTHS(TRUNC(p_to_date, 'MM'), 1)
      GROUP BY TRUNC(date_paid, 'MM')
      ORDER BY period_start;
  ELSIF UPPER(p_group_by) = 'YEAR' THEN
    OPEN p_cursor FOR
      SELECT TRUNC(date_paid, 'YYYY') AS period_start,
             COUNT(*)                 AS payments_count,
             SUM(amount)              AS total_amount
      FROM payment
      WHERE date_paid >= p_from_date
        AND date_paid <  ADD_MONTHS(TRUNC(p_to_date, 'YYYY'), 12)
      GROUP BY TRUNC(date_paid, 'YYYY')
      ORDER BY period_start;
  ELSE
    RAISE_APPLICATION_ERROR(-20300, 'Invalid p_group_by. Use DAY, MONTH, or YEAR.');
  END IF;
END;
/

