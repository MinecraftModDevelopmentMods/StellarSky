package stellarium.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import stellarium.StellarSky;
import stellarium.stellars.StellarManager;

public class CommandLock extends CommandBase {

	@Override
	public String getName() {
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
		if(server != null && server.isSinglePlayer())
			return true;
		else return super.checkPermission(server, sender);
    }

	@Override
	public String getUsage(ICommandSender p_71518_1_) {
		return "stellarsky.command.lock.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		boolean lock = true;

		if(args.length >= 1)
			lock = parseBoolean(args[0]);

		StellarManager manager = StellarManager.getManager(server.getEntityWorld());
		manager.setLocked(lock);
		StellarSky.INSTANCE.getNetworkManager().sendLockInformation(lock);
	}

}
