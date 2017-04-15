package es.vilagarcia.forocoches;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import es.uvigo.det.ro.simpledns.DomainName;
import es.uvigo.det.ro.simpledns.Message;
import es.uvigo.det.ro.simpledns.RRType;

public class Communication {

	private DatagramSocket socketUDP = new DatagramSocket();
	private DatagramPacket outputData;
	private DatagramPacket inputData;
	private Message temporal;
	private Message finalInput;
	private Message output;
	private Message input;
	private InetAddress ip;
	static final int PORT = 53;

	/**
	 * Método que genera el datagrama necesario para iniciar por primera vez la
	 * comunicación
	 * 
	 * @param ip
	 * @param comando
	 * @throws IOException
	 */
	public Communication(InetAddress ip, String comando) throws IOException {
		super();
		this.ip = ip;

		output = Communication.generarMensaje(comando);
		outputData = new DatagramPacket(output.toByteArray(), output.toByteArray().length, ip, PORT);
	}
	
	public Communication(InetAddress ip, DomainName domain, String type) throws IOException{
		super();
		this.ip = ip;
		output = Communication.generarMesaje2(domain, type);
		outputData = new DatagramPacket(output.toByteArray(), output.toByteArray().length, ip, PORT);
	}

	public void change() throws Exception {
		
		byte[] inputBuf;
		socketUDP.send(outputData);
		socketUDP.setSoTimeout(5000);
		inputBuf = new byte[socketUDP.getReceiveBufferSize()];
		inputData = new DatagramPacket(inputBuf, inputBuf.length);
		socketUDP.receive(inputData);
		input = new Message(inputData.getData());
		return;
	}

	public void changeInformation() throws Exception, SocketTimeoutException {

		byte[] inputBuf;
		socketUDP.send(outputData);
		socketUDP.setSoTimeout(5000);
		inputBuf = new byte[socketUDP.getReceiveBufferSize()];
		inputData = new DatagramPacket(inputBuf, inputBuf.length);
		socketUDP.receive(inputData);
		input = new Message(inputData.getData());
		temporal = input;
		return;

	}

	public static Message generarMensaje(String entrada) {

		String[] split = entrada.split("\\s* \\s*");
		Message message = new Message(split[1], RRType.valueOf(split[0]), false);
		return message;
	}
	
	public static Message generarMesaje2(DomainName domain, String type){
		Message message = new Message(domain, RRType.valueOf(type), false);
		return message;
	}

	public DatagramSocket getSocketUDP() {
		return socketUDP;
	}

	public void setSocketUDP(DatagramSocket socketUDP) {
		this.socketUDP = socketUDP;
	}

	public DatagramPacket getOutputData() {
		return outputData;
	}

	public void setOutputData(DatagramPacket outputData) {
		this.outputData = outputData;
	}

	public DatagramPacket getInputData() {
		return inputData;
	}

	public void setInputData(DatagramPacket inputData) {
		this.inputData = inputData;
	}

	public Message getTemporal() {
		return temporal;
	}

	public void setTemporal(Message temporal) {
		this.temporal = temporal;
	}

	public Message getOutput() {
		return output;
	}

	public void setOutput(Message output) {
		this.output = output;
	}

	public Message getInput() {
		return input;
	}

	public void setInput(Message input) {
		this.input = input;
	}

	public InetAddress getIp() {
		return ip;
	}

	public void setIp(InetAddress ip) {
		this.ip = ip;
	}


	public static int getPort() {
		return PORT;
	}

	public Message getFinalInput() {
		return finalInput;
	}

	public void setFinalInput(Message finalInput) {
		this.finalInput = finalInput;
	}

}
