package es.uvigo.alumno;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import es.uvigo.det.ro.simpledns.AResourceRecord;
import es.uvigo.det.ro.simpledns.CNAMEResourceRecord;
import es.uvigo.det.ro.simpledns.Message;
import es.uvigo.det.ro.simpledns.NSResourceRecord;
import es.uvigo.det.ro.simpledns.RRType;
import es.uvigo.det.ro.simpledns.ResourceRecord;
import es.uvigo.det.ro.simpledns.SOAResourceRecord;
import es.uvigo.det.ro.simpledns.Utils;

public class TcpConnection {

	private Socket socket;
	private InetAddress ip;
	protected static int PORT = 53;
	private final String PROTOCOL = "TCP";
	OutputStream out;
	InputStream in;

	public TcpConnection(InetAddress ip) throws IOException {
		super();
		this.ip = ip;
		this.socket = new Socket(ip, PORT);
		this.out = socket.getOutputStream();
		this.in = socket.getInputStream();
	}

	public void send(Message outputMessage) throws IOException {

		byte[] longitudBytes = Utils.int16toByteArray(outputMessage.toByteArray().length);
		byte[] salidaBytes = new byte[longitudBytes.length + outputMessage.toByteArray().length];

		/*
		 * Actualizamos los Streams, necesario para cuando se cambia la conexión
		 * del socket entre diferentes IPs
		 */

		this.out = socket.getOutputStream();
		this.in = socket.getInputStream();

		/*
		 * Creamos el array de bytes que especifica la longitud del mensaje y
		 * generamos un mensaje de salida con este array como CABECERA y la
		 * pregunta al servidor DNS como DATOS
		 */

		System.arraycopy(longitudBytes, 0, salidaBytes, 0, longitudBytes.length);
		System.arraycopy(outputMessage.toByteArray(), 0, salidaBytes, longitudBytes.length,
				outputMessage.toByteArray().length);

		out.write(salidaBytes);

		return;
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public byte[] receive() throws IOException {

		byte[] receivedBytes = new byte[socket.getReceiveBufferSize()];

		in.read(receivedBytes);

		int messageLenght = Utils.int16fromByteArray(receivedBytes);
		byte[] receivedMessage = new byte[messageLenght];

		System.arraycopy(receivedBytes, 2, receivedMessage, 0, messageLenght);

		return receivedMessage;
	}

	/**
	 * 
	 * @param askMessage
	 * @param ip2
	 * @param savedRoute
	 * @return
	 */
	public Message connection(Message askMessage, InetAddress ip2, Cache savedRoute) {

		
		Inet4Address IPPrivada = null;
		try {
			socket = new Socket(ip2, PORT);

			Utils.printQ(PROTOCOL, ip, askMessage);
			Message answerMessage;
			send(askMessage);
			answerMessage = new Message(receive());
			Message respuesta = null;
			

			if (!answerMessage.getAnswers().isEmpty()) {
				for (ResourceRecord rr : answerMessage.getAnswers()) {
					if (rr instanceof CNAMEResourceRecord && askMessage.getQuestionType() != RRType.CNAME) {
						Utils.printA(ip, rr);
						Message mensaje = new Message(((CNAMEResourceRecord) rr).getCanonicalName().toString(),
								answerMessage.getQuestionType(), false);
						return this.connection(mensaje, getIp(), savedRoute);
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
						// savedRoute.getAnswers().add(rr);
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
						respuesta = this.connection(mensaje, getIp(), savedRoute);
						if (respuesta == null) {
							continue;
						}
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
				for (ResourceRecord rr : answerMessage.getNameServers()) {
					if (rr instanceof SOAResourceRecord) {
						Utils.printA(ip, rr);
						savedRoute.getAnswers().add(rr);
						savedRoute.setTtl(((SOAResourceRecord) rr).getMinimum());
					}
				}
				return null;
			}
			askMessage = new Message(askMessage.getQuestion().toString(), answerMessage.getQuestionType(), false);
			return this.connection(askMessage, IPPrivada, savedRoute);

		} catch (ConnectException e) {
			System.out.println("\nConexión rechaza de:  " + ip2 + " inténtelo de nuevo o más tarde");
			return null;
		} catch (SocketTimeoutException e) {
			System.out.println("Timeout");
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public InetAddress getIp() {
		return ip;
	}

	public void setIp(InetAddress ip) {
		this.ip = ip;
	}

	public String getPROTOCOL() {
		return PROTOCOL;
	}

}
