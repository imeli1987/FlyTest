import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FlightFilterService {
    private final List<FlightFilter> filters = new ArrayList<>();

    public void addFilter(FlightFilter filter) {
        filters.add(filter);
    }

    public List<Flight> filterFlights(List<Flight> flights) {
        return flights.stream()
                .filter(flight -> filters.stream().allMatch(filter -> filter.test(flight)))
                .collect(Collectors.toList());
    }

    public List<Flight> filterFlights(List<Flight> flights, FlightFilter customFilter) {
        return flights.stream()
                .filter(customFilter::test)
                .collect(Collectors.toList());
    }
}
