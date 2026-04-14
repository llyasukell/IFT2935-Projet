import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ExpertDialog extends JDialog {

    private final int idAnnonceur;
    private final int idProduit;
    private final BigDecimal prixSouhaite;

    private int idEstimation = -1;
    private BigDecimal prixEstimation;

    public ExpertDialog(Frame parent, int idAnnonceur, int idProduit, BigDecimal prixSouhaite) {
        super(parent, "Estimation de l'expert", true);
        this.idAnnonceur = idAnnonceur;
        this.idProduit = idProduit;
        this.prixSouhaite = prixSouhaite;
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        construire();
    }

    private void construire() {
        try (Connection c = Db.getConnection()) {
            int idExpert = choisirExpertAleatoire(c);
            prixEstimation = calculerPrix(prixSouhaite);
            idEstimation = insererEstimation(c, idExpert, idProduit, prixEstimation);
            insererValideEnAttente(c, idAnnonceur, idEstimation);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(getParent(), ex.getMessage());
            dispose();
            return;
        }

        JPanel contenu = new JPanel(new BorderLayout(10, 10));
        contenu.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        contenu.add(new JLabel("Un expert a estimé votre produit à "
                + prixEstimation.toPlainString() + " $"), BorderLayout.CENTER);

        JButton ok = new JButton("OK");
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        JPanel sud = new JPanel(new FlowLayout(FlowLayout.CENTER));
        sud.add(ok);
        contenu.add(sud, BorderLayout.SOUTH);

        setContentPane(contenu);
        pack();
        setLocationRelativeTo(getParent());
    }

    public int getIdEstimation() {
        return idEstimation;
    }

    public BigDecimal getPrixEstimation() {
        return prixEstimation;
    }

    private static int choisirExpertAleatoire(Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(Queries.RANDOM_EXPERT);
             ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) throw new SQLException("Aucun expert disponible.");
            return rs.getInt(1);
        }
    }

    private static BigDecimal calculerPrix(BigDecimal prixSouhaite) {
        double facteur = 0.7 + Math.random() * 0.6;
        return prixSouhaite.multiply(BigDecimal.valueOf(facteur))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private static int insererEstimation(Connection c, int idExpert, int idProduit, BigDecimal prix)
            throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(Queries.INSERT_ESTIMATION)) {
            ps.setBigDecimal(1, prix);
            ps.setInt(2, idExpert);
            ps.setInt(3, idProduit);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("Insertion Estimation échouée.");
                return rs.getInt(1);
            }
        }
    }

    private static void insererValideEnAttente(Connection c, int idAnnonceur, int idEstimation)
            throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(Queries.INSERT_VALIDE_EN_ATTENTE)) {
            ps.setInt(1, idAnnonceur);
            ps.setInt(2, idEstimation);
            ps.executeUpdate();
        }
    }
}
