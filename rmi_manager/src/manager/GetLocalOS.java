package manager;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class GetLocalOS {
    char_c[] OS;
    char_c valid;
    Socket socket;

    public GetLocalOS(Socket socket){
        this.OS = new char_c[16];
        for(int i = 0; i < 16; ++i){
            this.OS[i] = new char_c(' ');
        }
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
            String os = sc.next();
            for(int i = 0; i < os.length(); ++i){
                this.OS[i] = new char_c(os.charAt(i));
            }
            this.valid.setValue(sc.next().charAt(0));
        }
        catch (IOException e){
            System.out.println(socket.getInetAddress() + ":" + socket.getLocalPort());
            System.out.println(e.getMessage());
        }
    }

    private byte[] setBuffer(){
        int offset = 100;
        int length = OS[0].getSize()*16 + valid.getSize();
        String cmdString = "GetLocalOS";
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
            String response = "";
            for(int i = 0; i < OS.length; ++i){
                response += this.OS[i].getValue();
            }
            System.out.println("RPC Response: " + response);
        }
        else{
            System.out.println("Invalid Response");
        }
    }
}
