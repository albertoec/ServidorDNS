import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Scanner;

import es.uvigo.alumno.Cache;
import es.uvigo.alumno.TcpConnection;
import es.uvigo.alumno.UdpConnection;
import es.uvigo.det.ro.simpledns.Message;
import es.uvigo.det.ro.simpledns.RRType;
import es.uvigo.det.ro.simpledns.ResourceRecord;
import es.uvigo.det.ro.simpledns.Utils;;

public class dnsclient {

	public static HashMap<String, Cache> cache = new HashMap<>();
	private String domain;
	private RRType rrType;

	public static void main(String[] args) {

		dnsclient cliente = new dnsclient();

		try {

			switch (args[0]) {

			case "-u":
				cliente.udpClient(args);
				break;

			case "-t":
				cliente.tcpClient(args);
				break;

			default:
				System.out.println("\n\n\tUso: -(t|u) DsServerIPaddr\n\n");
				break;
			}

		} catch (NullPointerException e) {
			System.out.println("No hay respuesta");
		} catch (SocketException | UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	

	public void tcpClient(String[] args) throws UnknownHostException, IOException {

		Scanner stadin = new Scanner(System.in);
		String entrada;
		System.out.println("\nIntroduzca el dominio a traducir:\n");

		while (stadin.hasNextLine()) {
			try {

				entrada = stadin.nextLine();
				TcpConnection tcpConnection = null;

				tcpConnection = new TcpConnection(InetAddress.getByName(args[1]));
				parametros(entrada);

				Message outputMessage = generarMensaje();

				if (isCache() != null) {

					if (cache.get(rrType.toString().concat(domain)).i == 1)
						Utils.noAnswer();
					Utils.printQ(tcpConnection.getPROTOCOL(), tcpConnection.getIp(), outputMessage);
					cache.get(rrType.toString().concat(domain)).showCache();

				} else {

					/* NUEVO OBJETO EN CACHE */
					Cache newRegister = new Cache(Calendar.getInstance());

					Message answer = tcpConnection.connection(outputMessage, InetAddress.getByName(args[1]),
							newRegister);

					if (answer == null) {
						Utils.noAnswer();
					} else if (!answer.getAnswers().isEmpty()) {
						newRegister.setTtl(answer.getAnswers().get(0).getTTL());
						for (ResourceRecord rr : answer.getAnswers()) {
							newRegister.getAnswers().add(rr);
						}

					} else {
						for (ResourceRecord rr : answer.getNameServers()) {
							// newRegister.getAnswers().add(rr.);
							Utils.printA(tcpConnection.getIp(), rr);
						}
					}
					cache.put(rrType.toString().concat(domain), newRegister);
				}
			} catch (IllegalArgumentException e) {
				System.out.println("Tipo no soportado(Asegúrese de estar usando mayúsculas)");
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("\nFalta algún dato para la consulta. Uso: RRType Nombre\n");
			} catch (UnknownHostException e) {
				System.out.println("No se ha podido contactar con la IP: " + args[1]);
				break;
			} finally {
				System.out.println("\nIntroduzca el dominio a traducir:\n");
			}
		}
		stadin.close();
		return;
	}

	public void udpClient(String[] args) throws IOException {

		// VALIDACION INICIAL
		Scanner stdin = new Scanner(System.in);
		String entrada;
		System.out.println("\nIntroduzca el dominio a traducir:\n");

		// EJECUTAMOS HASTA QUE PULSEMOS CTR+D EN LA ENTRADA ESTANDAR
		while (stdin.hasNextLine()) {

			try {

				entrada = stdin.nextLine();
				UdpConnection udpConnection = null;
				Message outputMessage;

				parametros(entrada);
				outputMessage = generarMensaje();
				udpConnection = new UdpConnection(InetAddress.getByName(args[1]), outputMessage.getQuestionType());

				if (isCache() != null) {
					
					if (cache.get(rrType.toString().concat(domain)).i == 1)
						Utils.noAnswer();
					Utils.printQ(udpConnection.getPROTOCOL(), udpConnection.getIP(), outputMessage);
					cache.get(rrType.toString().concat(domain)).showCache();

				} else {

					/* NUEVO OBJETO EN CACHE */
					Cache newRegister = new Cache(Calendar.getInstance());

					Message answer = udpConnection.connection(outputMessage, InetAddress.getByName(args[1]),
							newRegister);

					if (answer == null) {
						Utils.noAnswer();
					} else if (!answer.getAnswers().isEmpty()) {
						newRegister.setTtl(answer.getAnswers().get(0).getTTL());
						for (ResourceRecord rr : answer.getAnswers()) {
							newRegister.getAnswers().add(rr);
						}

					} else {
						for (ResourceRecord rr : answer.getNameServers()) {
							Utils.printA(udpConnection.getIP(), rr);
							// newRegister.getAnswers().add(rr);
						}
					}

					cache.put(rrType.toString().concat(domain), newRegister);
				}

			} catch (IllegalArgumentException e) {
				System.out.println("Tipo no soportado(Asegúrese de estar usando mayúsculas)");
				continue;
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("\nFalta algún dato para la consulta. Uso: RRType Nombre\n");
				continue;
			} catch (UnknownHostException e) {
				System.out.println("Formato IP incorrecto: " + args[1]);
				break;
			} finally {
				System.out.println("\nIntroduzca el dominio a traducir:\n");
			}
		}
		stdin.close();
		return;

	}

	public ArrayList<ResourceRecord> isCache() {

		if (!cache.containsKey(rrType.toString().concat(domain)))
			return null;

		if (!cache.get(rrType.toString().concat(domain)).notExpired()) {
			cache.remove(rrType.toString().concat(domain));
			return null;
		}

		return cache.get(rrType.toString().concat(domain)).getAnswers();
	}

	public void validacionComando(String[] args) {

		if (args.length != 2) {
			System.out.println("\n\n\tUso: -(t|u) DsServerIPaddr\n\n");
			System.exit(0);
		}

		String protocol = args[0];

		if (!protocol.trim().equals("-t") && !protocol.trim().equals("-u")) {
			System.out.println("\n\n\tUso: -(t|u) DsServerIPaddr\n\n");
			System.exit(0);
		}

		return;
	}

	public void parametros(String entrada) {

		String[] split = entrada.split("\\s* \\s*");
		domain = split[1].toLowerCase();
		rrType = RRType.valueOf(split[0]);

	}

	public Message generarMensaje() {

		Message message = new Message(domain, rrType, false);
		return message;
	}

}
