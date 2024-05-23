import java.lang.reflect.Array;
import java.util.ArrayList;

public class Cluster implements Cluster_Interface {

    private Image centroid;
    private ArrayList<Image> cluster;
    private int user_classified_digit;

    public Cluster() {
        this.cluster = new ArrayList<>();
    }

    public void add_image_to_cluster(Image image) {
        cluster.add(image);
    }

    public ArrayList<Image> get_cluster() {
        return cluster;
    }

    private Image calculate_centroid() {
        if (cluster.isEmpty()) {
            return null; 
        }

        int rows = cluster.get(0).rows();
        int columns = cluster.get(0).columns();
        byte[][] centroidPixels = new byte[rows][columns];
        
        for (Image image : cluster) {
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < columns; col++) {
                    centroidPixels[row][col] += image.get(row, col);
                }
            }
        }

        int clusterSize = cluster.size();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                centroidPixels[row][col] /= clusterSize;
            }
        }
        
        return new Image(centroidPixels);
    }

    public Image centroid() {
        return calculate_centroid();
    }

    public void label_digits() {
        user_classified_digit = centroid.label();
        for (Image image : cluster) {
            image.label(user_classified_digit);
        }
    }
}
