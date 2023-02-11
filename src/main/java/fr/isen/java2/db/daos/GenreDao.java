package fr.isen.java2.db.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.isen.java2.db.entities.Genre;

public class GenreDao {

	/**
	 * Get all the genres.
	 * 
	 * @return Every genre in the database.
	 */
	public List<Genre> listGenres() {
		List<Genre> genres = new ArrayList<>();
		try (Connection connection = DataSourceFactory.getDataSource().getConnection()) {
			try (Statement statement = connection.createStatement()) {
				try (ResultSet result = statement.executeQuery("SELECT * FROM genre")) {
					while (result.next()) {
						Genre genre = new Genre();
						genre.setId(result.getInt("idgenre"));
						genre.setName(result.getString("name"));
						genres.add(genre);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return genres;
	}

	/**
	 * Get genre.
	 * 
	 * @param name Name of the genre.
	 * @return The genre object with the given name or null if it's not found.
	 */
	public Genre getGenre(String name) {
		Genre genre = null;
		try (Connection connection = DataSourceFactory.getDataSource().getConnection()) {
			try (PreparedStatement statement =
					connection.prepareStatement("SELECT * FROM genre WHERE name = ?")) {
				statement.setString(1, name);
				try (ResultSet result = statement.executeQuery()) {
					if (result.next()) {
						genre = new Genre();
						genre.setId(result.getInt("idgenre"));
						genre.setName(result.getString("name"));
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return genre;
	}

	/**
	 * Add genre.
	 * 
	 * @param name Name of the genre.
	 */
	public void addGenre(String name) {
		try (Connection connection = DataSourceFactory.getDataSource().getConnection()) {
			try (PreparedStatement statement =
					connection.prepareStatement("INSERT INTO genre(name) VALUES(?)")) {
				statement.setString(1, name);
				statement.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
