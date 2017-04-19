import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
//import java.util.Base64;
import java.util.Scanner;
import java.util.Vector;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.xml.bind.DatatypeConverter;
public class Steganography {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Steganography se = new Steganography();
		String mesg = se.decoder("/home/pi/","abc3");
		//String mesg= "WkmwnWAQ1XInUkNPkQLGzw==%HoG/uA1u1wirtdWjsleFbA==";
		//byte[] bytes= Base64.decode(mesg);
		System.out.println("mesg ala"+mesg);
		String[] parts = mesg.split("%");
		String part1 = parts[0]; // 004
		String part2 = parts[1];
		// decode the base64 encoded string
		byte[] message=Base64.decode(part1);//DatatypeConverter.parseBase64Binary( part1);
		System.out.println("message"+message);
		byte[] decodedKey = Base64.decode(part2);//DatatypeConverter.parseBase64Binary( part2);
		//byte[] key=part2.getBytes("UTF-8");
		
		System.out.println("message"+decodedKey);
		// rebuild key using SecretKeySpec
		SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
		System.out.println("message"+originalKey.toString());
		try {
			String decryptedMsg= Steganography.getDecryptedText(message, originalKey).toString();
			System.out.println("Decrypted Text " +decryptedMsg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e);
		}
	}
	
	
	 public static String getDecryptedText(byte[] ciphertext, SecretKey key) throws Exception {
	        Cipher cipher= Cipher.getInstance("AES");
	        cipher.init(Cipher.DECRYPT_MODE,key);
	        byte [] decrypttext=cipher.doFinal(ciphertext);
	        return new String(decrypttext);
	    }

	public String decoder(String path, String name)
	{
		String decode;
		try
		{
			//user space is necessary for decrypting
			BufferedImage image  = user_space(getImage(image_path(path,name,"png")));
			System.out.println("ala bufer nantar");
				decode = Steganography.decode(image);
			//decode = decode_text(get_byte_data(image));
		//System.out.println("ala get byte");
			return(new String(decode));
		}
		catch(Exception e)
		{
//			JOptionPane.showMessageDialog(null, 
//				"There is no hidden message in this image!"+e.toString(),"Error",
//				JOptionPane.ERROR_MESSAGE);
			return "";
		}
	}
	
	public static String decode(BufferedImage bmp)
	{
		byte[] b = null;
		
		try 
		{
			//int[] pixels = new int[bmp.getWidth() * bmp.getHeight()];
			//bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
			int[] pixels = bmp.getRGB(0, 0, bmp.getWidth(), bmp.getHeight(), null, 0, bmp.getWidth());
			b = LSB2bit.convertArray(pixels);
		} 
		catch (OutOfMemoryError er) 
		{
			System.out.println( "Image too large, out of memory!");
		}
		
		final String vvv = LSB2bit.decodeMessage(b, bmp.getWidth(), bmp.getHeight());
		
		return vvv;
	}

	
	private BufferedImage user_space(BufferedImage image)
	{
		//create new_img with the attributes of image
		BufferedImage new_img  = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D	graphics = new_img.createGraphics();
		graphics.drawRenderedImage(image, null);
		graphics.dispose(); //release all allocated memory for this image
		return new_img;
	}
	private BufferedImage getImage(String f)
	{
		BufferedImage 	image	= null;
		File file 	= new File(f);
		
		try
		{
			image = ImageIO.read(file);
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog(null, "Image could not be read!","Error",JOptionPane.ERROR_MESSAGE);
		}
		return image;
	}
	
	private String image_path(String path, String name, String ext)
	{
		return path + "/" + name + "." + ext;
	}
	
	

}

   class LSB2bit {

	private static int[] binary = { 16, 8, 0 };	
	private static byte[] andByte = { (byte) 0xC0, 0x30, 0x0C, 0x03 };
	private static int[] toShift = { 6, 4, 2, 0 };
	public static String END_MESSAGE_COSTANT = "#!@";
	public static String START_MESSAGE_COSTANT = "@!#";

	/**
	 * This method represent the core of LSB on 2 bit (Encoding).
	 * @param oneDPix The <b>rgb</b> array.
	 * @param imgCols Image width.
	 * @param imgRows Image height.
	 * @param str Message to encode.
	 * @param hand A handler interface, for the progress bar.
	 * @return Encoded message image.
	 */
	public static byte[] encodeMessage(int[] oneDPix, int imgCols, int imgRows,
			String str) {
		str += END_MESSAGE_COSTANT;
		str = START_MESSAGE_COSTANT + str;
		byte[] msg = str.getBytes();
		int channels = 3;
		int shiftIndex = 4;
		//Array.newInstance(Byte.class, imgRows * imgCols * channels);
		byte[] result = new byte[imgRows * imgCols * channels];
		
		int msgIndex = 0;
		int resultIndex = 0;
		boolean msgEnded = false;
		for (int row = 0; row < imgRows; row++) {
			for (int col = 0; col < imgCols; col++) {
				int element = row * imgCols + col;
				byte tmp = 0;

				for (int channelIndex = 0; channelIndex < channels; channelIndex++) {
					if (!msgEnded) {
						tmp = (byte) ((((oneDPix[element] >> binary[channelIndex]) & 0xFF) & 0xFC) | ((msg[msgIndex] >> toShift[(shiftIndex++)
								% toShift.length]) & 0x3));// 6
						if (shiftIndex % toShift.length == 0) {
							msgIndex++;
						}
						if (msgIndex == msg.length) {
							msgEnded = true;
						}
					} else {
						tmp = (byte) ((((oneDPix[element] >> binary[channelIndex]) & 0xFF)));
					}
					result[resultIndex++] = tmp;
				}

			}

		}
		return result;

	}

	/**
	 * This is the decoding method of LSB on 2 bit.
	 * @param oneDPix The byte array image.
	 * @param imgCols Image width.
	 * @param imgRows Image height.
	 * @return The decoded message.
	 */
	public static String decodeMessage(byte[] oneDPix, int imgCols,
			int imgRows) {

		Vector<Byte> v = new Vector<Byte>();

		String builder = "";
		int shiftIndex = 4;
		byte tmp = 0x00;
		for (int i = 0; i < oneDPix.length; i++) {
			tmp = (byte) (tmp | ((oneDPix[i] << toShift[shiftIndex
					% toShift.length]) & andByte[shiftIndex++ % toShift.length]));
			if (shiftIndex % toShift.length == 0) {
				v.addElement(new Byte(tmp));
				byte[] nonso = { (v.elementAt(v.size() - 1)).byteValue() };
				String str = new String(nonso);
				// if (END_MESSAGE_COSTANT.equals(str)) {
				if (builder.endsWith(END_MESSAGE_COSTANT)) {
					break;
				} else {
					builder = builder + str;
					if (builder.length() == START_MESSAGE_COSTANT.length()
							&& !START_MESSAGE_COSTANT.equals(builder)) {
						builder = null;
						break;
					}
				}

				tmp = 0x00;
			}

		}
		if (builder != null)
			builder = builder.substring(START_MESSAGE_COSTANT.length(), builder
					.length()
					- END_MESSAGE_COSTANT.length());
		return builder;

	}
	
	/**
	 * Convert the byte array to an int array.
	 * @param b The byte array.
	 * @return The int array.
	 */

	public static int[] byteArrayToIntArray(byte[] b) {
		//Log.v("Size byte array", b.length+"");
		int size=b.length / 3;
		//Log.v("Size Int array",size+"");
		System.runFinalization();
		System.gc();
		//Log.v("FreeMemory", Runtime.getRuntime().freeMemory()+"");
		int[] result = new int[size];
		int off = 0;
		int index = 0;
		while (off < b.length) {
			result[index++] = byteArrayToInt(b, off);
			off = off + 3;
		}

		return result;
	}

	/**
	 * Convert the byte array to an int.
	 * 
	 * @param b
	 *            The byte array
	 * @return The integer
	 */
	public static int byteArrayToInt(byte[] b) {
		return byteArrayToInt(b, 0);
	}

	/**
	 * Convert the byte array to an int starting from the given offset.
	 * 
	 * @param b
	 *            The byte array
	 * @param offset
	 *            The array offset
	 * @return The integer
	 */
	public static int byteArrayToInt(byte[] b, int offset) {
		int value = 0x00000000;
		for (int i = 0; i < 3; i++) {
			int shift = (3 - 1 - i) * 8;
			value |= (b[i + offset] & 0x000000FF) << shift;
		}
		value = value & 0x00FFFFFF;
		return value;
	}

	/**
	 * Convert integer array representing [argb] values to byte array
	 * representing [rgb] values
	 * 
	 * @param array Integer array representing [argb] values.
	 * @return byte Array representing [rgb] values.
	 */

	public static byte[] convertArray(int[] array) {
		byte[] newarray = new byte[array.length * 3];

		for (int i = 0; i < array.length; i++) {

			/*
			 * newarray[i * 3] = (byte) ((array[i]) & 0xFF); newarray[i * 3 + 1]
			 * = (byte)((array[i] >> 8)& 0xFF); newarray[i * 3 + 2] =
			 * (byte)((array[i] >> 16)& 0xFF);
			 */

			newarray[i * 3] = (byte) ((array[i] >> 16) & 0xFF);
			newarray[i * 3 + 1] = (byte) ((array[i] >> 8) & 0xFF);
			newarray[i * 3 + 2] = (byte) ((array[i]) & 0xFF);

		}
		return newarray;
	}

}