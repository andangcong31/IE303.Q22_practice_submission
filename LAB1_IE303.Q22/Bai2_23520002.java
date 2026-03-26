import java.util.Random;

public class Bai2_23520002 {
    public static double PI_xapxi(){
        int tongdiem = 10000000;
        int diemtrong = 0;
        Random rand = new Random();
        for (int i = 0; i < tongdiem; i++) {
            double x = rand.nextDouble() * 2 - 1;
            double y = rand.nextDouble() * 2 - 1;
            if (x * x + y * y <= 1) {
                diemtrong++;
            }
        }
        return 4.0 * diemtrong / tongdiem;
    }

    public static void main(String[] args) {
        System.out.println("Pi xap xi la: " + PI_xapxi());
    }
}