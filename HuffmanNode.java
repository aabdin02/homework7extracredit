package homework7extracredit;

/**
 * Creates node to build HuffmanTree.
 *
 */
public class HuffmanNode implements Comparable<HuffmanNode> {

	// The left child of this tree node
	public HuffmanNode leftChild;
	// The right child of this tree node
	public HuffmanNode rightChild;

	// The frequency of node
	public int nodeFrequency;

	// The ASCII value of character
	public int character;

	/**
	 * Creates a leaf node for each character/frequency.
	 * 
	 * @param character
	 *            the ASCII value of character
	 * @param nodeFrequency
	 *            the frequency of each character in the file
	 */
	public HuffmanNode(int character, int nodeFrequency) {

		// Creates a leaf node. Leaf nodes have no children.
		this(character, nodeFrequency, null, null);
	}

	/**
	 * Combines two nodes of the smallest frequencies to make a new branch node
	 * then puts back into the queue
	 * 
	 * @param character
	 *            the ASCII value of character
	 * @param nodeFrequency
	 *            the frequency of each character
	 * @param leftChild
	 *            the left hand side node of this
	 * @param rightChild
	 *            the right hand side node of this
	 */
	public HuffmanNode(int character, int nodeFrequency, HuffmanNode leftChild, HuffmanNode rightChild) {

		// Builds a left leaf and a right leaf
		this.leftChild = leftChild;
		this.rightChild = rightChild;

		// Builds a new branch
		this.nodeFrequency = nodeFrequency;
		this.character = character;
	}

	/**
	 * Compares frequency of two nodes. If two frequencies are equal, return 0.
	 * If the frequency of 'this' is higher than the frequency of 'node', return
	 * 1. If the frequency of 'this' is lower than the frequency of 'node',
	 * return -1
	 * 
	 * @param node
	 *            The Huffman node
	 * 
	 * @return Return 0 if two frequencies are equal. Return -1 if this - node <
	 *         0. Return 1 if this - node > 0.
	 */
	@Override
	public int compareTo(HuffmanNode node) {
		return (int) Math.signum(this.nodeFrequency - node.nodeFrequency);
	}

	/**
	 * Determines whether the node is a leaf.
	 * 
	 * @return Returns true if leaf.
	 */
	public boolean isLeaf() {
		return (this.leftChild == null && this.rightChild == null);

	}
}
