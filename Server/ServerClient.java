/*
DUISEMBAYEV DIAS 30.03.2018
 */
package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Диас
 */
public class ServerClient extends Thread {

    private final Socket cSocket;
    private final ServerClients sClients;
    private String username=null;
    private boolean logged=false;
    private boolean joined = false;
    private boolean dm = false;
    private String group = "none";
    private OutputStream oStream;
   public ServerClient(ServerClients sClients, Socket cSocket) {
   this.sClients = sClients;
   this.cSocket=cSocket;
   }

    
    //this threads handles multiple clients
    @Override
    public void run(){
        try {     
            createClientThread();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(ServerClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

private  void createClientThread () throws IOException, InterruptedException{
        this.oStream = cSocket.getOutputStream();
        InputStream iStream = cSocket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
        String cMsg;
        
        while ((cMsg = reader.readLine()) != null){
        String[] tokens=cMsg.split(" ");
            if (tokens != null && tokens.length>1 && tokens[1] != null ){
            String cmd1=tokens[0];
            String cmd2=tokens[1];
            dm=this.isExists(cmd1, this.group);
                if (cmd1.equals("server") && cmd2.equals("exit")){
                   // client should be removed from every group if he exists
                   sClients.leaveAll(this);
                   sClients.leaveCsgo(this);
                   sClients.leaveDota(this);
                   sClients.leavePubg(this);
                   oStream.write("\n".getBytes());
                   oStream.write("LOGOUT".getBytes());
                   oStream.write("\n".getBytes());
                   break;
                   //here i do not need to comment, name of the methods is the key
                } else if(cmd1.equals("server") && cmd2.equals("hello") && tokens.length==3 && logged==false ) {
                    loginUser(tokens, oStream);}
                    else if (cmd1.equals("server") && cmd2.equals("grouplist")){
                        this.displayGroups();
                    } else if (cmd1.equals("server") && cmd2.equals("join") && joined==false){
                        this.joinGroup(tokens[2]);
                      } else if (cmd1.equalsIgnoreCase("toall") && logged==true && joined ==true){
                          sendAll(cMsg);
                        } else if (cmd1.equals("server") && cmd2.equals("leave")){
                             sClients.leaveCsgo(this);
                             sClients.leaveDota(this);
                             sClients.leavePubg(this);
                             joined=false;
                          } else if (cmd1.equals("server") && cmd2.equals("members") && joined==true){
                              showMembers();
                            } else if (dm){
                              sendDm(cMsg, cmd1);
                            }
            }
        
        
        
        }
        
        cSocket.close();

}


private  void loginUser(String[] tokens, OutputStream oStream) throws IOException {
   if (this.isExists(tokens[2], "all")==false){ //here we check if provided username exists
      this.username = tokens[2]; 
      this.logged = true; //now we now that current thread is logged
      oStream.write("hi ".getBytes());
      oStream.write(this.username.getBytes());
      oStream.write("\n".getBytes());
      sClients.addAll(this);
   } else {
       // if it is exists, we warn the user
       oStream.write("This username is taken".getBytes());
       oStream.write("\n".getBytes());
     }
}

public String getUsername(){
return this.username;
}



private void displayGroups () throws IOException{
List <ServerClient> csgo = sClients.getListCsgo();
List <ServerClient> dota = sClients.getListDota();
List <ServerClient> pubg = sClients.getListPubg();  
String msg="csgo:";
int i=0;
// here we parse needed list, to display online users
    for (ServerClient sClient: csgo){ 
        if (i==0){
        msg=msg+" "+sClient.getUsername();
        } else {
        msg=msg+", "+sClient.getUsername();
        } 
        i++;
    }
i=0;        
msg=msg+" | "+"dota:";
    for (ServerClient sClient: dota){
        if (i==0){
        msg=msg+" "+sClient.getUsername();
        } else {
        msg=msg+", "+sClient.getUsername();
        } 
        i++;
    }
i=0;        
msg=msg+" | "+"pubg:";
    for (ServerClient sClient: pubg){
        if (i==0){
        msg=msg+" "+sClient.getUsername();
        } else {
        msg=msg+", "+sClient.getUsername();
        } 
        i++;
    }
msg=msg+".";
oStream.write(msg.getBytes());
oStream.write("\n".getBytes());
}

private void joinGroup(String group) throws IOException{
joined=true; // now we now that user is joined to the group, so he cannot join other groups
//we add Thread to the list if user provides existing group
if (group.equalsIgnoreCase("csgo")){
   sClients.addCsgo(this);
   this.group="csgo";
} else if (group.equalsIgnoreCase("dota")){
   sClients.addDota(this);
   this.group="dota";
  } else if (group.equalsIgnoreCase("pubg")){
     sClients.addPubg(this);
    this.group="pubg";
    } 
}

private void sendAll(String msg) throws IOException {
List <ServerClient> csgo = sClients.getListCsgo();
List <ServerClient> dota = sClients.getListDota();
List <ServerClient> pubg = sClients.getListPubg(); 
msg=msg.replaceFirst("toall", "");
msg=this.username+"(ToAll)"+": "+msg;
//here we parse needed list and send the message to every Thread in the list
if (group.equalsIgnoreCase("csgo")){
    for (ServerClient sClient: csgo){
    sClient.oStream.write(msg.getBytes());
    sClient.oStream.write("\n".getBytes());
    }
} else if (group.equalsIgnoreCase("dota")){
      for (ServerClient sClient: dota){
      sClient.oStream.write(msg.getBytes());
      sClient.oStream.write("\n".getBytes());
      } 
  } else if (group.equalsIgnoreCase("pubg")){
       for (ServerClient sClient: pubg){
      sClient.oStream.write(msg.getBytes());
      sClient.oStream.write("\n".getBytes());
      }             
  } else if (group.equalsIgnoreCase("none")){
         System.out.println("Error");
     }  
}

private void sendDm(String msg, String receiver) throws IOException{
List <ServerClient> csgo = sClients.getListCsgo();
List <ServerClient> dota = sClients.getListDota();
List <ServerClient> pubg = sClients.getListPubg(); 
msg=msg.replaceFirst(receiver, "");
msg=this.username+"(private)"+": "+msg;
//here we parse needed list to find specific username, once we find it, we can write msg only to him
if (this.group.equalsIgnoreCase("csgo")){
      for (ServerClient sClient: csgo){
         if (receiver.equals(sClient.username)){
            sClient.oStream.write(msg.getBytes());
            sClient.oStream.write("\n".getBytes());
         } 
      }
} else if (this.group.equalsIgnoreCase("dota")){
      for (ServerClient sClient: dota){
         if (receiver.equals(sClient.username)){
            sClient.oStream.write(msg.getBytes());
            sClient.oStream.write("\n".getBytes());
         } 
      }
} else if (this.group.equalsIgnoreCase("pubg")){
      for (ServerClient sClient: pubg){
         if (receiver.equals(sClient.username)){
            sClient.oStream.write(msg.getBytes());
            sClient.oStream.write("\n".getBytes());
         } 
      }
}
}

private void showMembers() throws IOException {
List <ServerClient> csgo = sClients.getListCsgo();
List <ServerClient> dota = sClients.getListDota();
List <ServerClient> pubg = sClients.getListPubg(); 
String msg="Members:";
int i=0;
//here we parse needed list to show its content
if (this.group.equalsIgnoreCase("csgo")){
        for (ServerClient sClient: csgo){
        if (i==0){
        msg=msg+" "+sClient.getUsername();
        } else {
        msg=msg+", "+sClient.getUsername();
        } 
        i++;
    }
    oStream.write(msg.getBytes());
    oStream.write("\n".getBytes());

} else if (this.group.equalsIgnoreCase("dota")){
   i=0;
    for (ServerClient sClient: dota){
        if (i==0){
        msg=msg+" "+sClient.getUsername();
        } else {
        msg=msg+", "+sClient.getUsername();
        } 
        i++;
    }
   oStream.write(msg.getBytes());
   oStream.write("\n".getBytes());
  } else if (this.group.equalsIgnoreCase("pubg")){
   i=0;
    for (ServerClient sClient: pubg){
        if (i==0){
        msg=msg+" "+sClient.getUsername();
        } else {
        msg=msg+", "+sClient.getUsername();
        } 
        i++;
    }
   oStream.write(msg.getBytes());
   oStream.write("\n".getBytes());
  }

}

private boolean isExists(String name, String group){
List <ServerClient> all = sClients.getListAll();   
List <ServerClient> csgo = sClients.getListCsgo();
List <ServerClient> dota = sClients.getListDota();
List <ServerClient> pubg = sClients.getListPubg(); 
// here we parse every list to know whether given username exists
if (group.equalsIgnoreCase("all")){
        for (ServerClient sClient: all){
          if (name.equals(sClient.username)){
          return true;
          }
        }
} else if (group.equalsIgnoreCase("csgo")){
        for (ServerClient sClient: csgo){
          if (name.equals(sClient.username)){
          return true;
          }
        }
} else if (group.equalsIgnoreCase("dota")){
        for (ServerClient sClient: dota){
          if (name.equals(sClient.username)){
          return true;
          }
        }
} else if (group.equalsIgnoreCase("pubg")){
        for (ServerClient sClient: pubg){
          if (name.equals(sClient.username)){
          return true;
          }
        }
} 
return false;
}



}
