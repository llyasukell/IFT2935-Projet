-- ? = id_acheteur
-- Permet de récupérer les offres faites par un acheteur et les informations sur le produit concerné et la date de proposition.
BEGIN TRANSACTION;
SELECT Offre.*, Produit.nom_produit, Produit.description, propose.date_proposition
FROM Offre
JOIN propose ON Offre.id_offre = propose.id_offre
JOIN Produit ON Offre.id_produit = Produit.id_produit
WHERE propose.id_utilisateur = ?;
END TRANSACTION;