package com.androidapp.smartlock;

//import java.awt.Graphics2D;
//import java.awt.image.BufferedImage
//import java.awt.image.DataBufferByte;
//import java.awt.image.WritableRaster;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;


/**
 * Created by shivani on 4/8/17.
 */

public class Steganography {
    static ImageView v = null;
    static int counter = 0;
    public String cryptography(String message, ImageView view) {
        byte[] ciphertext1;
        SecretKey secretKey;
        v = view;
        String new_name;
        try {
            secretKey = getSecretKey();

//            byte[] key= secretKey.toString().getBytes("UTF-8");
//            MessageDigest sha = MessageDigest.getInstance("SHA-1");
//            key= sha.digest(key);

            //System.out.println("The secret key is" + secretKey);
            ciphertext1 = getEncryptedText(message, secretKey);
            String encryptedData= Base64.encodeToString(message.getBytes(),0);
            String encrypted= Base64.encodeToString(ciphertext1,0);
            String encryptedKey= Base64.encodeToString(secretKey.getEncoded(),0);
            //String data = Arrays.toString(secretKey.getEncoded());
            //String buffer = android.util.Base64.encodeToString(secretKey.getEncoded(), 16);
            //String buffer= bytesToString(secretKey.getEncoded());
            String encryptedMsg; //= ciphertext1.toString();
            encryptedMsg = encrypted+"%"+encryptedKey;
            Log.d("bagha","ans"+encryptedMsg);
            String decryptedMsg= getDecryptedText(ciphertext1, secretKey).toString();
            //Toast toast= Toast.makeText(this,encryptedMsg, 10);
            new_name= "abc"+counter;

            Log.d("bagha",encryptedMsg);
            Log.d("bagha",decryptedMsg);
            Log.d("bagha",secretKey.toString());
            Steganography steganography = new Steganography();
            File f = new File(MainActivity.imgDecodableString);
            String imageName = f.getName();
            String path = f.getParent();
            Log.d("path",imageName);
            Log.d("path",path);
            Log.d("path",f.getPath());

            steganography.encode(path, imageName, "", new_name, encryptedMsg,f.getPath());
           // Log.d("decodepath","encode method returned"+imageName);
            String decrypttext = steganography.decode(path, imageName);

            Log.d("decodepath","decode method returned"+decrypttext);
            System.out.println(getDecryptedText(ciphertext1, secretKey));
            String file= image_path(path,new_name,"png");
            File f1 = new File(file);

            Log.d("decodepath","decode method returned"+f1.getPath());
            counter=counter+1;
            return f1.getPath();
        } catch (Exception e) {
            System.out.println(e);
            Log.d("Exception ala",e+"problem");
            return null;
        }
    }



    public static String bytesToString(byte[] b ) {
        byte[] b2 = new byte[b.length + 1];
        b2[0] = 1;
        System.arraycopy(b, 0, b2, 1, b.length);
        return new BigInteger(b2).toString(36);
    }



	/*Cryptography*/
    static String encodedKey = null;
    public static SecretKey getSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator generator= KeyGenerator.getInstance("AES");

        generator.init(128);
        SecretKey key=generator.generateKey();
        /*String password = "password";
        byte[] encoded = new byte[0];
        SecretKeySpec secretKeySpec=null;
        try {
            encoded = password.getBytes("UTF-8");
            secretKeySpec = new SecretKeySpec(encoded,"AES");

            Log.d("encoded",secretKeySpec.getEncoded().toString());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/

        //encodedKey = Base64.encodeToString(encoded,Base64.DEFAULT);
        //Log.d("encodedkey",encodedKey);
        //Key aesKey = new SecretKeySpec(key.getEncoded(), "AES");
        return key;
    }

    public static byte[] getEncryptedText(String message,SecretKey key) throws Exception {

        Cipher cipher= Cipher.getInstance("AES");

        Log.d("Intry","cipher");
        cipher.init(Cipher.ENCRYPT_MODE,key);
        Log.d("Intry","init");
        byte[] ciphertext=cipher.doFinal(message.getBytes());
        return ciphertext;

    }

    public static String getDecryptedText(byte[] ciphertext, SecretKey key) throws Exception {
        Cipher cipher= Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE,key);
        byte [] decrypttext=cipher.doFinal(ciphertext);
        return new String(decrypttext);
    }


    public boolean encode(String path, String original, String ext1, String stegan, String message, String filename)
    {
        String file_name 	= filename;
        Bitmap image_orig	= getImage(file_name);
       // Log.d("path","inside encode"+image_orig.toString());
        //user space is not necessary for Encrypting
        Bitmap image = user_space(image_orig);
        image = Steganography.encode2(image_orig,message);
        //image = add_text(image_orig,message);
        Log.d("path",message);
        //Log.d("shivi",path);
        //Log.d("shivi",stegan);
        return(setImage(image,new File(image_path(path,stegan,"png")),"png"));
    }

	/*
	 *Decrypt assumes the image being used is of type .png, extracts the hidden text from an image
	 *@param path   The path (folder) containing the image to extract the message from
	 *@param name The name of the image to extract the message from
	 *@param type integer representing either basic or advanced encoding
	 */

    //convert to python
    public String decode(String path, String name)
    {
        byte[] decode;
        try
        {
            //user space is necessary for decrypting
            Log.d("shivi","userspac"+path);
            Log.d("shivi","userspac"+name);
            Bitmap image  = user_space(getImage(image_path(path,name,"png")));
            decode = decode_text(get_byte_data(image));
            return(new String(decode));
        }
        catch(Exception e)
        {
            /*JOptionPane.showMessageDialog(null,
                    "There is no hidden message in this image!","Error",
                    JOptionPane.ERROR_MESSAGE);*/
            return "";
        }
    }

    /*
	 *Returns the complete path of a file, in the form: path\name.ext
	 *@param path   The path (folder) of the file
	 *@param name The name of the file
	 *@param ext	  The extension of the file
	 *@return A String representing the complete path of a file
	 */
    private String image_path(String path, String name,String ext)
    {
        return path + "/" + name+"."+ext;
    }

    /*
     *Get method to return an image file
     *@param f The complete path name of the image.
     *@return A BufferedImage of the supplied file path
     *@see	Steganography.image_path
     */
    private Bitmap getImage(String f)
    {
        Bitmap image	= null;
        File file = new File(f);

        try
        {
            image = BitmapFactory.decodeFile(file.toString());
        }
        catch(Exception ex)
        {
            /*JOptionPane.showMessageDialog(null,
                    "Image could not be read!","Error",JOptionPane.ERROR_MESSAGE);*/
        }
        return image;
    }

    /*
     *Set method to save an image file
     *@param image The image file to save
     *@param file	  File  to save the image to
     *@param ext	  The extension and thus format of the file to be saved
     *@return Returns true if the save is succesful
     */
    private boolean setImage(Bitmap image, File file, String ext)
    {
        FileOutputStream out = null;
        try
        {
           // file.delete(); //delete resources used by the File
            out = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
            //Bitmap.compress(image,ext,file);

            return true;
        }
        catch(Exception e)
        {
            /*JOptionPane.showMessageDialog(null,
                    "File could not be saved!","Error",JOptionPane.ERROR_MESSAGE);*/
            return false;
        }
    }

    /*
     *Handles the addition of text into an image
     *@param image The image to add hidden text to
     *@param text	 The text to hide in the image
     *@return Returns the image with the text embedded in it
     */
    private Bitmap add_text(Bitmap image, String text)
    {
        //convert all items to byte arrays: image, message, message length
        byte img[]  = get_byte_data(image);
        byte msg[] = text.getBytes();
        byte len[]   = bit_conversion(msg.length);
        byte[] new_img;
        Bitmap bitmap;


        try

        {
            bitmap= BitmapFactory.decodeByteArray(img,0,img.length);
            Log.d("path",bitmap.toString()+"Image");
            encode_text(img, len,  0); //0 first positiong

            encode_text(img, msg, 32); //4 bytes of space for length: 4bytes*8bit = 32 bits
            //Log.d("path",new_img.toString()+"Here it is");
            Log.d("path",img.toString()+"Image");
        }
        catch(Exception e)
        {
            Log.d("path","Exception"+ e.toString());        }
        bitmap= BitmapFactory.decodeByteArray(img,0,img.length);
        //Log.d("path","bitmap"+bitmap.toString());
        return image;
    }

    /*
     *Creates a user space version of a Buffered Image, for editing and saving bytes
     *@param image The image to put into user space, removes compression interferences
     *@return The user space version of the supplied image
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private Bitmap user_space(Bitmap image)
    {
        //create new_img with the attributes of image
        Bitmap new_img  = Bitmap.createScaledBitmap(image,v.getWidth(), v.getHeight(),false);
        Canvas canvas = new Canvas(new_img);
        v.draw(canvas);
        //Log.d("path","canvas created");
        //canvas.drawBitmap(new_img,0,0,null);
       //canvas.restore();
        /*Graphics2D	graphics = new_img.createGraphics();
        graphics.drawRenderedImage(image, null);
        graphics.dispose(); //release all allocated memory for this image*/
        //Log.d("path","userspace"+new_img.toString());
        return new_img;



    }

    /*
     *Gets the byte array of an image
     *@param image The image to get byte data from
     *@return Returns the byte array of the image supplied
     *@see Raster
     *@see WritableRaster
     *@see DataBufferByte
     */
    private byte[] get_byte_data(Bitmap image)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    /*
     *Gernerates proper byte format of an integer
     *@param i The integer to convert
     *@return Returns a byte[4] array converting the supplied integer into bytes
     */
    private byte[] bit_conversion(int i)
    {
        //originally integers (ints) cast into bytes
        //byte byte7 = (byte)((i & 0xFF00000000000000L) >>> 56);
        //byte byte6 = (byte)((i & 0x00FF000000000000L) >>> 48);
        //byte byte5 = (byte)((i & 0x0000FF0000000000L) >>> 40);
        //byte byte4 = (byte)((i & 0x000000FF00000000L) >>> 32);

        //only using 4 bytes
        byte byte3 = (byte)((i & 0xFF000000) >>> 24); //0
        byte byte2 = (byte)((i & 0x00FF0000) >>> 16); //0
        byte byte1 = (byte)((i & 0x0000FF00) >>> 8 ); //0
        byte byte0 = (byte)((i & 0x000000FF)	   );
        //{0,0,0,byte0} is equivalent, since all shifts >=8 will be 0
        return(new byte[]{byte3,byte2,byte1,byte0});
    }



    /*
	 *Encode an array of bytes into another array of bytes at a supplied offset
	 *@param image	 Array of data representing an image
	 *@param addition Array of data to add to the supplied image data array
	 *@param offset	  The offset into the image array to add the addition data
	 *@return Returns data Array of merged image and addition data
	 */
    private byte[] encode_text(byte[] image, byte[] addition, int offset)
    {
        Log.d("pathinencode","here i am in encodetext");
        //check that the data + offset will fit in the image
        if(addition.length + offset > image.length)
        {
            Log.d("path","ekde pan exception- file not long enough");
            throw new IllegalArgumentException("File not long enough!");
        }
        //loop through each addition byte
        for(int i=0; i<addition.length; ++i)
        {
            //loop through the 8 bits of each byte
            int add = addition[i];
            for(int bit=7; bit>=0; --bit, ++offset) //ensure the new offset value carries on through both loops
            {
                //assign an integer to b, shifted by bit spaces AND 1
                //a single bit of the current byte
                int b = (add >>> bit) & 1;
                //assign the bit by taking: [(previous byte value) AND 0xfe] OR bit to add
                //changes the last bit of the byte in the image to be the bit of addition
                image[offset] = (byte)((image[offset] & 0xFE) | b );
            }
        }
        Bitmap bitmap=BitmapFactory.decodeByteArray(image, 0, image.length);
        Log.d("pathinencode",image.toString()+"zero here i am in encodetext");
        return image;
    }

    /*
     *Retrieves hidden text from an image
     *@param image Array of data, representing an image
     *@return Array of data which contains the hidden text
     */
    private byte[] decode_text(byte[] image)
    {
        int length = 0;
        int offset  = 32;
        //loop through 32 bytes of data to determine text length
        for(int i=0; i<32; ++i) //i=24 will also work, as only the 4th byte contains real data
        {
            length = (length << 1) | (image[i] & 1);
        }

        byte[] result = new byte[length];

        //loop through each byte of text
        for(int b=0; b<result.length; ++b )
        {
            //loop through each bit within a byte of text
            for(int i=0; i<8; ++i, ++offset)
            {
                //assign bit: [(new byte value) << 1] OR [(text byte) AND 1]
                result[b] = (byte)((result[b] << 1) | (image[offset] & 1));
            }
        }
        return result;
    }

    public static Bitmap encode2(Bitmap bmp, String secret)
    {
        int height = bmp.getHeight();
        int width = bmp.getWidth();

        Bitmap newImage = null;
        int[] imgPixels = new int[width * height];
        bmp.getPixels(imgPixels, 0, width, 0, 0, width, height);
        int density = bmp.getDensity();
        bmp.recycle();
        try
        {
            byte[] byteImage = LSB2bit.encodeMessage(imgPixels, width, height, secret);
            newImage = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            newImage.setDensity(density);
            int imgMod[] = LSB2bit.byteArrayToIntArray(byteImage);
            int masterIndex = 0;
            for (int j = 0; j < height; j++)
                for (int i = 0; i < width; i++){
                    // The unique way to write correctly the sourceBitmap, android bug!!!
                    newImage.setPixel(i, j, Color.argb(0xFF,
                            imgMod[masterIndex] >> 16 & 0xFF,
                            imgMod[masterIndex] >> 8 & 0xFF,
                            imgMod[masterIndex++] & 0xFF));
                }

        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
        return newImage;
    }


}
