package homework7extracredit;

import java.io.PrintStream;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Builds a single tree from the bottom up according to the frequencies of each
 * character in the file we wish to encode. First creates a leaf node for each
 * character/frequency pair and put them into a priority queue. Then picks the
 * two nodes with the smallest frequencies to combine them to make a new branch
 * node which we put back into the queue. Continue combining subtrees until
 * there is only one tree.
 * 
 * Allows the use of the code from the tree built to encode a compressed file
 * and decode the compressed file by reconstruction of the tree.
 * 
 * @author Hui Ting Cai and Aeone Singson
 *
 */
public class HuffmanTree2 {
	// For the new branch's ASCII value
	private static final int BRANCH_ASCII = 96;

	// Root of the tree
	private HuffmanNode root;

	// The array list
	private String[] codes;

	/**
	 * Constructs a Huffman tree using the given array of frequencies where
	 * count[i] is the number of occurrences of the character with ASCII value
	 * i.
	 * 
	 * @param count
	 *            array of frequencies where count[i] is the number of
	 *            occurrences of the character with ASCII value
	 */
	public HuffmanTree2(int[] count) {

		// A priority queue to store leaf nodes;
		Queue<HuffmanNode> priorityQ = new PriorityQueue<HuffmanNode>();

		// The pseudo-eof character (pseudo-eof's ASCII value is 256 ) to end of
		// file
		HuffmanNode pseudoEoF = new HuffmanNode(count.length, 1);

		// Goes through the count list and creates
		// a leaf node for each character/frequency pair.
		// Puts them into a priority queue.
		for (int i = 0; i < count.length; i++) {

			// If the frequency of each character is greater than 0, then put it
			// into priority queue. Otherwise ignore it.
			if (count[i] > 0) {
				priorityQ.offer(new HuffmanNode(i, count[i]));
			}
		}
		// Add into priority queue
		priorityQ.offer(pseudoEoF);

		/**
		 * Picks the two nodes with the smallest frequencies from the priority
		 * queue, and creates a new branch node a frequency that is the sum of
		 * the frequencies of the two children. This new node is then put back
		 * into the priority queue.
		 */
		while (priorityQ.size() >= 2) {

			// Gets the first value from the queue as the left child
			HuffmanNode leftChild = priorityQ.remove();

			// Gets the second value form the queue as the right child
			HuffmanNode rightChild = priorityQ.remove();

			int sumFrequency = leftChild.nodeFrequency + rightChild.nodeFrequency;

			// Creates a new branch node
			HuffmanNode branchNode = new HuffmanNode(BRANCH_ASCII, sumFrequency, leftChild, rightChild);

			// Adds the branch node into priorityQ
			priorityQ.offer(branchNode);
		}
		// Gets the root node
		root = priorityQ.remove();
	}

	/**
	 * Uses recursive method readSubtree to construct a new tree
	 * 
	 * @param input
	 *            where the code can be read from
	 */
	public HuffmanTree2(BitInputStream input) {
		// Passes input into recursive method to recreate the tree
		root = readSubtree(input);
	}

	/**
	 * Recreates the tree node by node using recursion.
	 * 
	 * @param input
	 *            Where the code can be read from
	 * @return Returns branch node or leaf node
	 */
	private HuffmanNode readSubtree(BitInputStream input) {
		// Reads data from the BitInputStream bit by bit
		int data = input.readBit();
		// If data is 0, then the node is a branch
		// Keeps passing through method until node is leaf
		if (data == 0) {
			HuffmanNode branch = new HuffmanNode(0, 0);
			branch.leftChild = readSubtree(input);
			branch.rightChild = readSubtree(input);
			return branch;

			// If node is leaf, create a leaf node using
			// character as given by read9 method.
			// Pass 0 as frequency in constructing new node, not relevant.
		} else {
			HuffmanNode leaf = new HuffmanNode(read9(input), 0);
			return leaf;
		}
	}

	/**
	 * Writes the current tree to the given output stream in standard format.
	 * 
	 * @param output
	 *            a BitOutputStream to write the character
	 */
	public void writeHeader(BitOutputStream output) {
		this.printSubtreePreOrder(output, this.root);
	}

	/**
	 * Prints the leaf nodes.
	 * 
	 * @param out
	 *            the PrintStream to print to
	 * @param root
	 *            the root of the subtree
	 */
	private void printSubtreePreOrder(BitOutputStream out, HuffmanNode root) {
		if (root != null) {
			// If node is leaf, write out
			if (root.isLeaf()) {
				// Write 1 for leaf
				out.writeBit(1);
				// Write value in 9 bits
				write9(out, root.character);

			} else {
				// Write 0 for a branch node
				out.writeBit(0);
				// The code adds 0 to each left branch, and add 1 to each right
				// branch
				printSubtreePreOrder(out, root.leftChild);
				printSubtreePreOrder(out, root.rightChild);
			}
		}
	}

	/**
	 * Writes a 9-bit representation of ASCII character value to a given output
	 * stream
	 * 
	 * @param output
	 *            Output stream
	 * @param n
	 *            ASCII character value
	 */
	private void write9(BitOutputStream output, int n) {
		for (int i = 0; i < 9; i++) {
			output.writeBit(n % 2);
			n /= 2;
		}
	}

	/**
	 * Reads a 9-bit representation of ASCII character value and reconstructs it
	 * 
	 * @param input
	 *            Input stream
	 * @return returns the original ASCII value from the encoded representation
	 */
	private int read9(BitInputStream input) {
		int multiplier = 1;
		int sum = 0;
		for (int i = 0; i < 9; i++) {
			sum += multiplier * input.readBit();
			multiplier *= 2;
		}
		return sum;
	}

	/**
	 * Reads bits from the given input stream and writes the corresponding
	 * characters to the output. Stops reading when it encounters a character
	 * with value equal to eof. Assumes the input stream contains a legal
	 * encoding of characters for this tree's Huffman code.
	 * 
	 * @param input
	 *            input stream to read
	 * @param output
	 *            the PrintStream to print to
	 * @param eof
	 *            The ASCII value is 256
	 */
	public void decode(BitInputStream input, PrintStream output, int eof) {

		// The parent node
		HuffmanNode parent = root;

		// Decodes until character is the eof character.
		while (parent.character != eof) {

			// Writes the integer code for that character to the output file if
			// it is a leaf
			if (parent.isLeaf()) {
				output.write(parent.character);
				// Goes back to the top of the tree.
				parent = root;

				// Goes left if 0
			} else if (input.readBit() == 0) {
				// Designates the left child as the parent node
				parent = parent.leftChild;

				// Goes right if 0
			} else {
				// Designates right child as parent node
				parent = parent.rightChild;
			}
		}
	}

	/**
	 * Assigns codes for each character of the tree. Assumes the array has null
	 * values before the method is called. Fills in a String for each character
	 * in the tree indicating its code.
	 * 
	 * @param codes
	 *            the null string array
	 */
	public void assign(String[] codes) {
		this.codes = codes;
		// Puts the code into the codes list
		this.getCode(this.codes, this.root, "");
	}

	private void getCode(String[] codes, HuffmanNode root, String code) {
		if (root != null) {
			// Puts the code in the codes array if the node is a leaf.
			if (root.isLeaf()) {

				// Uses ASCII integer as the index to put the code into
				// Ensures distinct index for each code
				codes[root.character] = code;

				// Adds 0 to the code for left and 1 for right
			} else {
				getCode(codes, root.leftChild, code + "0");
				getCode(codes, root.rightChild, code + "1");
			}
		}
	}
}
