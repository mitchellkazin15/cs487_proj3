package manager;

import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;
import java.time.*;
import java.util.concurrent.*;

public class GetLocalTime {
    u_int_32_c time;
    char_c valid;
    Socket socket;

    public GetLocalTime(Socket socket){
        this.time = new u_int_32_c(0);
        this.valid = new char_c('0');
        this.socket = socket;
    }

    public void execute(){
        byte[] buf = setBuffer();

        try {
            DataOutputStream cmd = new DataOutputStream(socket.getOutputStream());
            System.out.println("Sending command...");
            cmd.write(buf, 0, buf.length);
            cmd.flush();

            System.out.println("Waiting for response...");
            String buffer;
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            buffer = reader.readLine();
            Scanner sc = new Scanner(buffer);
            int c_time = sc.nextInt();
            this.time.setValue(c_time);
            this.valid.setValue(sc.next().charAt(0));
        }
        catch (IOException e){
            System.out.println(socket.getInetAddress() + ":" + socket.getLocalPort());
            System.out.println(e.getMessage());
        }
    }

    private byte[] setBuffer(){
        int offset = 100;
        int length = time.getSize() + valid.getSize();
        String cmdString = "GetLocalTime";
        byte[] buf = new byte[offset+4+length];
        int i = 0;
        for(; i < offset; ++i){
            if(i < cmdString.length()){
                buf[i] = (byte) cmdString.charAt(i);
            }
            else{
                buf[i] = (byte) 0;
            }
        }
        u_int_32_c length_c = new u_int_32_c(length);
        byte[] byteLength = length_c.toByte();
        for(; i < offset + 4; ++i){
            buf[i] = byteLength[i - offset];
        }
        offset += 4;
        for(; i < buf.length; ++i){
            buf[i] = (byte) 0;
        }

        return buf;
    }

    public void printResponse(){
        if(this.valid.getValue() == '1'){
            Date date = new Date((long)this.time.getValue()*1000);
            SimpleDateFormat jdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss z");
            System.out.println("RPC Response: " + jdf.format(date));
        }
        else{
            System.out.println("Invalid Response");
        }
    }
}
