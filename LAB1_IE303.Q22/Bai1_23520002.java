import java.util.Random;
import java.util.Scanner;

public class Bai1_23520002 {
    public static double Dientichxapxi(double r) {
        int tongdiem = 1000000;
        int diemtrong = 0;
        Random rand = new Random();
        for (int i = 0; i < tongdiem; i++) {
            double x = rand.nextDouble() * 2 * r - r;
            double y = rand.nextDouble() * 2 * r - r;
            if (x * x + y * y <= r * r) {
                diemtrong++;
            }
        }
        return 4 * r * r * (double) diemtrong / tongdiem;
    }

    public static void main(String[] args) {
        Scanner Scanner = new Scanner(System.in);
        System.out.print("Nhap ban kinh hinh tron: ");
        double r = Scanner.nextDouble();
        System.out.println("Dien tich hinh tron xap xi la: " + Dientichxapxi(r));
        Scanner.close();
    }
}