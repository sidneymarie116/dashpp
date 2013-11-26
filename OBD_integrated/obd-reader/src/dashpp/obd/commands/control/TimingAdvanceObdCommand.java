/*
 * TODO put header
 */
package dashpp.obd.commands.control;

import dashpp.obd.commands.PercentageObdCommand;
import dashpp.obd.enums.AvailableCommandNames;

/**
 * TODO put description
 * 
 * Timing Advance
 */
public class TimingAdvanceObdCommand extends PercentageObdCommand {

	public TimingAdvanceObdCommand() {
		super("01 0E");
	}

	public TimingAdvanceObdCommand(TimingAdvanceObdCommand other) {
		super(other);
	}

	@Override
	public String getName() {
		return AvailableCommandNames.TIMING_ADVANCE.getValue();
	}
}