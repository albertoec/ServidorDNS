package es.uvigo.det.ro.simpledns;

import static es.uvigo.det.ro.simpledns.RRType.MX;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MXResourceRecord extends ResourceRecord {

	private final DomainName exchange;
	private final int preference;

	public MXResourceRecord(DomainName domain, int ttl, DomainName mx, int preference) {
		super(domain, MX, ttl, mx.toByteArray());
		this.preference = preference;
		this.exchange = mx;
	}

	protected MXResourceRecord(ResourceRecord decoded, final byte[] message) {
		super(decoded);

		byte[] BUF = getRRData();
		byte[] exchangebyte = Arrays.copyOfRange(BUF, 2, BUF.length);

		exchange = new DomainName(exchangebyte, message);

		byte[] preferencebyte = Arrays.copyOfRange(BUF, 0, 2);

		preference = Utils.int16fromByteArray(preferencebyte);
	}

	@Override
	public byte[] toByteArray() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		try {
			os.write(super.toByteArray());
			os.write(exchange.toByteArray());
		} catch (IOException ex) {
			Logger.getLogger(CNAMEResourceRecord.class.getName()).log(Level.SEVERE, null, ex);
			System.exit(-1);
		}

		return os.toByteArray();
	}

	public DomainName getExchange() {
		return exchange;
	}

	public int getPreference() {
		return preference;
	}

	@Override
	public String toString() {
		return  "   " + preference + "   " + exchange;
	}

}
