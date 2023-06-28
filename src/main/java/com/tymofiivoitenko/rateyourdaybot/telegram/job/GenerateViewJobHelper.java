package com.tymofiivoitenko.rateyourdaybot.telegram.job;

import org.w3c.dom.Document;
import org.xhtmlrenderer.swing.Java2DRenderer;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class GenerateViewJobHelper {

    private static final Integer IMAGE_WIDTH = 2048;

    private static final Integer IMAGE_HEIGHT = -1;

    protected InputStream generateInputStreamFromHtml(String html) throws ParserConfigurationException, IOException, SAXException {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new ByteArrayInputStream(html.getBytes()));

        Java2DRenderer imageRenderer = new Java2DRenderer(doc, IMAGE_WIDTH, IMAGE_HEIGHT);
        imageRenderer.setBufferedImageType(BufferedImage.TYPE_INT_RGB);

        BufferedImage image = imageRenderer.getImage();
        var os = new ByteArrayOutputStream();

        ImageIO.write(image, "jpeg", os);
        return new ByteArrayInputStream(os.toByteArray());
    }
}
