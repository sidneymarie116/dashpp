/*
 * TODO put header
 */
package dashpp.obd.commands.engine;

import dashpp.obd.commands.PercentageObdCommand;
import dashpp.obd.enums.AvailableCommandNames;

/**
 * Calculated Engine Load value.
 */
public class EngineLoadObdCommand extends PercentageObdCommand {

	public EngineLoadObdCommand() {
		super("01 04");
	}

	/**
	 * @param other
	 */
	public EngineLoadObdCommand(EngineLoadObdCommand other) {
		super(other);
	}

	/* (non-Javadoc)
	 * @see eu.lighthouselabs.obd.commands.ObdBaseCommand#getName()
	 */
	@Override
	public String getName() {
		return AvailableCommandNames.ENGINE_LOAD.getValue();
	}

}