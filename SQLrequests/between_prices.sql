begin transaction;
-- Utiliser "set min_price" et "set max_price" pour définir les variables dans postgresql... je crois?
SELECT * FROM Produit
WHERE prix_souhaite BETWEEN :'min_price' AND :'max_price';

end transaction;