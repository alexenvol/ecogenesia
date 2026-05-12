package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import javax.swing.JPanel;
import entity.Entity;
import entity.Player;
import environnement.Zone;
import object.OBJ_DepollutionBuilding;
import object.OBJ_DepollutionGerminator;
import object.OBJ_DepollutionHothouse;
import tile.TileManager;

///////////////////////////////////////////
// Classe principale du moteur de jeu 2D //
///////////////////////////////////////////

public class GamePanel extends JPanel implements Runnable {
	
	/////////////////////
	// Screen settings //
	/////////////////////

	private static final long serialVersionUID = 1L;
	final int originalTileSize = 16; // 16 x 16 tile size
	final int scale = 3;
	public final int tileSize = originalTileSize * scale; // 48 x 48 tile size
	public final int maxScreenCol = 20;
	public final int maxScreenRow = 12;
	public final int screenWidth = tileSize * maxScreenCol; // 960 pixels
	public final int screenHeight = tileSize * maxScreenRow; // 576 pixels
	
	////////////////
	// Debug mode //
	////////////////
	
	public boolean debugMode = false; // Par défaut, le mode débogage est désactivé
	
	////////////////////
	// World settings //
	////////////////////
	
	public int maxWorldCol;
	public int maxWorldRow;
	public final int maxMap = 10;
	public int currentMap = 0;
	
	/////////////////////
	// For full screen //
	/////////////////////
	
	int screenWidth2 = screenWidth;
	int screenHeight2 = screenHeight;
	BufferedImage tempScreen;
	Graphics2D g2;
	public boolean fullScreenOn = false;
	
	/////////////////////////////
	// Frames Per Second (FPS) //
	/////////////////////////////
	
	int FPS = 60;
	
	////////////
	// System //
	////////////
	
	public TileManager tileM = new TileManager(this);
	public KeyHandler keyH = new KeyHandler(this);
	Sound music = new Sound();
	Sound se = new Sound();
	public CollisionChecker cChecker = new CollisionChecker(this);
	public AssetSetter aSetter = new AssetSetter(this);
	public UI ui = new UI(this);
	public EventHandler eHandler = new EventHandler(this);
	Config config = new Config(this);
	Thread gameThread;
	
	/////////////////
	// Depollution //
	/////////////////
	
	// Nombre de mer polluées
	int nbMerPolluees = countOccurrences(tileM.mapTileNum, 1);
	
	// Nombre d'arbres pollués
	int nbArbresPollues = countOccurrences(tileM.mapTileNum, 2);
	
	// Nombre de sables pollués
	int nbSablesPollues = countOccurrences(tileM.mapTileNum, 7);
	
	///////////////////////
	// Entity and object //
	///////////////////////
	
	public Player player = new Player(this, keyH);
	public Entity obj[][] = new Entity[maxMap][20];
	ArrayList<Entity> entityList = new ArrayList<>();
	
	////////////////////////////////
	// Pourcentage de dépollution //
	////////////////////////////////
	
	int depollutionPercentage = 0;
	
	///////////////////////
	// Zones contaminées //
	///////////////////////
	
	public ArrayList<Zone> zones = new ArrayList<>();
	
	////////////////
	// Game state //
	////////////////
	
	public int gameState;
	public final int titleState = 0;
	public final int playState = 1;
	public final int pauseState = 2;
	public final int dialogueState = 3;
	public final int characterState = 4;
	public final int optionsState = 5;
	public final int successState = 6;
	public final int tutorialState = 7;
	public boolean titleMusicState = true;
	public boolean gameMusicState = true;
	public boolean optionsMusicState = true;
	public GamePanel() {
		
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		this.addKeyListener(keyH);
		this.setFocusable(true);
		this.initializeZones();
	}
	
	public void initializeZones() {
        // Ajout de zones avec leurs coordonnées (x, y), dimensions (width, height) et niveau de pollution maximal (maxPollution)
        
        // Exemple de zone 1 : Position (2, 2), dimensions 5x5, pollution maximale de 100
        zones.add(new Zone(2, 2, 5, 5, 100)); 
        
        // Exemple de zone 2 : Position (10, 10), dimensions 7x7, pollution maximale de 200
        zones.add(new Zone(10, 10, 7, 7, 200));
        
        // Exemple de zone 3 : Position (20, 20), dimensions 6x6, pollution maximale de 150
        zones.add(new Zone(20, 20, 6, 6, 150));
	}
	
	// GamePanel.java

	@Override
	public void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    Graphics2D g2 = (Graphics2D) g;

	    // Dessiner le reste de l'UI
	    ui.draw(g2);

	    g2.dispose();
	}
	
	public void setupGame() {
		
		gameState = titleState;
			
		tempScreen = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
		g2 = (Graphics2D)tempScreen.getGraphics();
		
		if(fullScreenOn == true) {
			setFullScreen();
		}
	}
	
	public void clearDepollutionBuildingsOnMap() {
		for (int i = 0; i < obj[currentMap].length; i++) {
			if (obj[currentMap][i] instanceof OBJ_DepollutionBuilding) {
				obj[currentMap][i] = null;
			}
		}
	}
	
	public void clearDepollutionHothouseOnMap() {
		for (int i = 0; i < obj[currentMap].length; i++) {
			if (obj[currentMap][i] instanceof OBJ_DepollutionHothouse) {
				obj[currentMap][i] = null;
			}
		}
	}
	
	public void clearDepollutionGerminatorOnMap() {
		for (int i = 0; i < obj[currentMap].length; i++) {
			if (obj[currentMap][i] instanceof OBJ_DepollutionGerminator) {
				obj[currentMap][i] = null;
			}
		}
	}
	
	public void resetGameState() {
	    gameState = titleState; // Retourne au menu principal
	    depollutionPercentage = 0; // Réinitialise la dépollution
	    clearDepollutionBuildingsOnMap(); // Supprime les bâtiments de dépollution placés sur la carte
	    player.resetInventory(); // Réinitialise l'inventaire du joueur
	}
	
	public void restart() {
		player.setDefaultValues();
		player.setDefaultPositions();
		player.setItems();
		
		// Réinitialiser la barre de dépollution ou d'autres statistiques
	    depollutionPercentage = 0;
	    
	    // Vider la carte des bâtiments de depollution
	    clearDepollutionBuildingsOnMap();
	    clearDepollutionHothouseOnMap();
	    clearDepollutionGerminatorOnMap();
	    
	    resetTileValues(tileM.mapTileNum);
	    player.tuileDepollutionBuilding = false;
	    player.tuileDepollutionHothouse = false;
	    player.tuileDepollutionGerminator = false;
	    
		// Nombre de mer polluées
		nbMerPolluees = countOccurrences(tileM.mapTileNum, 1);
		
		// Nombre d'arbres pollués
		nbArbresPollues = countOccurrences(tileM.mapTileNum, 2);
		
		// Nombre de sables pollués
		nbSablesPollues = countOccurrences(tileM.mapTileNum, 7);
	    
	    // Réinitialiser la quantité d'objets dans l'inventaire
	    player.resetInventory();
	    
	    // Revenir au menu principal
        gameState = titleState;
	}
	
	public void retry() {
		
		player.setDefaultValues();
		player.setDefaultPositions();
		player.setItems();
	    
	    // Réinitialiser la barre de dépollution ou d'autres statistiques
	    depollutionPercentage = 0;
	    
	    // Vider la carte des bâtiments de depollution
	    clearDepollutionBuildingsOnMap();
	    clearDepollutionHothouseOnMap();
	    clearDepollutionGerminatorOnMap();
	    
	    resetTileValues(tileM.mapTileNum);
	    player.tuileDepollutionBuilding = false;
	    player.tuileDepollutionHothouse = false;
	    player.tuileDepollutionGerminator = false;
	    
		// Nombre de mer polluées
		nbMerPolluees = countOccurrences(tileM.mapTileNum, 1);
		
		// Nombre d'arbres pollués
		nbArbresPollues = countOccurrences(tileM.mapTileNum, 2);
		
		// Nombre de sables pollués
		nbSablesPollues = countOccurrences(tileM.mapTileNum, 7);
	    
	    // Réinitialiser la quantité d'objets dans l'inventaire
	    player.resetInventory();
	    
	    // Revenir à l'état de jeu actif
	    gameState = playState;
	}
	
	public void setFullScreen() {
		
		/////////////////////////////
		// Get local screen device //
		/////////////////////////////
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		gd.setFullScreenWindow(Main.window);
		
		//////////////////////////////////////
		// Get full screen width and height //
		//////////////////////////////////////
		screenWidth2 = Main.window.getWidth();
		screenHeight2 = Main.window.getHeight();
	}

	public void startGameThread() {
		
		gameThread = new Thread(this);
		gameThread.start();
	}
	
	public void run() {
		
		double drawInterval = 1000000000/FPS; // 0.01666 seconds
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;
		long timer = 0;
		long gameTurn = 0;
		
		while(gameThread != null) {
			
			currentTime = System.nanoTime();
			
			delta += (currentTime - lastTime) / drawInterval;
			timer += (currentTime - lastTime);
			lastTime = currentTime;
			gameTurn++;
			
			if(delta >=1) {

				////////////////////////////////////////////////////
				// Update information such as character positions //
				////////////////////////////////////////////////////
				update();
				
				//////////////////////////////////////////////////
				// Draw the screen with the updated information //
				//////////////////////////////////////////////////
				drawToTempScreen(); // Draw everything to the buffered image
				drawToScreen(); // Draw the buffered image to the screen
				delta--;
			}
			
			if(gameState==playState) {
				
				if(gameTurn >= 40000L) {
								
					if(player.tuileDepollutionBuilding == true) {

						if(nbMerPolluees > 0) {
							getAndReplaceRandomCoordinates(tileM.mapTileNum, 1, 6);
							nbMerPolluees--;
						}
						if (nbMerPolluees == 0) {
							increaseDepollutionBar();
							ui.addMessage("Bravo, toute la mer est dépolluée !");
							nbMerPolluees=-1;
						}
					}
					
					if(player.tuileDepollutionHothouse == true) {

						if(nbArbresPollues > 0) {
							getAndReplaceRandomCoordinates(tileM.mapTileNum, 2, 5);
							nbArbresPollues--;
						} 
						if(nbArbresPollues == 0) {
							increaseDepollutionBar();
							ui.addMessage("Bravo, tous les arbres ont été dépollués !");
							nbArbresPollues=-1;
						}
					}
					
					if(player.tuileDepollutionGerminator == true) {

						if(nbSablesPollues > 0) {
							getAndReplaceRandomCoordinates(tileM.mapTileNum, 7, 4);
							nbSablesPollues--;
						}
						if(nbSablesPollues == 0) {
							increaseDepollutionBar();
							ui.addMessage("Bravo, tous les sables ont été dépollués !");
							nbSablesPollues=-1;
						}
					}
					
					gameTurn = 0;
				}
			}
			
			if(timer >= 1000000000) {
				timer = 0;
			}
		}
	}
	
	private int currentMusicIndex = -1; // Suivi de la musique actuellement jouée
	private int pausedMusicFramePosition = 0; // Sauvegarde la position de la musique
	private boolean isMusicPaused = false;   // Indique si la musique est en pause

	public void update() {
		
		switch (gameState) {
	    case titleState:
	        if (currentMusicIndex != 0) { // Vérifie si ce n'est pas déjà la musique du menu
	            stopMusic();
	            playMusic(0); // Musique pour le menu
	            repeatMusic();
	            currentMusicIndex = 0;
	        }
	        break;

	    case playState:

	    	player.update();
	        if (isMusicPaused) { // Si la musique est en pause
	            music.resume(pausedMusicFramePosition); // Reprend la musique à la position sauvegardée
	            isMusicPaused = false; // Réinitialise l'état de pause
	        } else if (currentMusicIndex != 2) { // Si une autre musique est en cours
	            stopMusic();
	            playMusic(2); // Joue la musique du jeu normalement
	            currentMusicIndex = 2;
	            repeatMusic();
	        }
	        break;

	    case optionsState:
	        if (currentMusicIndex != 3) { // Vérifie si ce n'est pas déjà la musique des options
	            // Sauvegarde la position actuelle de la musique avant de l'arrêter
	            if (currentMusicIndex == 2 && !isMusicPaused) { // Si la musique de jeu est en cours
	                pausedMusicFramePosition = music.getCurrentPosition(); // Sauvegarde la position
	                isMusicPaused = true; // Indique que la musique est mise en pause
	            }
	            stopMusic();
	            playMusic(3); // Musique des options
	            currentMusicIndex = 3;
	            repeatMusic();
	        }
	        break;

	    case pauseState:
	        if (currentMusicIndex == 2 && !isMusicPaused) { // Si la musique de jeu est en cours
	            pausedMusicFramePosition = music.getCurrentPosition(); // Sauvegarde la position
	            isMusicPaused = true; // Indique que la musique est mise en pause
	            stopMusic(); // Arrête la musique
	        }
	        break;

	    case successState:
	        if (currentMusicIndex != -1) { // Vérifie si la musique du succès n'est pas déjà jouée
	            stopMusic();
	            playMusic(4); // Joue la musique de succès
	            currentMusicIndex = -1;
	            repeatMusic();
	        }
	        // Ne faites rien d'autre ici pour empêcher les transitions automatiques
	        break;
	    
	    case tutorialState:
	    	player.update();
	    	break;
	}
}
	
	public void drawToTempScreen() {
		///////////
		// Debug //
		///////////
		long drawStart = 0;
		if(keyH.showDebugText == true) {
			drawStart = System.nanoTime();
		}

		//////////////////
		// Title screen //
		//////////////////
		
		if(gameState == titleState) {
			ui.draw(g2);
		}

		////////////
		// Others //
		////////////
		else {
			
			//////////
			// Tile //
			//////////
			tileM.draw(g2);
			
			//////////////////////////////
			// Add entities to the list //
			//////////////////////////////
			entityList.add(player);
			
			for(int i = 0; i < obj[1].length; i++) {
				if(obj[currentMap][i] != null) {
					entityList.add(obj[currentMap][i]);
				}
			}
			
			//////////
			// Sort //
			//////////
			Collections.sort(entityList, new Comparator<Entity>() {

				@Override
				public int compare(Entity e1, Entity e2) {
					
					int result = Integer.compare(e1.worldY, e2.worldY);
					return result;
				}
			});
			
			///////////////////
			// Draw entities //
			///////////////////
			for(int i = 0; i < entityList.size(); i++) {
				entityList.get(i).draw(g2);
			}
			
			///////////////////////
			// Empty entity list //
			///////////////////////
			entityList.clear();
			
			////////
			// UI //
			////////
			ui.draw(g2);
			ui.drawDepollutionBar(g2);
			
			if (depollutionPercentage == 100) {
				gameState = successState;
			}
		}
		
		///////////
		// Debug //
		///////////
		if(keyH.showDebugText == true) {
			long drawEnd = System.nanoTime();
			long passed = drawEnd - drawStart;
			
			g2.setFont(new Font("Arial", Font.PLAIN, 20));
			g2.setColor(Color.white);
			int x = 10;
			int y = 400;
			int lineHeight = 20;
			
			g2.drawString("WorldX" + player.worldX, x, y); y += lineHeight;
			g2.drawString("WorldY" + player.worldY, x, y); y += lineHeight;
			g2.drawString("Col" + (player.worldX + player.solidArea.x)/tileSize, x, y); y += lineHeight;
			g2.drawString("Row" + (player.worldY + player.solidArea.y)/tileSize, x, y); y += lineHeight;
			g2.drawString("Draw time : " + passed, x, y);
		}
	}
	
	public void drawToScreen() {
		Graphics g = getGraphics();
		g.drawImage(tempScreen, 0, 0, screenWidth2, screenHeight2, null);
		g.dispose();
	}
	
	public void playMusic(int i) {
	    music.setFile(i);
	    if (music.clip != null) { // Vérifie si le clip est chargé
	        music.play();
	    } else {
	        System.err.println("Failed to load music index: " + i);
	    }
	}
	
	public void repeatMusic() {
        music.loop();
    }
	
	public void stopMusic() {
	    if (music.clip != null) {
	        music.stop();
	    } else {
	        System.err.println("");
	    }
	}
	
	public void playSE(int i) {
		se.setFile(i);
		se.play();
	}

	public Zone getZoneAt(int x, int y) {
	    for (Zone zone : zones) {
	        if (x >= zone.getX() && x < (zone.getX() + zone.getWidth()) &&
	            y >= zone.getY() && y < (zone.getY() + zone.getHeight())) {
	            return zone;
	        }
	    }
	    return null;
	}
	
	public void increaseDepollutionBar() {
	    if (gameState == playState) {
	        depollutionPercentage += 34;

	        // Limiter la valeur à 100%
	        if (depollutionPercentage > 100) {
	            depollutionPercentage = 100;
	        }

	        // Met à jour l'interface utilisateur
	        ui.drawDepollutionBar(g2);
	    }
	}
	
	public void decreaseDepollutionBar() {
	    // Vérifie si le jeu est en mode "play" avant de mettre à jour
	    if (gameState == playState) {
	        // Calculer le pourcentage de dépollution
	        depollutionPercentage -= 5;

	        // Met à jour la barre de dépollution dans l'interface utilisateur
	        ui.drawDepollutionBar(g2);
	    }
	}
	
	public int countOccurrences(int[][][] mapTileNum, int value) {
	    int count = 0;

	    for (int map = 0; map < mapTileNum.length; map++) { // Parcours des maps
	    	for (int col = 0; col < mapTileNum[map].length; col++) { // Parcours des colonnes
	            for (int row = 0; row < mapTileNum[map][col].length; row++) { // Parcours des lignes
	                if (mapTileNum[map][col][row] == value) {
	                    count++;
	                }
	            }
	        }
	    }
	    
	    return count;
	}
	
	public int[] getAndReplaceRandomCoordinates(int[][][] mapTileNum, int oldValue, int newValue) {
	    ArrayList<int[]> validCoordinates = new ArrayList<>();
	    Random random = new Random();

	    // Parcourt la map pour trouver toutes les coordonnées de oldValue
	    for (int map = 0; map < mapTileNum.length; map++) {
	        for (int col = 0; col < mapTileNum[map].length; col++) {
	            for (int row = 0; row < mapTileNum[map][col].length; row++) {
	                if (mapTileNum[map][col][row] == oldValue) {
	                    validCoordinates.add(new int[] {map, col, row});
	                }
	            }
	        }
	    }

	    // Si aucune tuile ne contient oldValue, retourne null ou une exception
	    if (validCoordinates.isEmpty()) {
	        return null;
	    }

	    // Sélectionne une coordonnée aléatoire parmi les coordonnées valides
	    int randomIndex = random.nextInt(validCoordinates.size());
	    int[] selectedCoordinates = validCoordinates.get(randomIndex);

	    // Remplace oldValue par newValue à cette position
	    int map = selectedCoordinates[0];
	    int col = selectedCoordinates[1];
	    int row = selectedCoordinates[2];
	    mapTileNum[map][col][row] = newValue;

	    // Retourne les coordonnées de la tuile modifiée
	    return selectedCoordinates;
	}
	
	public void resetTileValues(int[][][] mapTileNum) {
	    for (int[][] map : mapTileNum) {
	        for (int[] col : map) {
	            for (int i = 0; i < col.length; i++) {
	                // Réinitialisation instantanée avec des remplacements directs
	                if (col[i] == 6) {
	                    col[i] = 1;
	                } else if (col[i] == 5) {
	                    col[i] = 2;
	                } else if (col[i] == 4) {
	                    col[i] = 7;
	                }
	            }
	        }
	    }
	}
}