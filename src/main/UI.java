package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import object.OBJ_DepollutionBuilding;
import entity.Entity;
import entity.Player;
import environnement.Zone;

//////////////////////////////////////////////
// This class displays messages in the game //
//////////////////////////////////////////////

public class UI {
	
	GamePanel gp;
	Graphics2D g2;
	Font myFont;
	public boolean messageOn = false;
	ArrayList<String> message = new ArrayList<>();
	ArrayList<Integer> messageCounter = new ArrayList<>();
	public boolean gameFinished = false;
	public String currentDialogue = "";
	public int commandNum = 0;
	public int titleScreenState = 0;
	public int slotCol = 0;
	public int slotRow = 0;
	int subState = 0;
	
	public UI(GamePanel gp) {
		this.gp = gp;
		try {
			InputStream is = getClass().getResourceAsStream("/font/myFont.ttf");
			myFont = Font.createFont(Font.TRUETYPE_FONT, is);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addMessage(String text) {
		message.add(text);
		messageCounter.add(0);
	}
	
	public int getCurrentDepollution() {
	    int totalZones = gp.zones.size();
	    int cleanZones = 0;
	    for (Zone zone : gp.zones) {
	        if (zone.getPollutionLevel() <= 0) {
	            cleanZones++;
	        }
	    }
	    return (int)((double)cleanZones / totalZones * 100);
	}

	public void draw(Graphics2D g2) {
		this.g2 = g2;
		g2.setFont(myFont);
		g2.setColor(Color.white);
		
		/////////////////
		// Title state //
		/////////////////
		if(gp.gameState == gp.titleState) {
			drawTitleScreen();
		}
		
		////////////////
		// Play state //
		////////////////
		if(gp.gameState == gp.playState) {
			drawInventory();
			drawMessage();
		}
		
		/////////////////
		// Pause state //
		/////////////////
		if(gp.gameState == gp.pauseState) {
			drawPauseScreen();
			drawInventory();
		}
		
		////////////////////
		// Dialogue state //
		////////////////////
		if(gp.gameState == gp.dialogueState) {
			drawDialogueScreen();
			drawInventory();
		}
		
		//////////////////
		// Option state //
		//////////////////
		if(gp.gameState == gp.optionsState) {
			drawOptionsScreen();
		}
		
		///////////////////
		// Success state //
		///////////////////
		if(gp.gameState == gp.successState) {
			drawSuccessScreen();
		}
		
		////////////////////
		// Tutorial state //
		////////////////////
		if (gp.player.showTutorial) {
			gp.gameState = gp.playState;
		    drawTutorialScreen(g2); // Dessiner le menu didactiel
		}
	}
	
	public void drawMessage() {
		int messageX = gp.tileSize;
		int messageY = gp.tileSize*4;
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 32F));
		
		for(int i = 0; i < message.size(); i++) {
			if(message.get(i) != null) {
				g2.setColor(Color.black);
				g2.drawString(message.get(i), messageX+2, messageY+2);
				g2.setColor(Color.white);
				g2.drawString(message.get(i), messageX, messageY);
				
				int counter = messageCounter.get(i) + 1; // messageCounter++
				messageCounter.set(i,  counter); // Set the counter to the array
				messageY += 50;
				
				if(messageCounter.get(i) > 180) {
					message.remove(i);
					messageCounter.remove(i);
				}
			}
		}
	}
	
	public void drawTitleScreen() {
	    // Dessiner un fond de couleur vert foncé-gris
	    g2.setColor(new Color(34, 47, 34)); // Vert foncé-gris
	    g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

	    // Dessiner le titre principal
	    g2.setFont(g2.getFont().deriveFont(Font.BOLD, 100F));
	    String text = "ECOGENESIA";
	    int x = getXforCenteredText(text);
	    int y = gp.tileSize * 3;

	    // Ombre pour le titre
	    g2.setColor(Color.darkGray);
	    g2.drawString(text, x + 5, y + 5);

	    // Couleur principale du titre
	    g2.setColor(new Color(34, 139, 34)); // Vert forêt
	    g2.drawString(text, x, y);

	    // Ajouter un sous-titre
	    g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 28F));
	    text = "Sauvez un monde dévasté par l'Homme";
	    x = getXforCenteredText(text);
	    y += gp.tileSize;
	    g2.setColor(Color.gray);
	    g2.drawString(text, x, y);

	    // Dessiner les options dynamiques du menu
	    g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));
	    String[] menuOptions = {"Lancer une partie", "Quitter"};
	    y += gp.tileSize * 3;

	    for (int i = 0; i < menuOptions.length; i++) {
	        text = menuOptions[i];
	        x = getXforCenteredText(text);

	        // Dessiner un pointeur stylé à côté de l'option sélectionnée
	        if (commandNum == i) {
	            int pointerX = (int) (x - gp.tileSize * 0.5); // Position horizontale du pointeur
	            int pointerY = (int) (y - gp.tileSize / 1.9); // Position verticale ajustée

	            // Définir les points pour un triangle (flèche)
	            int[] triangleX = {pointerX, pointerX + gp.tileSize / 4, pointerX};
	            int[] triangleY = {pointerY, pointerY + gp.tileSize / 4, pointerY + gp.tileSize / 2};

	            // Ombre du triangle
	            int shadowOffset = 5;
	            int[] shadowX = {pointerX + shadowOffset, pointerX + gp.tileSize / 4 + shadowOffset, pointerX + shadowOffset};
	            int[] shadowY = {pointerY + shadowOffset, pointerY + gp.tileSize / 4 + shadowOffset, pointerY + gp.tileSize / 2 + shadowOffset};

	            g2.setColor(Color.darkGray); // Même couleur que l'ombre du texte
	            g2.fillPolygon(shadowX, shadowY, 3); // Dessiner l'ombre

	            g2.setColor(new Color(34, 139, 34)); // Couleur pour le pointeur
	            g2.fillPolygon(triangleX, triangleY, 3);

	            g2.setColor(Color.darkGray); // Ombre pour le texte sélectionné
	            g2.drawString(text, x + 5, y + 5);

	            g2.setColor(new Color(34, 139, 34)); // Couleur pour l'option sélectionnée
	        } else {
	            g2.setColor(Color.gray); // Couleur pour les options non sélectionnées
	        }

	        // Dessiner l'option
	        g2.drawString(text, x, y);

	        // Avancer vers la prochaine ligne
	        y += gp.tileSize * 2;
	    }
	}

	public void drawPauseScreen() {
		
		g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 80F));
		String text = "JEU EN PAUSE";
		int x = getXforCenteredText(text);
		int y = gp.screenHeight/2;
		g2.drawString(text, x, y);
	}

	private void drawDialogueScreen() {
		
		/////////////////////
		// Dialogue window //
		/////////////////////
		
		int x = gp.tileSize*2;
		int y = gp.tileSize/2;
		int width = gp.screenWidth - (gp.tileSize*4);
		int height = gp.tileSize*4;
		drawSubWindow(x, y, width, height);
		
		g2.setFont(g2.getFont().deriveFont(Font.PLAIN,32F));
		x += gp.tileSize;
		y += gp.tileSize;
		
		for(String line : currentDialogue.split("\n")) {
			g2.drawString(line, x, y);
			y += 40;
		}
	}
	
	public void drawInventory() {
	    // Calcul de la position de départ pour centrer l'inventaire
	    int slotSize = gp.tileSize;  // Taille de chaque slot
	    int inventoryWidth = gp.player.inventory.size() * (slotSize + 10);  // Largeur totale de l'inventaire
	    int frameX = (gp.screenWidth - inventoryWidth) / 2;  // Calculer la position de départ centrée
	    int frameY = gp.screenHeight - gp.tileSize * 2;  // Position verticale en bas de l'écran

	    // Réinitialise le curseur sur le premier objet valide si la case actuelle est vide
	    if (gp.ui.slotCol < gp.player.inventory.size() && gp.player.inventory.get(gp.ui.slotCol) == null) {
	        for (int i = 0; i < gp.player.inventory.size(); i++) {
	            if (gp.player.inventory.get(i) != null) {
	                gp.ui.slotCol = i; // Réinitialise la sélection
	                break;
	            }
	        }
	    }

	    // Dessine une barre d'inventaire horizontale
	    for (int i = 0; i < gp.player.inventory.size(); i++) {
	        Entity item = gp.player.inventory.get(i);
	        int slotX = frameX + (slotSize + 10) * i;  // Position horizontale de chaque slot

	        // Dessine le rectangle de l'inventaire
	        g2.setColor(new Color(70, 70, 70, 150));  // Couleur de fond des slots
	        g2.fillRoundRect(slotX, frameY, slotSize, slotSize, 10, 10);

	        // Surligner l'objet sélectionné en appuyant sur "Entrée"
	        if (item == Player.selectedItem) {
	            g2.setColor(new Color(240, 190, 90));
	            g2.fillRoundRect(slotX, frameY, slotSize, slotSize, 10, 10);
	        }

	        // Dessine l'image de l'objet dans l'inventaire
	        if (item != null) {
	            g2.drawImage(item.down1, slotX, frameY, slotSize, slotSize, null);

	            // Affiche la quantité d'objets en haut à droite SEULEMENT pour les bâtiments de dépollution
	            if (item instanceof OBJ_DepollutionBuilding && OBJ_DepollutionBuilding.quantity > 1) {
	                g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14f));  // Taille de la police pour la quantité
	                g2.setColor(Color.white);  // Couleur du texte
	                String quantityText = String.valueOf(OBJ_DepollutionBuilding.quantity);
	                int textX = slotX + slotSize - 10;  // Position horizontale en haut à droite du slot
	                int textY = frameY + 15;  // Position verticale en haut du slot
	                g2.drawString(quantityText, textX, textY);  // Dessine la quantité
	            }
	        }
	    }

	    // Dessine le curseur pour l'objet sélectionné
	    if (gp.ui.slotCol < gp.player.inventory.size() && gp.player.inventory.get(gp.ui.slotCol) != null) {
	        int cursorX = frameX + (slotSize + 10) * gp.ui.slotCol;
	        int cursorY = frameY;
	        g2.setColor(Color.white);
	        g2.setStroke(new BasicStroke(3));
	        g2.drawRoundRect(cursorX, cursorY, slotSize, slotSize, 10, 10);
	    }
	}

	public void drawSuccessScreen() {
	    g2.setColor(new Color(0, 0, 0, 150));
	    g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

	    int x;
	    int y;
	    String text;
	    g2.setFont(g2.getFont().deriveFont(Font.BOLD, 110f));

	    text = "Victoire !";

	    // Ombre
	    g2.setColor(Color.black);
	    x = getXforCenteredText(text);
	    y = gp.tileSize * 4;
	    g2.drawString(text, x, y);

	    // Texte principal
	    g2.setColor(Color.white);
	    g2.drawString(text, x - 4, y - 4);

	    // Option Recommencer
	    g2.setFont(g2.getFont().deriveFont(50f));
	    text = "Recommencer";
	    x = getXforCenteredText(text);
	    y += gp.tileSize * 4;
	    g2.drawString(text, x, y);
	    if (commandNum == 0) {
	        g2.drawString(">", x - 40, y);
	    }

	    // Option Quitter
	    text = "Quitter";
	    x = getXforCenteredText(text);
	    y += 55;
	    g2.drawString(text, x, y);
	    if (commandNum == 1) {
	        g2.drawString(">", x - 40, y);
	        if (gp.keyH.enterPressed) {
	            gp.gameState = gp.titleState; // Revenir au menu principal
	            gp.keyH.enterPressed = false;
	        }
	    }
	}
	
	public void drawOptionsScreen() {
		g2.setColor(Color.white);
		g2.setFont(g2.getFont().deriveFont(32F));
		
		////////////////
		// Sub window //
		////////////////
		int frameX = gp.tileSize*6;
		int frameY = gp.tileSize;
		int frameWidth = gp.tileSize*8;
		int frameHeight = gp.tileSize*10;
		drawSubWindow(frameX, frameY, frameWidth, frameHeight);
	
		switch(subState) {
		case 0: options_top(frameX, frameY); break;
		case 1: options_fullScreenNotification(frameX, frameY); break;
		case 2: options_control(frameX, frameY); break;
		case 3: options_endGameConfirmation(frameX, frameY); break;
		}
		
		gp.keyH.enterPressed = false;
	}
	
	public void drawTutorialScreen(Graphics2D g2) {
	    int x = gp.tileSize * 2;
	    int y = gp.tileSize * 2;
	    int width = gp.screenWidth - gp.tileSize * 3;
	    int height = gp.tileSize * 8;
	    
	    // Dessiner une fenêtre encadrée
	    drawSubWindow(x, y, width, height);
	    
	    // Ajouter du texte explicatif
	    g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 32F));
	    g2.setColor(Color.white);
	    String tutorialText = "Bienvenue dans Ecogenesia !\n"
                + "Ton objectif est de dépolluer toute la carte !\n"
                + "Place des bâtiments sur les dépôts rouges pour dépolluer.\n"
                + "Utilise ZQSD pour te déplacer.\n"
                + "Sélectionne des bâtiments avec la touche entrée.\n"
                + "Utilise les flèches pour naviguer dans l'inventaire.\n"
                + "Pose des bâtiments de dépollution avec la touche o.\n"
                + "Tu peux accéder aux options en appuyant sur échap.\n";

	    int textX = x + gp.tileSize;
	    int textY = y + gp.tileSize;

	    for (String line : tutorialText.split("\n")) {
	        g2.drawString(line, textX, textY);
	        textY += 40; // Espace entre les lignes
	    }
	}
	
	public void options_top(int frameX, int frameY) {
		
		int textX;
		int textY;
		
		///////////
		// Title //
		///////////
		String text = "Options";
		textX = getXforCenteredText(text);
		textY = frameY + gp.tileSize;
		g2.drawString(text, textX, textY);
		
		////////////////////////
		// Full screen ON/OFF //
		////////////////////////
		textX = frameX + gp.tileSize;
		textY += gp.tileSize*2;
		g2.drawString("Plein écran", textX, textY);
		if(commandNum == 0) {
			g2.drawString(">", textX-25, textY);
			if(gp.keyH.enterPressed == true) {
				if(gp.fullScreenOn == false) {
					gp.fullScreenOn = true;
				}
				else if(gp.fullScreenOn == true) {
					gp.fullScreenOn = false;
				}
				subState = 1;
			}
		}
		
		///////////
		// Music //
		///////////
		textY += gp.tileSize;
		g2.drawString("Musique", textX, textY);
		if(commandNum == 1) {
			g2.drawString(">", textX-25, textY);
		}
		
		////////
		// SE //
		////////
		textY += gp.tileSize;
		g2.drawString("Effets", textX, textY);
		if(commandNum == 2) {
			g2.drawString(">", textX-25, textY);
		}
		
		/////////////
		// Control //
		/////////////
		textY += gp.tileSize;
		g2.drawString("Commandes", textX, textY);
		if(commandNum == 3) {
			g2.drawString(">", textX-25, textY);
			if(gp.keyH.enterPressed == true) {
				subState = 2;
				commandNum = 0;
			}
		}
		
		//////////////
		// End game //
		//////////////
		textY += gp.tileSize;
		g2.drawString("Menu", textX, textY);
		if(commandNum == 4) {
			g2.drawString(">", textX-25, textY);
			if(gp.keyH.enterPressed == true) {
				subState = 3;
				commandNum = 0;
			}
		}
		
		//////////
		// Back //
		//////////
		textY += gp.tileSize*2;
		g2.drawString("Retour", textX, textY);
		if(commandNum == 5) {
			g2.drawString(">", textX-25, textY);
			if(gp.keyH.enterPressed == true) {
				gp.gameState = gp.playState;
			}
		}
		
		///////////////////////////
		// Full screen check box //
		///////////////////////////
		textX = frameX + (int)(gp.tileSize*4.5);
		textY = frameY + gp.tileSize*2 + 24;
		g2.setStroke(new BasicStroke(3));
		g2.drawRect(textX, textY, 24, 24);
		if(gp.fullScreenOn == true) {
			g2.fillRect(textX, textY, 24, 24);
		}
		
		//////////////////
		// Music volume //
		//////////////////
		textY += gp.tileSize;
		g2.drawRect(textX, textY, 120, 24); // 120/5 = 24
		int volumeWidth = 24 * gp.music.volumeScale;
		g2.fillRect(textX, textY, volumeWidth, 24);
		
		///////////////
		// SE volume //
		///////////////
		textY += gp.tileSize;
		g2.drawRect(textX, textY, 120, 24);
		volumeWidth = 24 * gp.se.volumeScale;
		g2.fillRect(textX, textY, volumeWidth, 24);
		
		gp.config.saveConfig();
	}
		
	public void options_fullScreenNotification(int frameX, int frameY) {
		int textX = frameX + gp.tileSize;
		int textY = frameY + gp.tileSize * 3; // Position de départ de Y pour le texte

	    currentDialogue = "Les changements prendront \neffet après le redémarrage \ndu jeu.";

	    for (String line : currentDialogue.split("\n")) {
	        // Mesurer la largeur de la ligne
	        int textWidth = g2.getFontMetrics().stringWidth(line);
	        // Calculer la position X pour centrer la ligne
	        int textX1 = frameX + (int)(gp.tileSize * 8.2 - textWidth) / 2; // Adaptez la taille du cadre si nécessaire

	        // Dessiner le texte centré
	        g2.drawString(line, textX1, textY);
	        textY += 40; // Espace entre les lignes
	    }

		
		//////////
		// Back //
		//////////
		textY = frameY + gp.tileSize*9;
		g2.drawString("Retour", textX, textY);
		if(commandNum == 0) {
			g2.drawString(">", textX-25, textY);
			if(gp.keyH.enterPressed == true) {
				subState = 0;
			}
		}
	}
	
	public void options_control(int frameX, int frameY) {
		int textX;
		int textY;
		
		///////////
		// Title //
		///////////
		String text = "Control";
		textX = getXforCenteredText(text);
		textY = frameY + gp.tileSize;
		g2.drawString(text, textX, textY);
		
		textX = frameX + gp.tileSize;
		textY += gp.tileSize;
		g2.drawString("Se déplacer", textX, textY); textY += gp.tileSize;
		g2.drawString("Sélectionner", textX, textY); textY += gp.tileSize;
		g2.drawString("Placer", textX, textY); textY += gp.tileSize;
		g2.drawString("Pause", textX, textY); textY += gp.tileSize;
		g2.drawString("Options", textX, textY); textY += gp.tileSize;
		g2.drawString("Debug", textX, textY); textY += gp.tileSize;
		
		textX = frameX + gp.tileSize*6;
		textY = frameY + gp.tileSize*2;
		g2.drawString("ZQSD", textX-10, textY); textY += gp.tileSize;
		g2.drawString("ENTRÉE", textX-10, textY); textY += gp.tileSize;
		g2.drawString("O", textX-10, textY); textY += gp.tileSize;
		g2.drawString("P", textX-10, textY); textY += gp.tileSize;
		g2.drawString("ÉCHAP", textX-10, textY); textY += gp.tileSize;
		g2.drawString("F1", textX-10, textY); textY += gp.tileSize;
		
		//////////
		// Back //
		//////////
		textX = frameX + gp.tileSize;
		textY = frameY + gp.tileSize*9;
		g2.drawString("Retour", textX, textY);
		if(commandNum == 0) {
			g2.drawString(">", textX-25, textY);
			if(gp.keyH.enterPressed == true) {
				subState = 0;
				commandNum = 3;
			}
		}
	}
	
	public void options_endGameConfirmation(int frameX, int frameY) {
		
		int textX = frameX + gp.tileSize;
		int textY = frameY + gp.tileSize * 3;

	    currentDialogue = "Voulez-vous quitter le jeu et \nrevenir au menu principal ?";

	    for (String line : currentDialogue.split("\n")) {
	        // Mesurer la largeur de la ligne
	        int textWidth = g2.getFontMetrics().stringWidth(line);
	        // Calculer la position X pour centrer la ligne
	        int textX1 = frameX + (int)(gp.tileSize * 8.2 - textWidth) / 2; // Adaptez la taille du cadre si nécessaire

	        // Dessiner le texte centré
	        g2.drawString(line, textX1, textY);
	        textY += 40; // Espace entre les lignes
	    }
		
		/////////
		// Yes //
		/////////
		String text = "Oui";
		textX = getXforCenteredText(text);
		textY += gp.tileSize*3;
		g2.drawString(text, textX, textY);
		if(commandNum == 0) {
			g2.drawString(">", textX-25, textY);
			if(gp.keyH.enterPressed == true) {
				subState = 0;
				gp.gameState = gp.titleState;
				gp.stopMusic();
			}
		}
		
		////////
		// No //
		////////
		text = "Non";
		textX = getXforCenteredText(text);
		textY += gp.tileSize;
		g2.drawString(text, textX, textY);
		if(commandNum == 1) {
			g2.drawString(">", textX-25, textY);
			if(gp.keyH.enterPressed == true) {
				subState = 0;
				commandNum = 4;
			}
		}
	}
	
	public int getItemIndexOnSlot() {
		int itemIndex = slotCol + (slotRow*5);
		return itemIndex;
	}
	
	public void drawSubWindow(int x, int y, int width, int height) {
		
		Color c = new Color(0, 0, 0, 210);
		g2.setColor(c);
		g2.fillRoundRect(x, y, width, height, 35, 35);
		
		c = new Color(255, 255, 255);
		g2.setColor(c);
		g2.setStroke(new BasicStroke(5));
		g2.drawRoundRect(x+5, y+5, width-10, height-10, 25, 25);
		
	}

	public int getXforCenteredText(String text) {
		
		int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
		int x = gp.screenWidth/2 - length/2;
		return x;
	}
	
	public int getXforAlignToRightText(String text, int tailX) {
		
		int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
		int x = tailX - length;
		return x;
	}

	public void drawDepollutionBar(Graphics2D g2) {
	    int barWidth = 200;
	    int barHeight = 33;
	    int barX = 740;
	    int barY = 20;

	    int currentBarWidth = (int)((double)gp.depollutionPercentage / 100 * barWidth);

	    // Couleur de fond de la barre
	    g2.setColor(Color.GRAY);
	    g2.fillRect(barX, barY, barWidth, barHeight);

	    // Couleur de progression
	    g2.setColor(Color.GREEN);
	    g2.fillRect(barX, barY, currentBarWidth, barHeight);

	    // Fixer la police pour le texte de pourcentage, indépendamment de l'état du jeu
	    g2.setFont(new Font("Arial", Font.BOLD, 30));  // Police fixe
	    g2.setColor(Color.WHITE); // Couleur du texte

	    // Afficher le pourcentage de dépollution centré sur la barre
	    String percentageText = gp.depollutionPercentage + "%";
	    int textX = barX + barWidth / 2 - g2.getFontMetrics().stringWidth(percentageText) / 2;
	    int textY = barY + barHeight - 5;
	    g2.drawString(percentageText, textX, textY);
	}

}