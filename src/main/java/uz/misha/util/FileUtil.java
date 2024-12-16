package uz.misha.util;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

public class FileUtil {

    private static Properties prop;

    static {
        prop = new Properties();
        try {
            prop.load(getFileInputStream("app.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static File getFileFromResource(String fileName) {
        ClassLoader classLoader = FileUtil.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            try {
                return new File(resource.toURI());
            } catch (URISyntaxException e) {
                System.out.println("file not found! " + fileName);
            }
        }
        return null;
    }

    public static URL getFileUrl(String fileName) {
        ClassLoader classLoader = FileUtil.class.getClassLoader();
        return classLoader.getResource(fileName);
    }

    public static Image getImage(String fileName) {
        return new ImageIcon(getFileUrl(fileName)).getImage();
    }

    public static InputStream getFileInputStream(String fileName) {
        ClassLoader classLoader = FileUtil.class.getClassLoader();
        return classLoader.getResourceAsStream(fileName);
    }

    public static String getProperty(String key) {
        return prop.getProperty(key);
    }
}
