import java.time.LocalDateTime;
import java.util.List;

class ExcessiveGroundTimeFilter implements FlightFilter {
    private static final long MAX_GROUND_TIME_HOURS = 2;

    @Override
    public boolean test(Flight flight) {
        List<Segment> segments = flight.getSegments();
        if (segments.size() <= 1) {
            return true;
        }

        long totalGroundTime = 0;
        for (int i = 0; i < segments.size() - 1; i++) {
            LocalDateTime currentArrival = segments.get(i).getArrivalDate();
            LocalDateTime nextDeparture = segments.get(i + 1).getDepartureDate();

            if (nextDeparture.isAfter(currentArrival)) {
                totalGroundTime += java.time.Duration.between(currentArrival, nextDeparture).toHours();
            }

            if (totalGroundTime > MAX_GROUND_TIME_HOURS) {
                return false;
            }
        }

        return true;
    }
}
