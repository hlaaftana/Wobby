package metagn.wobby;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Util {
	public static BufferedImage toImage(Object i) throws IOException {
		if (i instanceof File) return ImageIO.read((File) i);
		else if (i instanceof InputStream) return ImageIO.read((InputStream) i);
		else if (i instanceof byte[]) return ImageIO.read(new ByteArrayInputStream((byte[]) i));
		else if (i instanceof URL) return ImageIO.read((URL) i);
		else if (i instanceof BufferedImage) return (BufferedImage) i;
		else return null;
	}

	// groovy has no way to efficiently output integer division bytecode
	public static int intdiv(int a, int b) {
		return a / b;
	}
}
