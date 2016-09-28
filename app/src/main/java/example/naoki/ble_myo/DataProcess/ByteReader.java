package example.naoki.ble_myo.DataProcess;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by naoki on 15/04/06.
 * 
 * This class help you to read the byte line from Myo.
 * But be carefully to byte array size. There is no limitation of get() method, 
 * so there is a possibilty of overloading the byte buffer.
 * 
 */

public class ByteReader {
    private byte[] byteData;
    private ByteBuffer bbf;

    public void setByteData(byte[] data){
        this.byteData = data;
        this.bbf = ByteBuffer.wrap(this.byteData); // occur two ocasion, onething create Buffer, and input data
        this.bbf.order(ByteOrder.LITTLE_ENDIAN); // if 0x12345678 -> 0x78 0x56 0x34 0x12
    }

    public byte[] getByteData() {
        return byteData;
    }

    public short getShort() {
        return this.bbf.getShort();
    }

    public byte getByte(){
        return this.bbf.get();
    }

    public int getInt(){
        return this.bbf.getInt();
    }

    public String getByteDataString() {
        final StringBuilder stringBuilder = new StringBuilder(byteData.length);
        for (byte byteChar : byteData) {
            stringBuilder.append(String.format("%02X ", byteChar));
        }
        return stringBuilder.toString();
    }

    public String getIntDataString() {
        final StringBuilder stringBuilder = new StringBuilder(byteData.length);
        for (byte byteChar : byteData) {
            stringBuilder.append(String.format("%5d,", byteChar));
        }
        return stringBuilder.toString();
    }
}
