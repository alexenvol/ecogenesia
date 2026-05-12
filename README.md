# Ecogenesia

## Un monde à sauver

Dans un monde ravagé par la pollution et les activités humaines, la nature est en train de disparaître.

Votre mission sera d’explorer un labyrinthe dévasté et redonner vie à l’environnement.

Ecogenesia propose une micro-aventure inspirée des enjeux écologiques actuels dans un univers rétro en 2D.

Parviendrez-vous à restaurer l’équilibre de ce monde avant qu’il ne soit trop tard ?

Ecogenesia est un jeu Java développé dans le cadre d’un projet de conduite de projet objet à l’ENIB.

## Prérequis

Avant de lancer le projet, assurez-vous d’avoir installé :

* Java JDK 21 ou supérieur
* VS Code ou Eclipse (recommandé)

### Installer Java

Téléchargez un JDK récent ici :

* Eclipse Temurin : [https://adoptium.net/temurin/releases/](https://adoptium.net/temurin/releases/)

Après installation, vérifiez que Java fonctionne :

```bash
java -version
javac -version
```

Vous devriez obtenir une version 21 ou supérieure.

---

# Installation du projet

## 1. Cloner le repository

```bash
git clone https://github.com/alexenvol/ecogenesia.git
```

Puis :

```bash
cd ecogenesia
```

---

# Lancer le projet avec VS Code

## Extensions recommandées

Installez les extensions suivantes :

* Extension Pack for Java
* Language Support for Java™ by Red Hat

## Ouvrir le projet

1. Ouvrir VS Code
2. File → Open Folder
3. Sélectionner le dossier `Ecogenesia`

## Configurer le JDK

1. `Ctrl + Shift + P`
2. Rechercher :

```text
Java: Configure Java Runtime
```

3. Sélectionner le JDK 21 installé.

## Nettoyer le workspace Java

1. `Ctrl + Shift + P`
2. Rechercher :

```text
Java: Clean Java Language Server Workspace
```

3. Redémarrer VS Code.

---

# Compiler et lancer le jeu

## Compilation

Depuis un terminal PowerShell dans VS Code :

```powershell
javac -d bin (Get-ChildItem -Recurse -Filter *.java src).FullName
```

## Copier les ressources du jeu

```powershell
Copy-Item -Recurse -Force res/* bin/
```

Cette étape est importante pour copier :

* les musiques,
* les effets sonores,
* les textures,
* les cartes,
* les ressources du jeu.

## Exécution

```powershell
java -cp bin main.Main
```

---

# Lancer directement depuis VS Code

Le bouton `Run Java` fonctionne également.

Créer le fichier :

```text
.vscode/settings.json
```

avec le contenu suivant :

```json
{
    "java.project.sourcePaths": [
        "src",
        "res"
    ]
}
```

Puis dans VS Code :

```text
Ctrl + Shift + P
→ Java: Clean Java Language Server Workspace
```

et redémarrer VS Code.

---

# Structure du projet

```text
Ecogenesia/
├── src/        # Code source Java
├── res/        # Ressources du jeu
├── bin/        # Fichiers compilés
└── README.md
```

---

# Problèmes fréquents

## Les classes `String`, `Object` ou `JFrame` ne sont pas reconnues

Le JDK Java n’est probablement pas configuré correctement.

Vérifiez :

```bash
java -version
```

et reconfigurez le runtime Java dans VS Code.

---

## Erreur de classpath

Essayez :

```text
Ctrl + Shift + P
→ Java: Clean Java Language Server Workspace
```

Puis redémarrez VS Code.

---

# Auteurs

Projet réalisé dans le cadre du module de conduite de projet objet à l’ENIB.
