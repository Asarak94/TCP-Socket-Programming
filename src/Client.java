import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

public class Client {
    // constructor to put ip address and port
    public Client(String address, int port) {

        try {
            // establish a connection
            Socket socket = new Socket();
            SocketAddress socketAddress = new InetSocketAddress(address, port);
            socket.connect(socketAddress);
            //Set Socket Timeout for 2 minutes
            socket.setSoTimeout(120000);
            PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader clientInput = new BufferedReader(new InputStreamReader(System.in));

            String line = "";
            ArrayList<String> ar =new ArrayList<>();

            // keep reading until "over" is input
            while (!line.equals("over")) {
                try {
                    line = clientInput.readLine();
                    toServer.println(line);
                    ar.add(line);
                    toServer.flush();

                }
                catch (IOException i) {
                    System.out.println(i);
                }
            }
            //If the client input is List Command
            if(ar.get(0).equals("<REQ LIST>\\n") ){

                System.out.println("<REP LIST X>");

                while(fromServer.ready()){
                    System.out.println("<" +fromServer.readLine()+">\\n");
                    //fromServer.reset();
                }
                System.out.println("<REP LIST END>");
                }

            //If the client input is Close Command
            if(ar.get(0).equals( "<CLOSE>\\n")){
                System.out.println("Closing current TCP Connection");
            }
            //If the client input is GET Command
            if(ar.get(0).contains( "GET")){
                System.out.println("<REP GET BEGIN>\\n");
                System.out.println(fromServer.readLine());
                System.out.println("<REP GET END FileMD5>\\n");
            }
            System.out.println(fromServer.readLine());
        }
        catch (SocketTimeoutException e){
            System.out.println("<CLOSE TIMEOUT>\\n");
        } catch (IOException u) {
            System.out.println(u);
        }
    }

    public static void main(String args[]) {
        Client client = new Client("127.0.0.1", 5000);
    }
}
