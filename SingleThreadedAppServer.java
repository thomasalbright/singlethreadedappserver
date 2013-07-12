package singlethreadedappserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class uses the "Command Pattern" to implement different reactions to
 * server input.  All you must do to create an application which can run on this
 * server is to implement interface "App".
 * 
 * There is no 'shutdown' command for this server.  The only way to stop it is
 * to kill it.  Killing it will terminate the process immediately.  If it turns
 * out we need it, we will implement a signal handler that waits to kill the
 * process until no client connection is active.
 */
public class SingleThreadedAppServer {
  private App app;
  private ServerSocket serverSocket;
  private Socket clientSocket;
  private BufferedReader in;
  private PrintWriter out;

  private SingleThreadedAppServer(App _app) {
    app = _app;
  }

  public static void run(App _app) throws IOException {
    new SingleThreadedAppServer(_app).run();
  }

  public void run() throws IOException {
    startListeningOnPort();
    interactWithClientsForever();
  }

  private void startListeningOnPort() throws IOException {
    serverSocket = new ServerSocket(5555);
  }

  private void interactWithClientsForever()
          throws IOException {
    while (true) {
      nextClientConnection();
    }
  }

  private void nextClientConnection() throws IOException {
    openClientConnection();
    talkWithUser();
    closeClientConnection();
  }

  private void openClientConnection() throws IOException {
    clientSocket = serverSocket.accept();
    out = new PrintWriter(clientSocket.getOutputStream(), true);
    in = new BufferedReader(
            new InputStreamReader(
            clientSocket.getInputStream()));
  }

  private void talkWithUser() throws IOException {
    String inputLine;

    if ((inputLine = in.readLine()) != null)
      runCommandInApp(inputLine);
  }

  private void runCommandInApp(String inputLine) {
    out.println(app.processInput(inputLine));
  }

  private void closeClientConnection() throws IOException {
    out.close();
    in.close();
    clientSocket.close();
  }
}
