/*
 * TODO put header
 */
package dashpp.obd.commands.protocol;

/**
 * Turns off line-feed.
 */
public class LineFeedOffObdCommand extends ObdProtocolCommand {

	public LineFeedOffObdCommand() {
		super("AT L0");
	}

	/**
	 * @param other
	 */
	public LineFeedOffObdCommand(LineFeedOffObdCommand other) {
		super(other);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.lighthouselabs.obd.commands.ObdBaseCommand#getFormattedResult()
	 */
	@Override
	public String getFormattedResult() {
		return getResult();
	}

	@Override
	public String getName() {
		return "Line Feed Off";
	}

}