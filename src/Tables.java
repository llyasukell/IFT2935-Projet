import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Tables {

    public static void fillTable(JTable t, ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int nbCols = meta.getColumnCount();

        Vector<String> entetes = new Vector<>();
        for (int i = 1; i <= nbCols; i++) {
            entetes.add(meta.getColumnLabel(i));
        }

        Vector<Vector<Object>> lignes = new Vector<>();
        while (rs.next()) {
            Vector<Object> ligne = new Vector<>();
            for (int i = 1; i <= nbCols; i++) {
                ligne.add(rs.getObject(i));
            }
            lignes.add(ligne);
        }

        DefaultTableModel model = new DefaultTableModel(lignes, entetes) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        t.setModel(model);
    }
}
