BEGIN TRANSACTION
-- les experts les plus productifs : on calcule leur montant total d'estimations et leur nombre d'interventions
SELECT U_Expert.nom, U_Expert.prenom, Sum(Estimation.prix_estimation) AS total_estime, COUNT(Estimation.id_estimation) AS nb_estimation
FROM Expert
JOIN Utilisateur AS U_Expert ON Expert.id_utilisateur = U_Expert.id
JOIN Estimation ON U_Expert.id_utilisateur = Estimation.id_expert
JOIN Produit ON Estimation.id_produit = Produit.id_produit
JOIN Valide ON Estimation.id_estimation = Valide.id_estimation
WHERE Valide.decision = 'valide'
GROUP BY U_Expert.nom, U_Expert.prenom
HAVING COUNT(Estimation.id_estimation) > 1
ORDER BY total_estime DESC;

END TRANSACTION