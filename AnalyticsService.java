import java.sql.*;
import java.util.*;

public class AnalyticsService {

    // -----------------------------------------------
    // 1️⃣  Get Overall Total Marks + Rank
    // -----------------------------------------------
    public List<String[]> getStudentTotalRanks() {
        List<String[]> list = new ArrayList<>();
        String query = "SELECT student_id, name, total_marks, total_rank " +
                       "FROM student_total_rank ORDER BY total_rank, total_marks DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                list.add(new String[] {
                        rs.getString("student_id"),
                        rs.getString("name"),
                        rs.getString("total_marks"),
                        rs.getString("total_rank")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // -----------------------------------------------
    // 2️⃣  Get All Subjects
    // -----------------------------------------------
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

    // -----------------------------------------------
    // 3️⃣  Get Subject-wise Ranks
    // -----------------------------------------------
    public List<String[]> getSubjectRanks(int subjectId) {
        List<String[]> list = new ArrayList<>();
        String query = "SELECT subject_id, subject_name, student_id, student_name, " +
                       "marks_obtained, subject_rank " +
                       "FROM subject_ranks WHERE subject_id = ? " +
                       "ORDER BY subject_rank, marks_obtained DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, subjectId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[] {
                            rs.getString("subject_id"),
                            rs.getString("subject_name"),
                            rs.getString("student_id"),
                            rs.getString("student_name"),
                            rs.getString("marks_obtained"),
                            rs.getString("subject_rank")
                    });
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
