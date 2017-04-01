import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import javax.crypto.Cipher;

public class Server {
	
	public static final String PRIVATE_KEY_FILE_CLIENT = "D:\\Studies\\Java Eclipse\\Cryptosystem\\ClientServerSecurity\\src\\private_client.key";

	public static final String PUBLIC_KEY_FILE_CLIENT = "D:\\Studies\\Java Eclipse\\Cryptosystem\\ClientServerSecurity\\src\\public_client.key";

	
	public static final String PRIVATE_KEY_FILE_SERVER= "D:\\Studies\\Java Eclipse\\Cryptosystem\\ClientServerSecurity\\src\\private_server.key";

	public static final String PUBLIC_KEY_FILE_SERVER = "D:\\Studies\\Java Eclipse\\Cryptosystem\\ClientServerSecurity\\src\\public_server.key";

	
	// SERVER RSA KEY GENERATOR WITH 4096 bits
	
	public static void generateKey()
	{
	    try
	    {
	    	final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
	    	keyGen.initialize(4096);
	    	final KeyPair key = keyGen.generateKeyPair();

	    	File serverPrivateKeyFile = new File(PRIVATE_KEY_FILE_SERVER);
	    	File serverPublicKeyFile = new File(PUBLIC_KEY_FILE_SERVER);

	    	// Create files to store public and private key
	    	if (serverPrivateKeyFile.getParentFile() != null)
	    	{
	    		serverPrivateKeyFile.getParentFile().mkdirs();
	    	}
	    	serverPrivateKeyFile.createNewFile();

	    	if (serverPublicKeyFile.getParentFile() != null)
	    	{
	    		serverPublicKeyFile.getParentFile().mkdirs();
	    	}
	    	serverPublicKeyFile.createNewFile();

	    	// WRITING PUBLIC KEY OF THE SERVER
	    	ObjectOutputStream publicKeyOS = new ObjectOutputStream(new FileOutputStream(serverPublicKeyFile));
	    	publicKeyOS.writeObject(key.getPublic());
	    	publicKeyOS.close();

	    	// WRITING PRIVATE KEY OF THE SERVER
	    	ObjectOutputStream privateKeyOS = new ObjectOutputStream(new FileOutputStream(serverPrivateKeyFile));
	    	privateKeyOS.writeObject(key.getPrivate());
	    	privateKeyOS.close();
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	    }

	  }

	 //CHECKING KEYS EXISTENCE
	
	 public static boolean areKeysPresent()
	 {
		  File serverPrivateKey = new File(PRIVATE_KEY_FILE_SERVER);
		  File serverPublicKey = new File(PUBLIC_KEY_FILE_SERVER);

		  if (serverPrivateKey.exists() && serverPublicKey.exists())
		  {
			  return true;
		  }
		  return false;
	  }
	
	 
	// DECRYPTING  RECEIVED MESSAGE WITH SERVERS PRIVATE KEY
	 
	public static String decrypt(byte[] text, PrivateKey key)
	{
		byte[] decryptedText = null;
		try {
		     final Cipher cipher = Cipher.getInstance("RSA");    
		     cipher.init(Cipher.DECRYPT_MODE, key);
		     decryptedText = cipher.doFinal(text);

		    }
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return new String(decryptedText);
	}
	
	
	// ENCRYPTING HASH OF MESSAGE WITH CLIENTS PRIVATE KEY
	
	public static byte[] encrypt(String text, PrivateKey key) {
		  byte[] cipherText = null;
		  try
		  {
			  // get an RSA cipher object and print the provider
			  final Cipher cipher = Cipher.getInstance("RSA");
			  // encrypt the plain text using the public key
			  cipher.init(Cipher.ENCRYPT_MODE, key);
			  cipherText = cipher.doFinal(text.getBytes());
		  }
		  catch (Exception e)
		  {
			  e.printStackTrace();
		  }
		  return cipherText;
	  }
	
	
	//GENERATING HASH OF THE MESSAGE
	
	 public String gethash(String data) throws NoSuchAlgorithmException
	  {
		  MessageDigest m = MessageDigest.getInstance("SHA1");
		  byte[] hash = m.digest(data.getBytes());
		  StringBuffer s = new StringBuffer();
		  for(int i=0;i<hash.length;i++)
		  {
			  s.append(Integer.toString(hash[i]));
		  }
		  return s.toString();
	  }
	  
	 
	 // HEX STRING CONVERTER
	 
	  private String convertToHex(byte[] data) { 
	        StringBuffer buf = new StringBuffer();
	        for (int i = 0; i < data.length; i++) { 
	            int halfbyte = (data[i] >>> 4) & 0x0F;
	            int two_halfs = 0;
	            do { 
	                if ((0 <= halfbyte) && (halfbyte <= 9)) 
	                    buf.append((char) ('0' + halfbyte));
	                else 
	                    buf.append((char) ('a' + (halfbyte - 10)));
	                halfbyte = data[i] & 0x0F;
	            } while(two_halfs++ < 1);
	        } 
	        return buf.toString();
	    } 
	
	
	  // MAIN FUNCTION
	  
	  public static void main(String args[]) throws IOException, ClassNotFoundException, NoSuchAlgorithmException
	  {
		ServerSocket s = new ServerSocket(7);
		Socket s1=s.accept();
		Server ser = new Server();
		String str,serHash;
		
		if (!areKeysPresent())
	    {
			generateKey();
	    }
		
		// READING DATA FROM THE CLIENT
		
		DataInputStream d = new DataInputStream(s1.getInputStream());
		int len = d.readInt();
		byte[] cipherTextClient = new byte[len];
		if(len>0)
		{
			d.readFully(cipherTextClient,0,cipherTextClient.length);
		}
		
		// DECRYPTING THE CIPHERTEXT WITH SERVERS PRIVATE KEY
		
	    ObjectInputStream inputStream = null,inputStream2 = null;
	    
	    // READING SERVERS PRIVATE KEY
	    
	    inputStream = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE_SERVER));
	    final PrivateKey privateKey = (PrivateKey) inputStream.readObject();
	    final String plainText = decrypt(cipherTextClient, privateKey);
	    inputStream.close();
	    s.close();
	    s1.close();
		
		// GENERATING HASH OF THE MESSAGE
	    
		serHash = ser.gethash(plainText.substring(0,1));
		
		// READING CLIENTS PRIVATE KEY
		
		inputStream2 = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE_CLIENT));
		final PrivateKey serPrivateKey = (PrivateKey) inputStream2.readObject();
		
		//ENCRYPTING HASH WITH CLIENTS PRIVATE KEY
		
		final byte[] cipherText = encrypt(serHash, serPrivateKey);
	    StringBuffer sb = new StringBuffer();
		for(int i=0;i<cipherText.length;i++)
		  {
			  sb.append(Integer.toString(cipherText[i]).substring(1));
		  }
		inputStream2.close();
		
		// CONVERTING TO HEX STRING
		
		str = ser.convertToHex(cipherText);
		
		// CHECKING FOR INTEGRITY
		
		if (str.matches(plainText.substring(1, plainText.length())))
		{
			System.out.println("Integrity is confirmed");
			System.out.println("From Server: Message sent is:: "+plainText.substring(0,1));
		}
		else
		{
			System.out.println("Integrity not found");
		}	
		
	}
}
