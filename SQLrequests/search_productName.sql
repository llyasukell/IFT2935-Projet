-- ? = nom à rechercher
BEGIN TRANSACTION;
SELECT * FROM Produit
WHERE nom_produit LIKE CONCAT('%', ?, '%');
END TRANSACTION;