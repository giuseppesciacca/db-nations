package org.java.lessons;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		final String url = "jdbc:mysql://localhost:3306/db_nation";
		final String user = "root";
		final String password = "root";

		Scanner sc = new Scanner(System.in);
		System.out.print("Search: ");
		String filt = sc.nextLine();

		try (Connection con = DriverManager.getConnection(url, user, password)) {
			getCountryWithFilter(con, filt);

			System.out.print("Choose a country id: ");
			String idString = sc.nextLine();
			int idCountry = Integer.parseInt(idString);

			getLanguagesOfACountry(con, idCountry);

			getStatsAboutACountry(con, idCountry);

			sc.close();
		} catch (Exception e) {
			System.out.println("Errore di connessione: " + e.getMessage());
		}
	}

	private static final void getCountryWithFilter(Connection con, String filter) {

		final String sql = "SELECT c.name AS country_name, r.name AS region_name, r.region_id, c2.name AS continent_name\r\n"
				+ "FROM countries c \r\n" + "LEFT JOIN regions r ON r.region_id  = c.region_id \r\n"
				+ "LEFT JOIN continents c2 ON c2.continent_id = r.continent_id \r\n" + "WHERE c.name LIKE ?;";

		try {

			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, "%" + filter + "%");

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				String countryName = rs.getString("country_name");
				String regionName = rs.getString("region_name");
				int regionId = rs.getInt("region_id");
				String continentName = rs.getString("continent_name");

				System.out.println("Country_name: " + countryName + " |Region_name: " + regionName + " |Region_id: "
						+ regionId + " |Continent_name: " + continentName + "\n");
			}
		} catch (Exception e) {

			System.out.println(e.getMessage());
		}
	}

	private static final void getLanguagesOfACountry(Connection con, int idCountry) {

		final String sql = "SELECT c.name AS country_name, l.`language` \r\n" + "FROM countries c \r\n"
				+ "LEFT JOIN country_languages cl ON cl.country_id = c.country_id\r\n"
				+ "LEFT JOIN languages l ON l.language_id = cl.language_id  \r\n" + "WHERE c.country_id = ?;";

		try {

			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, idCountry);

			ResultSet rs = ps.executeQuery();

			String country = "";
			String languages = "";
			boolean isFirstLanguage = true;

			while (rs.next()) {

				String language = rs.getString("language");

			    if (isFirstLanguage) {
			    	//check if is the first language, if true don't add the comma;
			        languages += language;
			        isFirstLanguage = false;
			    } else {
			    	//if false, add the comma before the language;
			        languages += ", " + language;
			    }

				String countryName = rs.getString("country_name");
				country = countryName;
			}
			System.out.println("\nDetails for country: " + country);
			System.out.println("Languages: " + languages);

		} catch (Exception e) {

			System.out.println(e.getMessage());
		}
	}

	private static final void getStatsAboutACountry(Connection con, int idCountry) {

		final String sql = "SELECT c.name AS name_country, cs.population, cs.`year` AS year_stat, cs.gdp \r\n"
				+ "FROM countries c \r\n" + "LEFT JOIN country_stats cs ON cs.country_id = c.country_id \r\n"
				+ "WHERE c.country_id = ? \r\n" + "ORDER BY YEAR DESC\r\n" + "LIMIT 1;";

		try {

			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, idCountry);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				String population = rs.getString("population");
				int yearStat = rs.getInt("year_stat");
				long gdp = rs.getLong("gdp");

				System.out.println("Most recent stats");
				System.out.println("Year: " + yearStat);
				System.out.println("Population: " + population);
				System.out.println("GDP: " + gdp + "\n");
			}

		} catch (Exception e) {

			System.out.println(e.getMessage());
		}
	}
}
