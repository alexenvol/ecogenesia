package entity;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import main.GamePanel;
import main.KeyHandler;
import object.OBJ_DepollutionBuilding;
import object.OBJ_DepollutionGerminator;
import object.OBJ_DepollutionHothouse;

/////////////////////////////////////////////////
//This class manages the player's attributes,  //
//actions, inventory, and interactions within  //
//the game.                                    //
/////////////////////////////////////////////////

public class Player extends Entity {

    // Variables and constants
    KeyHandler keyH;
    public final int screenX;
    public final int screenY;
    int standCounter = 0;
    public ArrayList<Entity> inventory = new ArrayList<>();
    public final int maxInventorySize = 5;
    public static Entity selectedItem;  // Nouvelle variable pour suivre l'objet sélectionné
    public boolean tuileDepollutionBuilding = false; // Aucun dépollutionBuilding au début
    public boolean tuileDepollutionHothouse = false;
    public boolean tuileDepollutionGerminator = false;
    public boolean onInfoTile = false;
    public boolean showTutorial = false;
    private int targetX, targetY; // Coordonnées de la case cible
    private boolean isMoving = false; // Indique si le joueur est en train de bouger

    // Constructor
    public Player(GamePanel gp, KeyHandler keyH) {
        super(gp);
        this.keyH = keyH;
        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        // Initialize solid area
        solidArea = new Rectangle();
        solidArea.x = 1;
        solidArea.y = 1;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        solidArea.width = 40;
        solidArea.height = 40;

        setDefaultValues();
        getPlayerImage();
        setItems();
    }

    // Initialization methods
    public void setDefaultValues() {
        worldX = gp.tileSize * 52;
        worldY = gp.tileSize * 70;
        speed = 8;
        direction = "down";
        firstBuilding = new OBJ_DepollutionBuilding(gp);
        secondBuilding = new OBJ_DepollutionHothouse(gp);
        thirdBuilding = new OBJ_DepollutionGerminator(gp);
    }

    public void setDefaultPositions() {
        worldX = gp.tileSize * 52;
        worldY = gp.tileSize * 70;
        direction = "down";
    }

    public void setItems() {
        inventory.clear();
        inventory.add(firstBuilding);
        inventory.add(secondBuilding);
        inventory.add(thirdBuilding);
    }

    // Load player images
    public void getPlayerImage() {
        up1 = setup("/player/pointer", gp.tileSize, gp.tileSize);
    }

    // Player action methods
    public void update() {
        // Vérifie si le joueur est en mouvement
        if (isMoving) {
            // Déplacement progressif vers la case cible
            if (worldX < targetX) {
                worldX += speed;
            } else if (worldX > targetX) {
                worldX -= speed;
            }

            if (worldY < targetY) {
                worldY += speed;
            } else if (worldY > targetY) {
                worldY -= speed;
            }

            // Arrête le mouvement lorsque le joueur atteint la case cible
            if (worldX == targetX && worldY == targetY) {
                isMoving = false;
            }
        } else {
            // Vérifie si le joueur est sur une tuile d'information pour afficher le tutoriel
            boolean currentlyOnInfoTile = isOnInfoTile();
            showTutorial = currentlyOnInfoTile;

            // Gestion des touches pour le déplacement case par case
            if (keyH.upPressed) {
                targetX = worldX;
                targetY = worldY - gp.tileSize;
            } else if (keyH.downPressed) {
                targetX = worldX;
                targetY = worldY + gp.tileSize;
            } else if (keyH.leftPressed) {
                targetX = worldX - gp.tileSize;
                targetY = worldY;
            } else if (keyH.rightPressed) {
                targetX = worldX + gp.tileSize;
                targetY = worldY;
            }

            // Vérifie si la case cible est valide avant de démarrer le mouvement
            if ((keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) &&
                isTileWalkable(targetX / gp.tileSize, targetY / gp.tileSize)) {
                isMoving = true;
                keyH.upPressed = false;
                keyH.downPressed = false;
                keyH.leftPressed = false;
                keyH.rightPressed = false;
            }

            // Gère l'immobilité si le joueur ne bouge pas
            if (!isMoving) {
                handleStanding();
            }
        }
    }

    private void handleStanding() {
        standCounter++;
        if (standCounter == 20) {
            spriteNum = 1;
            standCounter = 0;
        }
    }
    
    public boolean isOnInfoTile() {
        // Calcul des coordonnées des bords de la zone du joueur
        int leftCol = (worldX + solidArea.x) / gp.tileSize; // Bord gauche
        int rightCol = (worldX + solidArea.x + solidArea.width - 1) / gp.tileSize; // Bord droit
        int topRow = (worldY + solidArea.y) / gp.tileSize; // Bord supérieur
        int bottomRow = (worldY + solidArea.y + solidArea.height - 1) / gp.tileSize; // Bord inférieur

        // Parcours de toutes les tuiles couvertes par la zone du joueur
        for (int col = leftCol; col <= rightCol; col++) {
            for (int row = topRow; row <= bottomRow; row++) {
            	
                // Récupération de l'ID de la tuile
                int tileNum = gp.tileM.mapTileNum[gp.currentMap][col][row];

                // Si une tuile est un "point d'infos", on retourne true
                if (tileNum == 8) {
                    return true;
                }
            }
        }
        // Aucun "point d'infos" détecté
        return false;
    }

    
    private boolean isTileWalkable(int x, int y) {
        // Obtient le numéro de la tuile à cette position
        int tileNum = gp.tileM.mapTileNum[gp.currentMap][x][y]; 
        
        // Retourne vrai si la tuile n'est pas bloquante
        if (!gp.debugMode) {
        	return !gp.tileM.tile[tileNum].collision;
        } else {
        	return true;
        }
    }

    public void placeDepollutionBuilding() {
        // Vérifie si un item est sélectionné
        if (selectedItem instanceof OBJ_DepollutionBuilding) {
            // Crée une nouvelle instance de OBJ_DepollutionBuilding pour chaque pose
            OBJ_DepollutionBuilding newBuilding = new OBJ_DepollutionBuilding(gp);

            // Calcule la position de placement basée sur la position du joueur
            int playerX = this.worldX / gp.tileSize;
            int playerY = this.worldY / gp.tileSize;

            int dropX = playerX;
            int dropY = playerY;

            switch (direction) {
                case "up":
                    dropY -= 0;
                    break;
                case "down":
                    dropY += 0;
                    break;
                case "left":
                    dropX -= 0;
                    break;
                case "right":
                    dropX += 0;
                    break;
            }
            

            // Vérifie si la position de dépôt est valide
            if (isPositionValidAndEmpty(dropX, dropY)) {
                // Place la nouvelle instance de bâtiment sur la carte
                placeObjectOnMap(newBuilding, dropX, dropY);
                tuileDepollutionBuilding = true; // Objet depollutionBuilding placé

                // Décrémente la quantité d'objets dans l'inventaire
                OBJ_DepollutionBuilding.quantity--;

                // Affiche un message à l'utilisateur
                gp.ui.addMessage("Vous avez placé : " + newBuilding.name);

                // Si la quantité est 0, retirer l'objet de l'inventaire
                if (OBJ_DepollutionBuilding.quantity <= 0) {
                    inventory.remove(selectedItem);
                    selectedItem = null;  // Réinitialise l'item sélectionné
                    gp.ui.addMessage(newBuilding.name + " est en rupture de stock");
                }
            } else {
                gp.ui.addMessage("Vous ne pouvez pas placer de bâtiment ici !");
            }
        } else {
            gp.ui.addMessage("Bâtiment sélectionné non valide !");
        }
    }
    
    public void placeDepollutionHothouse() {
        // Vérifie si un item est sélectionné
        if (selectedItem instanceof OBJ_DepollutionHothouse) {
            // Crée une nouvelle instance de OBJ_DepollutionBuilding pour chaque pose
            OBJ_DepollutionHothouse newBuilding = new OBJ_DepollutionHothouse(gp);

            // Calcule la position de placement basée sur la position du joueur
            int playerX = this.worldX / gp.tileSize;
            int playerY = this.worldY / gp.tileSize;

            int dropX = playerX;
            int dropY = playerY;

            switch (direction) {
                case "up":
                    dropY -= 0;
                    break;
                case "down":
                    dropY += 0;
                    break;
                case "left":
                    dropX -= 0;
                    break;
                case "right":
                    dropX += 0;
                    break;
            }
            
            // Vérifie si la position de dépôt est valide
            if (isPositionValidAndEmpty(dropX, dropY)) {
                // Place la nouvelle instance de bâtiment sur la carte
                placeObjectOnMap(newBuilding, dropX, dropY);
                tuileDepollutionHothouse = true; // Objet depollutionBuilding placé

                // Décrémente la quantité d'objets dans l'inventaire
                OBJ_DepollutionHothouse.quantity--;

                // Affiche un message à l'utilisateur
                gp.ui.addMessage("Vous avez placé : " + newBuilding.name);

                // Si la quantité est 0, retirer l'objet de l'inventaire
                if (OBJ_DepollutionHothouse.quantity <= 0) {
                    inventory.remove(selectedItem);
                    selectedItem = null;  // Réinitialise l'item sélectionné
                    gp.ui.addMessage(newBuilding.name + " est en rupture de stock");
                }
            } else {
                gp.ui.addMessage("Vous ne pouvez pas placer de bâtiment ici !");
            }
        } else {
            gp.ui.addMessage("Bâtiment sélectionné non valide !");
        }
    }
    
    public void placeDepollutionGerminator() {
        // Vérifie si un item est sélectionné
        if (selectedItem instanceof OBJ_DepollutionGerminator) {
            // Crée une nouvelle instance de OBJ_DepollutionBuilding pour chaque pose
            OBJ_DepollutionGerminator newBuilding = new OBJ_DepollutionGerminator(gp);

            // Calcule la position de placement basée sur la position du joueur
            int playerX = this.worldX / gp.tileSize;
            int playerY = this.worldY / gp.tileSize;

            int dropX = playerX;
            int dropY = playerY;

            switch (direction) {
                case "up":
                    dropY -= 0;
                    break;
                case "down":
                    dropY += 0;
                    break;
                case "left":
                    dropX -= 0;
                    break;
                case "right":
                    dropX += 0;
                    break;
            }
            

            // Vérifie si la position de dépôt est valide
            if (isPositionValidAndEmpty(dropX, dropY)) {
                // Place la nouvelle instance de bâtiment sur la carte
                placeObjectOnMap(newBuilding, dropX, dropY);
                tuileDepollutionGerminator = true; // Objet depollutionBuilding placé

                // Décrémente la quantité d'objets dans l'inventaire
                OBJ_DepollutionGerminator.quantity--;

                // Affiche un message à l'utilisateur
                gp.ui.addMessage("Vous avez placé : " + newBuilding.name);

                // Si la quantité est 0, retirer l'objet de l'inventaire
                if (OBJ_DepollutionGerminator.quantity <= 0) {
                    inventory.remove(selectedItem);
                    selectedItem = null;  // Réinitialise l'item sélectionné
                    gp.ui.addMessage(newBuilding.name + " est en rupture de stock");
                }
            } else {
                gp.ui.addMessage("Vous ne pouvez pas placer de bâtiment ici !");
            }
        } else {
            gp.ui.addMessage("Bâtiment sélectionné non valide !");
        }
    }

    // autoriser placement uniquement sur une tuile marchable et une tuile de type dépôt (value = 3)
    private boolean isPositionValidAndEmpty(int x, int y) {
        return x >= 0 && y >= 0 && x < gp.maxWorldCol && y < gp.maxWorldRow && isPositionEmpty(x, y) && isTileWalkable(x, y) && gp.tileM.mapTileNum[0][x][y] == 3;
    }

    // Méthode pour vérifier si une position est vide dans le tableau des objets
    private boolean isPositionEmpty(int x, int y) {
        for (int i = 0; i < gp.obj[gp.currentMap].length; i++) {
            if (gp.obj[gp.currentMap][i] != null && gp.obj[gp.currentMap][i].worldX / gp.tileSize == x && gp.obj[gp.currentMap][i].worldY / gp.tileSize == y) {
                return false; // Il y a déjà un objet ici
            }
        }
        return true; // Aucune collision, l'emplacement est vide
    }

    // Méthode pour placer l'objet sur la carte à la position spécifiée
    private void placeObjectOnMap(Entity object, int x, int y) {
        for (int i = 0; i < gp.obj[gp.currentMap].length; i++) {
            if (gp.obj[gp.currentMap][i] == null) {
                gp.obj[gp.currentMap][i] = object;
                object.worldX = x * gp.tileSize;
                object.worldY = y * gp.tileSize;
                break;
            }
        }
    }

    public void selectItem() {
        int itemIndex = gp.ui.getItemIndexOnSlot(); // Indice de l'objet sélectionné
        if (itemIndex < inventory.size()) {
            Entity selectedItem = inventory.get(itemIndex);

            // Si la case sélectionnée est vide, repositionnez la sélection
            if (selectedItem == null) {
                // Rechercher le premier objet valide
                for (int i = 0; i < inventory.size(); i++) {
                    if (inventory.get(i) != null) {
                        gp.ui.slotCol = i; // Met à jour la sélection
                        selectedItem = inventory.get(i);
                        break;
                    }
                }
            }

            // Si un objet valide a été trouvé, traiter la sélection
            if (selectedItem != null) {
                // Si l'item sélectionné est déjà celui en cours, on le désélectionne
                if (Player.selectedItem == selectedItem) {
                    Player.selectedItem = null;  // Désélectionner l'item
                    gp.ui.addMessage("Item désélectionné");
                } else {
                    Player.selectedItem = selectedItem;  // Mettre à jour l'objet sélectionné

                    // Vérification du type de l'item sélectionné pour les messages
                    if (selectedItem instanceof OBJ_DepollutionBuilding) {
                        //firstBuilding = (OBJ_DepollutionBuilding) selectedItem;  // Enregistre le bâtiment de dépollution
                        gp.ui.addMessage("Vous avez sélectionné : " + selectedItem.name + " prêt à placer !");
                    }
                    if (selectedItem instanceof OBJ_DepollutionGerminator) {
                        //firstBuilding = (OBJ_DepollutionGerminator) selectedItem;  // Enregistre le bâtiment de dépollution
                        gp.ui.addMessage("Vous avez sélectionné : " + selectedItem.name + " prêt à placer !");
                    }
                    if (selectedItem instanceof OBJ_DepollutionHothouse) {
                       // firstBuilding = (OBJ_DepollutionBuilding) selectedItem;  // Enregistre le bâtiment de dépollution
                        gp.ui.addMessage("Vous avez sélectionné : " + selectedItem.name + " prêt à placer !");
                    }
                }
            }
        }
    }
    
    public void resetInventory() {
    	inventory.clear();
    	OBJ_DepollutionBuilding.quantity = OBJ_DepollutionBuilding.DEFAULT_QUANTITY;
    	inventory.add(new OBJ_DepollutionBuilding(gp));
    	inventory.add(new OBJ_DepollutionHothouse(gp));
    	inventory.add(new OBJ_DepollutionGerminator(gp));
    }

    // Player drawing method
    public void draw(Graphics2D g2) {
        BufferedImage image = null;
        int tempScreenX = screenX;
        int tempScreenY = screenY;
        image = up1;
        g2.drawImage(image, tempScreenX, tempScreenY, null);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }
}