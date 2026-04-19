-- ? = id_produit
-- Permet de récupérer les estimations d'un produit, ainsi que les informations sur l'expert qui a réalisé l'estimation et la décision de validation (si elle existe).
BEGIN TRANSACTION;
SELECT Estimation.*, Utilisateur.nom, Utilisateur.prenom, Valide.decision
FROM Estimation
JOIN Expert ON Estimation.id_expert = Expert.id_utilisateur
JOIN Utilisateur ON Expert.id_utilisateur = Utilisateur.id
LEFT JOIN Valide ON Estimation.id_estimation = Valide.id_estimation
WHERE Estimation.id_produit = ?;
END TRANSACTION;