import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

public class AcheteurGUI extends JFrame {

    private final int idAcheteur;

    private JTable tableProduits;
    private JTextField champNom;
    private JTextField champPrixMin;
    private JTextField champPrixMax;

    public AcheteurGUI(int idAcheteur) {
        this.idAcheteur = idAcheteur;
        setTitle("Acheteur - id " + idAcheteur);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(5, 5));
        add(construireBarreRecherche(), BorderLayout.NORTH);
        add(construireTable(), BorderLayout.CENTER);
        add(construireBoutons(), BorderLayout.SOUTH);

        rafraichirTous();
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel construireBarreRecherche() {
        JPanel barre = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barre.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

        champNom = new JTextField(15);
        champPrixMin = new JTextField(6);
        champPrixMax = new JTextField(6);

        JButton bNom = new JButton("Rechercher par nom");
        JButton bPrix = new JButton("Rechercher par prix");
        JButton bTous = new JButton("Tous");

        bNom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rechercherParNom();
            }
        });
        bPrix.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rechercherParPrix();
            }
        });
        bTous.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rafraichirTous();
            }
        });

        barre.add(new JLabel("Nom :"));
        barre.add(champNom);
        barre.add(bNom);
        barre.add(new JLabel("Prix min :"));
        barre.add(champPrixMin);
        barre.add(new JLabel("Prix max :"));
        barre.add(champPrixMax);
        barre.add(bPrix);
        barre.add(bTous);
        return barre;
    }

    private JScrollPane construireTable() {
        tableProduits = new JTable();
        return new JScrollPane(tableProduits);
    }

    private JPanel construireBoutons() {
        JPanel sud = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton bOffre = new JButton("Faire une offre");
        bOffre.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                faireUneOffre();
            }
        });
        sud.add(bOffre);
        return sud;
    }

    private void rafraichirTous() {
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(Queries.PRODUITS_VALIDES_POUR_ACHETEUR);
             ResultSet rs = ps.executeQuery()) {
            Tables.fillTable(tableProduits, rs);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void rechercherParNom() {
        String nom = champNom.getText().trim();
        if (nom.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Entrez un nom à rechercher.");
            return;
        }
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(Queries.SEARCH_PRODUIT_NOM)) {
            ps.setString(1, nom);
            try (ResultSet rs = ps.executeQuery()) {
                Tables.fillTable(tableProduits, rs);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void rechercherParPrix() {
        BigDecimal min, max;
        try {
            min = new BigDecimal(champPrixMin.getText().trim());
            max = new BigDecimal(champPrixMax.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Prix min et max doivent être numériques.");
            return;
        }
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(Queries.SEARCH_PRODUIT_ENTRE_PRIX)) {
            ps.setBigDecimal(1, min);
            ps.setBigDecimal(2, max);
            try (ResultSet rs = ps.executeQuery()) {
                Tables.fillTable(tableProduits, rs);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void faireUneOffre() {
        int ligne = tableProduits.getSelectedRow();
        if (ligne < 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un produit.");
            return;
        }
        int idProduit = ((Number) tableProduits.getValueAt(ligne, 0)).intValue();

        BigDecimal prix = demanderPrix();
        if (prix == null) return;

        try (Connection c = Db.getConnection()) {
            int idOffre = insererOffre(c, prix, idProduit);
            insererPropose(c, idOffre, idAcheteur);

            if (venteConclue(c, prix, idProduit)) {
                JOptionPane.showMessageDialog(this, "Vente conclue!");
            } else {
                JOptionPane.showMessageDialog(this, "Offre enregistrée.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private BigDecimal demanderPrix() {
        JDialog dlg = new JDialog(this, "Faire une offre", true);
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JTextField champ = new JTextField(10);
        JButton ok = new JButton("Envoyer");
        JButton annuler = new JButton("Annuler");

        final BigDecimal[] resultat = new BigDecimal[1];

        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    resultat[0] = new BigDecimal(champ.getText().trim());
                    dlg.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dlg, "Prix invalide.");
                }
            }
        });
        annuler.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dlg.dispose();
            }
        });

        JPanel centre = new JPanel(new FlowLayout());
        centre.add(new JLabel("Prix proposé :"));
        centre.add(champ);
        JPanel sud = new JPanel(new FlowLayout());
        sud.add(ok);
        sud.add(annuler);

        dlg.setLayout(new BorderLayout());
        dlg.add(centre, BorderLayout.CENTER);
        dlg.add(sud, BorderLayout.SOUTH);
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);

        return resultat[0];
    }

    private int insererOffre(Connection c, BigDecimal prix, int idProduit) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(Queries.INSERT_OFFRE)) {
            ps.setBigDecimal(1, prix);
            ps.setInt(2, idProduit);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("Insertion Offre échouée.");
                return rs.getInt(1);
            }
        }
    }

    private void insererPropose(Connection c, int idOffre, int idUtilisateur) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(Queries.INSERT_PROPOSE)) {
            ps.setInt(1, idOffre);
            ps.setInt(2, idUtilisateur);
            ps.executeUpdate();
        }
    }

    private boolean venteConclue(Connection c, BigDecimal prix, int idProduit) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(Queries.VENTE_CONCLU)) {
            ps.setBigDecimal(1, prix);
            ps.setInt(2, idProduit);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return false;
                return "ACCEPT".equals(rs.getString(1));
            }
        }
    }
}
