-- ? = etat_produit ('neuf', 'usage', 'abime')
-- Permet de récupérer les produits en fonction de leur état, ainsi que les informations sur l'annonceur de ces produits.

BEGIN TRANSACTION;

SELECT Produit.*, Utilisateur.nom, Utilisateur.prenom
FROM Produit
JOIN Annonceur ON Produit.id_annonceur = Annonceur.id_utilisateur
JOIN Utilisateur ON Annonceur.id_utilisateur = Utilisateur.id
WHERE Produit.etat = ?;

END TRANSACTION;