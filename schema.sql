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
