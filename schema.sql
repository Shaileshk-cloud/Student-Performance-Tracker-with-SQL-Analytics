-- ======================================================
-- STUDENT PERFORMANCE ANALYTICS (Two Views Only)
-- Tables: Students, Subjects, Marks
-- Views: student_total_rank, subject_ranks
-- ======================================================

DROP DATABASE IF EXISTS student_performance;
CREATE DATABASE student_performance;
USE student_performance;

-- -------------------------------------
-- 1️⃣ TABLE: Students
-- -------------------------------------
CREATE TABLE Students (
  student_id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL
);

-- -------------------------------------
-- 2️⃣ TABLE: Subjects
-- -------------------------------------
CREATE TABLE Subjects (
  subject_id INT PRIMARY KEY AUTO_INCREMENT,
  subject_name VARCHAR(50) NOT NULL
);

-- -------------------------------------
-- 3️⃣ TABLE: Marks
-- -------------------------------------
CREATE TABLE Marks (
  mark_id INT PRIMARY KEY AUTO_INCREMENT,
  student_id INT NOT NULL,
  subject_id INT NOT NULL,
  marks_obtained INT NOT NULL CHECK (marks_obtained >= 0),
  FOREIGN KEY (student_id) REFERENCES Students(student_id) ON DELETE CASCADE,
  FOREIGN KEY (subject_id) REFERENCES Subjects(subject_id) ON DELETE CASCADE
);

-- -------------------------------------
-- 4️⃣ SAMPLE DATA
-- -------------------------------------
INSERT INTO Students (name) VALUES
('Asha'),
('Ravi'),
('Meena'),
('Kiran'),
('Anita');

INSERT INTO Subjects (subject_name) VALUES
('Math'),
('Science'),
('English');

INSERT INTO Marks (student_id, subject_id, marks_obtained) VALUES
(1, 1, 90), (1, 2, 85), (1, 3, 95),
(2, 1, 88), (2, 2, 80), (2, 3, 92),
(3, 1, 75), (3, 2, 70), (3, 3, 65),
(4, 1, 88), (4, 2, 90), (4, 3, 85),
(5, 1, 95), (5, 2, 88), (5, 3, 90);

-- ======================================================
-- 5️⃣ VIEW 1: student_total_rank
-- Calculates total marks and DENSE_RANK across all students
-- ======================================================
CREATE OR REPLACE VIEW student_total_rank AS
SELECT
  s.student_id,
  s.name,
  COALESCE(SUM(m.marks_obtained), 0) AS total_marks,
  DENSE_RANK() OVER (ORDER BY COALESCE(SUM(m.marks_obtained), 0) DESC) AS total_rank
FROM Students s
LEFT JOIN Marks m ON s.student_id = m.student_id
GROUP BY s.student_id, s.name;

-- ======================================================
-- 6️⃣ VIEW 2: subject_ranks
-- Calculates subject-wise rank for each student using DENSE_RANK()
-- ======================================================
CREATE OR REPLACE VIEW subject_ranks AS
SELECT
  sub.subject_id,
  sub.subject_name,
  s.student_id,
  s.name AS student_name,
  m.marks_obtained,
  DENSE_RANK() OVER (
    PARTITION BY sub.subject_id
    ORDER BY m.marks_obtained DESC
  ) AS subject_rank
FROM Marks m
JOIN Students s ON m.student_id = s.student_id
JOIN Subjects sub ON m.subject_id = sub.subject_id;

-- ======================================================
-- 7️⃣ TEST QUERIES
-- ======================================================
-- To view total marks and overall rank:
-- SELECT * FROM student_total_rank ORDER BY total_rank;

-- To view subject-wise ranks:
-- SELECT * FROM subject_ranks ORDER BY subject_name, subject_rank;
