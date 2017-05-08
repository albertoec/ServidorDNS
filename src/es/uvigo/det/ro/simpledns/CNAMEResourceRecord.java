package es.uvigo.det.ro.simpledns;

import static es.uvigo.det.ro.simpledns.RRType.CNAME;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CNAMEResourceRecord extends ResourceRecord {

	private final DomainName canonicalName;

	public CNAMEResourceRecord(DomainName domain, int ttl, DomainName canonicalName) {
		super(domain, CNAME, ttl, canonicalName.toByteArray());
		this.canonicalName = canonicalName;
	}

	protected CNAMEResourceRecord(ResourceRecord decoded, final byte[] message) {
		super(decoded);

		canonicalName = new DomainName(getRRData(), message);
	}

	public DomainName getCanonicalName() {
		return canonicalName;
	}

	@Override
	public byte[] toByteArray() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		try {
			os.write(super.toByteArray());
			os.write(canonicalName.toByteArray());
		} catch (IOException ex) {
			Logger.getLogger(CNAMEResourceRecord.class.getName()).log(Level.SEVERE, null, ex);
			System.exit(-1);
		}

		return os.toByteArray();
	}

	@Override
	public String toString() {
		return  "   " + canonicalName;
	}

}
