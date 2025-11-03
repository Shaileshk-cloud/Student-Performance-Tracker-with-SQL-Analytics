-- ==========================================================
-- STUDENT PERFORMANCE ANALYTICS SYSTEM
-- Supports Total-wise and Subject-wise Rank using DENSE_RANK()
-- ==========================================================

DROP DATABASE IF EXISTS student_performance;
CREATE DATABASE student_performance;
USE student_performance;

-- -------------------------------
-- 1. TABLE: Students
-- -------------------------------
CREATE TABLE Students (
  student_id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  class_name VARCHAR(20)
);

-- -------------------------------
-- 2. TABLE: Subjects
-- -------------------------------
CREATE TABLE Subjects (
  subject_id INT PRIMARY KEY AUTO_INCREMENT,
  subject_name VARCHAR(50) NOT NULL
);

-- -------------------------------
-- 3. TABLE: Marks
-- -------------------------------
CREATE TABLE Marks (
  mark_id INT PRIMARY KEY AUTO_INCREMENT,
  student_id INT NOT NULL,
  subject_id INT NOT NULL,
  marks_obtained INT NOT NULL CHECK (marks_obtained >= 0),
  FOREIGN KEY (student_id) REFERENCES Students(student_id) ON DELETE CASCADE,
  FOREIGN KEY (subject_id) REFERENCES Subjects(subject_id) ON DELETE CASCADE
);

-- -------------------------------
-- 4. INSERT SAMPLE DATA
-- -------------------------------

-- Students
INSERT INTO Students (name, class_name) VALUES
('Asha', '10A'),
('Ravi', '10A'),
('Meena', '10B'),
('Kiran', '10A'),
('Anita', '10B');

-- Subjects
INSERT INTO Subjects (subject_name) VALUES
('Math'),
('Science'),
('English');

-- Marks
INSERT INTO Marks (student_id, subject_id, marks_obtained) VALUES
(1, 1, 90), (1, 2, 85), (1, 3, 95),
(2, 1, 88), (2, 2, 80), (2, 3, 92),
(3, 1, 75), (3, 2, 70), (3, 3, 65),
(4, 1, 88), (4, 2, 90), (4, 3, 85),
(5, 1, 95), (5, 2, 88), (5, 3, 90);

-- -------------------------------
-- 5. VIEWS CREATION
-- -------------------------------

-- (a) View: student_total_rank
-- Shows total marks and DENSE_RANK across all students
CREATE OR REPLACE VIEW student_total_rank AS
SELECT
  s.student_id,
  s.name,
  s.class_name,
  COALESCE(SUM(m.marks_obtained), 0) AS total_marks,
  DENSE_RANK() OVER (ORDER BY COALESCE(SUM(m.marks_obtained), 0) DESC) AS total_rank
FROM Students s
LEFT JOIN Marks m ON s.student_id = m.student_id
GROUP BY s.student_id, s.name, s.class_name;

-- (b) View: subject_ranks
-- Shows each subject’s rank using DENSE_RANK() partitioned by subject
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

-- (c) View: student_subject_with_total
-- Combines subject-wise and total-wise data in one view
CREATE OR REPLACE VIEW student_subject_with_total AS
SELECT
  sr.subject_id,
  sr.subject_name,
  sr.student_id,
  sr.student_name,
  sr.marks_obtained,
  sr.subject_rank,
  st.total_marks,
  st.total_rank
FROM subject_ranks sr
LEFT JOIN student_total_rank st ON sr.student_id = st.student_id
ORDER BY sr.subject_name, sr.subject_rank, st.total_rank;

-- (d) View: student_subject_rank_summary
-- Displays a summary of subject ranks and total rank per student
CREATE OR REPLACE VIEW student_subject_rank_summary AS
SELECT
  st.student_id,
  st.name,
  st.class_name,
  st.total_marks,
  st.total_rank,
  GROUP_CONCAT(CONCAT(sr.subject_name, ':', sr.subject_rank)
               ORDER BY sr.subject_id SEPARATOR '; ') AS subject_ranks_summary
FROM student_total_rank st
LEFT JOIN subject_ranks sr ON st.student_id = sr.student_id
GROUP BY st.student_id, st.name, st.class_name, st.total_marks, st.total_rank;

-- -------------------------------
-- 6. TEST QUERIES
-- -------------------------------
-- Run these after loading the schema to verify
-- ----------------------------------------------

-- Total-wise ranks
-- SELECT * FROM student_total_rank ORDER BY total_rank;

-- Subject-wise ranks
-- SELECT * FROM subject_ranks ORDER BY subject_name, subject_rank;

-- Combined subject + total view
-- SELECT * FROM student_subject_with_total;

-- Compact summary
-- SELECT * FROM student_subject_rank_summary ORDER BY total_rank;
