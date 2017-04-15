import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;

import es.uvigo.det.ro.simpledns.AAAAResourceRecord;
import es.uvigo.det.ro.simpledns.AResourceRecord;
import es.uvigo.det.ro.simpledns.NSResourceRecord;
import es.uvigo.det.ro.simpledns.ResourceRecord;
import es.vilagarcia.forocoches.Communication;
import pantalla.PantallaUtils;;

public class Cliente {

	public static void main(String[] args) {

		try {

			// VALIDACION INICIAL
			String protocol = new Cliente().validacionComando(args);
			Scanner stadin = new Scanner(System.in);

			// EJECUTAMOS HASTA QUE PULSEMOS CTR+D EN LA ENTRADA ESTANDAR
			while (true) {

				System.out.println("******************************************\n\nIntroduzca el dominio a traducir:\n");
				String entrada = stadin.nextLine();

				InetAddress ip = InetAddress.getByName(args[2]);

				Communication initCommunication = new Communication(ip, entrada);

				// IMPRIMOS EL MENSAJE Q:
				System.out.println("Q: " + protocol + "   " + ip.toString().replace("/", "") + "   "
						+ initCommunication.getOutput().getQuestionType() + "   "
						+ initCommunication.getOutput().getQuestion() + "\n");

				// CREAMOS EL MENSAJE DE RESPUESTA Y EMPEZAMOS A ANALIZAR LA
				// INFORMACIÓN
				// Message answerMessage = new
				// Message(inputUdpPacket.getData());
				// Message temporalMessage = new
				// Message(inputUdpPacket.getData());

				// MIRAMOS QUE ES LO QUE ESTAMOS BUSCANDO
				/*
				 * switch(outputMessage.getQuestionType().toString()){
				 * 
				 * case "A": new Cliente(); break;
				 * 
				 * case "NS": new Cliente(); break; }
				 */

				initCommunication.changeInformation();
				List<ResourceRecord> nameServers;
				nameServers = initCommunication.getInput().getNameServers();

				try {
					new Cliente().iteraciones(initCommunication, nameServers);

					// LA EXCEPCION SALTA CUANDO NO EXISTE RESPUESTA ALGUNA
				} catch (IndexOutOfBoundsException e) {
					//e.printStackTrace();
					//System.out.println("Exception");
					for (ResourceRecord rr : nameServers) {
						Communication nameServersCommunication = new Communication(initCommunication.getIp(),
								((NSResourceRecord)rr).getNS(), "A");
						System.out.println("BUSCAMOS" + ((NSResourceRecord)rr).getNS() + "*****");
						nameServersCommunication.changeInformation();
						new Cliente().iteraciones(nameServersCommunication, nameServers);
						if(!nameServersCommunication.getInput().getAnswers().isEmpty()){
							new PantallaUtils().pantallaRR(nameServersCommunication.getInput(), 0);
							break;
						}
					}
				}
				// LEEMOS EL CAMPO ADDITIONALRECORDS MIENTRAS

				new PantallaUtils().pantallaRR(initCommunication.getInput(), 0);
			
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

	public void iteraciones(Communication initCommunication, List<ResourceRecord> nameServers)
			throws Exception, ArrayIndexOutOfBoundsException {

		int i = 0;
		while (initCommunication.getInput().getAnswers().isEmpty()) {
			System.out.println("\nITERACION Nº: " + i);

			/*
			 * if (!nameServers.isEmpty() && i >
			 * initCommunication.getTemporal().getAdditonalRecords().size()) {
			 * System.out.println("ENTRAMOS A ITERATUAR CON NAME SERVERS"); for
			 * (ResourceRecord rr : nameServers) { Communication
			 * nameServersCommunication = new
			 * Communication(initCommunication.getIp(),
			 * rr.getDomain().toString(), "A"); new
			 * Cliente().iteraciones(nameServersCommunication, nameServers); } }
			 * else break;
			 */

			if (initCommunication.getInput().getAdditonalRecords().get(i) instanceof AResourceRecord) {
				initCommunication.getOutputData().setAddress(
						((AResourceRecord) (initCommunication.getInput().getAdditonalRecords().get(i))).getAddress());
				new PantallaUtils().pantallaRR(initCommunication.getInput(), 0);
				initCommunication.change();

				if (initCommunication.getInput().getAdditonalRecords().isEmpty()
						&& initCommunication.getInput().getAnswers().isEmpty()) {
					System.out.println("Está todo VACIO");
					nameServers = initCommunication.getInput().getNameServers();
					//initCommunication.setFinalInput(initCommunication.getInput());
					new PantallaUtils().pantallaRR(initCommunication.getInput(), 1);
					initCommunication.setInput(initCommunication.getTemporal());
					i++;
				} else {
					initCommunication.setTemporal(initCommunication.getInput());
					i = 0;
				}

				i++;
				continue;
			}

			else if (initCommunication.getInput().getAdditonalRecords().get(i) instanceof AAAAResourceRecord) {
				System.out.println("Servidor AAAA");
				i++;
				continue;
			}
		}

		return;

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

}
