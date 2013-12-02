/*
 * TODO put header
 */
package dashpp.obd.enums;

/**
 * TODO put description
 */
public enum AvailableCommandNames {
	AIR_INTAKE_TEMP("Air Intake Temperature"),
	AMBIENT_AIR_TEMP("AirTemp"),
	ENGINE_COOLANT_TEMP("Engine Coolant Temperature"),
	BAROMETRIC_PRESSURE("Barometric Pressure"),
	FUEL_PRESSURE("Fuel Pressure"),
	INTAKE_MANIFOLD_PRESSURE("Intake Manifold Pressure"),
	ENGINE_LOAD("Engine Load"),
	ENGINE_RUNTIME("Engine Runtime"),
	LONG_TERM_BANK_1("FuelTrim"),
	ENGINE_RPM("EngineRPM"),
	SPEED("Speed"),
	MAF("MAF"),
	THROTTLE_POS("Throttle"),
	TROUBLE_CODES("Trouble"),
	FUEL_LEVEL("FuelLevel"),
	FUEL_TYPE("FuelType"),
	FUEL_CONSUMPTION("FuelCons"),
	FUEL_ECONOMY("FuelEcon"),
	FUEL_ECONOMY_WITH_MAF("FuelEcon"),
	FUEL_ECONOMY_WITHOUT_MAF("FuelEcon"),
	TIMING_ADVANCE("Timing Advance"),
	DTC_NUMBER("Diagnostic Trouble Codes"),
	EQUIV_RATIO("Command Equivalence Ratio");
	
	
	private final String value;

	/**
	 * 
	 * @param value
	 */
	private AvailableCommandNames(String value) {
		this.value = value;
	}

	/**
	 * 
	 * @return
	 */
	public final String getValue() {
		return value;
	}
	
}