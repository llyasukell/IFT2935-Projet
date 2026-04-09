begin transaction;
-- Je crois qu'on peut utiliser "set search_word" pour définir une variable dans postgresql, mais je ne suis pas sûr, la documentation n'était pas très clair à ce sujet.
SELECT * FROM Produit
WHERE nom_produit LIKE CONCAT('%', :'search_word', '%');

end transaction;