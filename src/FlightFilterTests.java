import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class FlightFilterTests {

    @Test
    void testDepartureBeforeCurrentTimeFilter() {

        FlightFilter filter = new DepartureBeforeCurrentTimeFilter();
        List<Flight> flights = FlightBuilder.createFlights();

        List<Flight> filteredFlights = flights.stream()
                .filter(filter::test)
                .toList();

        assertEquals(flights.size() - 1, filteredFlights.size());

        LocalDateTime now = LocalDateTime.now();
        for (Flight flight : filteredFlights) {
            for (Segment segment : flight.getSegments()) {
                assertTrue(segment.getDepartureDate().isAfter(now));
            }
        }
    }

    @Test
    void testArrivalBeforeDepartureFilter() {

        FlightFilter filter = new ArrivalBeforeDepartureFilter();
        List<Flight> flights = FlightBuilder.createFlights();

        List<Flight> filteredFlights = flights.stream()
                .filter(filter::test)
                .toList();

        assertEquals(flights.size() - 1, filteredFlights.size());

        for (Flight flight : filteredFlights) {
            for (Segment segment : flight.getSegments()) {
                assertTrue(segment.getArrivalDate().isAfter(segment.getDepartureDate()));
            }
        }
    }

    @Test
    void testExcessiveGroundTimeFilter() {

        FlightFilter filter = new ExcessiveGroundTimeFilter();
        List<Flight> flights = FlightBuilder.createFlights();

        List<Flight> filteredFlights = flights.stream()
                .filter(filter::test)
                .toList();

        assertTrue(filteredFlights.size() <= flights.size() - 2);

        for (Flight flight : filteredFlights) {
            List<Segment> segments = flight.getSegments();
            if (segments.size() > 1) {
                long totalGroundTime = 0;
                for (int i = 0; i < segments.size() - 1; i++) {
                    LocalDateTime currentArrival = segments.get(i).getArrivalDate();
                    LocalDateTime nextDeparture = segments.get(i + 1).getDepartureDate();

                    if (nextDeparture.isAfter(currentArrival)) {
                        totalGroundTime += java.time.Duration.between(currentArrival, nextDeparture).toHours();
                    }
                }
                assertTrue(totalGroundTime <= 2, "Ground time should be <= 2 hours");
            }
        }
    }

    @Test
    void testFlightFilterServiceWithSingleFilter() {

        FlightFilterService service = new FlightFilterService();
        service.addFilter(new ArrivalBeforeDepartureFilter());
        List<Flight> flights = FlightBuilder.createFlights();

        List<Flight> filteredFlights = service.filterFlights(flights);

        assertFalse(filteredFlights.isEmpty());
        assertEquals(flights.size() - 1, filteredFlights.size());
    }

    @Test
    void testFlightFilterServiceWithMultipleFilters() {

        FlightFilterService service = new FlightFilterService();
        service.addFilter(new DepartureBeforeCurrentTimeFilter());
        service.addFilter(new ArrivalBeforeDepartureFilter());
        service.addFilter(new ExcessiveGroundTimeFilter());

        List<Flight> flights = FlightBuilder.createFlights();

        List<Flight> filteredFlights = service.filterFlights(flights);

        LocalDateTime now = LocalDateTime.now();
        for (Flight flight : filteredFlights) {

            for (Segment segment : flight.getSegments()) {
                assertTrue(segment.getDepartureDate().isAfter(now));
            }

            for (Segment segment : flight.getSegments()) {
                assertTrue(segment.getArrivalDate().isAfter(segment.getDepartureDate()));
            }

            List<Segment> segments = flight.getSegments();
            if (segments.size() > 1) {
                long totalGroundTime = 0;
                for (int i = 0; i < segments.size() - 1; i++) {
                    LocalDateTime currentArrival = segments.get(i).getArrivalDate();
                    LocalDateTime nextDeparture = segments.get(i + 1).getDepartureDate();

                    if (nextDeparture.isAfter(currentArrival)) {
                        totalGroundTime += java.time.Duration.between(currentArrival, nextDeparture).toHours();
                    }
                }
                assertTrue(totalGroundTime <= 2);
            }
        }
    }

    @Test
    void testFlightFilterServiceWithCustomFilter() {

        FlightFilterService service = new FlightFilterService();
        List<Flight> flights = FlightBuilder.createFlights();

        FlightFilter twoSegmentsFilter = flight -> flight.getSegments().size() == 2;

        List<Flight> filteredFlights = service.filterFlights(flights, twoSegmentsFilter);

        for (Flight flight : filteredFlights) {
            assertEquals(2, flight.getSegments().size());
        }
    }

    @Test
    void testSingleSegmentFlightWithExcessiveGroundTimeFilter() {

        FlightFilter filter = new ExcessiveGroundTimeFilter();
        LocalDateTime now = LocalDateTime.now();

        Flight singleSegmentFlight = new Flight(List.of(
                new Segment(now.plusHours(1), now.plusHours(3))
        ));

        boolean result = filter.test(singleSegmentFlight);

        assertTrue(result, "Single segment flights should always pass ground time filter");
    }

    @Test
    void testSegmentCreationWithNullDates() {

        assertThrows(NullPointerException.class, () -> {
            new Segment(null, LocalDateTime.now());
        });

        assertThrows(NullPointerException.class, () -> {
            new Segment(LocalDateTime.now(), null);
        });
    }

    @Test
    void testFlightBuilderWithInvalidArguments() {

        assertThrows(IllegalArgumentException.class, () -> {
            FlightBuilder.createFlight(LocalDateTime.now());
        });
    }
}