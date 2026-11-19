# Nghiên cứu thực nghiệm: CUTOFF Benchmark cho Merge Sort

## 1. Tổng quan dự án
Dự án này thực hiện **benchmark** để xác định giá trị ngưỡng `CUTOFF` tối ưu khi chuyển từ **Merge Sort** sang **Insertion Sort** trên các mảng con kích thước nhỏ. Các thí nghiệm được tiến hành trên bốn loại phân phối dữ liệu với seed cố định `25020389` để đảm bảo **tái lập** kết quả.

## 2. Cấu trúc thư mục
```
cutoff-benchmark-merge-sort-/
├── src/                     # Mã nguồn Java
│   ├── MergeCutoff.java          # Triển khai Merge Sort + Insertion Sort (CUTOFF & Skip merge)
│   ├── DatasetGenerator.java     # Sinh dữ liệu 4 loại phân phối, seed cố định
│   └── CutoffBenchmark.java      # Chương trình chạy benchmark, warm‑up, ghi CSV
├── lib/                     # Thư viện algs4.jar (Sedgewick & Wayne)
├── data/                    # Kết quả đo đạc và mẫu dữ liệu
│   ├── ket_qua_thuc_nghiem_algs4.csv   # CSV kết quả các thí nghiệm
│   └── mẫu_dữ_liệu.txt          # Một vài file mẫu (đại diện)
├── charts/                  # Hình ảnh PNG được tạo từ CSV
│   ├── bieudo1_cutoff_vs_time.png
│   ├── bieudo2_so_sanh_cutoff.png
│   └── bieudo3_so_sanh_du_lieu.png
├── README.md                # Tài liệu hướng dẫn duy nhất (bản này)
└── Baocao.docx              # Báo cáo chi tiết (Word/PDF)
```

## 3. Mô tả mã nguồn
- **`MergeCutoff.java`**: triển khai thuật toán `MergeSort` với tùy chọn ngưỡng `CUTOFF`. Khi kích thước mảng con ≤ `CUTOFF` thì chuyển sang `InsertionSort`. Ngoài ra, cung cấp phương thức `skipMerge` để bỏ qua bước merge khi không cần thiết.
- **`DatasetGenerator.java`**: sinh mảng theo bốn phân phối:
  1. `RANDOM` – phân phối đồng đều `U(0,1)`.
  2. `NEARLY_SORTED` – 99% phần tử đã sắp, 1% hoán vị ngẫu nhiên.
  3. `REVERSED` – mảng giảm dần (trường hợp xấu nhất).
  4. `FEW_UNIQUE` – chỉ 10 giá trị duy nhất.
  Dữ liệu được tạo trong bộ nhớ và không ghi ra đĩa, giúp giảm dung lượng dự án.
- **`CutoffBenchmark.java`**: thực hiện:
  1. **Warm‑up** JVM 25 lần.
  2. Đo thời gian chạy `CutoffSort` cho mỗi kết hợp `CUTOFF`, `N`, và loại dữ liệu.
  3. Lặp `trials = 10` lần cho mỗi cấu hình, ghi `mean`, `stddev`, `min`, `max` vào CSV.

## 4. Dữ liệu (`data/`)
- **`ket_qua_thuc_nghiem_algs4.csv`**: chứa các cột `CUTOFF,N,distribution,trial,mean,stddev,min,max`. Đây là nguồn duy nhất để vẽ biểu đồ.
- Các file mẫu `.txt` chỉ dùng để minh họa cách dữ liệu trông như thế nào; thực tế dữ liệu được sinh động trong bộ nhớ.

## 5. Biểu đồ (`charts/`)
| Tên file | Nội dung |
|---|---|
| `bieudo1_cutoff_vs_time.png` | Thời gian chạy vs. giá trị `CUTOFF` cho mỗi kích thước mảng và phân phối dữ liệu. |
| `bieudo2_so_sanh_cutoff.png` | So sánh thời gian giữa `CUTOFF = 7` và `CUTOFF = 10` trên dữ liệu ngẫu nhiên (`RANDOM`). |
| `bieudo3_so_sanh_du_lieu.png` | Ảnh hưởng của các loại phân phối dữ liệu lên thời gian chạy với `CUTOFF` tối ưu. |

## 6. Hướng dẫn biên dịch & chạy thí nghiệm
### Bước 1: Biên dịch mã nguồn Java
```bash
javac -cp ".;lib/algs4.jar" src/*.java
```
### Bước 2: Chạy benchmark
```bash
java -cp "src;lib/algs4.jar" CutoffBenchmark
```
- Các tham số tùy chọn: `java -cp "src;lib/algs4.jar" CutoffBenchmark [trials] [baseSeed]`
- Kết quả sẽ được ghi vào `data/ket_qua_thuc_nghiem_algs4.csv`.

## 7. Tái lập & kích thước dữ liệu
- **Seed cố định**: `baseSeed = 25020389` → mọi máy tính tạo ra cùng một bộ dữ liệu.
- **Không lưu toàn bộ dataset**: Các mảng lớn (đến `N = 1,000,000`) được sinh tại thời gian chạy, tránh việc lưu các file dữ liệu khổng lồ (>150 MB) trong repo.

## 8. Kết quả chính
- `CUTOFF` tối ưu trên máy thử nghiệm nằm trong khoảng **8‑12** (đối với `N = 1,000,000`).
- `CUTOFF = 10` nhanh hơn `CUTOFF = 7` khoảng **20.9 %** trên dữ liệu ngẫu nhiên `RANDOM`.
- Phân phối `NEARLY_SORTED` hưởng lợi rất lớn từ `InsertionSort` → `CUTOFF` lớn (30‑40) cho thời gian nhanh nhất.
- Phân phối `REVERSED` giảm hiệu năng khi `CUTOFF` quá lớn vì độ phức tạp O(N²) của `InsertionSort`.

## 9. Liên hệ & tài liệu tham khảo
- **Giáo trình**: *Algorithms, 4th Edition* – Robert Sedgewick & Kevin Wayne.
- **Mã nguồn**: xem chi tiết trong thư mục `src/`.
- **Báo cáo chi tiết**: `Baocao.docx` (Word/PDF).

---
*Đây là README duy nhất của dự án, viết bằng tiếng Việt, các thuật ngữ chuyên ngành giữ nguyên tiếng Anh và giải thích ngắn gọn trong ngoặc.*