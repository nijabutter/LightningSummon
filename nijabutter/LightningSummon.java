package nijabutter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/*
 * Author: nijabutter
 * https://github.com/nijabutter
 */

public class LightningSummon extends JavaPlugin{
	
	Map<String, String> currentRequest = new HashMap<String, String>();
	Map<String, String> sentRequst = new HashMap<String, String>();
	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String commandLabel, final String[] args) {
		Player p = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("lsa"))
		{
			if (!(p.hasPermission("lightning.teleport"))) { return false; }
			if (args.length == 1) {
				final Player target = this.getServer().getPlayer(args[0]);
				if (target == null) {return false;}
				if (target == p) {return false;}
				this.sendRequest(p, target);
				
				this.getServer().getScheduler().scheduleSyncDelayedTask((Plugin)this, (Runnable)new Runnable() {
                    @Override
                    public void run() {
                        LightningSummon.this.killRequest(target.getName());
                    }
                }, 200L);
				
				return true;
			}
			return false;
		}
		if (cmd.getName().equalsIgnoreCase("lsahere"))
		{
			if (!(p.hasPermission("lightning.teleport"))) { return false; }
			if (args.length == 1) {
				final Player target = this.getServer().getPlayer(args[0]);
				if (target == null) {return false;}
				if (target == p) {return false;}
				p.sendMessage("Sending tp request to: " + target.getName());
				target.sendMessage(p.getName() + " has asked for you to teleport to them. /lsaccept to accept");
				this.sentRequst.put(target.getName(), p.getName());
				
				this.getServer().getScheduler().scheduleSyncDelayedTask((Plugin)this, (Runnable)new Runnable() {
                    @Override
                    public void run() {
                        LightningSummon.this.sendkillRequest(target.getName());
                    }
                }, 200L);
				
				return true;
			}
			return false;
		}
		else if(cmd.getName().equalsIgnoreCase("lsaccept"))
		{
			if (this.currentRequest.containsKey(p.getName())) {
				final Player target = this.getServer().getPlayer((String)this.currentRequest.get(p.getName()));
				this.currentRequest.remove(p.getName());
				if (target == null) {
					p.sendMessage("The player you were teleporting has since left the server");
					return true;
				}
				p.sendMessage("Teleporting...");
				target.teleport((Entity)p);
				Random rand = new Random();
				for (int i =0; i < 5; i++) {
					int newX = target.getLocation().getBlockX() + rand.nextInt(10);
					int newZ = target.getLocation().getBlockZ() + rand.nextInt(10);
					p.getWorld().strikeLightningEffect(new Location(target.getWorld(), newX, target.getLocation().getBlockY(), newZ));
				}

				return true;
			}
			if (this.sentRequst.containsKey(p.getName())) {
				final Player target = this.getServer().getPlayer((String)this.sentRequst.get(p.getName()));
				this.sentRequst.remove(p.getName());
				if (target == null) {
					p.sendMessage("The player you were teleporting to has since left the server");
					return true;
				}
				p.sendMessage("Teleporting...");
				p.teleport((Entity)target);
				Random rand = new Random();
				for (int i =0; i < 5; i++) {
					int newX = p.getLocation().getBlockX() + rand.nextInt(10);
					int newZ = p.getLocation().getBlockZ() + rand.nextInt(10);
					p.getWorld().strikeLightningEffect(new Location(p.getWorld(), newX, p.getLocation().getBlockY(), newZ));
				}
				return true;
			}
			p.sendMessage("You do not have any pending teleport requests");
			return false;
			
		}
		else if(cmd.getName().equalsIgnoreCase("lsdeny")) {
			if (!this.currentRequest.containsKey(p.getName())) {
				p.sendMessage("You do not have any pending requests");
				return true;
			}
			final Player rejected = this.getServer().getPlayer((String)this.currentRequest.get(p.getName()));
			this.currentRequest.remove(p.getName());
			if (rejected != null) {
				rejected.sendMessage(p.getName() + " has declined your teleport request");
				p.sendMessage("Teleport requst has been declined");
				return true;
			}
		}
		return true;
	}
	
	public boolean killRequest(final String key) {
		if (this.currentRequest.containsKey(key)) {
			final Player p = this.getServer().getPlayer((String)this.currentRequest.get(key));
			if (p != null) {
				p.sendMessage("Your tp request has expired");
			}
			this.currentRequest.remove(key);
			return true;
		}
		return false;
	}
	
	public boolean sendkillRequest(final String key) {
		if (this.sentRequst.containsKey(key)) {
			final Player p = this.getServer().getPlayer((String)this.sentRequst.get(key));
			if (p != null) {
				p.sendMessage("Your tp request has expired");
			}
			this.sentRequst.remove(key);
			return true;
		}
		return false;
	}
	
	public void sendRequest(final Player s, final Player t) {
		s.sendMessage("Sending tp request to: " + t.getName());
		t.sendMessage(s.getName() + " has asked to teleport to you. /lsaccept to accept.");
		this.currentRequest.put(t.getName(), s.getName());
	}
	
}
