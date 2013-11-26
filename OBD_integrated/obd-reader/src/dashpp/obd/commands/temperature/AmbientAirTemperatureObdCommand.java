/*
 * TODO put header
 */
package dashpp.obd.commands.temperature;

import dashpp.obd.enums.AvailableCommandNames;

/**
 * Ambient Air Temperature. 
 */
public class AmbientAirTemperatureObdCommand extends TemperatureObdCommand {

	/**
	 * @param cmd
	 */
	public AmbientAirTemperatureObdCommand() {
		super("01 46");
	}

	/**
	 * @param other
	 */
	public AmbientAirTemperatureObdCommand(TemperatureObdCommand other) {
		super(other);
	}

	@Override
    public String getName() {
		return AvailableCommandNames.AMBIENT_AIR_TEMP.getValue();
    }

}