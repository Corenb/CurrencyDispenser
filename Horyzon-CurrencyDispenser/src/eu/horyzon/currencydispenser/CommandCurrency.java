package eu.horyzon.currencydispenser;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class CommandCurrency implements CommandExecutor {

	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			if (!(s instanceof Player))
				s.sendMessage("Command must be run by a player!");
			else {
				Player p = (Player) s;
				s.sendMessage(ChatColor.GOLD + "Your accounts:");
				for (CurrencyManager currency : CurrencyManager.currencies) {
					s.sendMessage(ChatColor.YELLOW + "  " + currency.getName() + " -> "
							+ currency.getAccount(p.getUniqueId()));
				}
			}
			return true;
		} else if (args.length == 3) {
			if (args[0].equalsIgnoreCase("create")) {

				Double start;

				try {
					start = Double.parseDouble(args[2]);
				} catch (NumberFormatException e) {
					s.sendMessage(ChatColor.RED + "Amount of start must be a number!");
					return true;
				}


				if (!CurrencyManager.existCurrency(args[1])) {
					CurrencyManager.registerCurrency(args[1], start);
					s.sendMessage(ChatColor.GREEN + args[1] + " currency created!");
				} else
					s.sendMessage(ChatColor.RED + args[1] + " already exist!");

				return true;
			} else if (args[0].equalsIgnoreCase("get")) {

				Player p = Bukkit.getPlayer(args[1]);

				if (p == null) {
					s.sendMessage(ChatColor.RED + "Can't find player " + args[0]);
					return true;
				}

				CurrencyManager currency = null;

				if (CurrencyManager.existCurrency(args[2]))
					currency = CurrencyManager.getCurrency(args[2]);
				else {
					s.sendMessage(ChatColor.RED + "Can't find currency " + args[2]);
					return true;
				}

				s.sendMessage(ChatColor.GREEN + args[2] + " -> " + args[1] + ": " + currency.getAccount(p.getUniqueId()));

				return true;
			}
		} else if (args.length == 4) {
			Player p = Bukkit.getPlayer(args[1]);

			if (p == null) {
				s.sendMessage(ChatColor.RED + "Can't find player " + args[0]);
				return true;
			}

			CurrencyManager currency = null;

			if (CurrencyManager.existCurrency(args[3]))
				currency = CurrencyManager.getCurrency(args[3]);
			else {
				s.sendMessage(ChatColor.RED + "Can't find currency " + args[3]);
				return true;
			}

			Double amount;

			try {
				amount = Double.parseDouble(args[2]);
			} catch (NumberFormatException e) {
				s.sendMessage(ChatColor.RED + "Amount must be a number!");
				return true;
			}

			Double balance;

			if (args[0].equalsIgnoreCase("set")) {
				balance = currency.setAccount(p.getUniqueId(), amount);
			} else if (args[0].equalsIgnoreCase("add")) {
				balance = currency.addAccount(p.getUniqueId(), amount);
			} else if (args[0].equalsIgnoreCase("remove")) {
				balance = currency.removeAccount(p.getUniqueId(), amount);
			} else
				return true;

			s.sendMessage(
					ChatColor.YELLOW + p.getName() + " balance's is " + balance);

			return true;
		}

		s.sendMessage(ChatColor.RED + "Command error! Please use /" + label
				+ " [set|add|delete|get|create|remove] [player|currency] [amount|start] {currency}");
		return false;
	}
}