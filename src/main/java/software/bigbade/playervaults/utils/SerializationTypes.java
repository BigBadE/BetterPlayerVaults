package software.bigbade.playervaults.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
public enum SerializationTypes {
    NUMBER("|", Integer::parseInt, Number.class),
    ITERATOR("&", string -> {
        List<String> list = new ArrayList<>();
        for(String line : string.split("\\|"))
            list.add(line.replace("\uD83E\uDED9", "|"));
        return list;
    }, Iterable.class);

    @Getter
    private final String symbol;
    @Getter
    private final Function<String, Object> cast;
    @Getter
    private final Class<?> clazz;
}
