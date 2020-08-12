package software.bigbade.playervaults.util;

import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import software.bigbade.playervaults.BetterPlayerVaults;
import software.bigbade.playervaults.utils.CompressionUtil;

public class CompressionUtilTest {
  private static final String TEST_STRING = "testString123/.";
  @Test
  public void compressionDecompressionTest() {
    BetterPlayerVaults.setPluginLogger(Logger.getLogger("bpv-test"));
    CompressionUtil.setCompressionLevel(1);
    Assert.assertEquals(
        TEST_STRING,
        CompressionUtil.decompress(CompressionUtil.compress(TEST_STRING)));
  }
}
