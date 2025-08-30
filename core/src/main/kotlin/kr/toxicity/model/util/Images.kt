package kr.toxicity.model.util

import java.awt.image.RenderedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

fun RenderedImage.toByteArray(): ByteArray {
    return ByteArrayOutputStream().use { buffer ->
        ImageIO.write(this, "png", buffer)
        buffer.toByteArray()
    }
}
