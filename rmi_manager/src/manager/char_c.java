package manager;

public class char_c {
    byte buf; //little endian

    public char_c(byte b){
        this.setValue(b);
    }

    public char_c(char c){
        this.setValue(c);
    }

    // the size of buf
    public int getSize(){
        return 1;
    }

    // the int value represented by buf
    public char getValue(){
        return (char) buf;
    }

    // copy the value in b into buf
    public void setValue(byte b){
        buf = b;
    }
    // set buf according to v
    public void setValue(char c){
        buf = (byte) c;
    }
    // return buf
    public byte toByte(){
        return buf;
    }
}
