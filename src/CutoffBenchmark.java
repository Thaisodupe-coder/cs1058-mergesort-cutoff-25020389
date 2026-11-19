import edu.princeton.cs.algs4.Out;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;
import java.io.File;

public class CutoffBenchmark {

    public static double timeTrial(Double[] template, int cutoff) {
        Double[] a = new Double[template.length];
        System.arraycopy(template, 0, a, 0, template.length);

        Stopwatch timer = new Stopwatch();
        MergeCutoff.sort(a, cutoff);
        double elapsed = timer.elapsedTime();

        assert MergeCutoff.isSorted(a) : "Loi: Mang chua duoc sap xep dung!";
        return elapsed;
    }

    /**
     * Chạy thực nghiệm với số lượng bộ dữ liệu (trials) và hạt giống (baseSeed) truyền vào.
     */
    public static void runExperiment(int[] sizes, String[] dataTypes, int[] cutoffs, int trials, long baseSeed) {
        // 1. JVM Warm-up (chạy làm nóng máy để JIT Compiler tối ưu bytecode)
        StdOut.printf("Khoi dong JVM Warm-up (trials = %d, baseSeed = %d)...\n", trials, baseSeed);
        Double[] warmup = DatasetGenerator.generate(100_000, "RANDOM", baseSeed);
        for (int i = 0; i < 25; i++) {
            timeTrial(warmup, 10);
        }

        // Tạo thư mục data/ nếu chưa tồn tại
        File csvFile = new File("data/ket_qua_thuc_nghiem_algs4.csv");
        if (csvFile.getParentFile() != null) {
            csvFile.getParentFile().mkdirs();
        }

        Out csv = new Out(csvFile.getPath());
        csv.println("DataType,N,Cutoff,Trials,BaseSeed,MeanTime_s,StdDev_s,MinTime_s,MaxTime_s");

        StdOut.printf("%-14s | %-9s | %-6s | %-11s | %-11s | %-11s\n",
                "DataType", "N", "CUTOFF", "Mean(s)", "StdDev(s)", "Min(s)");
        StdOut.println("-------------------------------------------------------------------------");

        // 2. Chạy thực nghiệm chính
        for (String type : dataTypes) {
            for (int n : sizes) {
                // Thu gom rác trước khi cấp phát khối dữ liệu mới
                System.gc();

                // Tạo sẵn 'trials' bộ dữ liệu độc lập bằng cách dịch chuyển seed: baseSeed + t
                Double[][] datasets = new Double[trials][];
                for (int t = 0; t < trials; t++) {
                    datasets[t] = DatasetGenerator.generate(n, type, baseSeed + t);
                }

                for (int c : cutoffs) {
                    double[] times = new double[trials];
                    double sum = 0.0;
                    double min = Double.POSITIVE_INFINITY;
                    double max = Double.NEGATIVE_INFINITY;

                    for (int t = 0; t < trials; t++) {
                        times[t] = timeTrial(datasets[t], c);
                        sum += times[t];
                        if (times[t] < min) min = times[t];
                        if (times[t] > max) max = times[t];
                    }

                    double mean = sum / trials;
                    double sqSum = 0.0;
                    for (double time : times) {
                        sqSum += (time - mean) * (time - mean);
                    }
                    double stdDev = (trials > 1) ? Math.sqrt(sqSum / (trials - 1)) : 0.0;

                    csv.printf("%s,%d,%d,%d,%d,%.6f,%.6f,%.6f,%.6f\n",
                            type, n, c, trials, baseSeed, mean, stdDev, min, max);
                    StdOut.printf("%-14s | %-9d | %-6d | %-11.6f | %-11.6f | %-11.6f\n",
                            type, n, c, mean, stdDev, min);
                }
                StdOut.println("-------------------------------------------------------------------------");
            }
        }
        csv.close();
        StdOut.println("Hoan tat! Du lieu chi tiet da luu tai: data/ket_qua_thuc_nghiem_algs4.csv");
    }

    public static void main(String[] args) {
        // Cố định baseSeed = 25020389, số lần lặp trials = 10 (đủ tin cậy và tối ưu thời gian)
        int trials = (args.length >= 1) ? Integer.parseInt(args[0]) : 10;
        long baseSeed = (args.length >= 2) ? Long.parseLong(args[1]) : 25020389;

        // Các kích thước mảng đại diện theo cấp số nhân (từ nhỏ đến lớn)
        int[] sizes = {125_000, 250_000, 500_000, 1_000_000};
        String[] dataTypes = {"RANDOM", "NEARLY_SORTED", "REVERSED", "FEW_UNIQUE"};

        // Dải CUTOFF
        int[] cutoffs = {1, 2, 4, 6, 7, 8, 9, 10, 12, 15, 20, 30, 40};

        runExperiment(sizes, dataTypes, cutoffs, trials, baseSeed);
    }
}
