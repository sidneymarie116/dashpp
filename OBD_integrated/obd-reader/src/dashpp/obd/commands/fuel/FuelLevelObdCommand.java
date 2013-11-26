/*
 * TODO put header 
 */
package dashpp.obd.commands.fuel;

import dashpp.obd.commands.ObdCommand;
import dashpp.obd.enums.AvailableCommandNames;

/**
 * Get fuel level in percentage
 */
public class FuelLevelObdCommand extends ObdCommand {

	private float fuelLevel = 0f;

	public FuelLevelObdCommand() {
		super("01 2F");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.lighthouselabs.obd.commands.ObdBaseCommand#getFormattedResult()
	 */
	@Override
	public String getFormattedResult() {
		if (!"NODATA".equals(getResult())) {
			// ignore first two bytes [hh hh] of the response
			fuelLevel = 100.0f * buffer.get(2) / 255.0f;
		}

		return String.format("%.1f%s", fuelLevel, "%");
	}

	@Override
	public String getName() {
		return AvailableCommandNames.FUEL_LEVEL.getValue();
	}

}