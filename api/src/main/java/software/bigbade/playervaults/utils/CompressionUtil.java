package software.bigbade.playervaults.utils;

import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public final class CompressionUtil {
    private static final Inflater DECOMPRESSER = new Inflater();
    private static Deflater compresser;
    private CompressionUtil() {}

    public static void setCompressionLevel(int level) {
        compresser = new Deflater(level);
    }

    @SneakyThrows
    public static byte[] compress(String inputStr) {
        byte[] data = inputStr.getBytes(StandardCharsets.UTF_8);

        compresser.setInput(data);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];

        compresser.finish();
        while (!compresser.finished()) {
            int count = compresser.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        compresser.reset();

        outputStream.close();

        return Base64.getEncoder().encode(outputStream.toByteArray());
    }

    public static String decompress(String inputStr) {
        return decompress(inputStr.getBytes(StandardCharsets.ISO_8859_1));
    }

    @SneakyThrows
    public static String decompress(byte[] data) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];

        DECOMPRESSER.setInput(Base64.getDecoder().decode(data));
        while (!DECOMPRESSER.finished()) {
            int count = DECOMPRESSER.inflate(buffer);
            if(count == 0) {
                break;
            }
            outputStream.write(buffer, 0, count);
        }
        DECOMPRESSER.reset();

        outputStream.close();

        return new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
    }
}