package es.uvigo.det.ro.simpledns;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static es.uvigo.det.ro.simpledns.RRType.TXT;

/**
 * Un registro TXT es un tipo de registro DNS que proporciona información de
 * texto a fuentes externas a tu dominio. El texto puede ser lenguaje legible
 * por máquina o por el ser humano, y se puede utilizar para diversos fines.
 * 
 * @author Expploitt
 *
 */
public class TXTResourceRecord extends ResourceRecord {

	private final String txt;

	public TXTResourceRecord(DomainName domain, int ttl, String txt) {
		super(domain, TXT, ttl, txt.getBytes());
		this.txt = txt;
	}

	protected TXTResourceRecord(ResourceRecord record, byte[] message) throws Exception {
		super(record);

		txt = new DomainName(getRRData(), message).toString();
	}

	public String getTxt() {
		return txt;
	}

	@Override
	public byte[] toByteArray() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		try {
			os.write(super.toByteArray());
			os.write(txt.getBytes());
		} catch (IOException ex) {
			Logger.getLogger(AResourceRecord.class.getName()).log(Level.SEVERE, null, ex);
			System.exit(-1);
		}

		return os.toByteArray();
	}

	@Override
	public String toString() {
		return  "   " + txt;
	}

}
