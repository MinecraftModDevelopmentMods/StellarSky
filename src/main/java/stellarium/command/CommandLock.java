package stellarium.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import stellarium.StellarSky;
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
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
		if(MinecraftServer.getServer() !=null && MinecraftServer.getServer().isSinglePlayer())
			return true;
		else return super.canCommandSenderUseCommand(sender);
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
		
		StellarManager manager = StellarManager.getServerManager(MinecraftServer.getServer());
		manager.setLocked(lock);
		StellarSky.instance.getNetworkManager().sendLockInformation(lock);
	}

}
