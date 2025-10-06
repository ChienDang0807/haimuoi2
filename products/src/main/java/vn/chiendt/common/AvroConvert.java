package vn.chiendt.common;

import org.apache.avro.Conversions;
import org.apache.avro.LogicalTypes;

import java.math.BigDecimal;
import java.nio.ByteBuffer;

public class AvroConvert {

    private AvroConvert() {}

    /**
     * Convert BigDecimal → ByteBuffer (for Avro Decimal logical type)
     *
     * @param value     BigDecimal value
     * @param precision Tổng số chữ số (ví dụ 10)
     * @param scale     Số chữ số thập phân (ví dụ 2)
     * @return ByteBuffer representation of the decimal
     */
    public static ByteBuffer toByteBuffer(BigDecimal value, int precision, int scale) {
        if (value == null) return null;
        return new Conversions.DecimalConversion()
                .toBytes(value, null, LogicalTypes.decimal(precision, scale));
    }

    /**
     * Convert ByteBuffer → BigDecimal (reverse conversion)
     *
     * @param buffer    ByteBuffer from Avro
     * @param precision Tổng số chữ số (giống như khi encode)
     * @param scale     Số chữ số thập phân (giống như khi encode)
     * @return BigDecimal value
     */
    public static BigDecimal fromByteBuffer(ByteBuffer buffer, int precision, int scale) {
        if (buffer == null) return null;
        // Duplicating to avoid modifying the original ByteBuffer position
        ByteBuffer duplicate = buffer.duplicate();
        return new Conversions.DecimalConversion()
                .fromBytes(duplicate, null, LogicalTypes.decimal(precision, scale));
    }
}
