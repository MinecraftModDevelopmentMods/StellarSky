package stellarium.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.CommandTime;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import stellarium.api.ISkyProvider;
import stellarium.api.StellarSkyAPI;

public class FixedCommandTime extends CommandTime {
	
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length > 1)
        {
            if (args[0].equals("set"))
            {
                long l;

                if (args[1].equals("day"))
                {
                    l = this.getModifiedTimeByAngle(sender.getEntityWorld(), 10.0);
                }
                else if (args[1].equals("night"))
                {
                    l = this.getModifiedTimeByOffset(sender.getEntityWorld(), 0.5);
                }
                else
                {
                    l = parseInt(args[1], 0);
                }

                this.setTime(sender, l);
                notifyOperators(sender, this, "commands.time.set", new Object[] {Long.valueOf(l)});
                return;
            }

            if (args[0].equals("add"))
            {
                int k = parseInt(args[1], 0);
                this.addTime(sender, k);
                notifyOperators(sender, this, "commands.time.added", new Object[] {Long.valueOf(k)});
                return;
            }

            if (args[0].equals("query"))
            {
                if (args[1].equals("daytime"))
                {
                    int j = (int)(sender.getEntityWorld().getWorldTime() % 2147483647L);
                    sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, j);
                    notifyOperators(sender, this, "commands.time.query", new Object[] {Integer.valueOf(j)});
                    return;
                }

                if (args[1].equals("gametime"))
                {
                    int i = (int)(sender.getEntityWorld().getTotalWorldTime() % 2147483647L);
                    sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, i);
                    notifyOperators(sender, this, "commands.time.query", new Object[] {Integer.valueOf(i)});
                    return;
                }
            }
        }

        throw new WrongUsageException("commands.time.usage", new Object[0]);
    }
	
	/**
     * Set the time in the server object.
     */
    protected void setTime(ICommandSender p_71552_1_, long p_71552_2_)
    {
        for (int j = 0; j < MinecraftServer.getServer().worldServers.length; j++)
        {
            MinecraftServer.getServer().worldServers[j].setWorldTime(p_71552_2_);
        }
    }

    /**
     * Adds (or removes) time in the server object.
     */
    protected void addTime(ICommandSender p_71553_1_, long p_71553_2_)
    {
        for (int j = 0; j < MinecraftServer.getServer().worldServers.length; j++)
        {
            WorldServer worldserver = MinecraftServer.getServer().worldServers[j];
            worldserver.setWorldTime(worldserver.getWorldTime() + p_71553_2_);
        }
    }
	
	public long getModifiedTimeByAngle(World world, double angle) {
		long time = world.getWorldTime();
    	ISkyProvider skyProvider = StellarSkyAPI.getSkyProvider(world);
    	double wakeDayOffset = skyProvider.dayOffsetUntilSunReach(angle);
		double currentDayOffset = skyProvider.getDaytimeOffset(time);
		double dayLength = skyProvider.getDayLength();

    	double modifiedWorldTime = time + (-wakeDayOffset - currentDayOffset) * dayLength;
    	while(modifiedWorldTime < time)
    		modifiedWorldTime = dayLength;
    	
    	return (long) modifiedWorldTime;
	}
	
	public long getModifiedTimeByOffset(World world, double timeOffset) {
		long time = world.getWorldTime();
    	ISkyProvider skyProvider = StellarSkyAPI.getSkyProvider(world);
    	double wakeDayOffset = timeOffset;
		double currentDayOffset = skyProvider.getDaytimeOffset(time);
		double dayLength = skyProvider.getDayLength();

    	double modifiedWorldTime = time + (-wakeDayOffset - currentDayOffset) * dayLength;
    	while(modifiedWorldTime < time)
    		modifiedWorldTime = dayLength;
    	
    	return (long) modifiedWorldTime;
	}

}