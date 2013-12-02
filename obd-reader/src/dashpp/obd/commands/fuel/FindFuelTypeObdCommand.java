/*
 * TODO put header 
 */
package dashpp.obd.commands.fuel;

import dashpp.obd.commands.ObdCommand;
import dashpp.obd.commands.utils.ObdUtils;
import dashpp.obd.enums.AvailableCommandNames;

/**
 * This command is intended to determine the vehicle fuel type.
 */
public class FindFuelTypeObdCommand extends ObdCommand {

	private int fuelType = 0;

	/**
	 * Default ctor.
	 */
	public FindFuelTypeObdCommand() {
		super("10 51");
	}

	/**
	 * Copy ctor
	 * 
	 * @param other
	 */
	public FindFuelTypeObdCommand(FindFuelTypeObdCommand other) {
		super(other);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.lighthouselabs.obd.command.ObdBaseCommand#getFormattedResult()
	 */
	@Override
	public String getFormattedResult() {
		String res = getResult();

		if (!"NODATA".equals(res)) {
			// ignore first two bytes [hh hh] of the response
			fuelType = buffer.get(2);
			res = getFuelTypeName();
		}

		return res;
	}
	
	/**
	 * @return Fuel type name.
	 */
	public final String getFuelTypeName() {
		return ObdUtils.getFuelTypeName(fuelType);
	}

	@Override
	public String getName() {
		return AvailableCommandNames.FUEL_TYPE.getValue();
	}

}