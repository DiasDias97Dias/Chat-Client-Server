/*
DUISEMBAYEV DIAS 30.03.2018
 */
package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Диас
 */
public class ServerClients extends Thread {

    private final int port;
    private ArrayList<ServerClient> csgo = new ArrayList<>();
    private ArrayList<ServerClient> dota = new ArrayList<>();
    private ArrayList<ServerClient> pubg = new ArrayList<>();
    private ArrayList<ServerClient> all = new ArrayList<>();
   public  ServerClients(int i) {
        this.port=i;
    }
  

   @Override
   public void run(){
       
        try {
            ServerSocket  sSocket =  new ServerSocket(port);
            
            while(true){
                //here we accept new socket and create sClient, which is a list of clients
                Socket cSocket =   sSocket.accept();
                ServerClient sClient = new ServerClient(this, cSocket);
                sClient.start();
                
            }   } catch (IOException ex) {
            Logger.getLogger(ServerClients.class.getName()).log(Level.SEVERE, null, ex);
        }
      
   
   }
   // next block of codes are the functions which help us operate on lists
   public List<ServerClient> getListCsgo(){
   return csgo;
   }
   public List<ServerClient> getListDota(){
   return dota;
   }
   public List<ServerClient> getListPubg(){
   return pubg;
   }   
   public List<ServerClient> getListAll(){
   return all;
   }
   public void addAll(ServerClient sClient){
   all.add(sClient);
   }
   public void addCsgo(ServerClient sClient){
   csgo.add(sClient);
   }
   public void addDota(ServerClient sClient){
   dota.add(sClient);
   }
   public void addPubg(ServerClient sClient){
   pubg.add(sClient);
   }   
   public void leaveCsgo(ServerClient sClient){
   csgo.remove(sClient);
   }
   public void leaveDota(ServerClient sClient){
   dota.remove(sClient);
   }
   public void leavePubg(ServerClient sClient){
   pubg.remove(sClient);
   }
   public void leaveAll(ServerClient sClient){
   all.remove(sClient);
   }   
}
