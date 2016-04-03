package stellarium.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import stellarium.stellars.StellarManager;

public class CommandLock extends CommandBase {

	@Override
	public String getCommandName() {
		return "locksky";
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
		boolean lock = true;
		
		if(args.length >= 1)
			lock = this.parseBoolean(sender, args[0]);
		
		StellarManager manager = StellarManager.getManager(false);
		manager.setLocked(lock);
	}

}
