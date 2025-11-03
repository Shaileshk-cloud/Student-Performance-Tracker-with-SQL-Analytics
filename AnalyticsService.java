import java.sql.*;
import java.util.*;

public class AnalyticsService {

    // 1) Overall totals + dense rank (reads from view student_total_rank if present)
    public List<String[]> getStudentTotalRanks() {
        List<String[]> list = new ArrayList<>();
        String query = "SELECT student_id, name, total_marks, total_rank FROM student_total_rank ORDER BY total_rank, total_marks DESC";
        // fallback if view not present: compute inline using DENSE_RANK()
        String fallback = """
                SELECT student_id, name, total_marks,
                       DENSE_RANK() OVER (ORDER BY total_marks DESC) AS total_rank
                FROM (
                    SELECT s.student_id, s.name, COALESCE(SUM(m.marks_obtained),0) AS total_marks
                    FROM Students s
                    LEFT JOIN Marks m ON s.student_id = m.student_id
                    GROUP BY s.student_id, s.name
                ) AS derived
                ORDER BY total_rank, total_marks DESC
                """;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs;
            try {
                rs = stmt.executeQuery(query);
            } catch (SQLException ex) {
                // view might not exist — use fallback
                rs = stmt.executeQuery(fallback);
            }

            while (rs.next()) {
                list.add(new String[] {
                        rs.getString("student_id"),
                        rs.getString("name"),
                        rs.getString("total_marks"),
                        rs.getString("total_rank")
                });
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2) Return all subjects (subject_id, subject_name) — used to populate GUI dropdown
    public List<String[]> getAllSubjects() {
        List<String[]> list = new ArrayList<>();
        String query = "SELECT subject_id, subject_name FROM Subjects ORDER BY subject_id";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new String[] {
                        String.valueOf(rs.getInt("subject_id")),
                        rs.getString("subject_name")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 3) Subject-wise ranks for a specific subject (reads from view subject_ranks if present)
    public List<String[]> getSubjectRanks(int subjectId) {
        List<String[]> list = new ArrayList<>();
        String query = "SELECT subject_id, subject_name, student_id, student_name, marks_obtained, subject_rank " +
                       "FROM subject_ranks WHERE subject_id = ? ORDER BY subject_rank, marks_obtained DESC";
        String fallback = """
                SELECT sub.subject_id, sub.subject_name, s.student_id, s.name AS student_name, m.marks_obtained,
                       DENSE_RANK() OVER (PARTITION BY sub.subject_id ORDER BY m.marks_obtained DESC) AS subject_rank
                FROM Marks m
                JOIN Students s ON m.student_id = s.student_id
                JOIN Subjects sub ON m.subject_id = sub.subject_id
                WHERE sub.subject_id = ?
                ORDER BY subject_rank, marks_obtained DESC
                """;

        try (Connection conn = DBConnection.getConnection()) {
            // prefer prepared statement for parameterized query
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, subjectId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    // if view exists and returns rows, use them
                    boolean any = false;
                    while (rs.next()) {
                        any = true;
                        list.add(new String[] {
                                rs.getString("subject_id"),
                                rs.getString("subject_name"),
                                rs.getString("student_id"),
                                rs.getString("student_name"),
                                rs.getString("marks_obtained"),
                                rs.getString("subject_rank")
                        });
                    }
                    if (any) return list;
                }
            } catch (SQLException ex) {
                // view may not exist — fallback below
            }

            // Fallback: run inline query
            try (PreparedStatement fp = conn.prepareStatement(fallback)) {
                fp.setInt(1, subjectId);
                try (ResultSet rs2 = fp.executeQuery()) {
                    while (rs2.next()) {
                        list.add(new String[] {
                                rs2.getString("subject_id"),
                                rs2.getString("subject_name"),
                                rs2.getString("student_id"),
                                rs2.getString("student_name"),
                                rs2.getString("marks_obtained"),
                                rs2.getString("subject_rank")
                        });
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
