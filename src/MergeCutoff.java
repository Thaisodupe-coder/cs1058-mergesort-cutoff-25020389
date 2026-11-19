public class MergeCutoff {

    private MergeCutoff() { }

    // Trộn ổn định hai nửa mảng đã sắp xếp a[lo..mid] và a[mid+1..hi] qua mảng phụ aux[]
    private static void merge(Comparable[] a, Comparable[] aux, int lo, int mid, int hi) {
        for (int k = lo; k <= hi; k++) {
            aux[k] = a[k];
        }

        int i = lo, j = mid + 1;
        for (int k = lo; k <= hi; k++) {
            if      (i > mid)              a[k] = aux[j++];
            else if (j > hi)               a[k] = aux[i++];
            else if (less(aux[j], aux[i])) a[k] = aux[j++];
            else                           a[k] = aux[i++];
        }
    }

    // Sắp xếp đệ quy đoạn a[lo..hi]
    private static void sort(Comparable[] a, Comparable[] aux, int lo, int hi, int cutoff) {
        // Cải tiến 1: Dùng Insertion Sort khi độ dài mảng con <= cutoff
        if (hi <= lo + cutoff - 1) {
            insertionSort(a, lo, hi);
            return;
        }

        int mid = lo + (hi - lo) / 2;
        sort(a, aux, lo, mid, cutoff);
        sort(a, aux, mid + 1, hi, cutoff);

        // Cải tiến 2: Bỏ qua bước merge nếu mảng đã có thứ tự
        if (!less(a[mid + 1], a[mid])) return;

        merge(a, aux, lo, mid, hi);
        
    }

    public static void sort(Comparable[] a, int cutoff) {
        Comparable[] aux = new Comparable[a.length];
        sort(a, aux, 0, a.length - 1, cutoff);
    }

    // Cài đặt Insertion Sort chuẩn từ bài giảng (Mục 2.1) cho đoạn a[lo..hi]
    private static void insertionSort(Comparable[] a, int lo, int hi) {
        for (int i = lo + 1; i <= hi; i++) {
            for (int j = i; j > lo && less(a[j], a[j - 1]); j--) {
                exch(a, j, j - 1);
            }
        }
    }

    private static boolean less(Comparable v, Comparable w) {
        return v.compareTo(w) < 0;
    }

    private static void exch(Object[] a, int i, int j) {
        Object swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }

    public static boolean isSorted(Comparable[] a) {
        for (int i = 1; i < a.length; i++) {
            if (less(a[i], a[i - 1])) return false;
        }
        return true;
    }
}