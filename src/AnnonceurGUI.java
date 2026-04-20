import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class AnnonceurGUI extends JFrame {

    private final int idAnnonceur;

    private JTable tableProduits;
    private JTextField champNom;
    private JComboBox<String> champEtat;
    private JTextArea champDescription;
    private JTextField champPrix;

    public AnnonceurGUI(int idAnnonceur) {
        this.idAnnonceur = idAnnonceur;
        setTitle("Annonceur - id " + idAnnonceur);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane onglets = new JTabbedPane();
        onglets.addTab("Mes produits", construireOngletProduits());
        onglets.addTab("Ajouter un produit", construireOngletAjout());

        setContentPane(onglets);
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel construireOngletProduits() {
        JPanel panneau = new JPanel(new BorderLayout(5, 5));
        panneau.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        tableProduits = new JTable();
        panneau.add(new JScrollPane(tableProduits), BorderLayout.CENTER);

        JButton bVoirOffres = new JButton("Voir offres");
        JButton bValider = new JButton("Valider estimation");
        JButton bRafraichir = new JButton("Rafraîchir");

        bVoirOffres.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                voirOffres();
            }
        });
        bValider.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validerEstimationLignesSelectionnee();
            }
        });
        bRafraichir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rafraichirProduits();
            }
        });

        JPanel boutons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        boutons.add(bVoirOffres);
        boutons.add(bValider);
        boutons.add(bRafraichir);
        panneau.add(boutons, BorderLayout.SOUTH);

        rafraichirProduits();
        return panneau;
    }

    private JPanel construireOngletAjout() {
        JPanel panneau = new JPanel();
        panneau.setLayout(new BoxLayout(panneau, BoxLayout.Y_AXIS));
        panneau.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        champNom = new JTextField(25);
        champEtat = new JComboBox<>(new String[] {"neuf", "usagé", "abimé"});
        champDescription = new JTextArea(5, 25);
        champPrix = new JTextField(10);

        brider(champNom);
        brider(champEtat);
        brider(champPrix);

        ajouterLigne(panneau, "Nom du produit :", champNom);
        ajouterLigne(panneau, "État :", champEtat);
        ajouterLigne(panneau, "Description :", new JScrollPane(champDescription));
        ajouterLigne(panneau, "Prix souhaité :", champPrix);

        JButton soumettre = new JButton("Soumettre");
        soumettre.setAlignmentX(Component.LEFT_ALIGNMENT);
        soumettre.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                soumettreProduit();
            }
        });
        panneau.add(soumettre);

        return panneau;
    }

    private static void ajouterLigne(JPanel parent, String texte, Component champ) {
        JLabel label = new JLabel(texte);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (champ instanceof javax.swing.JComponent) {
            ((javax.swing.JComponent) champ).setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        parent.add(label);
        parent.add(champ);
        parent.add(javax.swing.Box.createVerticalStrut(8));
    }

    private static void brider(javax.swing.JComponent c) {
        Dimension pref = c.getPreferredSize();
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, pref.height));
    }

    private static String etatVersDb(String affichage) {
        if ("usagé".equals(affichage)) return "usage";
        if ("abimé".equals(affichage)) return "abime";
        return "neuf";
    }

    private void rafraichirProduits() {
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(Queries.PRODUITS_DE_ANNONCEUR)) {
            ps.setInt(1, idAnnonceur);
            try (ResultSet rs = ps.executeQuery()) {
                Tables.fillTable(tableProduits, rs);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void soumettreProduit() {
        String nom = champNom.getText().trim();
        String etat = etatVersDb((String) champEtat.getSelectedItem());
        String description = champDescription.getText().trim();
        String prixTexte = champPrix.getText().trim();

        if (nom.isEmpty() || prixTexte.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nom et prix sont obligatoires.");
            return;
        }

        BigDecimal prix;
        try {
            prix = new BigDecimal(prixTexte);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Prix invalide.");
            return;
        }

        try (Connection c = Db.getConnection()) {
            int idProduit = insererProduit(c, nom, etat, description, prix);

            ExpertDialog dlg = new ExpertDialog(this, idAnnonceur, idProduit, prix);
            dlg.setVisible(true);

            int idEstimation = dlg.getIdEstimation();
            if (idEstimation < 0) return;

            int rep = JOptionPane.showConfirmDialog(this,
                    "Accepter l'estimation?",
                    "Décision", JOptionPane.YES_NO_OPTION);
            String decision = rep == JOptionPane.YES_OPTION ? "valide" : "rejete";
            majDecision(c, idEstimation, decision);

            JOptionPane.showMessageDialog(this, "Produit enregistré (décision : " + decision + ").");
            viderFormulaire();
            rafraichirProduits();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private int insererProduit(Connection c, String nom, String etat, String description, BigDecimal prix)
            throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(Queries.INSERT_PRODUIT)) {
            ps.setString(1, nom);
            ps.setString(2, etat);
            ps.setString(3, description);
            ps.setBigDecimal(4, prix);
            ps.setInt(5, idAnnonceur);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("Insertion Produit échouée.");
                return rs.getInt(1);
            }
        }
    }

    private void majDecision(Connection c, int idEstimation, String decision) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(Queries.UPDATE_VALIDE_DECISION)) {
            ps.setString(1, decision);
            ps.setInt(2, idAnnonceur);
            ps.setInt(3, idEstimation);
            ps.executeUpdate();
        }
    }

    private void viderFormulaire() {
        champNom.setText("");
        champEtat.setSelectedIndex(0);
        champDescription.setText("");
        champPrix.setText("");
    }

    private Integer idProduitSelectionne() {
        int ligne = tableProduits.getSelectedRow();
        if (ligne < 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un produit.");
            return null;
        }
        Object val = tableProduits.getValueAt(ligne, 0);
        return ((Number) val).intValue();
    }

    private void voirOffres() {
        Integer idProduit = idProduitSelectionne();
        if (idProduit == null) return;

        JDialog fenetre = new JDialog(this, "Offres sur produit " + idProduit, true);
        fenetre.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JTable table = new JTable();
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(Queries.OFFRES_SUR_PRODUIT)) {
            ps.setInt(1, idProduit);
            try (ResultSet rs = ps.executeQuery()) {
                Tables.fillTable(table, rs);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            return;
        }

        marquerVendus(table);

        fenetre.setLayout(new BorderLayout());
        fenetre.add(new JScrollPane(table), BorderLayout.CENTER);
        fenetre.pack();
        fenetre.setLocationRelativeTo(this);
        fenetre.setVisible(true);
    }

    private void marquerVendus(JTable table) {
        int colPrix = -1;
        int colEstim = -1;
        for (int i = 0; i < table.getColumnCount(); i++) {
            String nomCol = table.getColumnName(i);
            if (nomCol.equalsIgnoreCase("prix_propose")) colPrix = i;
            if (nomCol.equalsIgnoreCase("prix_estimation")) colEstim = i;
        }
        if (colPrix < 0 || colEstim < 0) return;

        for (int r = 0; r < table.getRowCount(); r++) {
            Object propose = table.getValueAt(r, colPrix);
            Object estim = table.getValueAt(r, colEstim);
            if (propose instanceof Number && estim instanceof Number) {
                BigDecimal p = new BigDecimal(propose.toString());
                BigDecimal e = new BigDecimal(estim.toString());
                if (p.compareTo(e) >= 0) {
                    table.setValueAt("VENDU", r, colPrix);
                }
            }
        }
    }

    private void validerEstimationLignesSelectionnee() {
        Integer idProduit = idProduitSelectionne();
        if (idProduit == null) return;

        try (Connection c = Db.getConnection()) {
            Integer idEstimation = chercherEstimationEnAttente(c, idProduit);
            if (idEstimation == null) {
                JOptionPane.showMessageDialog(this,
                        "Aucune estimation en attente pour ce produit.");
                return;
            }
            int rep = JOptionPane.showConfirmDialog(this,
                    "Accepter l'estimation?",
                    "Décision", JOptionPane.YES_NO_OPTION);
            String decision = rep == JOptionPane.YES_OPTION ? "valide" : "rejete";
            majDecision(c, idEstimation, decision);
            rafraichirProduits();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private Integer chercherEstimationEnAttente(Connection c, int idProduit) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(Queries.ESTIMATION_EN_ATTENTE)) {
            ps.setInt(1, idProduit);
            ps.setInt(2, idAnnonceur);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return rs.getInt(1);
            }
        }
    }
}
