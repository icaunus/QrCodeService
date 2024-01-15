package qrcodeapi;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import org.slf4j.*;

class QrCodeGenerator {
    private static Logger logger = LoggerFactory.getLogger(QrCodeGenerator.class);
  
    public static BufferedImage correctQrCode(final String contents, final String correction, final int width, final int height) {
        QRCodeWriter writer = new QRCodeWriter();
        ErrorCorrectionLevel level = null;
        Map<EncodeHintType, ?> hints = null;
        BitMatrix matrix = null;
        BufferedImage ret = null;

        if (correction.equals("H")) {
            level = ErrorCorrectionLevel.H;
        }
        else if (correction.equals("L")) {
            level = ErrorCorrectionLevel.L;
        }
        else if (correction.equals("M")) {
            level = ErrorCorrectionLevel.M;
        }
        else if (correction.equals("Q")) {
            level = ErrorCorrectionLevel.Q;
        }
        else {
            logger.error("ILLEGAL CORRECTION LEVEL: " + correction);
        }

        try {
            hints = Map.of(EncodeHintType.ERROR_CORRECTION, level);
            matrix = writer.encode(contents, BarcodeFormat.QR_CODE, width, height, hints);
            ret = MatrixToImageWriter.toBufferedImage(matrix);
        }
        catch (WriterException we) {
            we.printStackTrace(System.err);
        }
        finally {
            return ret;
        }
    }

    public static BufferedImage createImage(final int SIZE) {
        BufferedImage ret = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = ret.createGraphics();

        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, SIZE, SIZE);

        return ret;
    }

    public static BufferedImage createQrCode(final String contents, final int width, final int height) {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = null;
        BufferedImage ret = null;

        try {
            bitMatrix = writer.encode(contents, BarcodeFormat.QR_CODE, width, height);
            ret = MatrixToImageWriter.toBufferedImage(bitMatrix);
        }
        catch (WriterException we) {
            we.printStackTrace(System.err);
        }
        finally {
            return ret;
        }
    }
}
