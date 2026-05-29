package com.jsp.book.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.jsp.book.entity.Movie;
import com.jsp.book.entity.Screen;
import com.jsp.book.entity.Seat;
import com.jsp.book.entity.Show;
import com.jsp.book.entity.ShowSeat;
import com.jsp.book.entity.Theater;
import com.jsp.book.repository.MovieRepository;
import com.jsp.book.repository.ScreenRepository;
import com.jsp.book.repository.SeatRepository;
import com.jsp.book.repository.ShowRepository;
import com.jsp.book.repository.TheaterRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

	private final TheaterRepository theaterRepository;
	private final ScreenRepository screenRepository;
	private final SeatRepository seatRepository;
	private final MovieRepository movieRepository;
	private final ShowRepository showRepository;

	@Override
	public void run(String... args) throws Exception {
		// Only run if there are movies but NO shows
		if (movieRepository.count() > 0 && showRepository.count() == 0) {
			System.out.println("No shows found! Generating dummy theater and shows for testing...");

			// 1. Create Theater
			Theater theater = new Theater();
			theater.setName("Test Theater");
			theater.setAddress("123 Test Ave");
			theater.setLocationLink("https://maps.google.com");
			theater.setImageLocation("https://placehold.co/600x400/EEE/31343C");
			theater.setScreenCount(1);
			theater = theaterRepository.save(theater);

			// 2. Create Screen
			Screen screen = new Screen();
			screen.setName("Screen 1");
			screen.setType("IMAX 3D");
			screen.setTheater(theater);
			screen = screenRepository.save(screen);

			// 3. Create Seats (50 seats: A-E, 1-10)
			List<Seat> seats = new ArrayList<>();
			char[] rows = {'A', 'B', 'C', 'D', 'E'};
			for (char rowName : rows) {
				String category = (rowName == 'E') ? "VIP" : (rowName == 'C' || rowName == 'D') ? "Premium" : "Standard";
				for (int i = 1; i <= 10; i++) {
					Seat seat = new Seat();
					seat.setScreen(screen);
					seat.setSeatRow(String.valueOf(rowName));
					seat.setSeatColumn(i);
					seat.setSeatNumber(rowName + String.valueOf(i));
					seat.setCategory(category.toUpperCase());
					seats.add(seat);
				}
			}
			seatRepository.saveAll(seats);

			// 4. Create Shows for all movies
			List<Movie> movies = movieRepository.findAll();
			for (Movie movie : movies) {
				Show show = new Show();
				show.setMovie(movie);
				show.setScreen(screen);
				show.setShowDate(LocalDate.now());
				show.setStartTime(LocalTime.of(18, 0));
				
				show.setEndTime(show.getStartTime().plusHours(movie.getDuration().getHour())
						.plusMinutes(movie.getDuration().getMinute() + 30));
				show.setTicketPrice(250.0);

				// Create ShowSeats
				List<ShowSeat> showSeats = new ArrayList<>();
				for (Seat s : seats) {
					ShowSeat showSeat = new ShowSeat();
					showSeat.setSeat(s);
					showSeat.setBooked(false);
					showSeats.add(showSeat);
				}
				show.setSeats(showSeats);
				
				showRepository.save(show);
			}
			
			System.out.println("Successfully generated test shows! Booking will now work.");
		}
	}
}
