package sd.oms.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import com.google.cloud.storage.BlobId; 
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import com.google.auth.oauth2.*;
import com.google.cloud.storage.*;

public class OMSUtil {
	
	public static String IMAGE_BASE_URL = "https://storage.cloud.google.com/oms_pictures/";
	public static String IMAGE_URL_SUFFIX = ".jpg?authuser=3";
	
	public static String USER_TYPE_SELLER = "SELLER";
	public static String USER_TYPE_CUSTOMER = "CUSTOMER";
	public static String USER_TYPE_ADMIN = "ADMIN";
	
	
	public static String ORDER_STATUS_NEW = "New";
	public static String ORDER_STATUS_PROCESSING = "Processing";
	public static String ORDER_STATUS_CANCELLED = "Cancelled";
	public static String ORDER_STATUS_COMPLETED = "Completed";
	public static String ORDER_STATUS_PAID = "Paid";

	public static String ITEM_STATUS_ORDERED = "Ordered";
	public static String ITEM_STATUS_CANCELLED = "Cancelled";
	public static String ITEM_STATUS_NOT_AVAILABLE = "Not Available";
	
	public static void main(String[] args) throws Exception {

		// Recipient's email ID needs to be mentioned.
		String to = "fromaddress@gmail.com";
		
		// Instantiates a client
	    Storage storage = StorageOptions.getDefaultInstance().getService();

	    // The name for the new bucket
	    String bucketName = args[0];  // "my-new-bucket";

	    // Creates the new bucket
	    Bucket bucket = storage.create(BucketInfo.of(bucketName));

	    System.out.printf("Bucket %s created.%n", bucket.getName());
		
		uploadObject(null, null, null, null);
	}

	public static void uploadObject(String projectId, String bucketName, String objectName, String filePath)
			throws IOException {

		projectId = "ecomm-280407";
		bucketName = "oms_pictures";
		objectName = "test-no-image.jpg";
		filePath = "C:\\Incub\\Workspace\\oms\\oms_pictures\\no-image.jpg";
		
		String jsonPath = "base-project-f69ff8d47af1.json";
		
		try {
//			System.out.println("Here I am");	
//			GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(jsonPath))
//					.createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
//			System.out.println(credentials);
//			Storage store = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
//			System.out.println(store);
//			
//			System.out.println("Buckets:");
//			Storage.Buckets.List buckets = store.buckets().list();
//			for (Bucket bucket : buckets.iterateAll()) {
//				System.out.println(bucket.toString());
//			}

			Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
			BlobId blobId = BlobId.of(bucketName, objectName);
			BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
			storage.create(blobInfo, Files.readAllBytes(Paths.get(filePath)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error: " + e.getMessage());
		}

		System.out.println("File " + filePath + " uploaded to bucket " + bucketName + " as " + objectName);
	}
}
