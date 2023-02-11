package fr.isen.java2.db.daos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fr.isen.java2.db.entities.Film;
import fr.isen.java2.db.entities.Genre;

public class FilmDaoTestCase {

	private FilmDao filmDao = new FilmDao();

	@Before
	public void initDb() throws Exception {
		Connection connection = DataSourceFactory.getDataSource().getConnection();
		Statement stmt = connection.createStatement();
		stmt.executeUpdate(
				"CREATE TABLE IF NOT EXISTS genre (idgenre INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , name VARCHAR(50) NOT NULL);");
		stmt.executeUpdate("CREATE TABLE IF NOT EXISTS film (\r\n"
				+ "  idfilm INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\r\n"
				+ "  title VARCHAR(100) NOT NULL,\r\n" + "  release_date DATETIME NULL,\r\n"
				+ "  genre_id INT NOT NULL,\r\n" + "  duration INT NULL,\r\n"
				+ "  director VARCHAR(100) NOT NULL,\r\n" + "  summary MEDIUMTEXT NULL,\r\n"
				+ "  CONSTRAINT genre_fk FOREIGN KEY (genre_id) REFERENCES genre (idgenre));");
		stmt.executeUpdate("DELETE FROM film");
		stmt.executeUpdate("DELETE FROM genre");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (1,'Drama')");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (2,'Comedy')");
		stmt.executeUpdate(
				"INSERT INTO film(idfilm,title, release_date, genre_id, duration, director, summary) "
						+ "VALUES (1, 'Title 1', '2015-11-26 12:00:00.000', 1, 120, 'director 1', 'summary of the first film')");
		stmt.executeUpdate(
				"INSERT INTO film(idfilm,title, release_date, genre_id, duration, director, summary) "
						+ "VALUES (2, 'My Title 2', '2015-11-14 12:00:00.000', 2, 114, 'director 2', 'summary of the second film')");
		stmt.executeUpdate(
				"INSERT INTO film(idfilm,title, release_date, genre_id, duration, director, summary) "
						+ "VALUES (3, 'Third title', '2015-12-12 12:00:00.000', 2, 176, 'director 3', 'summary of the third film')");
		stmt.close();
		connection.close();
	}

	@Test
	public void shouldListFilms() {
		// GIVEN
		// WHEN
		List<Film> films = filmDao.listFilms();
		// THEN
		assertThat(films).hasSize(3);
		assertThat(films).extracting("id", "title", "duration", "director", "summary").containsOnly(
				tuple(1, "Title 1", 120, "director 1", "summary of the first film"),
				tuple(2, "My Title 2", 114, "director 2", "summary of the second film"),
				tuple(3, "Third title", 176, "director 3", "summary of the third film"));
		assertThat(films.stream().map(film -> {
			return film.getReleaseDate().toString();
		}).toList()).containsOnly("2015-11-26", "2015-11-14", "2015-12-12");
		assertThat(films.stream().map(film -> {
			return film.getGenre().getName();
		}).toList()).containsOnly("Drama", "Comedy");
	}

	@Test
	public void shouldListFilmsByGenre() {
		// GIVEN
		// WHEN
		List<Film> dramaFilms = filmDao.listFilmsByGenre("Drama");
		List<Film> comedyFilms = filmDao.listFilmsByGenre("Comedy");
		// THEN
		assertThat(dramaFilms).hasSize(1);
		assertThat(dramaFilms).extracting("id", "title", "duration", "director", "summary")
				.containsOnly(tuple(1, "Title 1", 120, "director 1", "summary of the first film"));
		assertThat(dramaFilms.stream().map(film -> {
			return film.getReleaseDate().toString();
		}).toList()).containsOnly("2015-11-26");
		assertThat(dramaFilms.stream().map(film -> {
			return film.getGenre().getName();
		}).toList()).containsOnly("Drama");

		assertThat(comedyFilms).hasSize(2);
		assertThat(comedyFilms).extracting("id", "title", "duration", "director", "summary")
				.containsOnly(
						tuple(2, "My Title 2", 114, "director 2", "summary of the second film"),
						tuple(3, "Third title", 176, "director 3", "summary of the third film"));
		assertThat(comedyFilms.stream().map(film -> {
			return film.getReleaseDate().toString();
		}).toList()).containsOnly("2015-11-14", "2015-12-12");
		assertThat(comedyFilms.stream().map(film -> {
			return film.getGenre().getName();
		}).toList()).containsOnly("Comedy");
	}

	@Test
	public void shouldAddFilm() throws Exception {
		// GIVEN
		Film film = new Film();
		film.setTitle("The Pursuit of Happyness");
		film.setReleaseDate(LocalDate.of(2007, 1, 31));
		film.setGenre(new Genre(1, "Drama"));
		film.setDuration(117);
		film.setDirector("Gabriele Muccino");
		film.setSummary(
				"Life is a struggle for single father Chris Gardner (Will Smith). Evicted from their apartment, he and his young son (Jaden Christopher Syre Smith) find themselves alone with no place to go. Even though Chris eventually lands a job as an intern at a prestigious brokerage firm, the position pays no money. The pair must live in shelters and endure many hardships, but Chris refuses to give in to despair as he struggles to create a better life for himself and his son.");
		// WHEN
		Film addedFilm1 = filmDao.addFilm(film);
		Film addedFilm2 = filmDao.addFilm(film);
		// THEN
		assertThat(addedFilm2.getId() - 1).isEqualTo(addedFilm1.getId());
		assertThat(film.getTitle() == addedFilm1.getTitle()).isTrue();
		assertThat(film.getReleaseDate() == addedFilm1.getReleaseDate()).isTrue();
		assertThat(film.getGenre() == addedFilm1.getGenre()).isTrue();
		assertThat(film.getDuration() == addedFilm1.getDuration()).isTrue();
		assertThat(film.getDirector() == addedFilm1.getDirector()).isTrue();
		assertThat(film.getSummary() == addedFilm1.getSummary()).isTrue();
	}
}
