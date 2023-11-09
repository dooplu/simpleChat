package edu.seg2105.edu.server.ui;

import java.io.*;
import java.util.Scanner;
import edu.seg2105.client.common.ChatIF;
import edu.seg2105.edu.server.backend.EchoServer;

public class ServerConsole implements ChatIF{

    EchoServer echoServer;

    Scanner adminConsole;

    public ServerConsole(int port)
    {
        echoServer = new EchoServer(port, this);
        try
        {
        echoServer.listen(); //Start listening for connections
        } 
        catch (Exception ex) 
        {
        System.out.println("ERROR - Could not listen for clients!");
        }

        adminConsole = new Scanner(System.in);
    }

    @Override
    public void display(String message) {
        System.out.println("> " + message);
    }

    public void accept() 
  {
    try
    {

      String message;

      while (true) 
      {
        message = adminConsole.nextLine();
        echoServer.handleMessageFromServerUI(message);
      }
    } 
    catch (Exception ex) 
    {
      System.out.println
        ("Unexpected error while reading from console!");
    }
  }

    public static void main(String args[])
    {
        int port = 0; //Port to listen on

        try
        {
        port = Integer.parseInt(args[0]); //Get port from command line
        }
        catch(Throwable t)
        {
        port = EchoServer.DEFAULT_PORT; //Set port to 5555
        }
        
        ServerConsole serverConsole = new ServerConsole(port);
        serverConsole.accept();
    }
    
}
