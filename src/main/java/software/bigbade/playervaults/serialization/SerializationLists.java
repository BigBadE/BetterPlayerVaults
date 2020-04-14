package software.bigbade.playervaults.serialization;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@RequiredArgsConstructor
public enum SerializationLists {
    MAP("{", Map.class, (builder, value) -> {
        builder.append("{");
        for (Map.Entry<String, Object> entry : ((Map<String, Object>) value).entrySet()) {
            SerializationUtils.serialize(builder, entry.getKey(), entry.getValue());
        }
        builder.append("}");
    }, value -> SerializationUtils.deserializeMap(value.substring(1))),
    ITEM_META("{@", ItemMeta.class, (builder, value) -> {
        builder.append("{@");
        for (Map.Entry<String, Object> entry : ((ItemMeta) value).serialize().entrySet()) {
            SerializationUtils.serialize(builder, entry.getKey(), entry.getValue());
        }
        builder.append("}");
    }, value -> SerializationUtils.getMeta(SerializationUtils.deserializeMap(value.substring(2)))),
    COLLECTION("{&", Collection.class, (builder, value) -> {
        builder.append("{&");
        Iterator<?> iterator = ((Collection<?>) value).iterator();
        while (true) {
            builder.append(iterator.next().toString().replace("|", "\uD83E\uDED9"));
            if (iterator.hasNext())
                builder.append("|");
            else
                break;
        }
        builder.append("}");
    }, value -> {
        List<String> list = new ArrayList<>();
        for (String line : value.substring(2).split("\\|"))
            list.add(line.replace("\uD83E\uDED9", "|"));
        return list;
    }),
    BASIC_VALUE("|", Object.class, (builder, value) -> {
        builder.append("|");
        for (SerializationTypes types : SerializationTypes.values()) {
            if (types.getClazz().isAssignableFrom(value.getClass())) {
                builder.append(types.getSymbol());
            }
        }
        builder.append(value.toString().replace("|", "\uD83E\uDED9")).append("|");
    }, value -> {
        String deserializedValue = value.substring(1).split("\\|;", 2)[0];
        for (SerializationTypes types : SerializationTypes.values()) {
            if (deserializedValue.startsWith(types.getSymbol())) {
                return types.getCast().apply(deserializedValue.substring(1));
            }
        }
        deserializedValue = deserializedValue.replace("\uD83E\uDED9", "|");
        return deserializedValue;
    });

    @Getter
    private final String symbol;
    @Getter
    private final Class<?> clazz;
    private final BiConsumer<StringBuilder, Object> serializeConsumer;
    private final Function<String, Object> deserializeFunction;

    public void serialize(StringBuilder builder, Object value) {
        serializeConsumer.accept(builder, value);
    }

    public Object deserialize(String value) {
        return deserializeFunction.apply(value);
    }
}
