/*
 * TODO put header
 */
package dashpp.obd.commands.temperature;

import dashpp.obd.enums.AvailableCommandNames;

/**
 * TODO
 * 
 * put description
 */
public class AirIntakeTemperatureObdCommand extends TemperatureObdCommand {

	public AirIntakeTemperatureObdCommand() {
		super("01 0F");
	}

	public AirIntakeTemperatureObdCommand(AirIntakeTemperatureObdCommand other) {
		super(other);
	}

	@Override
	public String getName() {
		return AvailableCommandNames.AIR_INTAKE_TEMP.getValue();
	}
	
}