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
	 * The broadcast that this was measured as a part of
	 */
	public int broadcastNumber;
	
	public SignalStrength()
	{
	}
	
	public SignalStrength(String hex, int dbm, int broadcastNumber)
	{
		this.hex = hex;
		this.dbm = dbm;
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
		// Hex first
		hex = "-" + rawSigStrength;
		
		// Get number dbm
		dbm = Integer.parseInt(rawSigStrength, 16);
		
		this.broadcastNumber = broadcastNumber;
	}
}
