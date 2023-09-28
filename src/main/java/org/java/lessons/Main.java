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
		System.out.print("Inserisci lo stato che vuoi filtrare: ");
		String filt = sc.nextLine();
		sc.close();

		try (Connection con = DriverManager.getConnection(url, user, password)) {
			System.out.println("\nConnessione stabilita correttamente\n");
			getCountry(con, filt);
		} catch (Exception e) {

			System.out.println("Errore di connessione: " + e.getMessage());
		}

		System.out.println("\n----------------------------------\n");
		System.out.println("The end");

	}

	private static final void getCountry(Connection con, String filter) {

		final String sql = "SELECT c.name AS country_name, r.name AS region_name, r.region_id, c2.name AS continent_name\r\n"
				+ "FROM countries c \r\n"
				+ "LEFT JOIN regions r ON r.region_id  = c.region_id \r\n"
				+ "LEFT JOIN continents c2 ON c2.continent_id = r.continent_id \r\n"
				+ "WHERE c.name LIKE ?";

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

}
