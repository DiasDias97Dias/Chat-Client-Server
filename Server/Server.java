/*
DUISEMBAYEV DIAS 30.03.2018
 */
package Server;





/**
 *
 * @author Диас
 */

public class Server {
    public static void main(String argv[]) throws Exception {
        ServerClients sClients = new ServerClients(43434);
        sClients.start();
    


}
}
