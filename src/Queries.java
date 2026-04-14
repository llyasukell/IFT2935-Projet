public class Queries {

    public static final String FIND_USER_BY_EMAIL =
        "SELECT id, nom, prenom, courriel, mot_de_passe FROM Utilisateur WHERE courriel = ?";

    public static final String IS_ACHETEUR =
        "SELECT 1 FROM Acheteur WHERE id_utilisateur = ?";

    public static final String IS_ANNONCEUR =
        "SELECT 1 FROM Annonceur WHERE id_utilisateur = ?";

    public static final String IS_EXPERT =
        "SELECT 1 FROM Expert WHERE id_utilisateur = ?";

    public static final String PRODUITS_DE_ANNONCEUR =
        "SELECT p.id_produit, p.nom_produit, p.etat, p.prix_souhaite, " +
        "       COALESCE(v.decision::text, 'en_attente') AS statut " +
        "FROM Produit p " +
        "LEFT JOIN Estimation e ON e.id_produit = p.id_produit " +
        "LEFT JOIN Valide v ON v.id_estimation = e.id_estimation " +
        "WHERE p.id_annonceur = ?";

    public static final String INSERT_PRODUIT =
        "INSERT INTO Produit (nom_produit, etat, description, prix_souhaite, id_annonceur) " +
        "VALUES (?, ?::etat_produit, ?, ?, ?) RETURNING id_produit";

    public static final String RANDOM_EXPERT =
        "SELECT id_utilisateur FROM Expert ORDER BY random() LIMIT 1";

    public static final String INSERT_ESTIMATION =
        "INSERT INTO Estimation (prix_estimation, id_expert, id_produit) " +
        "VALUES (?, ?, ?) RETURNING id_estimation";

    public static final String INSERT_VALIDE_EN_ATTENTE =
        "INSERT INTO Valide (id_annonceur, id_estimation, decision) " +
        "VALUES (?, ?, 'en_attente')";

    public static final String UPDATE_VALIDE_DECISION =
        "UPDATE Valide SET decision = ?::etat_decision " +
        "WHERE id_annonceur = ? AND id_estimation = ?";

    public static final String ESTIMATION_EN_ATTENTE =
        "SELECT e.id_estimation FROM Estimation e " +
        "JOIN Valide v ON v.id_estimation = e.id_estimation " +
        "WHERE e.id_produit = ? AND v.id_annonceur = ? AND v.decision = 'en_attente' " +
        "ORDER BY e.date_estimation DESC LIMIT 1";

    public static final String OFFRES_DU_PRODUIT =
        "SELECT o.id_offre, o.prix_propose, u.courriel, " +
        "       (SELECT prix_estimation FROM Estimation e " +
        "         WHERE e.id_produit = o.id_produit " +
        "         ORDER BY e.date_estimation DESC LIMIT 1) AS prix_estimation " +
        "FROM Offre o " +
        "JOIN propose pr ON pr.id_offre = o.id_offre " +
        "JOIN Utilisateur u ON u.id = pr.id_utilisateur " +
        "WHERE o.id_produit = ?";

    public static final String PRODUITS_VALIDES_POUR_ACHETEUR =
        "SELECT p.id_produit, p.nom_produit, p.etat, p.description, p.prix_souhaite " +
        "FROM Produit p " +
        "JOIN Estimation e ON e.id_produit = p.id_produit " +
        "JOIN Valide v ON v.id_estimation = e.id_estimation " +
        "WHERE v.decision = 'valide'";

    public static final String SEARCH_PRODUIT_NOM =
        "SELECT p.id_produit, p.nom_produit, p.etat, p.description, p.prix_souhaite " +
        "FROM Produit p " +
        "JOIN Estimation e ON e.id_produit = p.id_produit " +
        "JOIN Valide v ON v.id_estimation = e.id_estimation " +
        "WHERE v.decision = 'valide' AND p.nom_produit LIKE CONCAT('%', ?, '%')";

    public static final String SEARCH_PRODUIT_ENTRE_PRIX =
        "SELECT p.id_produit, p.nom_produit, p.etat, p.description, p.prix_souhaite " +
        "FROM Produit p " +
        "JOIN Estimation e ON e.id_produit = p.id_produit " +
        "JOIN Valide v ON v.id_estimation = e.id_estimation " +
        "WHERE v.decision = 'valide' AND p.prix_souhaite BETWEEN ? AND ?";

    public static final String INSERT_OFFRE =
        "INSERT INTO Offre (prix_propose, id_produit) VALUES (?, ?) RETURNING id_offre";

    public static final String INSERT_PROPOSE =
        "INSERT INTO propose (id_offre, id_utilisateur) VALUES (?, ?)";

    public static final String VENTE_CONCLU =
        "SELECT CASE " +
        "         WHEN e.prix_estimation IS NULL THEN 'PENDING' " +
        "         WHEN ? >= e.prix_estimation THEN 'ACCEPT' " +
        "         ELSE 'REFUSE' " +
        "       END AS decision " +
        "FROM Produit p " +
        "LEFT JOIN Estimation e ON e.id_produit = p.id_produit " +
        "WHERE p.id_produit = ?";
}
