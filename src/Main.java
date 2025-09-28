import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Flight> flights = FlightBuilder.createFlights();

        System.out.println("Все перелёты:");
        flights.forEach(System.out::println);
        System.out.println();

        // Фильтр 1: Исключить вылеты до текущего момента времени
        System.out.println("1. Перелёты после исключения вылетов до текущего момента:");
        FlightFilterService service1 = new FlightFilterService();
        service1.addFilter(new DepartureBeforeCurrentTimeFilter());
        service1.filterFlights(flights).forEach(System.out::println);
        System.out.println();

        // Фильтр 2: Исключить сегменты с датой прилёта раньше даты вылета
        System.out.println("2. Перелёты после исключения сегментов с датой прилёта раньше даты вылета:");
        FlightFilterService service2 = new FlightFilterService();
        service2.addFilter(new ArrivalBeforeDepartureFilter());
        service2.filterFlights(flights).forEach(System.out::println);
        System.out.println();

        // Фильтр 3: Исключить перелеты, где общее время, проведённое на земле, превышает два часа
        System.out.println("3. Перелеты, где общее время, проведённое на земле, не превышает два часа:");
        FlightFilterService service3 = new FlightFilterService();
        service3.addFilter(new ExcessiveGroundTimeFilter());
        service3.filterFlights(flights).forEach(System.out::println);
        System.out.println();
    }
}