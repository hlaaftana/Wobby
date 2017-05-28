package hlaaftana.wobby

import java.awt.image.BufferedImage
import javax.imageio.ImageIO

class Util {
	static BufferedImage toImage(File i){ ImageIO.read(i) }
	static BufferedImage toImage(InputStream i){ ImageIO.read(i) }
	static BufferedImage toImage(byte[] i){ ImageIO.read(new ByteArrayInputStream(i)) }
	static BufferedImage toImage(URL i){ ImageIO.read(i) }
	static BufferedImage toImage(BufferedImage i){ i }
}
