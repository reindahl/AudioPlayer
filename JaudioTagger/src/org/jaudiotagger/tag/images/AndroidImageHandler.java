package org.jaudiotagger.tag.images;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;

/**
 Image Handling to to use when running on Android

 TODO need to provide Android compatible implementations
 */
public class AndroidImageHandler implements ImageHandler
{
    private static AndroidImageHandler instance;

    public static AndroidImageHandler getInstanceOf()
    {
        if(instance==null)
        {
            instance = new AndroidImageHandler();
        }
        return instance;
    }

    private AndroidImageHandler()
    {

    }

    /**
     * Resize the image until the total size require to store the image is less than maxsize
     * @param artwork
     * @param maxSize
     * @throws IOException
     */
    public void reduceQuality(Artwork artwork, int maxSize) throws IOException
    {
        while(artwork.getBinaryData().length > maxSize)
        {
        	Bitmap srcImage = (Bitmap)artwork.getImage();
            int w = srcImage.getWidth();
            int newSize = w /2;
            makeSmaller(artwork,newSize);
        }
    }
     /**
     * Resize image using Java 2D
      * @param artwork
      * @param size
      * @throws java.io.IOException
      */
    public void makeSmaller(Artwork artwork,int size) throws IOException
    {
//    	Bitmap.createScaledBitmap(src, dstWidth, dstHeight, filter)
    	Bitmap srcImage = (Bitmap)artwork.getImage();

        int w = srcImage.getWidth();
        int h = srcImage.getHeight();

        // Determine the scaling required to get desired result.
        float scaleW = (float) size / (float) w;
        float scaleH = (float) size / (float) h;

       Bitmap bi= Bitmap.createScaledBitmap(srcImage, (int)(w*scaleW), (int)(h*scaleH), false);
        
//        //Create an image buffer in which to paint on, create as an opaque Rgb type image, it doesnt matter what type
//        //the original image is we want to convert to the best type for displaying on screen regardless
//        BufferedImage bi = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
//
//        // Set the scale.
//        AffineTransform tx = new AffineTransform();
//        tx.scale(scaleW, scaleH);
//
//        // Paint image.
//        Graphics2D g2d = bi.createGraphics();
//        g2d.drawImage(srcImage, tx, null);
//        g2d.dispose();


        if(artwork.getMimeType()!=null && isMimeTypeWritable(artwork.getMimeType()))
        {
            artwork.setBinaryData(writeImage(bi,artwork.getMimeType()));
        }
        else
        {
            artwork.setBinaryData(writeImageAsPng(bi));
        }
    }

    public boolean isMimeTypeWritable(String mimeType)
    {
//        Iterator<ImageWriter> writers =  ImageIO.getImageWritersByMIMEType(mimeType);
//        return writers.hasNext();
    	
    	if(mimeType.equals("PNG") || mimeType.equals("JPEG") || mimeType.equals("WEBP")){
    		return true;
    	}else{
    		return false;
    	}
    }
    /**
     *  Write buffered image as required format
     *
     * @param bi
     * @param mimeType
     * @return
     * @throws IOException
     */
    public byte[] writeImage(Object bi,String mimeType) throws IOException
    {
    	
//        Iterator<ImageWriter> writers =  ImageIO.getImageWritersByMIMEType(mimeType);
//        if(writers.hasNext())
//        {
//            ImageWriter writer = writers.next();
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            writer.setOutput(ImageIO.createImageOutputStream(baos));
//            writer.write(bi);
//            return baos.toByteArray();
//        }
    	Bitmap.CompressFormat format;
    	if(mimeType.equals("PNG")){
    		format=Bitmap.CompressFormat.PNG;
    	}else if(mimeType.equals("JPEG")){
    		format=Bitmap.CompressFormat.JPEG;
    	}else if(mimeType.equals("WEBP")){
    		if(android.os.Build.VERSION.SDK_INT>=14){
    			format=Bitmap.CompressFormat.WEBP;
    		}else{
    			throw new IOException("Cannot write to this mimetype");
    		}
    	}else{
    		throw new IOException("Cannot write to this mimetype");
    	}

    	ByteArrayOutputStream stream = new ByteArrayOutputStream();
    	((Bitmap)bi).compress(format, 100, stream);
    	return stream.toByteArray();
    	
        
    }

    /**
     *
     * @param bi
     * @return
     * @throws IOException
     */
    public byte[] writeImageAsPng(Object bi) throws IOException
    {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ImageIO.write(bi, ImageFormats.MIME_TYPE_PNG,baos);
//        return baos.toByteArray();
//        
    	ByteArrayOutputStream stream = new ByteArrayOutputStream();
    	((Bitmap)bi).compress(Bitmap.CompressFormat.PNG, 100, stream);
    	return stream.toByteArray();
    }

    /**
     * Show read formats
     *
     * On Windows supports png/jpeg/bmp/gif
     */
    public void showReadFormats()
    {

    	System.out.println("r PNG");
    	System.out.println("r BMP");
    	if(android.os.Build.VERSION.SDK_INT>=14){
    		System.out.println("r WEBP");
    	}
    	
    }

    /**
     * Show write formats
     *
     * On Windows supports png/jpeg/bmp
     */
    public void showWriteFormats()
    {
    	System.out.println("w PNG");
    	System.out.println("w BMP");
    	if(android.os.Build.VERSION.SDK_INT>=14){
    	System.out.println("w WEBP");
    	}
    }
}