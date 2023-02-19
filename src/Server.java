import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;
import java.io.File;

public class Server
{
    //initialize socket and input stream
    private Socket		 server = null;
    private ServerSocket socket = null;
    PrintWriter toClient =null;
    FileInputStream fl=null;
    // constructor with port
    public Server(int port)
    {
        // starts server and waits for a connection
        try
        {
            socket = new ServerSocket(port);
            System.out.println("Server started");
            System.out.println("Waiting for a client ...");

            //Set Socket Timeout for 2 minutes
            socket.setSoTimeout(120000);

            while(true) {
                //Accept the client Connection
                server = socket.accept();
                System.out.println("Client accepted");

                toClient =new PrintWriter(server.getOutputStream(),true);
                BufferedReader fromClient =new BufferedReader(new InputStreamReader(server.getInputStream()));

                //Read Client Input  Message from the command line
                String line = fromClient.readLine();

                System.out.println("line received: " + line);

                //If the client input is List Command
                if(Objects.equals(line, "<REQ LIST>\\n")){
                    GetFileDetails();

                    //If the client input is Close Command
                }if (Objects.equals(line, "<CLOSE>\\n")) {
                    System.out.println("Closing the current Tcp Connection");
                    fromClient.close();
                    server.close();
                }
                //If the client input is GET Command
                if(line.contains("GET")){
                    String[] elements = line.split(" ");
                    String fileName = elements[2];
                    int startNumber = Integer.parseInt(elements[3]);
                    int endNumber= Integer.parseInt(elements[4]);
                    byte[] array=getFile(fileName,startNumber,endNumber);
                    toClient.println(Arrays.toString(array));
                }

            }

        }
        catch(IOException i)
        {
            System.out.println(i);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    public byte[] getFile(String fileName,int startNumber,int endNumber) throws IOException {
        String filePath= "C:\\Users\\DELL\\IdeaProjects\\".concat(fileName);
        byte[] array=null;
        if (endNumber==-1) {
            Path path = Paths.get(filePath);
            array = Files.readAllBytes(path);
        } else {
            System.out.println("Arrays.toString(array)");
            File file = new File(filePath);
            // Creating an object of FileInputStream to
            // read from a file
            fl = new FileInputStream(file);
            // Now creating byte array of same length as file
            array = new byte[(int) file.length()];
            if(endNumber>array.length){
                toClient.println("java.lang.IndexOutOfBoundsException: EndByteNumber can't larger than byte array length");
            }else{
                fl.read(array,startNumber,endNumber);
            }
        }
        return array;
    }

    public void GetFileDetails() throws NoSuchAlgorithmException, IOException {
        File folder = new File("C:\\Users\\DELL\\IdeaProjects\\test");
        long length = 0;
        File[] files = folder.listFiles();

        int count = files.length;

        // loop for traversing the directory
        for (int i = 0; i < count; i++) {
            if (files[i].isFile()) {
                length = files[i].length();
                String checksum= md5sum(files[i]);
                toClient.println( (i+1) +" "+ files[i].getName()+" "+ length +" "+ checksum);
            }
        }
    }

  // Generate md5sum value
   public static String md5sum(File file) throws IOException, NoSuchAlgorithmException {
       MessageDigest digest = MessageDigest.getInstance("MD5");

       FileInputStream fis = new FileInputStream(file);
       // Create byte array to read data in chunks
       byte[] byteArray = new byte[1024];
       int bytesCount = 0;

       // read the data from file and update that data in
       // the message digest
       while ((bytesCount = fis.read(byteArray)) != -1)
       {
           digest.update(byteArray, 0, bytesCount);
       };

       // close the input stream
       fis.close();

       // store the bytes returned by the digest() method
       byte[] bytes = digest.digest();

       // this array of bytes has bytes in decimal format
       // so we need to convert it into hexadecimal format

       // for this we create an object of StringBuilder
       // since it allows us to update the string i.e. its
       // mutable
       StringBuilder sb = new StringBuilder();

       // loop through the bytes array
       for (int i = 0; i < bytes.length; i++) {

           // the following line converts the decimal into
           // hexadecimal format and appends that to the
           // StringBuilder object
           sb.append(Integer
                   .toString((bytes[i] & 0xff) + 0x100, 16)
                   .substring(1));
       }

       // finally we return the complete hash
       return sb.toString();
   }

    public static void main(String args[])
    {
        Server server = new Server(5000);
    }
}
