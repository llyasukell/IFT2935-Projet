-- ? = id_annonceur
-- Permet de récupérer les produits d'un vendeur, ainsi que les informations sur le vendeur de ces produits.

BEGIN TRANSACTION;
SELECT Produit.*, Utilisateur.nom, Utilisateur.prenom
FROM Produit
JOIN Annonceur ON Produit.id_annonceur = Annonceur.id_utilisateur
JOIN Utilisateur ON Annonceur.id_utilisateur = Utilisateur.id
WHERE Produit.id_annonceur = ?;
END TRANSACTION;