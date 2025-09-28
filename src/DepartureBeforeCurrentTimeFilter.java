import java.time.LocalDateTime;

class DepartureBeforeCurrentTimeFilter implements FlightFilter {
    @Override
    public boolean test(Flight flight) {
        LocalDateTime now = LocalDateTime.now();
        return flight.getSegments().stream()
                .allMatch(segment -> segment.getDepartureDate().isAfter(now));
    }
}
