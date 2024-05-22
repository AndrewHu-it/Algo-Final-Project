import java.io.IOException;
import java.util.ArrayList;

public class K_Means {


    public enum DistanceType {HAMMING, MANHATTAN, EUCLIDEAN, COSINE, L2, L3 }

    public static ArrayList<Cluster> clusters = new ArrayList<>();
    public static DistanceType distance = DistanceType.L2;


    public int calculate_distance(Image image_1, Image image_2){
        switch (distance) {
            case HAMMING:
                return Hamming_Distance(image_1, image_2);
            case MANHATTAN:
                return Manhattan_Distance(image_1, image_2);
            case EUCLIDEAN:
                return Euclidean_Distance(image_1, image_2);
            case COSINE:
                return Cosine_Distance(image_1, image_2);
            case L2:
                return L2_Distance(image_1, image_2);
            case L3:
                return L3_Distance(image_1, image_2);
            default:
                throw new IllegalArgumentException("Unknown distance type: " + distance);
        }
    }

    public static void categorize_images(Image[] images){
        //TODO
        /*
        * Go through all of the images an match them with the centroid that is closest.
        * */
    }

    public static void initialize_centroids(){
        //TODO
        /*
        * Initialize the centroids here. We want to make them as different as possible I think
        * */

    }





    public static void main(String[] args) throws IOException {
        int max_k = 100;


        for (int i = 0; i < args.length; i ++){
            switch(args[i].toUpperCase()){
                case "-HAMMING":
                    distance = DistanceType.HAMMING;
                    break;
                case "-MANHATTAN":
                    distance = DistanceType.MANHATTAN;
                    break;
                case "-EUCLIDEAN":
                    distance = DistanceType.EUCLIDEAN;
                    break;
                case "-COSINE":
                    distance = DistanceType.COSINE;
                    break;
                case "-L2":
                    distance = DistanceType.L2;
                    break;
                case "-L3":
                    distance = DistanceType.L3;
                    break;
                case "-k":
                    max_k = Integer.parseInt(args[++i]);
                default:
                    System.out.println("Unknown parameter: " + args[i]);
            }

        }

        //**Create the clusters**
        ArrayList<Cluster> clusters = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            clusters.add(new Cluster());
        }

        //TODO: Initialize the centroids of the clusters:
        /*
        * We are going to need to think of some clever method to intialize teh centroids.
        * For now I am just going to pass the clusters arraylist into method that will do that for us.
        * */
        initialize_centroids();

        //TODO: Add each of the images to the cluster
        /*
        * We can do this using an additional method that takes in an image and the cluster array
        * It will than figure out which cluster the image most likely belongs in.
        * */
        Image[] images = Image.readImages("test-images");
        categorize_images(images);



        Viewer.invoke(images, "images", new Viewer.Attributes());
        

    }



    //--------------===<<DISTANCE METHODS>>===-----------------
    private int L1_Distance(Image image_1, Image image_2){
        //TODO
        /*
         * Calculates the L1_distance*/
        return -1;
    }

    private int L2_Distance(Image image_1, Image image_2){
        //TODO
        /*
         * Calculates the L1_distance*/
        return -1;
    }

    private int L3_Distance(Image image_1, Image image_2){
        //TODO
        /*
         * Calculates the L3_distance*/
        return -1;
    }

    private int Cosine_Distance(Image image_1, Image image_2){
        //TODO
        /*
         * Calculates the Costine_distance*/
        return -1;
    }

    private int Euclidean_Distance(Image image_1, Image image_2){
        //TODO
        /*
         * Calculates the Euclidean_Distance*/
        return -1;
    }

    private int Manhattan_Distance(Image image_1, Image image_2){
        //TODO
        /*
         * Calculates the Manhattan_Distance*/
        return -1;
    }

    private int Hamming_Distance(Image image_1, Image image_2) {
        //TODO
        /*
         * Calculates the Hamming_Distance*/
        return -1;
    }



}
