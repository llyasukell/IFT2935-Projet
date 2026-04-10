-- ? = min_price
-- ? = max_price

BEGIN TRANSACTION;
SELECT * FROM Produit
WHERE prix_souhaite BETWEEN ? AND ?;

END TRANSACTION;