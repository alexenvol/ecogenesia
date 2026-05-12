package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import object.OBJ_DepollutionBuilding;
import object.OBJ_DepollutionGerminator;
import object.OBJ_DepollutionHothouse;

public class KeyHandler implements KeyListener {

	GamePanel gp;
	
	public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed, shotKeyPressed;
	
	///////////
	// Debug //
	///////////
	
	boolean showDebugText = false;
	
	public void resetKeys() {
	    enterPressed = false;
	    upPressed = false;
	    downPressed = false;
	    leftPressed = false;
	    rightPressed = false;
	    shotKeyPressed = false;
	}
	
	public KeyHandler(GamePanel gp) {
		this.gp = gp;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
		int code = e.getKeyCode();
		
		/////////////////
		// Title state //
		/////////////////
		if(gp.gameState == gp.titleState) {
			titleState(code);
		}
		
		////////////////
		// Play state //
		////////////////
		else if (gp.gameState == gp.playState) {
			playState(code);
		}
		
		/////////////////
		// Pause state //
		/////////////////
		else if(gp.gameState == gp.pauseState) {
			pauseState(code);
		}
		
		///////////////////
		// Options state //
		///////////////////
		else if(gp.gameState == gp.optionsState) {
			optionsState(code);
		}
		
		///////////////////
		// Success state //
		///////////////////
		else if(gp.gameState == gp.successState) {
			successState(code);
		}
		
		////////////////
		// Debug mode //
		////////////////
		if(code==KeyEvent.VK_F1) {
			gp.debugMode = !gp.debugMode;
			System.out.println("Debug mode: " + (gp.debugMode ? "ON" : "OFF"));
		}
	}
	
	public void titleState(int code) {
		
		if(code == KeyEvent.VK_UP) {
			gp.ui.commandNum--;
			gp.playSE(5);
			if(gp.ui.commandNum < 0) {
				gp.ui.commandNum = 1;
				
			}
		}
		
		if(code == KeyEvent.VK_DOWN) {
			gp.ui.commandNum++;
			gp.playSE(5);
			if(gp.ui.commandNum > 1) {
				gp.ui.commandNum = 0;
			}
		}
		
		if(code == KeyEvent.VK_ENTER) {
			if(gp.ui.commandNum == 0) {
				gp.gameState = gp.playState;
			}
			
			if(gp.ui.commandNum == 1) {
				System.exit(0);
			}
		}
	}
	
	public void playState(int code) {
	    // Utiliser ZQSD pour le déplacement du personnage
	    if (code == KeyEvent.VK_Z) {  // Z pour haut
	        upPressed = true;
	    }
	    if (code == KeyEvent.VK_S) {  // S pour bas
	        downPressed = true;
	    }
	    if (code == KeyEvent.VK_Q) {  // Q pour gauche
	        leftPressed = true;
	    }
	    if (code == KeyEvent.VK_D) {  // D pour droite
	        rightPressed = true;
	    }

	    // Pour passer en pause
	    if (code == KeyEvent.VK_P) {
	        gp.gameState = gp.pauseState;
	    }

	    // Pour sélectionner un item ou interagir
	    if (code == KeyEvent.VK_ENTER) {
	        enterPressed = true;
	    }

	    // Pour ouvrir le menu des options
	    if (code == KeyEvent.VK_ESCAPE) {
	    	gp.keyH.resetKeys();
	        gp.gameState = gp.optionsState;
	    }

	    // Pour déposer un objet sélectionné
	    if (code == KeyEvent.VK_O) {
	    	if(entity.Player.selectedItem instanceof OBJ_DepollutionBuilding) {
	        gp.player.placeDepollutionBuilding();
	        OBJ_DepollutionBuilding.setPose(true);
	    	}
	    	
	    	if(entity.Player.selectedItem instanceof OBJ_DepollutionHothouse) {
		        gp.player.placeDepollutionHothouse();
		        OBJ_DepollutionHothouse.setPose(true);
		    }
	    	
	    	if(entity.Player.selectedItem instanceof OBJ_DepollutionGerminator) {
		        gp.player.placeDepollutionGerminator();
		        OBJ_DepollutionGerminator.setPose(true);
		    }
	    }

	    // Débogage
	    if (code == KeyEvent.VK_W) {
	        showDebugText = !showDebugText;
	    }
	    if (code == KeyEvent.VK_R) {
	        switch (gp.currentMap) {
	            case 0 -> gp.tileM.loadMap("/maps/worldmap.txt", 0);
	            case 1 -> gp.tileM.loadMap("/maps/indoor01.txt", 1);
	        }
	    }
	    
	    if (code == KeyEvent.VK_LEFT) {
	        // Déplace vers la gauche jusqu'à trouver un objet valide
	        do {
	            if (gp.ui.slotCol > 0) {
	                gp.ui.slotCol--;
	            } else {
	                break; // Arrête si on atteint le bord gauche
	            }
	        } while (gp.ui.slotCol >= 0 && gp.player.inventory.get(gp.ui.slotCol) == null);

	        gp.playSE(5); // Joue un son seulement si un mouvement a eu lieu
	    }

	    if (code == KeyEvent.VK_RIGHT) {
	        // Déplace vers la droite jusqu'à trouver un objet valide
	        do {
	            if (gp.ui.slotCol < gp.player.inventory.size() - 1) {
	                gp.ui.slotCol++;
	            } else {
	                break; // Arrête si on atteint le bord droit
	            }
	        } while (gp.ui.slotCol < gp.player.inventory.size() && gp.player.inventory.get(gp.ui.slotCol) == null);

	        gp.playSE(5); // Joue un son seulement si un mouvement a eu lieu
	    }

	    // Sélectionner un objet de l'inventaire
	    if (code == KeyEvent.VK_ENTER) {
	        gp.player.selectItem();
	    }
	}
	
	public void pauseState(int code) {
		if(code == KeyEvent.VK_P) {
			gp.gameState = gp.playState;
		}
	}
	
	public void optionsState(int code) {
		
		if(code == KeyEvent.VK_ESCAPE) {
			gp.keyH.resetKeys();
			gp.gameState = gp.playState;
		}
		
		if(code == KeyEvent.VK_ENTER) {
			enterPressed = true;
		}
		
		int maxCommandNum = 0;
		switch(gp.ui.subState) {
		case 0: maxCommandNum = 5; break;
		case 3: maxCommandNum = 1; break;
		}
		
		if(code == KeyEvent.VK_UP) {
			gp.ui.commandNum--;
			gp.playSE(5);
			if(gp.ui.commandNum < 0) {
				gp.ui.commandNum = maxCommandNum;
			}
		}
		
		if(code == KeyEvent.VK_DOWN) {
			gp.ui.commandNum++;
			gp.playSE(5);
			if(gp.ui.commandNum > maxCommandNum) {
				gp.ui.commandNum = 0;
			}
		}
		
		if(code == KeyEvent.VK_LEFT) {
			if(gp.ui.subState == 0) {
				if(gp.ui.commandNum == 1 && gp.music.volumeScale > 0) {
					gp.music.volumeScale--;
					gp.music.checkVolume();
					gp.playSE(5);
				}
				if(gp.ui.commandNum == 2 && gp.se.volumeScale > 0) {
					gp.se.volumeScale--;
					gp.se.checkVolume();
					gp.playSE(5);
				}
			}
		}
		
		if(code == KeyEvent.VK_RIGHT) {
			if(gp.ui.subState == 0) {
				if(gp.ui.commandNum == 1 && gp.music.volumeScale < 5) {
					gp.music.volumeScale++;
					gp.music.checkVolume();
					gp.playSE(5);
				}
				if(gp.ui.commandNum == 2 && gp.se.volumeScale < 5) {
					gp.se.volumeScale++;
					gp.se.checkVolume();
					gp.playSE(5);
				}
			}
		}
	}
	
	public void successState(int code) {
	    if (code == KeyEvent.VK_UP) {
	        gp.ui.commandNum--;
	        if (gp.ui.commandNum < 0) {
	            gp.ui.commandNum = 1;
	        }
	        gp.playSE(5);
	    }
	    if (code == KeyEvent.VK_DOWN) {
	        gp.ui.commandNum++;
	        if (gp.ui.commandNum > 1) {
	            gp.ui.commandNum = 0;
	        }
	        gp.playSE(5);
	    }
	    if (code == KeyEvent.VK_ENTER) {
	        if (gp.ui.commandNum == 0) {
	            // Recommencer le jeu
	            gp.retry();
	            gp.gameState = gp.playState;
	        } else if (gp.ui.commandNum == 1) {
	            // Quitter vers le menu principal et réinitialiser le succès
	            gp.resetGameState();  // Utiliser une méthode pour réinitialiser correctement
	            gp.restart();  // Redémarrer le jeu pour nettoyer tous les états persistants
	            gp.ui.commandNum = 0; // Réinitialiser commandNum
	        }
	    }
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
	    int code = e.getKeyCode();
	    
	    // Réinitialiser les touches de déplacement du personnage
	    if (code == KeyEvent.VK_Z) {
	        upPressed = false;
	    }
	    if (code == KeyEvent.VK_S) {
	        downPressed = false;
	    }
	    if (code == KeyEvent.VK_Q) {
	        leftPressed = false;
	    }
	    if (code == KeyEvent.VK_D) {
	        rightPressed = false;
	    }
	}
}