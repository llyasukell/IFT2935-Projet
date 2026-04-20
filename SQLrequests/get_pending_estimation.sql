-- ? = id_produit
-- ? = id_annonceur
-- Permet de retrouver l'identifiant d'une estimation encore en attente de décision pour un produit et un annonceur donnés. LIMIT 1 garantit
-- qu'une seule ligne est retournée même si plusieurs estimations sont en attente sur le même produit.

BEGIN TRANSACTION;
SELECT Estimation.id_estimation
FROM Estimation
JOIN Valide ON Valide.id_estimation = Estimation.id_estimation
WHERE Estimation.id_produit = ?
  AND Valide.id_annonceur = ?
  AND Valide.decision = 'en_attente'
LIMIT 1;
END TRANSACTION;
 