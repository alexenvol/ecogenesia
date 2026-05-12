# Ecogenesia

<|> Comment lancer le jeu Java ?

Pour exécuter le jeu Ecogenesia, suivez les étapes suivantes :

1 - Cloner le dépôt Git

Assurez-vous que Git est installé sur votre machine. Si ce n'est pas le cas, téléchargez-le et installez-le depuis https://git-scm.com/. Ouvrez un terminal ou une invite de commandes et exécutez la commande suivante : git clone https://gitlab.com/nom_utilisateur/ecogenesia.git. Accédez ensuite au répertoire cloné : cd ecogenesia.

2 - Installer et configurer Eclipse

Téléchargez Eclipse IDE pour Java Developers depuis https://www.eclipse.org/downloads/. Installez Eclipse en suivant les instructions de l'installateur. Lancez Eclipse et configurez votre espace de travail. Importez le projet Java dans Eclipse : cliquez sur File > Import. Sélectionnez Existing Projects into Workspace. Naviguez jusqu’au répertoire cloné et cliquez sur Finish. Exécuter le jeu.

Assurez-vous que toutes les dépendances nécessaires sont installées. Si des bibliothèques externes sont utilisées, elles doivent être ajoutées au chemin de build du projet. Dans Eclipse, localisez le fichier contenant la méthode main (souvent dans une classe nommée Main ou similaire). Faites un clic droit sur le fichier et sélectionnez Run As > Java Application.

3 - Résolution des problèmes

Si des erreurs surviennent : vérifiez que toutes les bibliothèques nécessaires sont correctement importées. Consultez les logs dans la console Eclipse pour identifier les problèmes spécifiques. En cas de besoin, ouvrez une issue sur le dépôt Git ou contactez l'équipe de développement.

<|> Portfolio

05/11/2024 : Mise en place initiale

    Configuration Git :
    - Déploiement du dépôt Git.
    - Import initial du projet pour centraliser les contributions de l'équipe.

12/11/2024 : Avancées graphiques et structurelles

    Assets graphiques :
    - Création de 8 frames pour l'animation de l’éolienne.
    - Finalisation de l’asset graphique de la serre.

    Analyse du code :
    - Début de l’analyse du fichier DepollutionBuilding.java : objectif d’agrandir l’inventaire et d’ajouter d’autres bâtiments fonctionnels.

    Modifications en cours :
    - Menu "Options" dans UI.java (non poussé).

    Sound Design : 
    - Intégration d’un son fonctionnel (non poussé).

19/11/2024 : Avancées mécaniques et optimisation

    Mécaniques de jeu :
    - Introduction de la dépollution de la mer et des arbres.
    - Ajout d’une zone dédiée au dépôt.

    Optimisation :
    - Nettoyage du code mort.
    - Débogage de la musique et de l’inventaire.

26/11/2024 : Ajout du didacticiel et corrections

    Implémentation :
    - Création du didacticiel interactif.

    Débogage :
    - Correction d’un bug lié à la barre de dépollution après un succès.
    - Résolution d’un problème avec la musique en arrière-plan.

03/12/2024 : Nouvelles fonctionnalités et planning final

    Nouveautés :
     - Introduction d’un labyrinthe dans le jeu.

    Débogages :
     - Réparation de la barre de son des effets.

    Gestion du projet :
    - Mise à jour du diagramme de Gantt.
    - Planification des dernières tâches pour la semaine 50 (dernière semaine de travail).

10/12/2024 : Finalisation du projet

    GIT :
    - Merge de la branche develop sur la branche main