-- ? = id_offre
-- Permet de récupérer les informations complètes d'une offre, y compris les détails du produit concerné, les informations sur l'acheteur qui a fait l'offre, les informations sur l'annonceur du produit et la date de proposition de l'offre.

BEGIN TRANSACTION;
SELECT Offre.*, Produit.nom_produit, Produit.description, Produit.etat, Produit.prix_souhaite,
       U_Acheteur.nom AS acheteur_nom, U_Acheteur.prenom AS acheteur_prenom,
       U_Annonceur.nom AS annonceur_nom, U_Annonceur.prenom AS annonceur_prenom,
       propose.date_proposition
FROM Offre
JOIN Produit ON Offre.id_produit = Produit.id_produit
JOIN Annonceur ON Produit.id_annonceur = Annonceur.id_utilisateur
JOIN Utilisateur U_Annonceur ON Annonceur.id_utilisateur = U_Annonceur.id
JOIN propose ON Offre.id_offre = propose.id_offre
JOIN Acheteur ON propose.id_utilisateur = Acheteur.id_utilisateur
JOIN Utilisateur U_Acheteur ON Acheteur.id_utilisateur = U_Acheteur.id
WHERE Offre.id_offre = ?;
END TRANSACTION;