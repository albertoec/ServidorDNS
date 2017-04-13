import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

import es.uvigo.det.ro.simpledns.AAAAResourceRecord;
import es.uvigo.det.ro.simpledns.AResourceRecord;
import es.uvigo.det.ro.simpledns.Message;
import es.uvigo.det.ro.simpledns.NSResourceRecord;
import es.uvigo.det.ro.simpledns.RRType;
import es.uvigo.det.ro.simpledns.ResourceRecord;;

public class Cliente {

	public static void main(String[] args) {

		try {

			// VALIDACION INICIAL
			String protocol = new Cliente().validacionComando(args);
			Scanner stadin = new Scanner(System.in);

			// EJECUTAMOS HASTA QUE PULSEMOS CTR+D EN LA ENTRADA ESTANDAR
			while (true) {

				System.out.println("Introduzca el dominio a traducir:\n");
				String entrada = stadin.nextLine();

				InetAddress ip = InetAddress.getByName(args[2]);
				DatagramSocket socketUDP = new DatagramSocket();

				Message outputMessage = new Cliente().generarMensaje(entrada);

				// IMPRIMOS EL MENSAJE Q:
				System.out.println("Q: " + protocol + "   " + ip.toString().replace("/", "") + "   "
						+ outputMessage.getQuestionType() + "   " + outputMessage.getQuestion() + "\n");

				byte[] buf = outputMessage.toByteArray();

				DatagramPacket outputUdpPacket = new DatagramPacket(buf, buf.length, ip, 53);
				outputUdpPacket.setAddress(ip);
				socketUDP.send(outputUdpPacket);

				byte[] inputBuf = new byte[socketUDP.getReceiveBufferSize()];
				DatagramPacket inputUdpPacket = new DatagramPacket(inputBuf, inputBuf.length);
				socketUDP.receive(inputUdpPacket);

				// CREAMOS EL MENSAJE DE RESPUESTA Y EMPEZAMOS A ANALIZAR LA
				// INFORMACIÓN
				Message answerMessage = new Message(inputUdpPacket.getData());
				Message temporalMessage = new Message(inputUdpPacket.getData());

				// MIRAMOS QUE ES LO QUE ESTAMOS BUSCANDO
				/*
				 * switch(outputMessage.getQuestionType().toString()){
				 * 
				 * case "A": new Cliente(); break;
				 * 
				 * case "NS": new Cliente(); break; }
				 */

				// LEEMOS EL CAMPO ADDITIONALRECORDS MIENTRAS
				int i = 0;
				while (answerMessage.getAnswers().isEmpty()) {
					System.out.println(i);
					// System.out.println(answerMessage.getAdditonalRecords());
					if (answerMessage.getAdditonalRecords().get(i) instanceof AResourceRecord) {
						System.out.println("SII-ARESOURCERECORD");
						outputUdpPacket.setAddress(
								((AResourceRecord) answerMessage.getAdditonalRecords().get(i)).getAddress());
						new Cliente().pantallaRR(answerMessage);
						System.out.println("Esta es la ip con la que contactamos: " + outputUdpPacket.getAddress());
						socketUDP.send(outputUdpPacket);
						socketUDP.receive(inputUdpPacket);
						answerMessage = new Message(inputUdpPacket.getData());

						if (answerMessage.getAdditonalRecords().isEmpty() && answerMessage.getAnswers().isEmpty()) {
							System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
							answerMessage = temporalMessage;
							i++;
						} else {
							i = 0;
							temporalMessage = answerMessage;
						}
					} else if (answerMessage.getAdditonalRecords().get(i) instanceof AAAAResourceRecord) {
						System.out.println("PASAMOS ES UN AAAA");
						/*new Cliente().pantallaRR(answerMessage);
						outputUdpPacket.setAddress(
								(Inet6Address) (((AAAAResourceRecord) answerMessage.getAdditonalRecords().get(i))
										.getAddress()));
						System.out.println("Esta es la ip con la que contactamos: " + outputUdpPacket.getAddress());
						DatagramSocket socketUDPipv6 = new DatagramSocket();
						socketUDPipv6.send(outputUdpPacket);
						socketUDPipv6.receive(inputUdpPacket);
						answerMessage = new Message(inputUdpPacket.getData());*/
						i++;
						continue;
					}

				}
				new Cliente().pantallaRR(answerMessage);

				// System.out.println("\n\n" + answerMessage.getAnswers());

				// YA HEMOS ENCONTRADA UNA QUERY QUE NO TIENE VACIA EL APARTADO
				// ANSWER

				/*
				 * for (ResourceRecord answers : answerMessage.getAnswers()) {
				 * if (answers instanceof AResourceRecord) { AResourceRecord aRR
				 * = (AResourceRecord) answers; System.out.println( "A: " +
				 * answers.getRRType() + "   " + answers.getTTL() + "   " +
				 * aRR.getAddress() + "\n"); } if (answers instanceof
				 * AAAAResourceRecord) { AAAAResourceRecord aRR =
				 * (AAAAResourceRecord) answers; System.out.println( "A:" +
				 * answers.getRRType() + "   " + answers.getTTL() + "   " +
				 * aRR.getAddress() + "\n"); } }
				 */

			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void pantallaRR(Message answerMessage) {
		System.out.println("\nAnswers:\n\n");
		if (!answerMessage.getAnswers().isEmpty())
			for (ResourceRecord rr : answerMessage.getAnswers()) {
				if (rr instanceof AResourceRecord)
					System.out.println(rr.getDomain() + "  " + rr.getRRType() + "  " + rr.getTTL() + "  "
							+ ((AResourceRecord) rr).getAddress());
				if (rr instanceof AAAAResourceRecord)
					System.out.println(rr.getDomain() + "  " + rr.getRRType() + "  " + rr.getTTL() + "  "
							+ ((AAAAResourceRecord) rr).getAddress());
				if (rr instanceof NSResourceRecord) {
					System.out.println(rr.getDomain() + "  " + rr.getRRType() + "  " + rr.getTTL() + "  "
							+ ((NSResourceRecord) rr).getNS());
				}
			}
		System.out.println("\nAdditional Section:\n\n");
		for (ResourceRecord rr : answerMessage.getAdditonalRecords()) {
			if (rr instanceof AResourceRecord)
				System.out.println(rr.getDomain() + "  " + rr.getRRType() + "  " + rr.getTTL() + "  "
						+ ((AResourceRecord) rr).getAddress());
			if (rr instanceof AAAAResourceRecord)
				System.out.println(rr.getDomain() + "  " + rr.getRRType() + "  " + rr.getTTL() + "  "
						+ ((AAAAResourceRecord) rr).getAddress());
			if (rr instanceof NSResourceRecord) {
				System.out.println(rr.getDomain() + "  " + rr.getRRType() + "  " + rr.getTTL() + "  "
						+ ((NSResourceRecord) rr).getNS());
			}
		}
	}

	public String validacionComando(String[] args) {

		if (args.length != 3) {
			System.out.println("\n\n\tUso: dnsclient -(t|u) DsServerIPaddr\n\n");
			System.exit(0);
		}
		String commandName = args[0];
		String protocol = args[1];

		// Validamos el comando
		if (!(commandName.equals("dnsclient") && (protocol.equals("-t") || protocol.equals("-u")))) {
			System.out.println("\n\n\tUso: dnsclient -(t|u) DsServerIPaddr\n\n");
			System.exit(0);
		}

		if (protocol.equals("-u"))
			protocol = "UDP";
		else
			protocol = "TCP";
		return protocol;
	}

	public void validacionParametros() {

	}

	public Message generarMensaje(String entrada) {

		String[] split = entrada.split("\\s* \\s*");
		Message message = new Message(split[1], RRType.valueOf(split[0]), false);
		return message;
	}

	/**
	 * Método para validar la IP si es necesario
	 * 
	 * @param ip
	 */
	public void isAnIP(InetAddress ip) {

	}
}
