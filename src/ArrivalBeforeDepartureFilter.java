class ArrivalBeforeDepartureFilter implements FlightFilter {
    @Override
    public boolean test(Flight flight) {
        return flight.getSegments().stream()
                .allMatch(segment -> segment.getArrivalDate().isAfter(segment.getDepartureDate()));
    }
}
