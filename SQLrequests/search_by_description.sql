-- ? = description à chercher

BEGIN TRANSACTION;
SELECT * FROM Produit
WHERE description LIKE CONCAT('%', ?, '%');
END TRANSACTION;