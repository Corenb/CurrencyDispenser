package eu.horyzon.currencydispenser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CurrencyManager {
	public static Set<CurrencyManager> currencies = new HashSet<CurrencyManager>();

	private final String name;
	private final Double startCurrency;

	public CurrencyManager(String currency, Double startCurrency) {
		this.name = currency;
		this.startCurrency = startCurrency;
		currencies.add(this);
	}

	public String getName() {
		return name;
	}

	public boolean createAccount(UUID id) {
		return createAccount(id, startCurrency);
	}

	public boolean createAccount(UUID id, Double amount) {
		try {
			return CurrencyDispenser.getSQL().updateSQL("INSERT INTO Currency (UUID, Currency, Balance) VALUES ('" + id
					+ "', '" + name + "', '" + amount + "')") == 1;
		} catch (ClassNotFoundException | SQLException e) {
			return false;
		}
	}

	public boolean hasAccount(UUID id) {
		try {
			ResultSet rs = CurrencyDispenser.getSQL()
					.querySQL("SELECT * FROM Currency WHERE UUID='" + id + "' AND Currency='" + name + "'");
			return rs.next();
		} catch (SQLException | ClassNotFoundException e) {
		}
		return false;
	}

	public Double getAccount(UUID id) {
		if (hasAccount(id)) {
			try {
				ResultSet rs = CurrencyDispenser.getSQL()
						.querySQL("SELECT Balance FROM Currency WHERE UUID='" + id + "' AND Currency='" + name + "'");
				if (rs.next())
					return rs.getDouble("Balance");
			} catch (SQLException | ClassNotFoundException e) {
			}
		}
		return startCurrency;
	}

	public boolean hasEnough(UUID id, Double amount) {
		return getAccount(id) >= amount;
	}

	public Double setAccount(UUID id, Double amount) {
		if (hasAccount(id))
			try {
				CurrencyDispenser.getSQL().updateSQL("UPDATE Currency SET Balance='" + amount + "' WHERE UUID='" + id
						+ "' AND Currency='" + name + "'");
			} catch (ClassNotFoundException | SQLException e) {
				createAccount(id, amount);
			}
		else
			createAccount(id, amount);
		return amount;
	}

	public Double addAccount(UUID id, Double amount) {
		if (hasAccount(id)) {
			return setAccount(id, getAccount(id) + amount);
		} else
			createAccount(id, amount);
		return amount;
	}

	public Double removeAccount(UUID id, Double amount) {
		return setAccount(id, getAccount(id) - amount);
	}

	public Boolean deleteAccount(UUID id) {
		try {
			return CurrencyDispenser.getSQL()
					.updateSQL("DELETE FROM Currency WHERE UUID='" + id + "' AND Currency='" + name + "'") == 1;
		} catch (ClassNotFoundException | SQLException e) {
		}
		return false;
	}

	public static CurrencyManager registerCurrency(String currency) {
		return registerCurrency(currency, 0D);
	}

	public static CurrencyManager registerCurrency(String currency, Double start) {
		if (!CurrencyManager.existCurrency(currency)) {
			return new CurrencyManager(currency, start);
		} else
			return CurrencyManager.getCurrency(currency);
	}

	public static boolean existCurrency(String currency) {
		for (CurrencyManager currencies : currencies) {
			if (currencies.getName().equalsIgnoreCase(currency))
				return true;
		}
		return false;
	}

	public static CurrencyManager getCurrency(String currencyName) {
		for (CurrencyManager currency : currencies) {
			if (currency.getName().equalsIgnoreCase(currencyName))
				return currency;
		}
		return null;
	}

	public static CurrencyManager[] getAllCurrency() {
		return (CurrencyManager[]) currencies.toArray();
	}
}