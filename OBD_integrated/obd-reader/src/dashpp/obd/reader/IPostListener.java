/*
 * TODO put header 
 */
package dashpp.obd.reader;

import dashpp.obd.reader.io.ObdCommandJob;

/**
 * TODO put description
 */
public interface IPostListener {

	void stateUpdate(ObdCommandJob job);
	
}