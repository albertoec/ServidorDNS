package pantalla;

import es.uvigo.det.ro.simpledns.AAAAResourceRecord;
import es.uvigo.det.ro.simpledns.AResourceRecord;
import es.uvigo.det.ro.simpledns.Message;
import es.uvigo.det.ro.simpledns.NSResourceRecord;
import es.uvigo.det.ro.simpledns.ResourceRecord;

public class PantallaUtils {

	public PantallaUtils() {

	}

	public void pantallaRR(Message answerMessage, int a) {

		if (a == 0) {
			System.out.println("\nAnswers:\n");
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
			
			System.out.println("\nAdditional Section:\n");
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
		} else {
			System.out.println("\nName servers:\n");
			for (ResourceRecord rr : answerMessage.getNameServers()) {
				System.out.println(((NSResourceRecord) rr).getNS());
				System.out.println("");
			}
		}
	}
}
