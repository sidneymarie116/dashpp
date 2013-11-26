/*
 * TODO put header
 */
package dashpp.obd.reader.config;

import java.util.ArrayList;

import dashpp.obd.commands.ObdBaseCommand;
import dashpp.obd.commands.SpeedObdCommand;
import dashpp.obd.commands.control.CommandEquivRatioObdCommand;
import dashpp.obd.commands.control.DtcNumberObdCommand;
import dashpp.obd.commands.control.TimingAdvanceObdCommand;
import dashpp.obd.commands.control.TroubleCodesObdCommand;
import dashpp.obd.commands.engine.EngineLoadObdCommand;
import dashpp.obd.commands.engine.EngineRPMObdCommand;
import dashpp.obd.commands.engine.EngineRuntimeObdCommand;
import dashpp.obd.commands.engine.MassAirFlowObdCommand;
import dashpp.obd.commands.engine.ThrottlePositionObdCommand;
import dashpp.obd.commands.fuel.FindFuelTypeObdCommand;
import dashpp.obd.commands.fuel.FuelLevelObdCommand;
import dashpp.obd.commands.fuel.FuelTrimObdCommand;
import dashpp.obd.commands.pressure.BarometricPressureObdCommand;
import dashpp.obd.commands.pressure.FuelPressureObdCommand;
import dashpp.obd.commands.pressure.IntakeManifoldPressureObdCommand;
import dashpp.obd.commands.protocol.ObdResetCommand;
import dashpp.obd.commands.temperature.AirIntakeTemperatureObdCommand;
import dashpp.obd.commands.temperature.AmbientAirTemperatureObdCommand;
import dashpp.obd.commands.temperature.EngineCoolantTemperatureObdCommand;
import dashpp.obd.enums.FuelTrim;


/**
 * TODO put description
 */
public final class ObdConfig {

  public static ArrayList<ObdBaseCommand> getCommands() {
    ArrayList<ObdBaseCommand> cmds = new ArrayList<ObdBaseCommand>();
    // Protocol
    cmds.add(new ObdResetCommand());

    // Control
    cmds.add(new CommandEquivRatioObdCommand());
    cmds.add(new DtcNumberObdCommand());
    cmds.add(new TimingAdvanceObdCommand());
    cmds.add(new TroubleCodesObdCommand(0));

    // Engine
    cmds.add(new EngineLoadObdCommand());
    cmds.add(new EngineRPMObdCommand());
    cmds.add(new EngineRuntimeObdCommand());
    cmds.add(new MassAirFlowObdCommand());

    // Fuel
    // cmds.add(new AverageFuelEconomyObdCommand());
    // cmds.add(new FuelEconomyObdCommand());
    // cmds.add(new FuelEconomyMAPObdCommand());
    // cmds.add(new FuelEconomyCommandedMAPObdCommand());
    cmds.add(new FindFuelTypeObdCommand());
    cmds.add(new FuelLevelObdCommand());
    cmds.add(new FuelTrimObdCommand(FuelTrim.LONG_TERM_BANK_1));
    cmds.add(new FuelTrimObdCommand(FuelTrim.LONG_TERM_BANK_2));
    cmds.add(new FuelTrimObdCommand(FuelTrim.SHORT_TERM_BANK_1));
    cmds.add(new FuelTrimObdCommand(FuelTrim.SHORT_TERM_BANK_2));

    // Pressure
    cmds.add(new BarometricPressureObdCommand());
    cmds.add(new FuelPressureObdCommand());
    cmds.add(new IntakeManifoldPressureObdCommand());

    // Temperature
    cmds.add(new AirIntakeTemperatureObdCommand());
    cmds.add(new AmbientAirTemperatureObdCommand());
    cmds.add(new EngineCoolantTemperatureObdCommand());

    // Misc
    cmds.add(new SpeedObdCommand());
    cmds.add(new ThrottlePositionObdCommand());

    return cmds;
  }

}