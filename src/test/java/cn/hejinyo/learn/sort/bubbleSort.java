package cn.hejinyo.learn.sort;

import org.junit.Test;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/11/20 20:59
 */
public class bubbleSort {

    @Test
    public void bubble() {
        int[] array = {5, 3, 2, 4, 1};
        int size = array.length;
        int temp = 0;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size - i - 1; j++) {
                if (array[j + 1] < array[j]) {
                    temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
        for (int x : array) {
            System.out.println(x);
        }

    }

    @Test
    public void choose() {
        int[] array = {5, 3, 2, 4, 1};
        int size = array.length;
        int temp;
        int minIndex = 0;
        for (int i = 0; i < size; i++) {
            minIndex = i;
            for (int j = i + 1; j < size; j++) {
                if (array[j] < array[minIndex]) {
                    minIndex = j;
                }
            }
            temp = array[i];
            array[i] = array[minIndex];
            array[minIndex] = temp;
        }
        for (int x : array) {
            System.out.println(x);
        }

    }

    @Test
    public void insertionSort() {
        int[] arr = {5, 3, 2, 4, 1};
        for (int i = 1; i < arr.length; i++) {
            int j = i;
            while (j > 0 && arr[j] < arr[j - 1]) {
                swap(arr, j, j - 1);
                j--;
            }
        }
        for (int x : arr) {
            System.out.println(x);
        }
    }

    public void swap(int[] arr, int a, int b) {
        arr[a] = arr[a] + arr[b];
        arr[b] = arr[a] - arr[b];
        arr[a] = arr[a] - arr[b];
    }

}
