/*
 * TODO put header 
 */
package dashpp.obd.reader;

import dashpp.obd.reader.io.ObdCommandJob;

/**
 * TODO put description
 */
public interface IPostMonitor {
	void setListener(IPostListener callback);

	boolean isRunning();

	void executeQueue();
	
	void addJobToQueue(ObdCommandJob job);
}