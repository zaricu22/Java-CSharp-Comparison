// VERDICT | T05 Operators & value types | BETTER: C#
// WHY: operator overloading, structs, decimal and unsigned types give `(price * qty + ship) * 0.9m`; Java chains methods and masks bytes.

package shop.t05_operators;

/**
 * Java has no unsigned types: byte is -128..127, so every byte from a device or
 * network packet needs a "& 0xFF" mask, and an unsigned 16-bit value has to live in an int.
 */
public final class PacketReader {

    private PacketReader() {}

    public static int checksum(byte[] payload) {
        int sum = 0;
        for (byte b : payload) {
            sum += b & 0xFF;
        }
        return sum;
    }

    public static int readUInt16(byte[] data, int offset) {
        return ((data[offset] & 0xFF) << 8) | (data[offset + 1] & 0xFF);
    }
}
