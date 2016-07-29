package tests;

import java.net.*;

public class TestInetAddr {

	public static void main(String[] args) throws UnknownHostException{
        InetAddress Address = InetAddress.getLocalHost();

        System.out.println(Address);

//        Address = InetAddress.getByName("212.174.226.105");
        
        Address = InetAddress.getByAddress(new byte[] {(byte)212, (byte)174, (byte)226, (byte)105});
       
        String dns = Address.getCanonicalHostName(); 
        
        System.out.println(Address + " -  " + dns);

        InetAddress SW[] = InetAddress.getAllByName("www.microsoft.com");
        for (int i=0; i<SW.length; i++)
            System.out.println(SW[i]);

    }

}
