package fr.isen.java2.db.daos;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.isen.java2.db.entities.Film;
import fr.isen.java2.db.entities.Genre;

public class FilmDao {

	/**
	 * Get every film.
	 * 
	 * @return All the films in the database.
	 */
	public List<Film> listFilms() {
		List<Film> films = new ArrayList<>();
		try (Connection connection = DataSourceFactory.getDataSource().getConnection()) {
			try (Statement statement = connection.createStatement()) {
				try (ResultSet result = statement.executeQuery(
						"SELECT * FROM film JOIN genre ON film.genre_id = genre.idgenre")) {
					while (result.next()) {
						Genre genre = new Genre();
						genre.setId(result.getInt("idgenre"));
						genre.setName(result.getString("name"));

						Film film = new Film();
						film.setId(result.getInt("idfilm"));
						film.setTitle(result.getString("title"));
						film.setReleaseDate(result.getDate("release_date").toLocalDate());
						film.setGenre(genre);
						film.setDuration(result.getInt("duration"));
						film.setDirector(result.getString("director"));
						film.setSummary(result.getString("summary"));
						films.add(film);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return films;
	}

	/**
	 * Get films of the given genre.
	 * 
	 * @param genreName A film genre.
	 * @return Every film in the database with the given genre.
	 */
	public List<Film> listFilmsByGenre(String genreName) {
		List<Film> films = new ArrayList<>();
		try (Connection connection = DataSourceFactory.getDataSource().getConnection()) {
			try (PreparedStatement statement = connection.prepareStatement(
					"SELECT * FROM film JOIN genre ON film.genre_id = genre.idgenre WHERE genre.name = ?")) {
				statement.setString(1, genreName);
				try (ResultSet result = statement.executeQuery()) {
					while (result.next()) {
						Genre genre = new Genre();
						genre.setId(result.getInt("idgenre"));
						genre.setName(result.getString("name"));

						Film film = new Film();
						film.setId(result.getInt("idfilm"));
						film.setTitle(result.getString("title"));
						film.setReleaseDate(result.getDate("release_date").toLocalDate());
						film.setGenre(genre);
						film.setDuration(result.getInt("duration"));
						film.setDirector(result.getString("director"));
						film.setSummary(result.getString("summary"));
						films.add(film);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return films;
	}

	/**
	 * Add film.
	 * 
	 * @param film Film to be added to the database.
	 * @return A film object whose properties are the same as the given film except its id which is
	 *         the auto generated value from the database.
	 */
	public Film addFilm(Film film) {
		Film addedFilm = new Film(film);
		try (Connection connection = DataSourceFactory.getDataSource().getConnection()) {
			try (PreparedStatement statement = connection.prepareStatement(
					"INSERT INTO film(title,release_date,genre_id,duration,director,summary) VALUES(?,?,?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS)) {
				statement.setString(1, film.getTitle());
				statement.setDate(2, Date.valueOf(film.getReleaseDate()));
				statement.setInt(3, film.getGenre().getId());
				statement.setInt(4, film.getDuration());
				statement.setString(5, film.getDirector());
				statement.setString(6, film.getSummary());
				statement.executeUpdate();
				ResultSet resultSet = statement.getGeneratedKeys();
				addedFilm.setId(resultSet.getInt(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return addedFilm;
	}
}
