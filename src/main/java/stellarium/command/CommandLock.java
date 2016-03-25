package stellarium.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
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
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
		if(server.isSinglePlayer())
			return true;
        return sender.canCommandSenderUseCommand(this.getRequiredPermissionLevel(), this.getCommandName());
    }

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "stellarsky.command.lock.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
		StellarManager manager = StellarManager.getManager(sender.getEntityWorld().isRemote);
		manager.lock();
	}

}
