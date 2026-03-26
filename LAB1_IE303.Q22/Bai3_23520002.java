import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

class Point implements Comparable<Point> {
    long x, y;

    Point() {}

    Point(long x, long y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int compareTo(Point other) {
        if (this.x != other.x) {
            return Long.compare(this.x, other.x);
        }
        return Long.compare(this.y, other.y);
    }
}

class Vector2D {
    long x, y;

    Vector2D() {}

    Vector2D(long x, long y) {
        this.x = x;
        this.y = y;
    }

    long crossProduct(Vector2D other) {
        return x * other.y - y * other.x;
    }
}

public class Bai3_23520002 {
    static Vector2D subtract(Point a, Point b) {
        return new Vector2D(a.x - b.x, a.y - b.y);
    }

    // Xác định hướng A -> B -> C
    static int orientation(Point a, Point b, Point c) {
        Vector2D x = subtract(b, a);
        Vector2D y = subtract(c, b);

        long orient = x.crossProduct(y);

        if (orient < 0) return -1;
        if (orient == 0) return 0;
        return 1;
    }

    static boolean ccw(Point a, Point b, Point c, boolean includeCollinear) {
        int orient = orientation(a, b, c);
        return orient > 0 || (orient == 0 && includeCollinear);
    }

    static boolean cw(Point a, Point b, Point c, boolean includeCollinear) {
        int orient = orientation(a, b, c);
        return orient < 0 || (orient == 0 && includeCollinear);
    }

    static List<Point> convexHull(int n, List<Point> p, boolean includeCollinear) {

        Collections.sort(p.subList(1, p.size()));

        Point first = p.get(1);
        Point last = p.get(n);

        List<Point> up = new ArrayList<>();
        List<Point> down = new ArrayList<>();

        up.add(first);
        down.add(first);

        for (int i = 2; i <= n; i++) {

            if (i == n || cw(first, p.get(i), last, includeCollinear)) {

                while (up.size() >= 2 &&
                        !cw(up.get(up.size() - 2), up.get(up.size() - 1), p.get(i), includeCollinear)) {
                    up.remove(up.size() - 1);
                }

                up.add(p.get(i));
            }

            if (i == n || ccw(first, p.get(i), last, includeCollinear)) {

                while (down.size() >= 2 &&
                        !ccw(down.get(down.size() - 2), down.get(down.size() - 1), p.get(i), includeCollinear)) {
                    down.remove(down.size() - 1);
                }

                down.add(p.get(i));
            }
        }

        List<Point> hull = new ArrayList<>();

        for (Point point : up) {
            hull.add(point);
        }

        for (int i = down.size() - 2; i >= 1; i--) {
            hull.add(down.get(i));
        }

        return hull;
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Nhap so luong tram: ");
        int n = sc.nextInt();

        List<Point> p = new ArrayList<>();

        p.add(new Point()); // dummy để index bắt đầu từ 1

        System.out.println("Nhap toa do cho tung tram (x y):");

        for (int i = 1; i <= n; i++) {
            System.out.print("Tram " + i + ": ");
            long x = sc.nextLong();
            long y = sc.nextLong();
            p.add(new Point(x, y));
        }

        List<Point> hull = convexHull(n, p, false);

        System.out.println("Toa do tram canh bao:");

        for (Point pt : hull) {
            System.out.println(pt.x + " " + pt.y);
        }

        sc.close();
    }
}
