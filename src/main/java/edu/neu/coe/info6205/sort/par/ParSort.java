package edu.neu.coe.info6205.sort.par;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

/**
 * This code has been fleshed out by Ziyao Qiao. Thanks very much.
 * TODO tidy it up a bit.
 */
class ParSort {

    public static int cutoff = 1000;
    public static void sort(int[] array, int from, int to, ForkJoinPool forkjoin_pool) {
        if (to - from <= cutoff || ForkJoinPool.getCommonPoolParallelism() == 1) {
            Arrays.sort(array, from, to);
        } else {
//            System.out.println("from: " + from + " to: " + to);
            // FIXME next few lines should be removed from public repo.
            int mid = from + (to - from) / 2;
            CompletableFuture<int[]> parsort1 = parsort(array, from, mid, forkjoin_pool); // TO IMPLEMENT
            CompletableFuture<int[]> parsort2 = parsort(array, mid, to, forkjoin_pool); // TO IMPLEMENT
            CompletableFuture<int[]> parsort = parsort1.thenCombine(parsort2, (xs1, xs2) -> {
                int[] result = new int[xs1.length + xs2.length];
                // TO IMPLEMENT
                int i = 0;
                int j = 0;
                for (int k = 0; k < result.length; k++) {
                    if (i >= xs1.length) {
                        result[k] = xs2[j++];
                    } else if (j >= xs2.length) {
                        result[k] = xs1[i++];
                    } else if (xs2[j] < xs1[i]) {
                        result[k] = xs2[j++];
                    } else {
                        result[k] = xs1[i++];
                    }
                }

                return result;
            });

            parsort.whenComplete((result, throwable) -> System.arraycopy(result, 0, array, from, result.length));
//            System.out.println("# threads: " + ForkJoinPool.commonPool().getRunningThreadCount());
            parsort.join();
        }
    }

    private static CompletableFuture<int[]> parsort(int[] array, int from, int to, ForkJoinPool forkjoin_pool) {
        return CompletableFuture.supplyAsync(
            () -> {
                int[] result = new int[to - from];
                // TO IMPLEMENT
                System.arraycopy(array, from, result, 0, result.length);
                sort(result, 0, result.length, forkjoin_pool);
                return result;
            }, forkjoin_pool);
    }

    static class MergeSortTask extends RecursiveAction {
        private int[] arr;
        private int start, end;
        private int threshold;

        public MergeSortTask(int[] arr, int start, int end, int threshold) {
            this.arr = arr;
            this.start = start;
            this.end = end;
            this.threshold = threshold;
        }

        @Override
        protected void compute() {
            if (end - start <= threshold) {
                // sequential sort
                Arrays.sort(arr, start, end);
                return;
            }

            // Sort halves in parallel
            int mid = start + (end - start) / 2;
            invokeAll(
                new MergeSortTask(arr, start, mid, threshold),
                new MergeSortTask(arr, mid, end, threshold)
            );

            // sequential merge
            sequentialMerge(mid);
        }

        private void sequentialMerge(int mid) {
            int[] temp = new int[end - start];
            int i = 0, j = start, k = mid;
            while (true) {
                if (j >= mid) {
                    System.arraycopy(arr, k, temp, i, end - k);
                    break;
                }
                if (k >= end) {
                    System.arraycopy(arr, j, temp, i, mid - j);
                    break;
                }
                int left = arr[j];
                int right = arr[k];
                if (left < right) {
                    temp[i++] = left;
                    j++;
                } else {
                    temp[i++] = right;
                    k++;
                }
            }

            System.arraycopy(temp, 0, arr, start, temp.length);
        }
    }
}