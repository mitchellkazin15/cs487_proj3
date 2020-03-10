package manager;

import java.nio.*;

public class u_int_32_c {
    byte[] buf = new byte[4]; //little endian

    public u_int_32_c(byte[] b){
        buf = b;
    }

    public u_int_32_c(int v){
        setValue(v);
    }

    // the size of buf
    public int getSize(){
        return buf.length;
    }

    // the int value represented by buf
    public int getValue(){
        return   buf[3] & 0xFF |
                (buf[2] & 0xFF) << 8 |
                (buf[1] & 0xFF) << 16 |
                (buf[0] & 0xFF) << 24;
    }

    // copy the value in b into buf
    public void setValue(byte[] b){
        buf = b;
    }

    // set buf according to v
    public void setValue(int v){
        buf = ByteBuffer.allocate(4).putInt(v).array();
    }
    // return buf
    public byte[] toByte(){
        return buf;
    }
}
