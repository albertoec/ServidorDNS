package es.uvigo.det.ro.simpledns;

import java.util.Arrays;


public class SOAResourceRecord extends ResourceRecord {
	private final DomainName MName;
	private final DomainName RName;
	private final int serial;
	private final int refresh;
	private final int retry;
	private final int expire;
	private final int minimum;

	protected SOAResourceRecord(ResourceRecord record, byte[] message) throws Exception {
		super(record);
		byte[] buf = getRRData();

		this.MName = new DomainName(buf, message);

		byte[] RNameByte = Arrays.copyOfRange(buf, MName.getEncodedLength(), getRDLength());
		this.RName = new DomainName(RNameByte, message);
		buf = Arrays.copyOfRange(buf, MName.getEncodedLength() + RName.getEncodedLength(), getRDLength());

		/* Tenemos 16 bytes de TTLs */
		byte[] serialByte = Arrays.copyOfRange(buf, 0, 4);
		byte[] refreshByte = Arrays.copyOfRange(buf, 4, 8);
		byte[] RetryByte = Arrays.copyOfRange(buf, 8, 12);
		byte[] expireByte = Arrays.copyOfRange(buf, 12, 16);
		byte[] minimumByte = Arrays.copyOfRange(buf, 16, 20);

		this.serial = Utils.int32fromByteArray(serialByte);
		this.refresh = Utils.int32fromByteArray(refreshByte);
		this.retry = Utils.int32fromByteArray(RetryByte);
		this.expire = Utils.int32fromByteArray(expireByte);
		this.minimum = Utils.int32fromByteArray(minimumByte);
		
	}

	public DomainName getMName() {
		return MName;
	}

	@Override
	public String toString() {
		return "   " + MName + "   " + RName + "   " + serial + "   " + refresh + "   "
				+ retry + "   " + expire + "   " + minimum;
	}

	public DomainName getRName() {
		return RName;
	}

	public int getSerial() {
		return serial;
	}

	public int getRefresh() {
		return refresh;
	}

	public int getRetry() {
		return retry;
	}

	public int getExpire() {
		return expire;
	}

	public int getMinimum() {
		return minimum;
	}
}
