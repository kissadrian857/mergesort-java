import java.util.Arrays;

/* Task2: no slicing, no bullshit memcopy, parallelized merge */
public class Task2 {

    /* Create new sorted array by merging 2 smaller sorted arrays */
    private static void merge(int[] src, int idx1, int idx2, int end) {
        int[] l = new int[idx2 - idx1], r = new int[end - idx2];
        l = getSlice(src, idx1, idx2);
        r = getSlice(src, idx2, end);

        int i = 0, j = 0, k = 0;
        while (i < idx2 - idx1 && j < end - idx2) {
            if (l[i] <= r[j]) {
                src[idx1 + k++] = l[i++];
            } else {
                src[idx1 + k++] = r[j++];
            }
        }
        while (i < idx2 - idx1) {
            src[idx1 + k++] = l[i++];
        }
        while (j < end - idx2) {
            src[idx1 + k++] = r[j++];
        }
    }

    private static int[] getSlice(int[] arr, int start, int end) {
        int[] result = new int[end - start];
        for (int i = start; i < end; i++) {
            result[i - start] = arr[i];
        }
        return result;
    }

    /* Recursive core, calls a sibling thread until max depth is reached */
    public static void kernel(int[] src, int start, int end, int depth) {
        if (depth == 0) {
            int[] helper = new int[end - start];
            for (int i = 0; i < end - start; i++) {
                helper[i] = src[start + i];
            }
            Arrays.sort(helper);
            for (int i = 0; i < helper.length; i++) {
                src[start + i] = helper[i];
            }
            return;
        }
        int mid = (end + start) / 2;
        Thread leftThread = new Thread(new Runnable() {
            @Override
            public void run() {
                kernel(src, start, mid, depth - 1);
            }
        });

        Thread rightThread = new Thread(new Runnable() {
            @Override
            public void run() {
                kernel(src, mid, end, depth - 1);
            }
        });

        leftThread.start();
        rightThread.start();

        try {
            leftThread.join();
            rightThread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        merge(src, start, mid, end);
    }

    /* Creates a sorted version of any int array */
    public static int[] sort(int[] array) {

        /* Initialize variables */
        int[] src = array.clone();
        int start = 0, end = array.length;

        /* Calculate optimal depth */
        int minSize = 1000;
        int procNum = Runtime.getRuntime().availableProcessors();
        int procDepth = (int) Math.ceil(Math.log(procNum) / Math.log(2));
        int arrDepth = (int) (Math.log(array.length / minSize) / Math.log(2));
        int optDepth = Math.max(0, Math.min(procDepth, arrDepth));

        kernel(src, start, end, optDepth);
        return src;

    }
}
