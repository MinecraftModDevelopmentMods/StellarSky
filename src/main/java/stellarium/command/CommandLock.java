package stellarium.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import stellarium.stellars.StellarManager;

public class CommandLock extends CommandBase {

	@Override
	public String getCommandName() {
		return "lock";
	}
	
	@Override
    public int getRequiredPermissionLevel()
    {
        return 3;
    }

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "stellarsky.command.lock.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		StellarManager manager = StellarManager.getManager(sender.getEntityWorld());
		manager.lock();
	}

}
