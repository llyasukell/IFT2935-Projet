import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main().creerFenetreConnexion();
            }
        });
    }

    private JFrame frame;
    private JTextField champCourriel;
    private JPasswordField champMotDePasse;

    private void creerFenetreConnexion() {
        frame = new JFrame("Kijiji UdeM - Connexion");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel champs = new JPanel();
        champs.setLayout(new BoxLayout(champs, BoxLayout.Y_AXIS));

        champCourriel = new JTextField(25);
        champMotDePasse = new JPasswordField(25);

        champs.add(new JLabel("Courriel :"));
        champs.add(champCourriel);
        champs.add(new JLabel("Mot de passe :"));
        champs.add(champMotDePasse);

        JButton bouton = new JButton("Connexion");
        bouton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tenterConnexion();
            }
        });

        JPanel boutons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        boutons.add(bouton);

        frame.setLayout(new BorderLayout(10, 10));
        frame.add(champs, BorderLayout.CENTER);
        frame.add(boutons, BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void tenterConnexion() {
        String courriel = champCourriel.getText().trim();
        String motDePasse = new String(champMotDePasse.getPassword());

        if (courriel.isEmpty() || motDePasse.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Veuillez remplir les deux champs.");
            return;
        }

        try (Connection c = Db.getConnection()) {
            int idUtilisateur = verifierIdentifiants(c, courriel, motDePasse);
            if (idUtilisateur < 0) {
                JOptionPane.showMessageDialog(frame, "Courriel ou mot de passe invalide.");
                return;
            }

            String role = determinerRole(c, idUtilisateur);
            ouvrirGuiSelonRole(role, idUtilisateur);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, ex.getMessage());
        }
    }

    private int verifierIdentifiants(Connection c, String courriel, String motDePasse) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(Queries.FIND_USER_BY_EMAIL)) {
            ps.setString(1, courriel);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return -1;
                String motStocke = rs.getString("mot_de_passe");
                if (!motDePasse.equals(motStocke)) return -1;
                return rs.getInt("id");
            }
        }
    }

    private String determinerRole(Connection c, int idUtilisateur) throws SQLException {
        if (estDansTable(c, Queries.IS_ANNONCEUR, idUtilisateur)) return "annonceur";
        if (estDansTable(c, Queries.IS_ACHETEUR, idUtilisateur)) return "acheteur";
        if (estDansTable(c, Queries.IS_EXPERT, idUtilisateur)) return "expert";
        return "inconnu";
    }

    private boolean estDansTable(Connection c, String requete, int id) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(requete)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void ouvrirGuiSelonRole(String role, int idUtilisateur) {
        if (role.equals("expert")) {
            JOptionPane.showMessageDialog(frame,
                "Les experts agissent via popup, pas via login.");
            return;
        }
        if (role.equals("annonceur")) {
            new AnnonceurGUI(idUtilisateur).setVisible(true);
            frame.dispose();
            return;
        }
        if (role.equals("acheteur")) {
            new AcheteurGUI(idUtilisateur).setVisible(true);
            frame.dispose();
            return;
        }
        JOptionPane.showMessageDialog(frame, "Utilisateur sans rôle défini.");
    }
}
