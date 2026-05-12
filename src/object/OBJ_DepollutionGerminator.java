package object;

import entity.Entity;
import main.GamePanel;

public class OBJ_DepollutionGerminator extends Entity {
	
	public static final int DEFAULT_QUANTITY = 1;  // Quantité par défaut
    public static int quantity = DEFAULT_QUANTITY;  // Quantité actuelle de bâtiments
    public static boolean pose = false; 

	public OBJ_DepollutionGerminator(GamePanel gp) {
		super(gp);
		
        type = type_building;
        name = "Germinator";
        down1 = setup("/objects/germinator", gp.tileSize, gp.tileSize);
	}
	
    public static void setPose(boolean state) {
    	OBJ_DepollutionGerminator.pose = state; 
    }
}