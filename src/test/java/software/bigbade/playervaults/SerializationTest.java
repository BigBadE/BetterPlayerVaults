package software.bigbade.playervaults;

import org.bukkit.inventory.ItemStack;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
<<<<<<< HEAD
import software.bigbade.playervaults.utils.SerializationUtils;
=======
import software.bigbade.playervaults.utils.SerializationManager;
>>>>>>> 717768a3a7e522fd24fa46c46194f40c00773874

import java.util.HashMap;
import java.util.Map;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ItemStack.class })
public class SerializationTest {

<<<<<<< HEAD
    @Test(expected = ExceptionInInitializerError.class)
    public void serializationTest() throws ExceptionInInitializerError {
=======
    @Test
    public void serializationTest() {
>>>>>>> 717768a3a7e522fd24fa46c46194f40c00773874
        ItemStack mock = mock(ItemStack.class);
        Map<String, Object> answer = new HashMap<>();
        answer.put("test", "123");
        when(mock.serialize()).thenReturn(answer);
        Map<Integer, ItemStack> data = new HashMap<>();
        data.put(1, mock);
<<<<<<< HEAD
        Assert.assertEquals("[1;test=|123|;]", SerializationUtils.serialize(data));
        PowerMockito.mockStatic(ItemStack.class);
        when(ItemStack.deserialize(answer)).thenReturn(mock);
        Assert.assertEquals(data, SerializationUtils.deserialize("[1;test=|123|;]"));
=======
        Assert.assertEquals("[1;test=|123|;]", SerializationManager.serialize(data));
        PowerMockito.mockStatic(ItemStack.class);
        when(ItemStack.deserialize(answer)).thenReturn(mock);
        Assert.assertEquals(data, SerializationManager.deserialize("[1;test=|123|;]"));
>>>>>>> 717768a3a7e522fd24fa46c46194f40c00773874
    }
}
