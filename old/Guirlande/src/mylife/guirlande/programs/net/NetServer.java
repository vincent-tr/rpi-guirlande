package mylife.guirlande.programs.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Logger;

import mylife.guirlande.programs.Manager;
import mylife.guirlande.programs.Program;
import mylife.guirlande.programs.ProgramState;

public class NetServer {

	private static final int PROGRAM_PORT = 16661;
	private static final int NEXT_PORT = 16662;

	private final InetAddress LOOPBACK;
	
	private static final Logger log = Logger.getLogger(Manager.class.getName());

	private final static NetServer instance = new NetServer();

	public static NetServer getInstance() {
		return instance;
	}

	private NetServer() {
		try {
			LOOPBACK = InetAddress.getByName("127.0.0.1");
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	public void start() throws SocketException {
		workerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				worker();
			}
		});

		workerThread.setName("NetServer.Worker");
		workerThread.setDaemon(true);
		workerStopFlag = false;
		server = new DatagramSocket(NEXT_PORT, LOOPBACK);
		workerThread.start();
	}

	public void stop() {
		workerStopFlag = true;
		server.close();
		try {
			workerThread.join();
		} catch (InterruptedException e) {
			log.severe("Error stopping worker : " + e.toString());
		}
	}

	private DatagramSocket server;
	private boolean workerStopFlag;
	private Thread workerThread;

	private void worker() {

		byte[] buffer = new byte[1024];

		while (true) {

			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

			try {
				server.receive(packet);
			} catch (IOException e) {
				if (e instanceof SocketException && workerStopFlag)
					return; // c'est normal

				log.severe("Error in worker : " + e.toString());
				return;
			}

			// on a recu un message
			receiveNext();
		}
	}

	public void sendExecute(Program program) {

		// formattage du programme en une suite de groupe de 16 bits
		// représentant le statut

		List<ProgramState> states = program.getStates();

		byte[] buffer = new byte[states.size() * 16];
		for (int stateIndex = 0; stateIndex < states.size(); stateIndex++) {
			ProgramState state = states.get(stateIndex);
			for (int buffIndex = 0; buffIndex < 16; buffIndex++) {
				buffer[stateIndex * 16 + buffIndex] = (byte)state.getItem(buffIndex);
			}
		}
		
		try {
			DatagramSocket client = new DatagramSocket();
			try {
				client.send(new DatagramPacket(buffer, buffer.length, LOOPBACK, PROGRAM_PORT));
			} finally {
				client.close();
			}
			log.info("Program sent : " + program.getId());
		} catch (IOException e) {
			log.severe("Error sending program : " + e.toString());
		}
	}

	private void receiveNext() {
		log.info("Received next");
		Manager.getInstance().moveNext();
	}

}
