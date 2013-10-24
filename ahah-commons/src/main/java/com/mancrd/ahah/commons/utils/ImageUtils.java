/**
 * Copyright (c) 2013 M. Alexander Nugent Consulting <i@alexnugent.name>
 *
 * M. Alexander Nugent Consulting Research License Agreement
 * Non-Commercial Academic Use Only
 *
 * This Software is proprietary. By installing, copying, or otherwise using this
 * Software, you agree to be bound by the terms of this license. If you do not agree,
 * do not install, copy, or use the Software. The Software is protected by copyright
 * and other intellectual property laws.
 *
 * You may use the Software for non-commercial academic purpose, subject to the following
 * restrictions. You may copy and use the Software for peer-review and methods verification
 * only. You may not create derivative works of the Software. You may not use or distribute
 * the Software or any derivative works in any form for commercial or non-commercial purposes.
 *
 * Violators will be prosecuted to the full extent of the law.
 *
 * All rights reserved. No warranty, explicit or implicit, provided.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRßANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.mancrd.ahah.commons.utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author timmolter
 */
public final class ImageUtils {

  private static final Logger logger = LoggerFactory.getLogger(ImageUtils.class);

  /**
   * Constructor - Private constructor to prevent instantiation
   */
  private ImageUtils() {

  }

  public static boolean saveJPGWithQuality(BufferedImage pBufferedImage, String pPath, String pName, float quality) {

    if (pBufferedImage.getColorModel().getTransparency() != Transparency.OPAQUE) {
      pBufferedImage = fillTransparentPixels(pBufferedImage, Color.WHITE);
    }

    boolean saveSuccessful = true;
    try {

      new File(pPath).mkdirs(); // make the dirs if they don't exist

      Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
      ImageWriter writer = iter.next();
      // instantiate an ImageWriteParam object with default compression options
      ImageWriteParam iwp = writer.getDefaultWriteParam();
      iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
      iwp.setCompressionQuality(quality); // a float between 0 and 1
      // 1 specifies minimum compression and maximum quality
      File file = new File(pPath + pName + ".jpg");
      logger.debug("Save Path: " + pPath + pName + ".jpg");
      FileImageOutputStream output = new FileImageOutputStream(file);
      writer.setOutput(output);
      IIOImage image = new IIOImage(pBufferedImage, null, null);
      writer.write(null, image, iwp);
      writer.dispose();

    } catch (Exception e) {
      saveSuccessful = false;
      logger.debug("ERROR SAVING JPEG IMAGE!!!", e);
    }
    return saveSuccessful;
  }

  public static byte[] getJpegImageByteArray(BufferedImage bufferedImage, float quality) {

    if (bufferedImage.getColorModel().getTransparency() != Transparency.OPAQUE) {
      bufferedImage = fillTransparentPixels(bufferedImage, Color.WHITE);
    }

    byte[] imageInBytes = null;
    try {

      Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
      ImageWriter writer = iter.next();
      // instantiate an ImageWriteParam object with default compression options
      ImageWriteParam iwp = writer.getDefaultWriteParam();
      iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
      iwp.setCompressionQuality(quality); // a float between 0 and 1
      ByteArrayOutputStream baos = new ByteArrayOutputStream(37628);
      ImageOutputStream output = ImageIO.createImageOutputStream(baos);
      writer.setOutput(output);
      IIOImage image = new IIOImage(bufferedImage, null, null);
      writer.write(null, image, iwp);
      imageInBytes = baos.toByteArray();
      writer.dispose();

    } catch (Exception e) {
      logger.debug("ERROR SAVING JPEG IMAGE!!!", e);
    }

    return imageInBytes;
  }

  public static boolean saveByteArrayToJpeg(byte[] imageInBytes, String path, String name) {

    boolean saveSuccessful = false;
    try {
      // convert byte array back to BufferedImage
      InputStream in = new ByteArrayInputStream(imageInBytes);
      BufferedImage bufferedImage = ImageIO.read(in);
      saveSuccessful = ImageIO.write(bufferedImage, "jpeg", new File(path + name));
    } catch (IOException e) {
      logger.error("ERROR SAVING IMAGE!!!", e);
    }
    return saveSuccessful;

  }

  public static BufferedImage fillTransparentPixels(BufferedImage image, Color fillColor) {

    int w = image.getWidth();
    int h = image.getHeight();
    BufferedImage image2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = image2.createGraphics();
    g.setColor(fillColor);
    g.fillRect(0, 0, w, h);
    g.drawRenderedImage(image, null);
    g.dispose();
    return image2;
  }

  public static boolean savePNG(BufferedImage pBufferedImage, String pPath, String pName) {

    return ImageUtils.saveImage(pBufferedImage, pPath, pName, "png");

  }

  public static boolean saveJPG(BufferedImage pBufferedImage, String pPath, String pName) {

    return ImageUtils.saveImage(pBufferedImage, pPath, pName, "jpg");

  }

  public static boolean saveImage(BufferedImage pBufferedImage, String pPath, String pName, String pFileExtension) {

    new File(pPath).mkdirs(); // make the dirs if they don't exist

    File file = new File(pPath + pName + "." + pFileExtension);

    boolean saveSuccessful = false;
    try {
      saveSuccessful = ImageIO.write(pBufferedImage, pFileExtension, file);
    } catch (IOException e) {
      logger.error("ERROR SAVING IMAGE!!!", e);
    }
    return saveSuccessful;
  }

  public static int[] getPixelARGB(int pixel) {

    int[] argb = new int[4];
    argb[0] = (pixel >> 24) & 0xff; // alpha
    argb[1] = (pixel >> 16) & 0xff; // red
    argb[2] = (pixel >> 8) & 0xff; // green
    argb[3] = (pixel) & 0xff; // blue
    // System.out.println("argb: " + argb[0] + ", " + argb[1] + ", " + argb[2] + ", " + argb[3]);

    return argb;

  }

  public static int getPixelInt(int a, int r, int g, int b) {

    return (a << 24) | (r << 16) | (g << 8) | b;

  }

  /**
   * Generates a BufferedImage from a given URL
   * 
   * @param imageUrl
   * @return BufferedImage - returns null and logs and error if there was a problem
   */
  public static BufferedImage getBufferedImageFromURL(String imageUrl) {

    URL url;
    try {
      url = new URL(imageUrl);
    } catch (MalformedURLException e) {
      logger.error("ERROR GETTING BUFFERED IMAGE FROM URL!!! [" + imageUrl + "] " + e.getClass().getSimpleName());
      return null;
    }

    URLConnection urlConn = null;
    try {
      urlConn = url.openConnection();
    } catch (IOException e) {
      logger.error("ERROR GETTING BUFFERED IMAGE FROM URL!!! [" + imageUrl + "] " + e.getClass().getSimpleName());

    }
    urlConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.21; Mac_PowerPC)");

    try {
      urlConn.connect();
    } catch (IOException e) {
      logger.error("ERROR GETTING BUFFERED IMAGE FROM URL!!! [" + imageUrl + "] " + e.getClass().getSimpleName());
    }

    InputStream urlStream = null;
    try {
      urlStream = urlConn.getInputStream();
    } catch (IOException e) {
      logger.error("ERROR GETTING BUFFERED IMAGE FROM URL!!! [" + imageUrl + "] " + e.getClass().getSimpleName());
    }

    try {
      // BufferedImage image = ImageIO.read(url);
      // BufferedImage image = ImageIO.read(url.openStream());
      BufferedImage image = ImageIO.read(urlStream);
      return image;
    } catch (Exception e) {
      logger.error("ERROR GETTING BUFFERED IMAGE FROM URL!!! [" + imageUrl + "] " + e.getClass().getSimpleName());
      return null;
    }

  }

  public static Dimension getImageSizeWithoutFullDownload(URL url, int timeout) {

    URLConnection urlConn = null;
    try {
      urlConn = url.openConnection();
    } catch (IOException e) {
      logger.error("IOEXCEPTION!!! " + e.getClass().getSimpleName());

    }
    urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.99 Safari/537.22");
    urlConn.setReadTimeout(timeout);
    try {
      urlConn.connect();
    } catch (IOException e) {
      logger.error("IOEXCEPTION!!!  " + e.getClass().getSimpleName());
    }

    InputStream urlStream = null;
    try {
      urlStream = urlConn.getInputStream();
    } catch (IOException e) {
      logger.error("IOEXCEPTION!!! " + e.getClass().getSimpleName());
    }

    try {
      ImageInputStream in = ImageIO.createImageInputStream(urlStream);
      try {
        final Iterator readers = ImageIO.getImageReaders(in);
        if (readers.hasNext()) {
          ImageReader reader = (ImageReader) readers.next();
          try {
            reader.setInput(in);
            return new Dimension(reader.getWidth(0), reader.getHeight(0));
          } finally {
            reader.dispose();
          }
        }
      } finally {
        if (in != null)
          in.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Creates a Thumbnail of an image, maintaining the aspect ratio, and scaling the largest dimension to pSize
   * 
   * @param pBufferedImage
   * @param pSize
   * @return
   */
  public static BufferedImage createThumbnail(BufferedImage pBufferedImage, int pSize) {

    if (pBufferedImage == null) {
      logger.error("GIVEN IMAGE WAS NULL!!! ");
      return null;
    }

    double w = pBufferedImage.getWidth(null);
    double h = pBufferedImage.getHeight(null);

    double scale;
    if (w > h) {// width bigger then height. scale width.
      scale = pSize / w;
    }
    else {
      scale = pSize / h;
    }

    int newHeight = (int) (pBufferedImage.getHeight(null) * scale);
    int newWidth = (int) (pBufferedImage.getWidth(null) * scale);

    // return (BufferedImage) pBufferedImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

    Image lThumbnailImage = pBufferedImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

    return ImageUtils.imageToBufferedImage(lThumbnailImage);

  }

  /**
   * @param pImage
   * @return
   */
  private static BufferedImage imageToBufferedImage(Image pImage) {

    int w = pImage.getWidth(null);
    int h = pImage.getHeight(null);
    int type = BufferedImage.TYPE_INT_ARGB; // other options
    BufferedImage lBufferedImage = new BufferedImage(w, h, type);

    Graphics2D g2 = lBufferedImage.createGraphics();
    g2.drawImage(pImage, 0, 0, null);
    g2.dispose();
    return lBufferedImage;
  }

  /**
   * Creates a BufferredImage give a File
   * 
   * @param pFile
   * @return
   */
  public static BufferedImage getBufferedImageFromFile(File pFile) {

    BufferedImage lBufferedImage = null;
    try {
      lBufferedImage = ImageIO.read(pFile);
    } catch (IOException e) {
      logger.error("ERROR GETTING BUFFERED IMAGE!!!", e);
    }
    return lBufferedImage;
  }

  /**
   * Generates a MD5 Hash for a given Bufferred Image
   * 
   * @param pBufferedImage
   * @return String - a 32 char String representing the MD5 hash of the image.
   */
  public static String getMD5Hash(BufferedImage pBufferedImage) {

    String hexString = "";
    try {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      ImageIO.write(pBufferedImage, "png", outputStream); // works for any type of image
      byte[] data = outputStream.toByteArray();
      MessageDigest md = null;
      md = MessageDigest.getInstance("MD5");
      md.update(data);
      byte[] bytes = md.digest();
      for (int i = 0; i < bytes.length; i++) {
        hexString += Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1);
      }
    } catch (Exception e) {
      logger.error("ERROR CREATING MD5 HASH!!!", e);
    }
    return hexString;
  }

}
