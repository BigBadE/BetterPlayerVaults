package software.bigbade.playervaults.utils;

import software.bigbade.playervaults.BetterPlayerVaults;

import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public final class CompressionUtil {
    private static final Deflater COMPRESSER = new Deflater(Deflater.BEST_SPEED, true);
    private static final Inflater DECOMPRESSER = new Inflater(true);
    private CompressionUtil() {}

    public static String compress(String inputStr) {
        byte[] input = inputStr.getBytes(StandardCharsets.UTF_8);

        // Compress the bytes
        byte[] output = new byte[1024];
        COMPRESSER.setInput(input);
        COMPRESSER.finish();
        StringBuilder outputStr = new StringBuilder();
        while (!COMPRESSER.finished()) {
            int compressedDataLength = COMPRESSER.deflate(output);
            outputStr.append(new String(output, 0, compressedDataLength, StandardCharsets.UTF_8));
        }
        COMPRESSER.reset();

        return outputStr.toString();
    }

    public static String decompress(String inputStr) {
        DECOMPRESSER.setInput(inputStr.getBytes(StandardCharsets.UTF_8), 0, inputStr.length());
        StringBuilder builder = new StringBuilder();
        byte[] result = new byte[1024];
        while(!DECOMPRESSER.finished()) {
            try {
                int resultLength = DECOMPRESSER.inflate(result);
                builder.append(new String(result, 0, resultLength, StandardCharsets.UTF_8));
            } catch (DataFormatException e) {
                BetterPlayerVaults.getPluginLogger().log(Level.SEVERE, e, () -> "Error compressing data " + inputStr);
            }
        }
        DECOMPRESSER.reset();

        // Decode the bytes into a String
        return builder.toString();
    }
}