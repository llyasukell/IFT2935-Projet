## IFT2935-Projet

Système permettant d’annoncer la vente de produits.


1. Configuration JDBC

On doit renommer le fichier db.properties.example en db.properties à la racine du projet.

Ensuite, on modifie les valeurs pour qu'elles correspondent à l'environnement local :
Properties

# Format : jdbc:postgresql://[HÔTE]:[PORT]/[NOM_DE_LA_BD]

db.properties =

```
url=jdbc:postgresql://localhost:5432/IFT2935_Projet
user=postgres
password=ton_mot_de_passe
```

Hôte et Port : Par défaut localhost:5432. Si votre instance PostgreSQL utilise un autre port (ex: 5433), on doit modifier cette valeur dans l'URL.
Base de données : Par défaut IFT2935_Projet. S'assurer que le nom correspond exactement à la base créée avec DDL.sql.
Identifiants : user est souvent postgres. Remplacer ton_mot_de_passe par celui défini lors de l'installation de PostgreSQL.

2. Compilation

Depuis la racine du projet, on utilise la commande suivante :

`javac -d src -cp "lib/postgresql-42.7.2.jar" src/*.java`

3. Lancement

Depuis la racine du projet :

`java -cp "src;lib/postgresql-42.7.2.jar;." Main`

Comptes de test:

Une fois l'application lancée, on peut utiliser les comptes suivants pré-configurés dans le fichier LMD.sql :
Rôle	       Courriel	            Mot de passe
Acheteur	prof.acheteur@test.com	 test
Annonceur	prof.annonceur@test.com	 test


