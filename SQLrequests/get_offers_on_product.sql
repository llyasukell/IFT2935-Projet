-- ? = id_produit
-- Permet de récupérer les offres faites sur un produit et les informations sur les acheteurs qui ont fait ces offres et la date de proposition.
BEGIN TRANSACTION;
SELECT Offre.*, Utilisateur.nom, Utilisateur.prenom, propose.date_proposition
FROM Offre
JOIN propose ON Offre.id_offre = propose.id_offre
JOIN Acheteur ON propose.id_utilisateur = Acheteur.id_utilisateur
JOIN Utilisateur ON Acheteur.id_utilisateur = Utilisateur.id
WHERE Offre.id_produit = ?;
END TRANSACTION;