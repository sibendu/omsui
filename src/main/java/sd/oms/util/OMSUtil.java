package sd.oms.util;

import java.util.Properties;

public class OMSUtil {
	
	public static String USER_TYPE_SELLER = "SELLER";
	public static String USER_TYPE_CUSTOMER = "";
	public static String USER_TYPE_ADMIN = "ADMIN";
	
	
	public static String ORDER_STATUS_NEW = "New";
	public static String ORDER_STATUS_PROCESSING = "Processing";
	public static String ORDER_STATUS_CANCELLED = "Cancelled";
	public static String ORDER_STATUS_COMPLETED = "Completed";
	public static String ORDER_STATUS_PAID = "Paid";

	public static String ITEM_STATUS_ORDERED = "Ordered";
	public static String ITEM_STATUS_CANCELLED = "Cancelled";
	public static String ITEM_STATUS_NOT_AVAILABLE = "Not Available";
	
	public static void main(String[] args) {

		// Recipient's email ID needs to be mentioned.
        String to = "fromaddress@gmail.com";
		
	}
	/*
	
	public static void mail(String to) {    

        // Sender's email ID needs to be mentioned
        String from = "toaddress@gmail.com";

        // Assuming you are sending email from through gmails smtp
        String host = "smtp.gmail.com";
        String port = "465";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        // Get the Session object.// and pass username and password
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("fromaddress@gmail.com", "*******");
            }
        });

        // Used to debug SMTP issues
        session.setDebug(true);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            message.setSubject("This is the Subject Line!");

            // Now set the actual message
            message.setText("This is actual message");

            System.out.println("sending...");
            // Send message
            Transport.send(message);
            System.out.println("Sent message successfully....");
            
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
	   }
	*/
}
