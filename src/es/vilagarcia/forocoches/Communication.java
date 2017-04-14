package es.vilagarcia.forocoches;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Communication {

	private DatagramSocket socketUDP;
	private DatagramPacket output;
	private DatagramSocket input;
	private DatagramSocket temporal;
	
	
	public Communication(DatagramSocket socketUDP, DatagramPacket output, DatagramSocket input) {
		super();
		this.socketUDP = socketUDP;
		this.output = output;
		this.input = input;
	}
	
	
}
