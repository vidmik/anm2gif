package se.vidstedt.anmdecoder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageOutputStream;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

class GifWriter {
    private final Animation animation;
    private final File file;
    private final boolean loop;

    private GifWriter(Animation animation, File file, boolean loop) {
        this.animation = animation;
        this.file = file;
        this.loop = loop;
    }

    GifWriter(Animation animation, File file) {
        this(animation, file, true);
    }

    public void write() throws IOException {
        ImageWriter writer = getGifWriter();

        FileImageOutputStream outputStream = new FileImageOutputStream(file);
        writer.setOutput(outputStream);
        writer.prepareWriteSequence(null);

        if (loop) {
            setLooping(writer);
        }

        writeFrames(writer);
        writer.endWriteSequence();
        outputStream.close();
    }

    private ImageWriter getGifWriter() throws IIOException {
        Iterator<ImageWriter> gifWriters = ImageIO.getImageWritersBySuffix("gif");
        if (!gifWriters.hasNext()) {
            throw new IIOException("No GIF writer found");
        }
        return gifWriters.next();
    }

    private void setLooping(ImageWriter writer) throws IIOInvalidTreeException {
        ImageTypeSpecifier imageTypeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(TYPE_INT_RGB);
        IIOMetadata imageMetaData = writer.getDefaultImageMetadata(imageTypeSpecifier, writer.getDefaultWriteParam());
        String metaFormatName = imageMetaData.getNativeMetadataFormatName();
        IIOMetadataNode root = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);
        IIOMetadataNode applicationExtension = new IIOMetadataNode("ApplicationExtensions");
        root.appendChild(applicationExtension);

        IIOMetadataNode child = new IIOMetadataNode("ApplicationExtension");
        child.setAttribute("applicationID", "NETSCAPE");
        child.setAttribute("authenticationCode", "2.0");
        child.setUserObject(new byte[]{0x1, 0x1, 0x0});
        applicationExtension.appendChild(child);
        imageMetaData.setFromTree(metaFormatName, root);
    }

    private void writeFrames(ImageWriter writer) throws IOException {
        byte[] pixels = new byte[animation.getHeader().getWidth() * animation.getHeader().getHeight()];
        for (int i = 0; i < animation.getRecords().length; i++) {
            Record record = animation.getRecord(i);
            if (record != null) {
                record.decode(pixels);
            }

            writeFrame(writer, pixels);
        }
    }

    private void writeFrame(ImageWriter writer, byte[] pixels) throws IOException {
        BufferedImage bif = new BufferedImage(animation.getHeader().getWidth(), animation.getHeader().getHeight(), TYPE_INT_RGB);
        copyFrame(animation, pixels, bif);

        writer.writeToSequence(new IIOImage(bif, null, null), null);
    }

    private void copyFrame(Animation animation, byte[] pixels, BufferedImage image) {
        for (int y = 0; y < animation.getHeader().getHeight(); y++) {
            for (int x = 0; x < animation.getHeader().getWidth(); x++) {
                image.setRGB(x, y, animation.getPalette().getPalette(pixels[y * animation.getHeader().getWidth() + x]));
            }
        }
    }
}
