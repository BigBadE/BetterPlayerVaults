package software.bigbade.playervaults.serialization;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@RequiredArgsConstructor
public enum SerializationTypes {
    NUMBER("|", Integer::parseInt, Number.class);

    @Getter
    private final String symbol;
    @Getter
    private final Function<String, Object> cast;
    @Getter
    private final Class<?> clazz;
}
