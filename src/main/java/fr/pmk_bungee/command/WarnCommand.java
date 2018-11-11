package fr.pmk_bungee.command;

import java.sql.Timestamp;

import fr.pmk_bungee.Main;
import fr.pmk_bungee.object.Warn;
import fr.pmk_bungee.utils.PlayerSituation;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class WarnCommand extends Command {

	public WarnCommand(String name) {

		super(name);
	}

	@SuppressWarnings("unused")
	@Override
	public void execute(CommandSender sender, String[] args) {

		if(sender.hasPermission("bungeestaff.command.warn")) {

			if(args.length >= 4) {

				String playername = args[0];
				String warnReason = "";
				PlayerSituation situation = new PlayerSituation(playername);
				for(int i = 1; i <= args.length - 1; i++) {

					warnReason+=warnReason + args[i] + " ";
				}

				Main.getConfigManager().save();

				if(situation != null) {


					Warn warn = new Warn();
					long seconds = Integer.parseInt(args[1]);
					Main.TimeUnit unit = Main.TimeUnit.getByString(args[2]);
					if(unit != null) {

						seconds*= unit.getSeconds();
						warn.setWarnDate(new Timestamp(System.currentTimeMillis()));
						warn.setWarnBy(situation.getPlayerId(sender.getName()));
						warn.setPlayerId(situation.getPlayerId(playername));
						warn.setWarnReason(warnReason);

						PlayerSituation.setWarn(warn);
						
						sender.sendMessage(new TextComponent(Main.PREFIX + Main.getConfigManager().getString("lang.commands.warn.succes", new String[] {
								"{NAME}~" + playername,

						})));

					} else {
						sender.sendMessage(new TextComponent(Main.PREFIX + Main.getConfigManager().getString("lang.commands.warn.syntax")));
					}
				}
			} else {
				sender.sendMessage(new TextComponent(Main.PREFIX + Main.getConfigManager().getString("lang.commands.warn.syntax")));
			}
		} else {
			sender.sendMessage(new TextComponent(Main.PREFIX +Main.getConfigManager().getString("lang.errors.no_permissions")));
		}
	}
}
