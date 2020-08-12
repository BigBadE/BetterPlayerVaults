package software.bigbade.playervaults.serialization;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Function;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NumberTypes {
  BYTE(Byte::parseByte, Byte.class, 'b'),
  SHORT(Short::parseShort, Short.class, 's'),
  INTEGER(Integer::parseInt, Integer.class, 'i'),
  LONG(Long::parseLong, Long.class, 'l'),
  FLOAT(Float::parseFloat, Float.class, 'f'),
  DOUBLE(Double::parseDouble, Double.class, 'd'),
  BIG_INTEGER(BigInteger::new, BigInteger.class, 'z'),
  BIG_DECIMAL(BigDecimal::new, BigDecimal.class, 'a');

  private final Function<String, Number> converter;
  private final Class<?> clazz;
  private final char character;
}
