import java.io.IOException;
import java.util.ArrayList;

public class K_Means {


    public enum DistanceType {HAMMING, MANHATTAN, EUCLIDEAN, COSINE, L3 }

    public static ArrayList<Cluster> clusters = new ArrayList<>();
    public static DistanceType distance = DistanceType.EUCLIDEAN;


    public static int calculate_distance(Image image_1, Image image_2){
        switch (distance) {
            case HAMMING:
                return Hamming_Distance(image_1, image_2);
            case MANHATTAN:
                return Manhattan_Distance(image_1, image_2);
            case EUCLIDEAN:
                return Euclidean_Distance(image_1, image_2);
            case COSINE:
                return Cosine_Distance(image_1, image_2);
            case L3:
                return L3_Distance(image_1, image_2);
            default:
                throw new IllegalArgumentException("Unknown distance type: " + distance);
        }
    }

    public static void categorize_images(Image[] images){
        for (int i = 0; i < images.length; i++){
            Image current_image = images[i];
            int smallest_centroid_distance = Integer.MAX_VALUE;
            int index_of_centroid = -1;
            for (int j = 0; j < clusters.size(); j ++){
                Cluster current_cluster = clusters.get(j);
                int distance = calculate_distance(current_image, current_cluster.centroid());
                if (distance < smallest_centroid_distance){
                    smallest_centroid_distance = distance;
                    index_of_centroid = j;
                }
            }
            clusters.get(index_of_centroid).add_image_to_cluster(current_image);
        }
    }

    public static void initialize_centroids(){
        //TODO
        /*
        * Initialize the centroids here. We want to make them as different as possible I think
        * */

    }

    public static void k_means(Image[] images, int max_k){
        //TODO:
        /*
        * Returns the clusters all sorted with the proper images in each one
        * Going to have to keep track of all of the number of buckets that change, or stop at the cutoff number,
        * */

    }

    public static void user_classify(){
        Image[] centroids = new Image[10];
        for (int i = 0; i < clusters.size(); i++){
            centroids[i] = clusters.get(i).centroid();
        }
        Viewer.invoke(centroids, "User Classification", new Viewer.Attributes());
        for (Cluster cluster : clusters){
            cluster.label_digits();
        }

    }

    public static double percentage_accuracy(Image[] images, String filename){
        int[] answers = new int[0];
        //TODO VERIFY THAT THIS IS SUPPOSED TO BE PARLLEL ARRAYS.
        try{
            answers = Image.readLabels(filename);
        } catch (Exception e){
            System.out.println("Invalid filename: " + filename);
        }
        int numCorrect = 0;
        int total = images.length;
        for (int i = 0; i < images.length; i ++){
            if (answers[i] == images[i].label()){
                numCorrect++;
            }
        }
        return numCorrect/(double)total;
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

        //TODO: Add each of the images to a cluster
        /*
        * We can do this using an additional method that takes in an image and the cluster array
        * It will than figure out which cluster the image most likely belongs in.
        * */
        Image[] images = Image.readImages("train-images");
        categorize_images(images);

        //TODO: Compute K-Means
        /*
        *the result of the k-means is all of the clusters with the images placed in them properly.
        * */
        k_means(images, max_k);


        //TODO: Label each of the clusters the correct value
        /*
        * Display the gui, and display each of the centroids (user will then label them)
        * Invoke the classify
        * */
        user_classify();

        //------<<AT THIS POINT THE TRAINING IS DONE>>-----------


        //TODO: Calculate Accuracy
        /*
        * Go through all of the images in the MNIST set,
        * */
        double accuracy = percentage_accuracy(images, "train-labels");












        //-----

        //TODO: File output:
        /*
        * I think after we complete the calculations it will be pretty easy to export the correct files.
        * */

    }








    //--------------===<<DISTANCE METHODS>>===-----------------
    private static int L1_Distance(Image image_1, Image image_2){
        //TODO
        /*
         * Calculates the L1_distance*/
        return -1;
    }

    private static int L3_Distance(Image image_1, Image image_2){
        //TODO
        /*
         * Calculates the L3_distance*/
        return -1;
    }

    private static int Cosine_Distance(Image image_1, Image image_2){
        //TODO
        /*
         * Calculates the Costine_distance*/
        return -1;
    }

    private static int Euclidean_Distance(Image image_1, Image image_2){
        //TODO
        /*
         * Calculates the Euclidean_Distance*/
        return -1;
    }

    private static int Manhattan_Distance(Image image_1, Image image_2){
        //TODO
        /*
         * Calculates the Manhattan_Distance*/
        return -1;
    }

    private static int Hamming_Distance(Image image_1, Image image_2) {
        //TODO
        /*
         * Calculates the Hamming_Distance*/
        return -1;
    }



}
