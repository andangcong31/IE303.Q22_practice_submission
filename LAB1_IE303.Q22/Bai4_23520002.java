import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class Bai4_23520002 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        if (!sc.hasNextInt()) return;
        int n = sc.nextInt();
        int k = sc.nextInt();

        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = sc.nextInt();
        }

        // dp[j] là số lượng phần tử tối đa để có tổng j
        int[] dp = new int[k + 1];
        Arrays.fill(dp, -1);
        dp[0] = 0;

        // parentIdx[i][j] lưu vết: tại phần tử thứ i, tổng j được tạo từ đâu
        int[] lastUsedIdx = new int[k + 1];
        Arrays.fill(lastUsedIdx, -1);
        
        // Dùng mảng để biết số nào tạo ra tổng nào
        int[][] parent = new int[n][k + 1];
        for (int[] row : parent) Arrays.fill(row, -1);

        for (int i = 0; i < n; i++) {
            int currentVal = a[i];
            // Duyệt ngược để đảm bảo mỗi phần tử i chỉ dùng 1 lần
            for (int j = k; j >= currentVal; j--) {
                if (dp[j - currentVal] != -1) {
                    // Nếu tìm thấy cách có nhiều phần tử hơn
                    if (dp[j - currentVal] + 1 > dp[j]) {
                        dp[j] = dp[j - currentVal] + 1;
                        parent[i][j] = j - currentVal; // Lưu tổng trước đó
                    }
                }
            }
        }

        if (dp[k] != -1) {
            List<Integer> result = new ArrayList<>();
            int currK = k;
            // Truy vết ngược từ phần tử cuối cùng về đầu
            for (int i = n - 1; i >= 0; i--) {
                if (parent[i][currK] != -1) {
                    result.add(a[i]);
                    currK = parent[i][currK];
                }
            }

            // In theo thứ tự xuất hiện trong mảng gốc
            for (int i = result.size() - 1; i >= 0; i--) {
                System.out.print(result.get(i) + (i == 0 ? "" : ", "));
            }
        } else {
            System.out.println("Không tìm thấy dãy con.");
        }

        sc.close();
    }
}