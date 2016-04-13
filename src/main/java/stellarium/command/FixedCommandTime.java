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
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length > 1)
        {
            if (args[0].equals("set"))
            {
                long i1;

                if (args[1].equals("day"))
                {
                    i1 = this.getModifiedTimeByAngle(sender.getEntityWorld(), 10.0);
                }
                else if (args[1].equals("night"))
                {
                    i1 = this.getModifiedTimeByOffset(sender.getEntityWorld(), 0.5);
                }
                else
                {
                    i1 = parseInt(args[1], 0);
                }

                this.setAllWorldTimes(server, i1);
                notifyOperators(sender, this, "commands.time.set", new Object[] {Long.valueOf(i1)});
                return;
            }

            if (args[0].equals("add"))
            {
                int l = parseInt(args[1], 0);
                this.incrementAllWorldTimes(server, l);
                notifyOperators(sender, this, "commands.time.added", new Object[] {Integer.valueOf(l)});
                return;
            }

            if (args[0].equals("query"))
            {
                if (args[1].equals("daytime"))
                {
                    int k = this.getTimeInDay(sender.getEntityWorld());
                    sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, k);
                    notifyOperators(sender, this, "commands.time.query", new Object[] {Integer.valueOf(k)});
                    return;
                }

                if (args[1].equals("day"))
                {
                    int j = this.getDay(sender.getEntityWorld());
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
	
	public long getModifiedTimeByAngle(World world, double angle) {
		if(!StellarSkyAPI.hasSkyProvider(world)) {
			return (long) (angle / 180.0 * 24000);
		}
		
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
		if(!StellarSkyAPI.hasSkyProvider(world)) {
			return (long) (timeOffset * 24000);
		}
		
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
	
	public int getDay(World world) {
		if(StellarSkyAPI.hasSkyProvider(world)) {
			ISkyProvider skyProvider = StellarSkyAPI.getSkyProvider(world);
			return (int)Math.floor(skyProvider.getYearlyOffset() * skyProvider.getYearLength());
		}
		else return (int) ((world.getWorldTime() / 24000L) % (Integer.MAX_VALUE + 1));
	}
	
	public int getTimeInDay(World world) {
		if(StellarSkyAPI.hasSkyProvider(world)) {
			ISkyProvider skyProvider = StellarSkyAPI.getSkyProvider(world);
			return (int)Math.floor(skyProvider.getDaytimeOffset() * skyProvider.getYearLength());
		}
		else return (int) (world.getWorldTime() % 24000L);
	}

    protected void setAllWorldTimes(MinecraftServer server, long time)
    {
        for (int i = 0; i < server.worldServers.length; ++i)
        {
            server.worldServers[i].setWorldTime(time);
        }
    }

    protected void incrementAllWorldTimes(MinecraftServer server, long amount)
    {
        for (int i = 0; i < server.worldServers.length; ++i)
        {
            WorldServer worldserver = server.worldServers[i];
            worldserver.setWorldTime(worldserver.getWorldTime() + amount);
        }
    }
}
