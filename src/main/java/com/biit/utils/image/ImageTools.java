package com.biit.utils.image;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.biit.logger.BiitCommonLogger;
import com.biit.utils.file.FileReader;
import com.biit.utils.image.exceptions.InvalidRemoteImageDefinition;

public class ImageTools {

	/**
	 * Loads an image from a specific path
	 * 
	 * @param path the path of the image
	 * @return
	 * @throws IOException
	 */
	public static BufferedImage loadFromFile(String path) throws IOException {
		return ImageIO.read(new File(path));
	}

	public static byte[] loadImageFromAbsolutePath(String pathToImage) throws IOException {
		Path path = Paths.get(pathToImage);
		byte[] data = Files.readAllBytes(path);
		return data;
	}

	public static byte[] loadImageFromResource(String resourceName) throws IOException {
		URL url = FileReader.class.getClassLoader().getResource(resourceName);
		Path path = Paths.get(FileReader.convert2OsPath(url));
		byte[] data = Files.readAllBytes(path);
		return data;
	}

	/**
	 * Converts a bufferedImage to byte array.
	 * 
	 * @param image
	 *            the image to convert.
	 * @param format
	 *            the final image format. Can be "jpg", "png", "gif", "bmp",
	 *            "wbmp".
	 * @return
	 * @throws IOException
	 */
	public static byte[] getBytes(BufferedImage image, String format) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, format, baos);
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		baos.close();
		return imageInByte;
	}

	/**
	 * Convert a byte array in a BufferedImage.
	 * 
	 * @param imageInBytes
	 * @return
	 * @throws IOException
	 */
	public static BufferedImage getImage(byte[] imageInBytes) throws IOException {
		InputStream in = new ByteArrayInputStream(imageInBytes);
		BufferedImage bufferedImage = ImageIO.read(in);
		return bufferedImage;
	}

	/**
	 * Saves a buffered image in a file.
	 * 
	 * @param image
	 *            the image to save.
	 * @param format
	 *            the final image format. Can be "jpg", "png", "gif", "bmp",
	 *            "wbmp".
	 * @param path
	 *            the path where the image will be saved.
	 * @throws IOException
	 */
	public static void saveInFile(BufferedImage image, String format, String path) throws IOException {
		ImageIO.write(image, "jpg", new File(path));
	}

	/**
	 * Resizes an image to the specified width and height. The relation between
	 * high and width will be preserved.
	 * 
	 * @param originalImage
	 * @param scaledWidth
	 *            width in pixels.
	 * @param scaledHeigh
	 *            height in pixels.
	 * @return
	 */
	public static BufferedImage resizeImage(Image originalImage, int scaledWidth, int scaledHeigh) {
		return resizeImage(toBufferedImage(originalImage), scaledWidth, scaledHeigh, true);
	}

	/**
	 * Resizes an image to the specified width and height. The relation between
	 * high and width will be preserved.
	 * 
	 * @param originalImage
	 * @param scaledWidth
	 *            width in pixels.
	 * @param scaledHeigh
	 *            height in pixels.
	 * @return
	 */
	public static BufferedImage resizeImage(BufferedImage originalImage, int scaledWidth, int scaledHeigh) {
		return resizeImage(originalImage, scaledWidth, scaledHeigh, true);
	}

	/**
	 * Resizes an image to the specified width and height. The relation between
	 * high and width will be preserved.
	 * 
	 * @param originalImage
	 * @param scaledWidth
	 *            width in pixels.
	 * @param scaledHeigh
	 *            height in pixels.
	 * @param preserveAlpha
	 *            preserve alpha channel.
	 * @return
	 */
	public static BufferedImage resizeImage(BufferedImage originalImage, int scaledWidth, int scaledHeigh,
			boolean preserveAlpha) {
		BufferedImage resizedImage;
		int finalHeigh, finalWidth;
		if (((double) originalImage.getHeight()) / scaledHeigh < ((double) originalImage.getWidth()) / scaledWidth) {
			finalWidth = scaledWidth;
			finalHeigh = (int) (originalImage.getHeight() * scaledWidth / (double) originalImage.getWidth());
		} else {
			finalWidth = (int) (originalImage.getWidth() * scaledHeigh / (double) originalImage.getHeight());
			finalHeigh = scaledHeigh;
		}
		int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
		resizedImage = new BufferedImage(finalWidth, finalHeigh, imageType);
		Graphics2D graphic = resizedImage.createGraphics();
		graphic.setComposite(AlphaComposite.Src);
		graphic.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphic.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		graphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphic.drawImage(originalImage, 0, 0, finalWidth, finalHeigh, null);
		graphic.dispose();

		return resizedImage;
	}

	/**
	 * Converts a given Image into a BufferedImage
	 *
	 * @param image
	 *            The Image to be converted
	 * @return The converted BufferedImage
	 */
	public static BufferedImage toBufferedImage(Image image) {
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}

		// Create a buffered image with transparency
		BufferedImage bufferedimage = new BufferedImage(image.getWidth(null), image.getHeight(null),
				BufferedImage.TYPE_INT_ARGB);

		// Draw the image on to the buffered image
		Graphics2D graphic = bufferedimage.createGraphics();
		graphic.drawImage(image, 0, 0, null);
		graphic.dispose();

		// Return the buffered image
		return bufferedimage;
	}

	/**
	 * Creates a default image with a text.
	 * 
	 * @return
	 */

	public static byte[] createDefaultImage(int imageWidth, int imageHeight, String text) {
		int MIN_SIZE = 200;
		ByteArrayOutputStream imagebuffer = null;
		// No data (because there is not any flow).
		// HEIGHT discount the top margin of panel.
		int width = imageWidth < MIN_SIZE ? MIN_SIZE : imageWidth;
		int height = imageHeight < MIN_SIZE ? MIN_SIZE : imageHeight;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics drawable = image.getGraphics();
		drawable.setColor(Color.WHITE);
		drawable.fillRect(0, 0, (int) width, height);
		drawable.setColor(Color.black);
		drawable.drawString("Image cannot be created. ", width / 2 - 75, height / 2);

		try {
			// Write the image to a buffer.
			imagebuffer = new ByteArrayOutputStream();
			ImageIO.write(image, "png", imagebuffer);

			// Return a stream from the buffer.
			return imagebuffer.toByteArray();
		} catch (IOException e) {
			BiitCommonLogger.severe(ImageTools.class.getName(), e);
			return null;
		}
	}

	/**
	 * Gets an image from an URL and converts it to a byte[] in a specific
	 * format.
	 * 
	 * @param url
	 * @param format
	 *            JPG, PNG, GIF...
	 * @return
	 * @throws InvalidRemoteImageDefinition
	 */
	public static byte[] getImageFromUrl(String url) throws InvalidRemoteImageDefinition {
		try {
			URL urlPath = new URL(url);
			return getImageFromUrl(urlPath);
		} catch (IOException e) {
			BiitCommonLogger.severe(ImageTools.class.getName(), e);
			throw new InvalidRemoteImageDefinition(e.getMessage());
		}
	}

	/**
	 * Gets an image from an URL and converts it to a byte[] in a specific
	 * format.
	 * 
	 * @param url
	 * @param format
	 *            JPG, PNG, GIF...
	 * @return
	 * @throws IOException
	 */
	public static byte[] getImageFromUrl(URL url) throws IOException {
		if (url == null) {
			return null;
		}
		// Read the image ...
		InputStream inputStream;

		// Configure secure connections.
		if (url.toString().startsWith("https")) {
			try {
				// Ignore untrusted certificates in SSL.
				SSLContext sslContext = SSLContext.getInstance("SSL");
				sslContext.init(new KeyManager[0], new TrustManager[] { new DefaultTrustManager() },
						new SecureRandom());
				SSLContext.setDefault(sslContext);
				HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
			} catch (Exception e) {
				BiitCommonLogger.severe(ImageTools.class.getName(), e);
			}
		}
		// Obtain the image.
		inputStream = url.openStream();

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];

		int n = 0;
		while (-1 != (n = inputStream.read(buffer))) {
			output.write(buffer, 0, n);
		}
		inputStream.close();

		byte[] data = output.toByteArray();
		output.close();

		return data;
	}

	public static class DefaultTrustManager implements X509TrustManager {
		public DefaultTrustManager() {
		}

		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}

}
