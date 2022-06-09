import java.util.List;

public class Main {
    public static void main(String[] args) {
        String token = "u54dTcCVZsd2NQ1a2BCJmGOYarlxqwzLrAxcCum6";

        NasaCollector nasaCollector = new NasaCollector(token);

        List<Nasa> nasaList = nasaCollector.collectInfo();
        System.out.println(nasaList.size());
        //nasaList.forEach(System.out::println);
    }
}
