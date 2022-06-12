public class Main {
    public static void main(String[] args) {
        String token = "u54dTcCVZsd2NQ1a2BCJmGOYarlxqwzLrAxcCum6";

        NasaCollector nasaCollector = new NasaCollector(token);

        Nasa nasa = nasaCollector.getNasaObject();
        nasaCollector.saveImg(nasa, true);

        System.out.println(nasa.toString());
    }
}
