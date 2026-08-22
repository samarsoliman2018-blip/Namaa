package UI;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.io.File;

public class ImageLoader {
    
    /**
     * Loads an image from the Images folder
     * @param path The path to the image (e.g., "/Images/logo.png" or "Images/logo.png")
     * @return ImageIcon or null if not found
     */
	public static ImageIcon loadImage(String path) {
	    // Try as resource
	    URL imgURL = ImageLoader.class.getResource(path);
	    if (imgURL != null) {
	        return new ImageIcon(imgURL);
	    }
	    
	    // Try from working directory
	    File file = new File(path);
	    if (file.exists()) {
	        return new ImageIcon(file.getAbsolutePath());
	    }
	    
	    System.err.println("⚠️ Image not found: " + path);
	    return null;
	}
    /**
     * Loads a scaled image
     */
    public static ImageIcon loadScaledImage(String path, int width, int height) {
        ImageIcon icon = loadImage(path);
        if (icon != null) {
            Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        }
        return null;
    }
}