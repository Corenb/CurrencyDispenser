package eu.horyzon.currencydispenser;

import java.sql.SQLException;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.huskehhh.mysql.Database;
import com.huskehhh.mysql.mysql.MySQL;

import net.md_5.bungee.api.ChatColor;

public class CurrencyDispenser extends JavaPlugin {
	private static CurrencyDispenser instance;
	private static CurrencyManager vault;
	private static Database sql;

	public void onEnable() {
		instance = this;
		saveDefaultConfig();

		FileConfiguration config = getConfig();
		Logger log = getLogger();

		// INITIALIZE MySQL
		if (config.getBoolean("mysql.enable")) {
			String host = config.getString("mysql.host");
			String port = config.getString("mysql.port");
			String database = config.getString("mysql.database");
			String username = config.getString("mysql.username");
			String password = config.getString("mysql.password");
			sql = new MySQL(host, port, database, username, password);
		} else {
			log.warning("Please configure mysql in config.");
			setEnabled(false);
		}

		// INITIALIZE CONFIGURATION
		{
			String currency = config.getString("default.name");
			Double start = config.getDouble("default.start");

			vault = new CurrencyManager(currency, start);
		}

		// INITIALIZE CONNECTION
		try {
			sql.openConnection();

			sql.updateSQL(
					"CREATE TABLE IF NOT EXISTS Currency (UUID CHARACTER(36) NOT NULL, Currency VARCHAR(26) NOT NULL, Balance DECIMAL(16,2));");

			log.info(ChatColor.GREEN + "Connection to the database etablished!");
		} catch (ClassNotFoundException | SQLException e) {
			log.warning("Can't connect to database.");
			setEnabled(false);
		}

		// INITIALIZE COMMANDS
		getCommand("currencydispenser").setExecutor(new CommandCurrency());
	}

	public static CurrencyDispenser getInstance() {
		return instance;
	}

	public CurrencyManager getCurrency() {
		return vault;
	}

	public static Database getSQL() {
		return sql;
	}
}