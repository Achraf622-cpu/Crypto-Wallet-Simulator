## Wallet Crypto – Java 8 (Console) – PostgreSQL

### Contexte
Application console en Java 8 qui simule un wallet crypto avec mempool et calcul des frais (fees). L’objectif est d’illustrer les concepts de transactions, priorités (ÉCONOMIQUE/STANDARD/RAPIDE), et positionnement dans le mempool, tout en persistant les wallets et transactions dans PostgreSQL.

### Fonctionnalités
- Création de wallet (BITCOIN ou ETHEREUM) avec adresse générée automatiquement et persistance en base
- Création de transaction avec calcul de fees selon le type de crypto et la priorité, persistance en base (statut PENDING)
- Simulation de mempool (transactions aléatoires) et calcul de la position d’une transaction (ordre par fees décroissants) + estimation du temps (position × 10 minutes)
- Comparaison des 3 niveaux de frais (tableau console: fees, position, temps estimé)
- Consultation DB: lister les wallets et lister les transactions par wallet

### Architecture et principes
- Java 8 (sans Maven)
- Couches: Présentation (console), Services, Repositories (in-memory + JDBC service), Domaine, Utilitaires, Base de données (Singleton)
- SOLID & Patterns:
  - Singleton: `Database`
  - Repository Pattern: interfaces génériques + implémentations in-memory
  - Polymorphisme/Stratégie via héritage: `Transaction` abstraite, `BitcoinTransaction` et `EthereumTransaction` pour calculer les fees
  - Interfaces clés: `Repository<T>`, `Identifiable`

### Prérequis
- JDK 8
- PostgreSQL (base de données `crypto_wallet`)
- Pilote JDBC PostgreSQL (fichier `postgresql-<version>.jar` dans le dossier `lib/`)

### Schéma de base de données
- Fichier: `schema.sql`
- Tables: `wallets`, `transactions`
- Index: sur `wallet_id`, `status`, `priority`
- Exemple de données optionnelles incluses

### Installation
1. Cloner/copier le projet
2. Créer la base et exécuter le contenu de `schema.sql` dans PostgreSQL (psql/pgAdmin)
3. Placer le pilote JDBC dans `lib/`, par ex.: `lib/postgresql-42.7.4.jar`

### Compilation (Windows, sans Maven)
Dans PowerShell ou CMD à la racine du projet:

CMD:
```
cmd /c "if not exist out mkdir out && dir /s /b src\*.java > sources.txt && javac -source 1.8 -target 1.8 -encoding UTF-8 -cp lib\postgresql-42.7.4.jar -d out @sources.txt"
```

### Exécution
PowerShell (recommandé, avec guillemets autour du classpath):
```
$env:JDBC_URL = "jdbc:postgresql://localhost:5432/crypto_wallet"
$env:JDBC_USER = "postgres"
$env:JDBC_PASSWORD = "password"   # ou "" si pas de mot de passe
$env:JDBC_DRIVER = "org.postgresql.Driver"
java -cp "out;lib\postgresql-42.7.4.jar" com.crypto.app.Main
# Astuce: vous pouvez aussi utiliser lib/* pour éviter de spécifier la version
# java -cp "out;lib/*" com.crypto.app.Main
```

### Utilisation (Menu)
1. Créer un wallet
2. Créer une nouvelle transaction
3. Voir la position d’une transaction dans le mempool + temps estimé
4. Comparer les 3 niveaux de frais (position & temps pour ÉCONOMIQUE/STANDARD/RAPIDE)
5. Consulter l’état actuel du mempool (simulation)
6. Lister mes wallets (depuis la base PostgreSQL)
7. Lister mes transactions par wallet (depuis la base PostgreSQL)

### Notes
- Le mempool est simulé en mémoire pour représenter l’activité réseau (non persisté)
- Les balances de wallets ne sont pas recalculées automatiquement; possible extension: somme des transactions CONFIRMED
- Le code respecte Java 8, sans Maven, avec séparation claire des responsabilités

### Dépannage
- `ClassNotFoundException: org.postgresql.Driver` → vérifier que `lib/postgresql-<version>.jar` existe et que le classpath est correctement cité avec des guillemets en PowerShell
- Aucune donnée visible dans la DB → vérifier que vous exécutez l’appli avec les variables JDBC correctement définies et que vous interrogez la bonne base `crypto_wallet`


### Screenshots
- `Menu Principale`
![alt text](image.png)
- `Architecture (En couche)`
![alt text](image-1.png)
- `Comparaison de mempool`
![alt text](image-2.png)
