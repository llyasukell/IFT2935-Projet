-- Get recent offers (last 30 days)
-- Permet de récupérer les offres faites au cours des 30 derniers jours, ainsi que les informations sur le produit concerné, l'acheteur qui a fait l'offre et la date de proposition.
BEGIN TRANSACTION;
SELECT Offre.*, Produit.nom_produit, Utilisateur.nom, Utilisateur.prenom, propose.date_proposition
FROM Offre
JOIN propose ON Offre.id_offre = propose.id_offre
JOIN Acheteur ON propose.id_utilisateur = Acheteur.id_utilisateur
JOIN Utilisateur ON Acheteur.id_utilisateur = Utilisateur.id
JOIN Produit ON Offre.id_produit = Produit.id_produit
WHERE propose.date_proposition >= CURRENT_DATE - INTERVAL '30 days'
ORDER BY propose.date_proposition DESC;
END TRANSACTION;