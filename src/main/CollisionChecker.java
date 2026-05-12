package main;

import entity.Entity;

public class CollisionChecker {
	
	GamePanel gp;
	
	public CollisionChecker(GamePanel gp) {
		
		this.gp = gp;
	}
	
	public void checkTile(Entity entity) {
		
	    int entityLeftWorldX = entity.worldX + entity.solidArea.x;
	    int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
	    int entityTopWorldY = entity.worldY + entity.solidArea.y;
	    int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;
	    int entityLeftCol = entityLeftWorldX / gp.tileSize;
	    int entityRightCol = entityRightWorldX / gp.tileSize;
	    int entityTopRow = entityTopWorldY / gp.tileSize;
	    int entityBottomRow = entityBottomWorldY / gp.tileSize;
	    int tileNum1, tileNum2;
	    
	    if (isInfoTile(entityLeftCol, entityTopRow) || isInfoTile(entityRightCol, entityBottomRow)) {
	        gp.player.onInfoTile = true; // Nouveau boolean dans Player
	    } else {
	        gp.player.onInfoTile = false;
	    }

	    switch (entity.direction) {
	        case "up":
	            entityTopRow = (entityTopWorldY - entity.speed) / gp.tileSize;
	            if (isValidTile(entityLeftCol, entityTopRow) && isValidTile(entityRightCol, entityTopRow)) {
	                tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityTopRow];
	                tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityTopRow];
	                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
	                    entity.collisionOn = true;
	                    System.out.println("TileNum1 et 2 :" + tileNum1 + tileNum2);
	                }
	            }
	            break;
	        case "down":
	            entityBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;
	            if (isValidTile(entityLeftCol, entityBottomRow) && isValidTile(entityRightCol, entityBottomRow)) {
	                tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityBottomRow];
	                tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityBottomRow];
	                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
	                    entity.collisionOn = true;
	                }
	            }
	            break;
	        case "left":
	            entityLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;
	            if (isValidTile(entityLeftCol, entityTopRow) && isValidTile(entityLeftCol, entityBottomRow)) {
	                tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityTopRow];
	                tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityBottomRow];
	                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
	                    entity.collisionOn = true;
	                }
	            }
	            break;
	        case "right":
	            entityRightCol = (entityRightWorldX + entity.speed) / gp.tileSize;
	            if (isValidTile(entityRightCol, entityTopRow) && isValidTile(entityRightCol, entityBottomRow)) {
	                tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityTopRow];
	                tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityBottomRow];
	                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
	                    entity.collisionOn = true;
	                }
	            }
	            break;
	    }
	}

	private boolean isValidTile(int col, int row) {
	    return col >= 0 && col < gp.tileM.mapTileNum[gp.currentMap].length && row >= 0 && row < gp.tileM.mapTileNum[gp.currentMap][0].length;
	}
	
	public boolean isInfoTile(int x, int y) {
	    int tileNum = gp.tileM.mapTileNum[gp.currentMap][x][y];
	    return tileNum == 8; // 5 est l'ID de la tuile "point d'infos".
	}
	
	public int checkObject(Entity entity, boolean player) {
	    int index = 999;

	    // Store original solid area positions
	    int originalEntitySolidAreaX = entity.solidArea.x;
	    int originalEntitySolidAreaY = entity.solidArea.y;

	    for (int i = 0; i < gp.obj[gp.currentMap].length; i++) {
	        if (gp.obj[gp.currentMap][i] != null) {
	            // Store original object solid area positions
	            int originalObjectSolidAreaX = gp.obj[gp.currentMap][i].solidArea.x;
	            int originalObjectSolidAreaY = gp.obj[gp.currentMap][i].solidArea.y;

	            // Calculate solid area positions
	            entity.solidArea.x = entity.worldX + entity.solidArea.x;
	            entity.solidArea.y = entity.worldY + entity.solidArea.y;
	            gp.obj[gp.currentMap][i].solidArea.x = gp.obj[gp.currentMap][i].worldX + gp.obj[gp.currentMap][i].solidArea.x;
	            gp.obj[gp.currentMap][i].solidArea.y = gp.obj[gp.currentMap][i].worldY + gp.obj[gp.currentMap][i].solidArea.y;

	            // Adjust solid area position based on direction
	            switch (entity.direction) {
	                case "up": entity.solidArea.y -= entity.speed; break;
	                case "down": entity.solidArea.y += entity.speed; break;
	                case "left": entity.solidArea.x -= entity.speed; break;
	                case "right": entity.solidArea.x += entity.speed; break;
	            }

	            // Check collision
	            if (entity.solidArea.intersects(gp.obj[gp.currentMap][i].solidArea)) {
	                if (gp.obj[gp.currentMap][i].collision) {
	                    entity.collisionOn = true;
	                }
	                if (player) {
	                    index = i;
	                }
	            }

	            // Reset solid area positions
	            entity.solidArea.x = originalEntitySolidAreaX;
	            entity.solidArea.y = originalEntitySolidAreaY;
	            gp.obj[gp.currentMap][i].solidArea.x = originalObjectSolidAreaX;
	            gp.obj[gp.currentMap][i].solidArea.y = originalObjectSolidAreaY;
	        }
	    }
	    return index;
	}
	
	////////////////////
	// NPC or monster //
	////////////////////
	
	public int checkEntity(Entity entity, Entity[][] target) {
		
		int index = 999;
		
		for(int i = 0; i < target[1].length; i++) {
			
			if(target[gp.currentMap][i] != null) {
				
				//////////////////////////////////////
				// Get entity's solid area position //
				//////////////////////////////////////
				
				entity.solidArea.x = entity.worldX + entity.solidArea.x;
				entity.solidArea.y = entity.worldY + entity.solidArea.y;
				
				//////////////////////////////////////
				// Get object's solid area position //
				//////////////////////////////////////
				
				target[gp.currentMap][i].solidArea.x = target[gp.currentMap][i].worldX + target[gp.currentMap][i].solidArea.x;
				target[gp.currentMap][i].solidArea.y = target[gp.currentMap][i].worldY + target[gp.currentMap][i].solidArea.y;
				
				switch(entity.direction) { 
				case "up": entity.solidArea.y -= entity.speed; break;
				case "down": entity.solidArea.y += entity.speed; break;
				case "left": entity.solidArea.x -= entity.speed; break;
				case "right": entity.solidArea.x += entity.speed; break;
				}
				
				if(entity.solidArea.intersects(target[gp.currentMap][i].solidArea)) {
					if(target[gp.currentMap][i] != entity) {
						entity.collisionOn = true;
						index = i;
					}
				}
				
				entity.solidArea.x = entity.solidAreaDefaultX;
				entity.solidArea.y = entity.solidAreaDefaultY;
				target[gp.currentMap][i].solidArea.x = target[gp.currentMap][i].solidAreaDefaultX;
				target[gp.currentMap][i].solidArea.y = target[gp.currentMap][i].solidAreaDefaultY;
			}
		}
		return index;
	}
	
	public boolean checkPlayer(Entity entity) {
		
		boolean contactPlayer = false;
		
		//////////////////////////////////////
		// Get entity's solid area position //
		//////////////////////////////////////
		entity.solidArea.x = entity.worldX + entity.solidArea.x;
		entity.solidArea.y = entity.worldY + entity.solidArea.y;
		
		//////////////////////////////////////
		// Get object's solid area position //
		//////////////////////////////////////
		gp.player.solidArea.x = gp.player.worldX + gp.player.solidArea.x;
		gp.player.solidArea.y = gp.player.worldY + gp.player.solidArea.y;
		
		switch(entity.direction) { 
		case "up": entity.solidArea.y -= entity.speed; break;
		case "down": entity.solidArea.y += entity.speed; break;
		case "left": entity.solidArea.x -= entity.speed; break;
		case "right": entity.solidArea.x += entity.speed; break;
		}
		
		if(entity.solidArea.intersects(gp.player.solidArea)) {
			entity.collisionOn = true;
			contactPlayer = true;
		}
		
		entity.solidArea.x = entity.solidAreaDefaultX;
		entity.solidArea.y = entity.solidAreaDefaultY;
		gp.player.solidArea.x = gp.player.solidAreaDefaultX;
		gp.player.solidArea.y = gp.player.solidAreaDefaultY;
		
		return contactPlayer;
	}
}