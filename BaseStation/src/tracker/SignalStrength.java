package tracker;

public class SignalStrength 
{
	/**
	 * Signal strength in hex.
	 */
	public String hex;
	
	/**
	 * Signal strength in dBm
	 */
	public int dbm;
	
	/**
	 * Signal strength in percent
	 */
	public double percent;
	
	/**
	 * The broadcast that this was measured as a part of
	 */
	public int broadcastNumber;
	
	public SignalStrength()
	{
	}
	
	public SignalStrength(String hex, int dbm, double percent, int broadcastNumber)
	{
		this.hex = hex;
		this.dbm = dbm;
		this.percent = percent;
		this.broadcastNumber = broadcastNumber;
	}
	
	/**
	 * Convert the raw string signal strength into a signal strength object.  This is the signal
	 * strength that comes from the network.
	 * @param raw - String of format "<hex> <dbm> <(percent)>"
	 * @param broadcastNumber - The broadcast that this signal strength was measured as a part of
	 */
	public SignalStrength(String rawSigStrength, int broadcastNumber)
	{
		String[] split = rawSigStrength.split(" ");
		
		// Hex first
		hex = split[0];
		
		// Get number dbm
		dbm = Integer.parseInt(split[1].replaceFirst("dBm", ""));
		
		// Get percent
		percent = Double.parseDouble(split[2].replaceFirst("\\(", "").replaceFirst("\\%\\)", ""));
		
		this.broadcastNumber = broadcastNumber;
	}
}
