import java.util.ArrayList;

public class Cluster implements Cluster_Interface {

    private static int numClusters = 0; // Static variable to keep track of the number of clusters

    private Image centroid;
    private ArrayList<Image> cluster = new ArrayList<>();
    private int userClassifiedDigit;
    private boolean centroidNeedsUpdate = false;
    private int clusterId;

    // Constructor with centroid initialization
    public Cluster(Image centroid) {
        this.centroid = centroid;
        incrementClusterCount();
        this.clusterId = numClusters;
    }

    // Default constructor
    public Cluster() {
        incrementClusterCount();
        this.clusterId = numClusters;
    }

    // Increment the static cluster count
    private static void incrementClusterCount() {
        numClusters++;
    }

    @Override
    public void add_image_to_cluster(Image image) {
        cluster.add(image);
        centroidNeedsUpdate = true;
    }

    @Override
    public ArrayList<Image> get_cluster() {
        return cluster;
    }

    public void set_image_cluster(ArrayList<Image> cluster) {
        this.cluster = cluster;
    }

    // Calculate centroid if needed
    private Image calculateCentroid() {
        if (!centroidNeedsUpdate && centroid != null) {
            return this.centroid;
        }

        if (cluster.isEmpty()) {
            return null;
        }

        int rows = cluster.get(0).rows();
        int columns = cluster.get(0).columns();
        double[][] centroidPixels = new double[rows][columns];

        for (Image image : cluster) {
            addImageToCentroidPixels(image, centroidPixels);
        }

        normalizeCentroidPixels(centroidPixels, cluster.size());

        byte[][] finalCentroidPixels = new byte[rows][columns];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                finalCentroidPixels[row][col] = (byte) Math.round(centroidPixels[row][col]);
            }
        }

        return new Image(finalCentroidPixels);
    }

    // Add image pixels to centroid pixels
    private void addImageToCentroidPixels(Image image, double[][] centroidPixels) {
        int rows = image.rows();
        int columns = image.columns();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                centroidPixels[row][col] += image.get(row, col);
            }
        }
    }

    // Normalize the centroid pixels by the cluster size
    private void normalizeCentroidPixels(double[][] centroidPixels, int clusterSize) {
        int rows = centroidPixels.length;
        int columns = centroidPixels[0].length;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                centroidPixels[row][col] /= clusterSize;
            }
        }
    }

    // Update centroid if needed
    private void updateCentroid() {
        if (centroidNeedsUpdate) {
            this.centroid = calculateCentroid();
            centroidNeedsUpdate = false; // Reset dirty flag after updating centroid
        }
    }

    @Override
    public void set_centroid(Image image) {
        this.centroid = image;
        this.centroidNeedsUpdate = false;
    }

    @Override
    public int getUser_classified_digit() {
        return this.userClassifiedDigit;
    }

    @Override
    public Image centroid() {
        updateCentroid();
        return this.centroid;
    }

    public Image non_updated_centroid() {
        return this.centroid;
    }

    @Override
    public void label_digits() {
        userClassifiedDigit = centroid.label();
        for (Image image : cluster) {
            image.label(userClassifiedDigit);
        }
    }

    public int getCluster_id() {
        return this.clusterId;
    }
}
