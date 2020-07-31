package software.bigbade.playervaults.utils;

import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public final class CompressionUtil {
    private static final Deflater COMPRESSER = new Deflater(Deflater.BEST_SPEED);
    private static final Inflater DECOMPRESSER = new Inflater();
    private CompressionUtil() {}

    @SneakyThrows
    public static byte[] compress(String inputStr) {
        byte[] data = inputStr.getBytes(StandardCharsets.UTF_8);

        COMPRESSER.setInput(data);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];

        COMPRESSER.finish();
        while (!COMPRESSER.finished()) {
            int count = COMPRESSER.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        COMPRESSER.reset();

        outputStream.close();

        return outputStream.toByteArray();
    }

    public static String decompress(String inputStr) {
        return decompress(inputStr.getBytes(StandardCharsets.ISO_8859_1));
    }

    @SneakyThrows
    public static String decompress(byte[] data) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];

        DECOMPRESSER.setInput(data);
        while (!DECOMPRESSER.finished()) {
            int count = DECOMPRESSER.inflate(buffer);
            if(count == 0) {
                break;
            }
            outputStream.write(buffer, 0, count);
        }
        DECOMPRESSER.reset();

        outputStream.close();

        return new String(outputStream.toByteArray(), StandardCharsets.ISO_8859_1);
    }
}