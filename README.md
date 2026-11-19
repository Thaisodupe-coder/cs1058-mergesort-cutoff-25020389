# Nghiên Cứu Thực Nghiệm: CUTOFF Benchmark Cho Thuật Toán Merge Sort

Dự án thực nghiệm xác định giá trị ngưỡng **CUTOFF** tối ưu khi chuyển từ Merge Sort sang Insertion Sort đối với các mảng con kích thước nhỏ, dựa trên nội dung bài giảng môn Cấu trúc dữ liệu và Giải thuật (*Algorithms, 4th Edition* - Robert Sedgewick & Kevin Wayne).

---

## 1. Cấu Trúc Dự Án

```text
cutoff-benchmark-merge-sort-/
├── src/
│   ├── MergeCutoff.java          # Cài đặt Merge Sort kết hợp Insertion Sort (CUTOFF & Skip merge)
│   ├── DatasetGenerator.java     # Bộ sinh dữ liệu theo 4 phân phối với Random Seed cố định
│   └── CutoffBenchmark.java      # Script tự động chạy benchmark, warm-up và xuất CSV
├── lib/
│   └── algs4.jar                 # Thư viện thuật toán chuẩn của giáo trình
├── data/
│   ├── ket_qua_thuc_nghiem_algs4.csv  # Kết quả đo đạc thực tế (Mean, StdDev, Min, Max)
│   └── *.txt                     # Các file mẫu dữ liệu đại diện
├── scripts/
│   └── plot_results.py           # Script Python vẽ 3 biểu đồ chuẩn kỹ thuật
├── results/
│   └── charts/                   # Thư mục chứa hình ảnh biểu đồ kết quả
├── Baocao.docx                   # Báo cáo thí nghiệm chi tiết (Word / PDF)
└── README.md                     # Tài liệu hướng dẫn duy nhất của dự án
```

---

## 2. Thiết Kế Thí Nghiệm & Bộ Dữ Liệu

* **Biến độc lập (CUTOFF):** Quét mịn quanh vùng ứng viên và mở rộng: $\{1, 2, 4, 6, 7, 8, 9, 10, 12, 15, 20, 30, 40\}$.
* **Kích thước mảng ($N$):** $125.000$, $250.000$, $500.000$, $1.000.000$.
* **4 Loại phân phối dữ liệu:**
  1. `RANDOM`: Phân phối đều ngẫu nhiên $U(0, 1)$.
  2. `NEARLY_SORTED`: Mảng có thứ tự bị hoán vị ngẫu nhiên 1% phần tử.
  3. `REVERSED`: Mảng giảm dần hoàn toàn (trường hợp xấu nhất của Insertion Sort).
  4. `FEW_UNIQUE`: Mảng chỉ chứa 10 giá trị phân biệt $\{0, 1, ..., 9\}$.
* **Tính tái lập (Deterministic Seed):** Cố định `baseSeed = 25020389`. Dữ liệu sinh ra trên bộ nhớ RAM hoàn toàn đồng nhất $100\%$ giữa các lần chạy và trên mọi máy tính khác nhau.
* **Chính sách dữ liệu nộp bài:** Để tối ưu dung lượng nộp bài (tránh file phình to hàng trăm Megabytes gây lỗi upload lên hệ thống LMS/Git), benchmark chạy trực tiếp dữ liệu trên RAM. Thư mục `data/` lưu trữ file kết quả đo thô `ket_qua_thuc_nghiem_algs4.csv` và các file mẫu đại diện.
* **Kiểm soát sai số:** Có 25 lượt chạy JVM Warm-up trước khi đo, mỗi cấu hình đo 10 lần lặp độc lập (`trials = 10`) trên cùng bản sao mảng (`System.arraycopy`).

---

## 3. Hướng Dẫn Biên Dịch & Chạy Thí Nghiệm

### Bước 1: Biên dịch mã nguồn Java
```bash
javac -cp ".;lib/algs4.jar" src/*.java
```

### Bước 2: Chạy thí nghiệm Benchmark
Lệnh này sẽ tự động chạy đo lường và cập nhật kết quả vào `data/ket_qua_thuc_nghiem_algs4.csv`:
```bash
java -cp "src;lib/algs4.jar" CutoffBenchmark
```
*(Tùy chọn truyền tham số dòng lệnh: `java -cp "src;lib/algs4.jar" CutoffBenchmark [trials] [baseSeed]`)*

### Bước 3: Vẽ biểu đồ kết quả (Tùy chọn)
Yêu cầu Python đã cài `pandas` và `matplotlib`:
```bash
pip install pandas matplotlib
python scripts/plot_results.py
```
Biểu đồ sẽ được tự động xuất ra thư mục `results/charts/`.

---

## 4. Tóm Tắt Kết Quả Chính

* **CUTOFF tối ưu trên máy thử nghiệm:** Nằm trong khoảng **$8 - 12$** (với $N = 1.000.000$, $CUTOFF = 12$ đạt thời gian nhanh nhất: $0.6227$ s).
* **So sánh CUTOFF = 7 vs CUTOFF = 10:** 
  * $CUTOFF = 10$ ($0.6378$ s) nhanh hơn và ổn định hơn $CUTOFF = 7$ ($0.8070$ s) khoảng **$20.9\%$** trên dữ liệu ngẫu nhiên $N = 1.000.000$.
  * Phản ánh đúng sự cải tiến của giáo trình mới, phù hợp với kiến trúc CPU hiện đại có Cache L1/L2 dung lượng lớn.
* **Đặc tính dữ liệu:** Dữ liệu `NEARLY_SORTED` tận dụng tối đa lợi thế của Insertion Sort nên CUTOFF lớn ($30 - 40$) chạy rất nhanh; ngược lại dữ liệu `REVERSED` bị giảm hiệu năng khi CUTOFF quá lớn do độ phức tạp $O(N^2)$.
