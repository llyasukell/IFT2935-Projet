-- Permet de récupérer la liste des produits visibles aux acheteurs, c'est-à-dire ceux dont au moins une estimation
-- a été validée par leur annonceur

BEGIN TRANSACTION;
SELECT DISTINCT Produit.*
FROM Produit
JOIN Estimation ON Estimation.id_produit = Produit.id_produit
JOIN Valide ON Valide.id_estimation = Estimation.id_estimation
WHERE Valide.decision = 'valide';
END TRANSACTION;