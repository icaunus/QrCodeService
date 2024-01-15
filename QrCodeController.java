package qrcodeapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.image.BufferedImage;
import java.util.*;

@RestController
public class QrCodeController {
    private final int IMG_SIZE_MIN = 150;
    private final int IMG_SIZE_MAX = 350;
    private String MSG_HEALTH = "Take care!";

    @GetMapping("/api/health")
    public ResponseEntity health() {
        return new ResponseEntity(MSG_HEALTH, HttpStatus.OK);
    }

    @GetMapping("/api/qrcode")
    public ResponseEntity qrCode(
            @RequestParam String contents,
            @RequestParam(defaultValue="L") String correction,
            @RequestParam(defaultValue="250") int size,
            @RequestParam(defaultValue="png") String type
        ) {
        final String ERR_CONTENT_JSON = "{ \"error\": \"Contents cannot be null or blank\" }";
        final String ERR_SIZE_JSON = "{ \"error\": \"Image size must be between 150 and 350 pixels\" }";
        final String ERR_CORRECTION_JSON = "{ \"error\": \"Permitted error correction levels are L, M, Q, H\" }";
        final String ERR_TYPE_JSON = "{ \"error\": \"Only png, jpeg and gif image types are supported\" }";
        final BufferedImage IMG = QrCodeGenerator.createImage(size);
        final BufferedImage QR_CODE = QrCodeGenerator.createQrCode(contents, size, size);
        final BufferedImage QR_CODE_CORRECTED = QrCodeGenerator.correctQrCode(contents, correction, size, size);
        final boolean IS_CORRECTION_OK = isCorrectionOk(correction);
        final boolean IS_SIZE_OK = size >= IMG_SIZE_MIN && size <= IMG_SIZE_MAX;
        final boolean IS_TYPE_OK = isTypeOk(type);
        final boolean IS_CONTENTS_BLANK = (contents == null) || contents.isBlank();
        HttpHeaders qrCodeHeaders = new HttpHeaders();
        MediaType mediaType = null;

        if (type.equals("gif")) {
            mediaType = MediaType.IMAGE_GIF;
        }
        else if (type.equals("jpeg")) {
            mediaType = MediaType.IMAGE_JPEG;
        }
        else if (type.equals("png")) {
            mediaType = MediaType.IMAGE_PNG;
        }

        if ((! IS_CONTENTS_BLANK) && IS_SIZE_OK && IS_CORRECTION_OK && IS_TYPE_OK) {
            return ResponseEntity.
                ok().
                headers(qrCodeHeaders).
                contentType(mediaType).
                body(QR_CODE_CORRECTED);
        }
        else if ((! IS_CONTENTS_BLANK) && IS_SIZE_OK && (! IS_CORRECTION_OK) && IS_TYPE_OK) {
            return ResponseEntity.
                    ok().
                    headers(qrCodeHeaders).
                    contentType(mediaType).
                    body(QR_CODE);
        }
        else if (IS_CONTENTS_BLANK) {
            return ResponseEntity.
                badRequest().
                body(ERR_CONTENT_JSON);
        }
        else if (! IS_SIZE_OK) {
            return ResponseEntity.
                badRequest().
                body(ERR_SIZE_JSON);
        }
        else if (! IS_CORRECTION_OK) {
            return ResponseEntity.
                badRequest().
                body(ERR_CORRECTION_JSON);
        }
        else if ((! IS_SIZE_OK) && (! IS_TYPE_OK)) {
            return ResponseEntity.
                badRequest().
                body(ERR_SIZE_JSON);
        }
        else if (! IS_TYPE_OK) {
            return ResponseEntity.
                    badRequest().
                    body(ERR_TYPE_JSON);
        }

        return new ResponseEntity(HttpStatusCode.valueOf(400));
    }

    private boolean isCorrectionOk(final String correction) {
        return List.of("H", "L", "M", "Q").contains(correction);
    }

    private boolean isTypeOk(final String type) {
        return List.of("gif", "jpeg", "png").contains(type);
    }
}
