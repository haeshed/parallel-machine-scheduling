
public class Priority {

	static int SIZE = 100;
	int[] priorityList = new int[SIZE];
	int realSize;

	public Priority(int[] arr) {
		this.realSize = arr.length;
		for (int i = 0; i < realSize; i++) {
			priorityList[i] = arr[i];
		}
	}

	public int[] getRealPriorityList() {
		int[] arrOut = new int[realSize];
		for (int i = 0; i < realSize; i++) {
			arrOut[i] = priorityList[i];
		}
		return arrOut;
	}

	public int getRealSize() {
		return this.realSize;
	}

	/*
	 * finds the index of a given int value within the REAL priority array
	 */
	public int find(int x) {
		int[] arr = getRealPriorityList();
		for (int i = 0; i < realSize; i++) {
			if (arr[i] == x) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * A textual representation of this node, useful for debugging.
	 */
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("{ ");
		for (int i = 0; i < (realSize - 1); i++) {
			s.append(priorityList[i] + " , ");
		}
		s.append(priorityList[realSize - 1] + " }");
		return s.toString();
	}
}