BEGIN TRANSACTION;
--Cette requête sort la liste des prix officiels 
--on voit quel annonceur a quel produit, quel expert a fait l'estimation, et on affiche seulement si le prix a été validé.
SELECT U_Annonceur.nom AS nom_annonceur,
Produit.nom_produit,
U_Expert.nom AS nom_expert,
Estimation.prix_estimation
FROM Annonceur
JOIN Utilisateur U_Annonceur ON Annonceur.id_utilisateur = U_Annonceur.id
JOIN Produit ON Annonceur.id_utilisateur = Produit.id_annonceur
JOIN Estimation ON Estimation.id_produit = Produit.id_produit
JOIN Expert ON Estimation.id_expert = Expert.id_utilisateur
JOIN Utilisateur U_Expert ON Expert.id_utilisateur = U_Expert.id
JOIN Valide ON Valide.id_estimation = Estimation.id_estimation
    AND Valide.id_annonceur = Produit.id_annonceur
WHERE Valide.decision = 'valide';

END TRANSACTION;