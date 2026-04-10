-- ? = proposed price
-- ? = proposed price
-- ? = id

BEGIN TRANSACTION;
SELECT
    CASE
        WHEN prix_souhaite IS NOT NULL AND ? < prix_souhaite THEN 'REFUSE'
        WHEN prix_souhaite IS NOT NULL AND ? >= prix_souhaite THEN 'ACCEPT'
        ELSE 'PENDING'
    END AS decision
FROM Produit
WHERE id_produit =?;
END TRANSACTION;
 