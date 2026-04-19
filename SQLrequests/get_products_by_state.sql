-- ? = etat_produit ('neuf', 'usage', 'abime')
-- Permet de récupérer les produits en fonction de leur état, ainsi que les informations sur l'annonceur de ces produits.

BEGIN TRANSACTION;
SELECT * FROM Produit
WHERE etat = ?;
END TRANSACTION;