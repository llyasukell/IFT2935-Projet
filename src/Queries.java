public class Queries {

    // --- AUTHENTIFICATION ET UTILISATEURS ---
    // Source: get_user_by_email.sql
    public static final String FIND_USER_BY_EMAIL =
        "SELECT id, nom, prenom, courriel, mot_de_passe FROM Utilisateur WHERE courriel = ?";

    public static final String IS_ACHETEUR =
        "SELECT 1 FROM Acheteur WHERE id_utilisateur = ?";

    public static final String IS_ANNONCEUR =
        "SELECT 1 FROM Annonceur WHERE id_utilisateur = ?";

    public static final String IS_EXPERT =
        "SELECT 1 FROM Expert WHERE id_utilisateur = ?";


    // --- GESTION DES PRODUITS (Annonceur & Recherche) ---
    // Source: get_products_by_seller.sql
    public static final String PRODUITS_DE_ANNONCEUR =
        "SELECT Produit.*, Utilisateur.nom, Utilisateur.prenom FROM Produit " +
        "JOIN Annonceur ON Produit.id_annonceur = Annonceur.id_utilisateur " +
        "JOIN Utilisateur ON Annonceur.id_utilisateur = Utilisateur.id " +
        "WHERE Produit.id_annonceur = ?";

    // Source: get_valid_products_for_buyer.sql
    public static final String PRODUITS_VALIDES_POUR_ACHETEUR =
        "SELECT DISTINCT Produit.* FROM Produit " +
        "JOIN Estimation ON Estimation.id_produit = Produit.id_produit " +
        "JOIN Valide ON Valide.id_estimation = Estimation.id_estimation " +
        "WHERE Valide.decision = 'valide'";

    public static final String INSERT_PRODUIT =
        "INSERT INTO Produit (nom_produit, etat, description, prix_souhaite, id_annonceur) " +
        "VALUES (?, ?::etat_produit, ?, ?, ?) RETURNING id_produit";

    // Source: search_productName.sql
    public static final String SEARCH_PRODUIT_NOM =
        "SELECT * FROM Produit WHERE nom_produit LIKE CONCAT('%', ?, '%')";

    // Source: search_by_description.sql
    public static final String SEARCH_PRODUIT_DESCRIPTION =
        "SELECT * FROM Produit WHERE description LIKE CONCAT('%', ?, '%')";

    // Source: between_prices.sql
    public static final String SEARCH_PRODUIT_ENTRE_PRIX =
        "SELECT * FROM Produit WHERE prix_souhaite BETWEEN ? AND ?";

    // Source: get_products_by_state.sql
    public static final String GET_PRODUITS_PAR_ETAT =
        "SELECT Produit.*, Utilisateur.nom, Utilisateur.prenom FROM Produit " +
        "JOIN Annonceur ON Produit.id_annonceur = Annonceur.id_utilisateur " +
        "JOIN Utilisateur ON Annonceur.id_utilisateur = Utilisateur.id " +
        "WHERE Produit.etat = ?";


    // --- ESTIMATIONS ET EXPERTS ---
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

    // Source: get_pending_estimation.sql
    public static final String ESTIMATION_EN_ATTENTE =
        "SELECT Estimation.id_estimation FROM Estimation " +
        "JOIN Valide ON Valide.id_estimation = Estimation.id_estimation " +
        "WHERE Estimation.id_produit = ? AND Valide.id_annonceur = ? " +
        "AND Valide.decision = 'en_attente' LIMIT 1";

    // Source: get_estimates_on_product.sql
    public static final String TOUTES_ESTIMATIONS_PRODUIT =
        "SELECT Estimation.*, Utilisateur.nom, Utilisateur.prenom, Valide.decision " +
        "FROM Estimation JOIN Expert ON Estimation.id_expert = Expert.id_utilisateur " +
        "JOIN Utilisateur ON Expert.id_utilisateur = Utilisateur.id " +
        "LEFT JOIN Valide ON Estimation.id_estimation = Valide.id_estimation " +
        "WHERE Estimation.id_produit = ?";

    // Source: get_VALID_estimates_on_product.sql
    public static final String ESTIMATIONS_VALIDEES_PRODUIT =
        "SELECT Estimation.*, Utilisateur.nom, Utilisateur.prenom " +
        "FROM Estimation JOIN Expert ON Estimation.id_expert = Expert.id_utilisateur " +
        "JOIN Utilisateur ON Expert.id_utilisateur = Utilisateur.id " +
        "JOIN Valide ON Estimation.id_estimation = Valide.id_estimation " +
        "WHERE Estimation.id_produit = ? AND Valide.decision = 'valide'";


    // --- OFFRES ET VENTES ---
    public static final String INSERT_OFFRE =
        "INSERT INTO Offre (prix_propose, id_produit) VALUES (?, ?) RETURNING id_offre";

    public static final String INSERT_PROPOSE =
        "INSERT INTO propose (id_offre, id_utilisateur) VALUES (?, ?)";

    // Source: get_offers_on_product.sql
    public static final String OFFRES_SUR_PRODUIT =
        "SELECT Offre.*, Utilisateur.nom, Utilisateur.prenom, propose.date_proposition " +
        "FROM Offre JOIN propose ON Offre.id_offre = propose.id_offre " +
        "JOIN Acheteur ON propose.id_utilisateur = Acheteur.id_utilisateur " +
        "JOIN Utilisateur ON Acheteur.id_utilisateur = Utilisateur.id " +
        "WHERE Offre.id_produit = ?";

    // Source: viewFullOfferInfo.sql
    public static final String INFO_COMPLETE_OFFRE =
        "SELECT Offre.*, Produit.nom_produit, Produit.description, Produit.etat, Produit.prix_souhaite, " +
        "U_Acheteur.nom AS acheteur_nom, U_Acheteur.prenom AS acheteur_prenom, " +
        "U_Annonceur.nom AS annonceur_nom, U_Annonceur.prenom AS annonceur_prenom, propose.date_proposition " +
        "FROM Offre JOIN Produit ON Offre.id_produit = Produit.id_produit " +
        "JOIN Annonceur ON Produit.id_annonceur = Annonceur.id_utilisateur " +
        "JOIN Utilisateur U_Annonceur ON Annonceur.id_utilisateur = U_Annonceur.id " +
        "JOIN propose ON Offre.id_offre = propose.id_offre " +
        "JOIN Acheteur ON propose.id_utilisateur = Acheteur.id_utilisateur " +
        "JOIN Utilisateur U_Acheteur ON Acheteur.id_utilisateur = U_Acheteur.id " +
        "WHERE Offre.id_offre = ?";

    // Source: vente_conclu.sql
    public static final String VENTE_CONCLU =
        "SELECT CASE " +
        "WHEN prix_souhaite IS NOT NULL AND ? < prix_souhaite THEN 'REFUSE' " +
        "WHEN prix_souhaite IS NOT NULL AND ? >= prix_souhaite THEN 'ACCEPT' " +
        "ELSE 'PENDING' END AS decision FROM Produit WHERE id_produit = ?";


    // --- ANALYSE ET STATISTIQUES ---
    // Source: stats_performance_experts.sql
    public static final String STATS_EXPERTS =
        "SELECT U_Expert.nom, U_Expert.prenom, Sum(Estimation.prix_estimation) AS total_estime, " +
        "COUNT(Estimation.id_estimation) AS nb_estimation " +
        "FROM Expert JOIN Utilisateur AS U_Expert ON Expert.id_utilisateur = U_Expert.id " +
        "JOIN Estimation ON U_Expert.id_utilisateur = Estimation.id_expert " +
        "JOIN Valide ON Estimation.id_estimation = Valide.id_estimation " +
        "WHERE Valide.decision = 'valide' GROUP BY U_Expert.nom, U_Expert.prenom " +
        "HAVING COUNT(Estimation.id_estimation) > 1 ORDER BY total_estime DESC";

    // Source: consultation_prix_valides.sql
    public static final String LISTE_PRIX_OFFICIELS =
        "SELECT U_Annonceur.nom AS nom_annonceur, Produit.nom_produit, U_Expert.nom AS nom_expert, " +
        "Estimation.prix_estimation FROM Annonceur " +
        "JOIN Utilisateur U_Annonceur ON Annonceur.id_utilisateur = U_Annonceur.id " +
        "JOIN Produit ON Annonceur.id_utilisateur = Produit.id_annonceur " +
        "JOIN Estimation ON Estimation.id_produit = Produit.id_produit " +
        "JOIN Expert ON Estimation.id_expert = Expert.id_utilisateur " +
        "JOIN Utilisateur U_Expert ON Expert.id_utilisateur = U_Expert.id " +
        "JOIN Valide ON Valide.id_estimation = Estimation.id_estimation " +
        "WHERE Valide.decision = 'valide'";

    // Source: get_products_without_offers.sql
    public static final String PRODUITS_SANS_OFFRES =
        "SELECT Produit.*, Utilisateur.nom, Utilisateur.prenom FROM Produit " +
        "JOIN Annonceur ON Produit.id_annonceur = Annonceur.id_utilisateur " +
        "JOIN Utilisateur ON Annonceur.id_utilisateur = Utilisateur.id " +
        "WHERE NOT EXISTS (SELECT 1 FROM Offre WHERE Offre.id_produit = Produit.id_produit)";

    // Source: get_recent_offers.sql
    public static final String OFFRES_RECENTES =
        "SELECT Offre.*, Produit.nom_produit, Utilisateur.nom, Utilisateur.prenom, propose.date_proposition " +
        "FROM Offre JOIN propose ON Offre.id_offre = propose.id_offre " +
        "JOIN Acheteur ON propose.id_utilisateur = Acheteur.id_utilisateur " +
        "JOIN Utilisateur ON Acheteur.id_utilisateur = Utilisateur.id " +
        "JOIN Produit ON Offre.id_produit = Produit.id_produit " +
        "WHERE propose.date_proposition >= CURRENT_DATE - INTERVAL '30 days' " +
        "ORDER BY propose.date_proposition DESC";
}