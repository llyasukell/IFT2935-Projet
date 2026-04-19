-- ? = courriel
-- Permet de récupérer les informations d'un utilisateur à partir de son courriel.
BEGIN TRANSACTION;
SELECT * FROM Utilisateur
WHERE courriel = ?;
END TRANSACTION;