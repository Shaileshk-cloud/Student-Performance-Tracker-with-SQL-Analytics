import java.sql.*;
import java.util.*;

public class AnalyticsService {
    public List<String[]> getStudentRanks() {
        List<String[]> list = new ArrayList<>();
        String query = """
                SELECT student_id, name, total_marks,
                       DENSE_RANK() OVER (ORDER BY total_marks DESC) AS rank
                FROM (
                    SELECT s.student_id, s.name, SUM(m.marks_obtained) AS total_marks
                    FROM Students s
                    JOIN Marks m ON s.student_id = m.student_id
                    GROUP BY s.student_id
                ) AS derived;
                """;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new String[]{
                        rs.getString("student_id"),
                        rs.getString("name"),
                        rs.getString("total_marks"),
                        rs.getString("rank")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}

