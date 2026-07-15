import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

public class TextureGenerator {
    public static void main(String[] args) {
        try {
            int size = 16;
            BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Random random = new Random();

            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    // Generate a pinkish color with some noise
                    int baseR = 255;
                    int baseG = 182; // Pink hue
                    int baseB = 193; // Pink hue
                    
                    int noise = random.nextInt(30) - 15;
                    int r = Math.min(255, Math.max(0, baseR + noise));
                    int g = Math.min(255, Math.max(0, baseG + noise));
                    int b = Math.min(255, Math.max(0, baseB + noise));
                    
                    int a = 255; // opaque
                    int p = (a << 24) | (r << 16) | (g << 8) | b;
                    img.setRGB(x, y, p);
                }
            }

            File f = new File("C:/foam-bombs-26.2/src/main/resources/assets/foambombs/textures/block/healing_foam.png");
            f.getParentFile().mkdirs();
            ImageIO.write(img, "png", f);
            System.out.println("Generated healing_foam.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
