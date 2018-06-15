
package Client;


import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class Client {

    public static void main(String argv[]) throws Exception
    {
        // here I connect to my server using predefined port
        Socket clientSocket = new Socket("localhost", 43434);
        
// here I create 2 threads for input and output
        Thread out = new Thread(){
        @Override
              public void run(){
              BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
              DataOutputStream outToServer=null;
            try {
                outToServer = new DataOutputStream(clientSocket.getOutputStream());
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                while(true){
                
                       String send = inFromUser.readLine();
                 outToServer.writeBytes(send + '\n');
              if (send.equalsIgnoreCase("server exit")){
              Thread.sleep(1000);
              clientSocket.close();
              break;
              }}
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
             
              }
        
        }; 
        
        Thread in = new Thread(){
        @Override
              public void run(){
              BufferedReader inFromServer=null;
            try {
                inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                while(true){
              String rec= inFromServer.readLine();
              System.out.println(rec);
              System.out.flush();
             if (rec.equalsIgnoreCase("LOGOUT")){
              break;
              }
            } }catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }

              }        
        
        }; 
        // here I run those threads to get input form server and send output
        in.start();
        out.start();
        Thread.sleep(300);
           }
         

    
}
