package hlaaftana.wobby

import groovy.transform.CompileStatic

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

@CompileStatic
class Util {
	static BufferedImage toImage(i) {
		switch (i) {
			case File: return ImageIO.read((File) i)
			case InputStream: return ImageIO.read((InputStream) i)
			case byte[]: return ImageIO.read(new ByteArrayInputStream((byte[]) i))
			case URL: return ImageIO.read((URL) i)
			case BufferedImage: return (BufferedImage) i
			default: return null
		}
	}
}
