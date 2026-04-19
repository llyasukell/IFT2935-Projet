-- Permet de récupérer les produits qui n'ont pas d'offres, ainsi que les informations sur l'annonceur de ces produits.
BEGIN TRANSACTION;
SELECT Produit.*, Utilisateur.nom, Utilisateur.prenom
FROM Produit
JOIN Annonceur ON Produit.id_annonceur = Annonceur.id_utilisateur
JOIN Utilisateur ON Annonceur.id_utilisateur = Utilisateur.id
WHERE NOT EXISTS (
    SELECT 1 FROM Offre WHERE Offre.id_produit = Produit.id_produit
);
END TRANSACTION;