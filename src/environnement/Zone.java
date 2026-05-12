package environnement;
public class Zone {

    private int pollutionLevel;  // Niveau de pollution de la zone
    private int maxPollution;    // Niveau maximum de pollution pour calculer le pourcentage de dépollution
    private int x;               // Position de la zone sur la carte
    private int y;
    private int width, height;   // Dimensions de la zone

    // Constructeur
    public Zone(int x, int y, int width, int height, int maxPollution) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.maxPollution = maxPollution;
        this.pollutionLevel = maxPollution;  // La zone commence avec le niveau de pollution maximum
    }

    // Réduit le niveau de pollution
    public void reducePollution(int amount) {
        System.out.println("Pollution avant : " + pollutionLevel);
        pollutionLevel -= amount;
        if (pollutionLevel < 0) {
            pollutionLevel = 0;
        }
        System.out.println("Pollution après : " + pollutionLevel);
    }


    // Récupère le niveau de pollution actuel
    public int getPollutionLevel() {
        return pollutionLevel;
    }

    // Récupère le pourcentage de dépollution actuel
    public int getDepollutionPercentage() {
        return (int) ((1 - (double) pollutionLevel / maxPollution) * 100);
    }

    // Vérifie si un bâtiment peut être placé dans cette zone
    public boolean canPlaceBuilding(int buildingX, int buildingY) {
        return buildingX >= x && buildingX < x + width && buildingY >= y && buildingY < y + height;
    }

    // Accesseurs pour obtenir la position et les dimensions de la zone
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    // Retourne le niveau de pollution maximum pour cette zone
    public int getMaxPollution() {
        return maxPollution;
    }
}