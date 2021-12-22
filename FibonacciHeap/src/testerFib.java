

public class testerFib {

	public static void main(String[] args) {

		FibonacciHeap heap1 = new FibonacciHeap();

		for (int i = 0; i < 10; i++){

			heap1.insert(i);
		}

		heap1.deleteMin();
		

		FiboHeapPrinter.printHeap(heap1);
	}


}
