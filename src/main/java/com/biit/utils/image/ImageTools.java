package com.biit.utils.image;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class ImageTools {

	/**
	 * Converts a bufferedImage to byte array.
	 * 
	 * @param image
	 *            the image to convert.
	 * @param format
	 *            the image format. Can be "jpg", "png", "gif", "bmp", "wbmp".
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
	 *            the image format. Can be "jpg", "png", "gif", "bmp", "wbmp".
	 * @param path
	 *            the path where the image will be saved.
	 * @throws IOException
	 */
	public static void saveInFile(BufferedImage image, String format, String path) throws IOException {
		ImageIO.write(image, "jpg", new File(path));
	}

	public static BufferedImage resizeImage(Image originalImage, int scaledWidth, int scaledHeight) {
		return resizeImage(toBufferedImage(originalImage), scaledWidth, scaledHeight, true);
	}

	public static BufferedImage resizeImage(BufferedImage originalImage, int scaledWidth, int scaledHeight) {
		return resizeImage(originalImage, scaledWidth, scaledHeight, true);
	}

	public static BufferedImage resizeImage(BufferedImage originalImage, int scaledWidth, int scaledHeight,
			boolean preserveAlpha) {
		BufferedImage resizedImage;
		int finalHeight, finalWidth;
		if (((double) originalImage.getHeight()) / scaledHeight < ((double) originalImage.getWidth()) / scaledWidth) {
			finalWidth = scaledWidth;
			finalHeight = (int) (originalImage.getHeight() * scaledWidth / (double) originalImage.getWidth());
		} else {
			finalWidth = (int) (originalImage.getWidth() * scaledHeight / (double) originalImage.getHeight());
			finalHeight = scaledHeight;
		}
		int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
		resizedImage = new BufferedImage(finalWidth, finalHeight, imageType);
		Graphics2D graphic = resizedImage.createGraphics();
		graphic.setComposite(AlphaComposite.Src);
		graphic.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphic.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		graphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphic.drawImage(originalImage, 0, 0, finalWidth, finalHeight, null);
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
}
