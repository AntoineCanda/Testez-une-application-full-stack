# Testez une application fullstack

L'objectif de ce projet est de découvrir différents tests qu'un développeur devrait mettre en place dans le cadre d'un projet d'application full stack. 

Les tests en question devraient respecter la structure de la pyramide des tests, à savoir une majorité de tests unitaires permettant 
de s'assurer du bon fonctionnement au niveau des fonctions en utilisant notamment les mocks sur la partie back-end et front-end.

Les tests suivants sont les tests d'intégrations permettant de valider que les différents services et composants s'intégrent correctement ensemble.

Enfin des tests end to end permettant de tester et valider une fonctionnalité dans sa totalité.


## Installation

### Récupération des sources
1. Allez sur le dépot github suivant pour récupérer le code source de l'application: [le dépôt GitHub du projet](https://github.com/OpenClassrooms-Student-Center/Testez-une-application-full-stack).

### Base de données

1. Lancez un terminal et lancez votre application MySQL

2. Créer la base de donnée qui sera utilisé par l'application

3. Exécutez le script SQL qui se trouve dans le répertoire suivant : ressources/sql/script.sql.

### Backend
1. Lancez un terminal et placez vous dans le répertoire backend.

2. Exécutez la commande suivante pour installer les dépendances :
    ```
    mvn install
    ```

3. Démarrez le serveur backend avec la commande :
    ```
    mvn spring-boot:run
    ```

### Front-end
1. Lancez un terminal et placez vous dans le répertoire frontend.

2. Exécutez la commande suivante pour installer les dépendances :
    ```
    npm install
    ```

3. Démarrez le serveur de développement en exécutant :
    ```
    npm run start
    ```

Le frontend sera accessible à l'adresse [localhost:4200](http://localhost:4200/).



## Tests

### Frontend

1. Lancez un terminal et placez vous dans le répertoire frontend.
   
2. Exécutez la commande suivante pour lancer les tests unitaires :
    ```
    ng test
    ```
3. Pour générer le rapport de couverture de code, exécutez la commande suivante :
    ```
    ng test --coverage
    ```

Le rapport de couverture sera présent aussi dans le fichier index.html qui se trouve dans le répertoire coverage/jest/lcov-report du frontend.

### End to end
1. Lancez un terminal et placez vous dans le répertoire frontend.
   
2. Exécutez la commande suivante pour lancer les tests end-to-end :
    ```
    npm run cypress:run
    ```

3. Pour générer le rapport de couverture des tests end-to-end, exécutez dans un premier temps la commande suivante :
    ```
    npm run cypress:run
    ```

Puis, exécutez la commande suivante :
    ```
    npm run e2e:coverage
    ```

Le rapport de couverture sera présent aussi dans le fichier index.html qui se trouve dans le répertoire coverage/lcov-report du frontend.

### Backend

1. Lancez un terminal et rendez-vous dans le répertoire du backend.

2. Exécutez la commande suivante pour lancer les tests unitaires :
    ```
    mvn test
    ```
   
3. Pour générer le rapport de couverture de code, exécutez la commande suivante :
    ```
    mvn test jacoco:report
    ```

Le rapport de couverture de code sera présent aussi dans le fichier index.html qui se trouve dans le répertoire target/site/jacoco du backend.


## Technologies utilisées

* Java
* Spring Boot
* jUnit
* Mockito
* AssertJ
---
* Angular
* Jest
* Cypress

---

### Contact

Pour toute question ou commentaire, n'hésitez pas à me contacter sur mon mail : [candaantoine@gmail.com](mailto:candaantoine@gmail.com).

---