package es.uvigo.alumno;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import es.uvigo.det.ro.simpledns.AResourceRecord;
import es.uvigo.det.ro.simpledns.CNAMEResourceRecord;
import es.uvigo.det.ro.simpledns.Message;
import es.uvigo.det.ro.simpledns.NSResourceRecord;
import es.uvigo.det.ro.simpledns.RRType;
import es.uvigo.det.ro.simpledns.ResourceRecord;
import es.uvigo.det.ro.simpledns.SOAResourceRecord;
import es.uvigo.det.ro.simpledns.Utils;

public class UdpConnection {

	private DatagramSocket socketUDP;
	private static int PORT = 53;
	private InetAddress IP;
	private final String PROTOCOL = "UDP";
	private final RRType TYPE;

	public UdpConnection(InetAddress ip, RRType type) throws SocketException {
		this.IP = ip;
		this.TYPE = type;
		socketUDP = new DatagramSocket();
	}

	public void send(Message askMessage, InetAddress ip) throws IOException {
		byte[] buf = askMessage.toByteArray();
		DatagramPacket outputUdpPacket = new DatagramPacket(buf, buf.length, ip, PORT);
		socketUDP.send(outputUdpPacket);
	}

	public Message receive() throws Exception {

		byte[] inputBuf = new byte[socketUDP.getReceiveBufferSize()];
		DatagramPacket inputUdpPacket = new DatagramPacket(inputBuf, inputBuf.length);
		socketUDP.receive(inputUdpPacket);

		return new Message(inputUdpPacket.getData());
	}

	public Message connection(Message askMessage, InetAddress ip, Cache savedRoute) {
		try {

			Message answerMessage;
			socketUDP.setSoTimeout(8000);

			Utils.printQ(PROTOCOL, ip, askMessage);

			send(askMessage, ip);
			answerMessage = receive();
			Message respuesta = null;
			Inet4Address IPPrivada = null;

			if (!answerMessage.getAnswers().isEmpty()) {
				for (ResourceRecord rr : answerMessage.getAnswers()) {
					if (rr instanceof CNAMEResourceRecord && askMessage.getQuestionType() != RRType.CNAME) {
						Utils.printA(ip, rr);
						Message mensaje = new Message(((CNAMEResourceRecord) rr).getCanonicalName().toString(),
								answerMessage.getQuestionType(), false);
						return this.connection(mensaje, IP, savedRoute);
					} else if (rr.getRRType() == askMessage.getQuestionType()) {

						for (ResourceRecord rrr : answerMessage.getAnswers()) {
							if (rrr.getRRType() == askMessage.getQuestionType()) {
								savedRoute.getAnswers().add(rrr);
								Utils.printA(ip, rrr);
							}
						}
						return answerMessage;
					}
				}
			}

			if (!answerMessage.getAdditonalRecords().isEmpty()) {
				int i = 0;
				for (ResourceRecord rr : answerMessage.getAdditonalRecords()) {
					if (rr.getRRType() == RRType.A) {
						IPPrivada = ((AResourceRecord) rr).getAddress();
						Utils.printA(ip, answerMessage.getNameServers().get(i));
						Utils.printA(ip, rr);
						//savedRoute.getAnswers().add(rr);
						break;
					}
					i++;
				}
			}

			if (IPPrivada == null) {
				// AQUI LAS INSTRUCCIONES PARA CONTROLAR CUANDO TENGO QUE
				// HACER NS
				for (ResourceRecord rr : answerMessage.getNameServers()) {
					if (rr.getRRType() == RRType.NS) {
						Message mensaje = new Message(((NSResourceRecord) rr).getNS().toString(), RRType.A, false);
						Utils.printA(ip, rr);
						respuesta = this.connection(mensaje, IP, savedRoute);
						if (respuesta == null) {
							continue;
						}/*miraaaaar error l imprimir*/
						for (ResourceRecord rrr : respuesta.getAnswers()) {
							if (rrr instanceof AResourceRecord) {
								Utils.printA(ip, rrr);
								IPPrivada = ((AResourceRecord) rrr).getAddress();
								break;
							}
						}

						if (IPPrivada != null) {
							break;
						}
					}
				}
			}

			
			if (IPPrivada == null) {
				for(ResourceRecord rr :answerMessage.getNameServers()){
					if(rr instanceof SOAResourceRecord){			
						Utils.printA(ip, rr);
						savedRoute.getAnswers().add(rr);
						savedRoute.setTtl(((SOAResourceRecord) rr).getMinimum());
					}
				}
				return null;
			}
			askMessage = new Message(askMessage.getQuestion().toString(), answerMessage.getQuestionType(), false);
			return this.connection(askMessage, IPPrivada, savedRoute);

		} catch (SocketTimeoutException e) {
			System.out.println("Timeout");
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public DatagramSocket getSocketUDP() {
		return socketUDP;
	}

	public void setSocketUDP(DatagramSocket socketUDP) {
		this.socketUDP = socketUDP;
	}

	public static int getPORT() {
		return PORT;
	}

	public static void setPORT(int pORT) {
		PORT = pORT;
	}

	public InetAddress getIP() {
		return IP;
	}

	public void setIP(InetAddress iP) {
		IP = iP;
	}

	public String getPROTOCOL() {
		return PROTOCOL;
	}

	public RRType getTYPE() {
		return TYPE;
	}
}