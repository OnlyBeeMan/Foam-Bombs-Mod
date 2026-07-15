import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class BucketTextureGenerator {
    public static void main(String[] args) {
        try {
            int size = 16;
            BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            
            // Bucket outline (gray/iron)
            int outline = 0xFF555555;
            int fill = 0xFFCCCCCC;
            int foam = 0xFFFFB6C1; // Pink foam
            
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    img.setRGB(x, y, 0); // transparent
                }
            }
            
            // Draw bucket shape
            for (int x = 4; x <= 11; x++) {
                img.setRGB(x, 13, outline);
                img.setRGB(x, 12, fill);
            }
            img.setRGB(3, 12, outline);
            img.setRGB(12, 12, outline);
            
            for (int y = 7; y <= 11; y++) {
                img.setRGB(3, y, outline);
                img.setRGB(12, y, outline);
                for (int x = 4; x <= 11; x++) {
                    img.setRGB(x, y, fill);
                }
            }
            
            // Bucket rim
            for (int x = 2; x <= 13; x++) {
                img.setRGB(x, 6, outline);
            }
            
            // Fill with foam
            for (int y = 5; y <= 6; y++) {
                for (int x = 4; x <= 11; x++) {
                    img.setRGB(x, y, foam);
                }
            }
            // Some foam spilling out
            img.setRGB(4, 4, foam);
            img.setRGB(5, 3, foam);
            img.setRGB(6, 4, foam);
            img.setRGB(8, 4, foam);
            img.setRGB(9, 3, foam);
            img.setRGB(10, 4, foam);
            
            File f = new File("C:/foam-bombs-26.2/src/main/resources/assets/foambombs/textures/item/healing_foam_bucket.png");
            f.getParentFile().mkdirs();
            ImageIO.write(img, "png", f);
            System.out.println("Generated healing_foam_bucket.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
