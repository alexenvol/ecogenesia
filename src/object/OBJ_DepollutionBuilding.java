package object;

import entity.Entity;
import main.GamePanel;

public class OBJ_DepollutionBuilding extends Entity {

	public static final int DEFAULT_QUANTITY = 1;  // Quantité par défaut
    public static int quantity = DEFAULT_QUANTITY;  // Quantité actuelle de bâtiments
    public static boolean pose = false; 
    
    public OBJ_DepollutionBuilding(GamePanel gp) {
        super(gp);
        
        type = type_building;
        name = "Unité de traitement de l'eau";
        down1 = setup("/objects/depollution_building", gp.tileSize, gp.tileSize);
    }
    
    public static void setPose(boolean state) {
    	OBJ_DepollutionBuilding.pose = state; 
    }
}