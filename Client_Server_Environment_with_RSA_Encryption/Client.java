import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.crypto.*;
import java.security.*;

public class Client {
	
	public static final String PRIVATE_KEY_FILE_CLIENT= "D:\\Studies\\Java Eclipse\\Cryptosystem\\ClientServerSecurity\\src\\private_client.key";

	public static final String PUBLIC_KEY_FILE_CLIENT = "D:\\Studies\\Java Eclipse\\Cryptosystem\\ClientServerSecurity\\src\\public_client.key";

	public static final String PRIVATE_KEY_FILE_SERVER= "D:\\Studies\\Java Eclipse\\Cryptosystem\\ClientServerSecurity\\src\\private_server.key";

	public static final String PUBLIC_KEY_FILE_SERVER = "D:\\Studies\\Java Eclipse\\Cryptosystem\\ClientServerSecurity\\src\\public_server.key";

	// KEY GENERATOR WITH 1024 bit FOR CLIENT
	
	public static void generateKey()
	{
	    try
	    {
	    	final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
	    	keyGen.initialize(1024);
	    	final KeyPair key = keyGen.generateKeyPair();

	    	File clientPrivateKeyFile = new File(PRIVATE_KEY_FILE_CLIENT);
	    	File clientPublicKeyFile = new File(PUBLIC_KEY_FILE_CLIENT);

	    	// Create files to store public and private key
	    	if (clientPrivateKeyFile.getParentFile() != null)
	    	{
	    		clientPrivateKeyFile.getParentFile().mkdirs();
	    	}
	    	clientPrivateKeyFile.createNewFile();

	    	if (clientPublicKeyFile.getParentFile() != null)
	    	{
	    		clientPublicKeyFile.getParentFile().mkdirs();
	    	}
	    	clientPublicKeyFile.createNewFile();

	    	// Saving the Public key in a file
	    	ObjectOutputStream publicKeyOS = new ObjectOutputStream(new FileOutputStream(clientPublicKeyFile));
	    	publicKeyOS.writeObject(key.getPublic());
	    	publicKeyOS.close();

	    	// Saving the Private key in a file
	    	ObjectOutputStream privateKeyOS = new ObjectOutputStream(new FileOutputStream(clientPrivateKeyFile));
	    	privateKeyOS.writeObject(key.getPrivate());
	    	privateKeyOS.close();
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	    }

	  }

	//KEY CHECKER
	
	  public static boolean areKeysPresent()
	  {
		  File clientPrivateKey = new File(PRIVATE_KEY_FILE_CLIENT);
		  File clientPublicKey = new File(PUBLIC_KEY_FILE_CLIENT);

		  if (clientPrivateKey.exists() && clientPublicKey.exists())
		  {
			  return true;
		  }
		  return false;
	  }

	  //RSA HASH ENCRYPTER WITH CLIENTS PRIVATE KEY
	  
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
	  
	  
	  // RSA MESSAGE+HASH ENCRYPTER WITH SERVER PUBLIC KEY
	  
	  public static byte[] encrypt2(String text, PublicKey key) {
		  byte[] cipherText1 = null;
		  try
		  {
			  // get an RSA cipher object and print the provider
			  final Cipher cipher = Cipher.getInstance("RSA");
			  // encrypt the plain text using the public key
			  cipher.init(Cipher.ENCRYPT_MODE, key);
			  cipherText1 = cipher.doFinal(text.getBytes());
		  }
		  catch (Exception e)
		  {
			  e.printStackTrace();
		  }
		  return cipherText1;
	  }
	  
	  
	  // HASH GENERATOR
	  
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
	  
	  public static void main (String args[]) throws UnknownHostException, IOException
	  {
		String hash, hashedText;
		Client c = new Client();
		Socket s = new Socket("127.0.0.2", 7);			 

		try
		{
			
		    if (!areKeysPresent())
		    {
		    	generateKey();
		    }

		    String originalText = "M";	//MESSAGE
		    ObjectInputStream inputStream = null, inputStream2 = null;

		    // READING CLIENTS PRIVATE KEY
		    inputStream = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE_CLIENT));
			final PrivateKey privateKey = (PrivateKey) inputStream.readObject();
			
		    //CREATING A HASH OF THE MESSAGE
			hash = c.gethash(originalText);
			
			//ENCRYPTING THE HASH OF THE MESSAGE WITH CLIENT PRIVATE KEY
		    final byte[] cipherText = encrypt(hash, privateKey);
		    StringBuffer sb = new StringBuffer();
			for(int i=0;i<cipherText.length;i++)
			  {
				  sb.append(Integer.toString(cipherText[i]).substring(1));
			  }
			inputStream.close();
			
			//CONVERTING ENCRYPTED BYTE TO HEXSTRING
		    String str = c.convertToHex(cipherText);
		    
		    //CONCATENATING ORIGINAL TEXT WITH THE ENCRYPTED HASH
		    hashedText = originalText+str;
		    
		    
		    //ENCRYPTING USING SERVER PUBLIC KEY
		    inputStream2 = new ObjectInputStream (new FileInputStream(PUBLIC_KEY_FILE_SERVER));
		    final PublicKey publicKey = (PublicKey) inputStream2.readObject();
		    final byte[] cipherTextServer = encrypt2(hashedText,publicKey);
		    inputStream2.close();
		    
		    //SENDING ENCRYPTED MESSAGE TO THE SERVER
			DataOutputStream d = new DataOutputStream(s.getOutputStream());
			d.writeInt(cipherTextServer.length);
			d.write(cipherTextServer,0,cipherTextServer.length);
			d.flush();
			s.close();
			
		    }
		catch (Exception e)
		{
			e.printStackTrace();
		}
		}
	}

