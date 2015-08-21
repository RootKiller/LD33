package es.bimgam.ld33.core;

import java.util.HashMap;

public class CommandManager {
	private HashMap<String, Command> commands = new HashMap<String, Command>();

	public static CommandManager Instance = null;

	public CommandManager() {
		Instance = this;
	}

	public void register(Command cmd) {
		this.commands.put(cmd.getName(), cmd);
	}

	public boolean execute(String commandName) {
		Command cmd = this.commands.get(commandName);
		if (cmd != null) {
			cmd.run();
			return true;
		}
		return false;
	}
}
