import java.sql.*;
import java.util.*;

public class StudentService {
    public boolean insertStudent(int id, String name, String className) {
        String query = "INSERT INTO Students (student_id, name, class_name) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.setString(2, name);
            stmt.setString(3, className);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertMark(int studentId, int subjectId, int marks) {
        String query = "INSERT INTO Marks (student_id, subject_id, marks_obtained) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, subjectId);
            pstmt.setInt(3, marks);

            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateMark(int studentId, int subjectId, int newMarks) {
        String query = "UPDATE Marks SET marks_obtained = ? WHERE student_id = ? AND subject_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, newMarks);
            pstmt.setInt(2, studentId);
            pstmt.setInt(3, subjectId);

            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteMark(int studentId, int subjectId) {
        String query = "DELETE FROM Marks WHERE student_id = ? AND subject_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, subjectId);

            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String[]> getAllStudents() {
        List<String[]> list = new ArrayList<>();
        String query = "SELECT student_id, name, class_name FROM Students";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new String[]{
                        String.valueOf(rs.getInt("student_id")),
                        rs.getString("name"),
                        rs.getString("class_name")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String[]> getIndividualMarks(int studentId) {
        List<String[]> list = new ArrayList<>();
        String query = "SELECT m.subject_id, m.marks_obtained FROM Marks m WHERE m.student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            int total = 0;
            while (rs.next()) {
                int marks = rs.getInt("marks_obtained");
                total += marks;
                list.add(new String[]{
                        String.valueOf(rs.getInt("subject_id")),
                        String.valueOf(marks)
                });
            }
            list.add(new String[]{"Total", String.valueOf(total)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
