import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Task1 {

    private static int[] merge(int[] arr1, int[] arr2) {
        int[] result = new int[arr1.length + arr2.length];

        int i = 0, j = 0, k = 0;
        while (i < arr1.length && j < arr2.length) {
            if (arr1[i] <= arr2[j]) {
                result[k++] = arr1[i++];
            } else {
                result[k++] = arr2[j++];
            }
        }
        while (i < arr1.length) {
            result[k++] = arr1[i++];
        }
        while (j < arr2.length) {
            result[k++] = arr2[j++];
        }

        return result;
    }

    private static int[][] slice(int[] arr, int k) {
        if (k == 0) {
            throw new IllegalArgumentException();
        }

        int[][] result = new int[k][];
        int remainingSubArrays = k, remainingElements = arr.length;

        int i = 0, index = 0, chunkLength;
        do {
            chunkLength = (int) Math.ceil((double) remainingElements / remainingSubArrays);
            result[index] = getSlice(arr, i, chunkLength);
            remainingSubArrays--;
            remainingElements -= chunkLength;
            index++;
            i += chunkLength;
        } while (index < k);
        return result;
    }

    private static int[] getSlice(int[] arr, int start, int len) {
        int[] result = new int[len];
        for (int i = 0; i < len; i++) {
            result[i] = arr[start + i];
        }
        return result;
    }

    public static int[] sort(int[] array) {
        int[][] subArrays;
        int[] sorted = new int[0];
        int k = Runtime.getRuntime().availableProcessors();

        subArrays = slice(array, k);

        ExecutorService executorService = Executors.newFixedThreadPool(k);
        for (int[] subArray : subArrays) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    Arrays.sort(subArray);
                    System.out.println("The Thread name is " + Thread.currentThread().getName());
                }
            });
        }

        for (int[] subArray : subArrays) {
            sorted = merge(sorted, subArray);
        }

        return sorted;
    }

    public static void main(String[] args) {
        int[] arr = {10, 6, 5, 12, 56, 32, 48, 1, 2, 3};
        int[][] sliced = slice(arr, 6);
        int[] sorted = sort(arr);
        for (int number : sorted) {
            System.out.println(number + " ");
        }
    }
}
