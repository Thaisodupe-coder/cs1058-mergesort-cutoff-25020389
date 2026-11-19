import edu.princeton.cs.algs4.Out;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import java.io.File;

public class DatasetGenerator {

    private DatasetGenerator() { }

    /**
     * Sinh mảng Double[] nhất quán (deterministic) trên mọi máy thông qua seed cố định.
     *
     * @param n    Kích thước mảng
     * @param type Phân phối dữ liệu: RANDOM, NEARLY_SORTED, REVERSED, FEW_UNIQUE
     * @param seed Hạt giống khởi tạo PRNG
     * @return Mảng Double[] đã khởi tạo
     */
    public static Double[] generate(int n, String type, long seed) {
        StdRandom.setSeed(seed);
        Double[] a = new Double[n];

        switch (type) {
            case "RANDOM":
                for (int i = 0; i < n; i++) {
                    a[i] = StdRandom.uniformDouble(0.0, 1.0);
                }
                break;
            case "NEARLY_SORTED":
                for (int i = 0; i < n; i++) {
                    a[i] = (double) i / n;
                }
                int swaps = Math.max(1, n / 100); // Hoán vị ngẫu nhiên 1% phần tử
                for (int i = 0; i < swaps; i++) {
                    int idx1 = StdRandom.uniformInt(n);
                    int idx2 = StdRandom.uniformInt(n);
                    Double tmp = a[idx1];
                    a[idx1] = a[idx2];
                    a[idx2] = tmp;
                }
                break;
            case "REVERSED":
                for (int i = 0; i < n; i++) {
                    a[i] = (double) (n - i);
                }
                break;
            case "FEW_UNIQUE":
                for (int i = 0; i < n; i++) {
                    a[i] = (double) StdRandom.uniformInt(10);
                }
                break;

            default:
                throw new IllegalArgumentException("Loai du lieu khong ho tro: " + type);
        }
        return a;
    }
}
