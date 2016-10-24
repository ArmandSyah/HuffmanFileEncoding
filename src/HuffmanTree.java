import java.util.Arrays;
import java.util.Iterator;

/**
 * HuffmanTree creates and stores a Huffman tree based on Huffman nodes (an inner class),
 * It also provide a series of methods for encoding and decoding.
 * It uses a BitFeedOut which allows a stream of bits be sent continuously 
 * to be used for encoding.
 * It also uses an Iterator<Byte> which allows a stream of bits to be read continuously
 * to be used when decoding.
 * 
 * @author Lucia Moura
 */

public class HuffmanTree {
	
 public static int EndOfText=((int)'\uffff')+1; //special symbol created to indicate end of text
	
 HuffmanNode root=null; // root of the Huffman tree
 HuffmanNode[] leafWhereLetterIs;   // array indexed by characters, storing a reference to 
 									// the Huffman Node (leaf) in which the character is stored

 
 // Constructor receives frequency information which is used to call BuildTree
 public HuffmanTree (LetterFrequencies letterFreq) {
	 
	 root=BuildTree (letterFreq.getFrequencies(),letterFreq.getLetters());
	 
 }

 // BuildTree builds the Huffman tree based on the letter frequencies
 /**
  * Method I coded, follows Huffman Algorithm to create a Huffman Tree 
  * @param frequencies, holds reference to how many of each letters there are
  * @param letters, holds reference to the specific letter 
  * @return The root of the entire Huffman Tree
  * @author Armand
  */
 
 private HuffmanNode BuildTree(int[] frequencies,char[] letters) {
	 
	 
	/******** STEP 2 of Algorithm Huffman(X) **********************************/
	// we use a priority queue to store frequencies of subtrees created 
	// during the construction of the Huffman tree
	HeapPriorityQueue<HuffmanNode, HuffmanNode> heap = 
			new HeapPriorityQueue<HuffmanNode, HuffmanNode>(frequencies.length+1);
	 
    // initialize array leftWhereLetterIs 
	leafWhereLetterIs =new HuffmanNode[(int)'\uffff'+2]; // need 2^16+1 spaces
	for (int i=0; i< (int)'\uffff'+2; i++)
		leafWhereLetterIs[i]=null;
	
	/********* STEPS 3-5 of Algorithm Huffman(X) **********************************/
	// creating one node per letter as a single tree inserted into the priority queue
	// char letter is converted to an int
	for (int i=0; i<frequencies.length; i++) {
		if (frequencies[i]>0) {
			HuffmanNode node= new HuffmanNode( (int)letters[i], frequencies[i],null,null,null);
			leafWhereLetterIs[(int)letters[i]]=node;
			heap.insert(node,node);
		
		}
	}
	// creating node for "EndOfText" special symbol
	HuffmanNode specialNode= new HuffmanNode( EndOfText,0,null,null,null);
	leafWhereLetterIs[EndOfText]=specialNode; // last position reserved
	heap.insert(specialNode,specialNode);
	
	
	/************ STEPS 6-13 of Algorithm Huffman(X): task to be implemented ************/
	while(heap.size()>1){
		Entry<HuffmanNode, HuffmanNode> e1 = heap.removeMin();
		Entry<HuffmanNode, HuffmanNode> e2 = heap.removeMin();
		int newFreq = e1.getKey().getFrequency() + e2.getKey().getFrequency();
		
		HuffmanNode newNode = new HuffmanNode(0,newFreq,null,e1.getKey(),e2.getKey());
		heap.insert(newNode, newNode);
		e1.getKey().setParent(newNode);
		e2.getKey().setParent(newNode);
	}
	Entry<HuffmanNode, HuffmanNode> e = heap.removeMin();
	
	return e.getKey(); 
	
 }
 
// encodeCharacter encodes the character c using the Huffman tree
// returning its encoding as String of 0s and 1s representing the bits
// In the handout example if c='G' this method will return "011"
 
 /**
  * Method I designed. Here, a character c is referenced as an int and is encoded using the Huffman tree
  * @param c, a reference to one of the leaf characters, as an int
  * @return The specific Huffman Code for that letter
  * @author Armand
  */
 
private String encodeCharacter(int c) {
	
	Boolean found = false;
	String s = "";
	HuffmanNode leaf = leafWhereLetterIs[c];
	HuffmanNode current = root;
	HeapPriorityQueue <HuffmanNode, HuffmanNode> heap =
			new HeapPriorityQueue <HuffmanNode, HuffmanNode>();
	Entry <HuffmanNode, HuffmanNode> e;
	heap.insert(current, current);
	while(found != true){
		e = heap.removeMin();
		if(e.getKey().getLetter() == leaf.getLetter()){
			current = e.getKey();
			found = true;
		}
		if(e.getKey().rightChild() != null){
			heap.insert(e.getValue().rightChild(), e.getKey().rightChild());
		}
		if(e.getKey().leftChild() != null){
			heap.insert(e.getKey().leftChild(), e.getKey().leftChild());
		}
	}
	/*This traverses from the leaf to the roof, giving up a reversed huffman code. 
	The code is then re-reversed afterwards */
	while(current.parent != null){
		HuffmanNode temp = current;
		current = current.parent();
		if(current.leftChild() == temp){
			s += 0;
		}
		else if(current.rightChild() == temp){
			s += 1;
		}
	}
	String out = new StringBuilder(s).reverse().toString();
	return out; 
 } 

// Encode the a character c using the Huffman tree
// sending the encoded bits to argument BitFeedOut bfo
// (please do not change this method)

 public void encodeCharacter (int c, BitFeedOut bfo) {
	 String s=encodeCharacter(c);
	 for (int i=0; i< s.length();i++) bfo.putNext(s.charAt(i));
	
 }
 
// decodeCharacter receives Iterator<Byte> bit that iterates through a sequence
//  of bits of the  encoded string; this sequence must be
// compatible with the Huffman tree (has been previously generated by
// a tree like this one.
// This method will be "consuming" bits until it completes the
// decoding of a letter which is then returned.
// In the handout example, if the next bits are 011001...
// decodeCharacter will apply bit.next() 3 times until if decodes
// the first letter, which in this case is 'G'
 /**
  * Method I coded. Self-explanatory
  * @param bit, an iterator that checks each individual bit
  * @return an int value that references a letter in the huffman tree
  */
public int decodeCharacter(Iterator<Byte> bit) {
	 
	 if (root == null) return Integer.MAX_VALUE; // empty tree is not valid when decoding

	 HuffmanNode current = root;
	 while(bit.hasNext()){
		 Byte b = bit.next();
		 if (b == 0){
			 current = current.leftChild();
		 }
		 else{
			 current = current.rightChild();
		 }
		 if(current.isLeaf()){
			 return current.getLetter();
		 }
	 }
	 return 0; 
 }
 
 
 
 // auxiliary methods for printing the codes in the Huffman tree

 void printCodeTable() {
	 System.out.println("**** Huffman Tree: Character Codes ****");
	 if (root!=null) 
		 traverseInOrder(root,""); // uses inorder traversal to print the codes
	 else 
		 System.out.println("No character codes: the tree is still empty");
	 System.out.println("***************************************");
	 
 }
 
 // In-order traversal of the Huffman tree keeping track of
 // the paths to leaves so it can print the codeword for each letter
 private void traverseInOrder(HuffmanNode current, String c) {
	 if (current.isLeaf()) {
		if (current.getLetter()!=EndOfText)
		       System.out.println((char)current.getLetter()+":"+c);
		else   System.out.println("EndOfText:"+c);
	 }
	 else { 
		 traverseInOrder(current.leftChild(),c+"0");
		 traverseInOrder(current.rightChild(),c+"1");
	 }
		 
 }
 
 // provided byte encoding of the frequency information
 // in the format of 4 bytes per letter
 // 2 first bytes represent letter 2 last bytes represent frequency
 // This is useful for file decoding where the letter frequencies need
 // to be stored in a "header" of the encoded file 
 // (not used in the current version of the assignment)
 
 byte[] freqsToBytes() {
    int b=0;
	byte [] treeBytes= new byte[(int)'\uffff'*4];
    for (int i=0;i<'\uffff';i++) {
		if (leafWhereLetterIs[i]!=null) {
			int freq=leafWhereLetterIs[i].getFrequency();
			char letter=(char)leafWhereLetterIs[i].getLetter();
			treeBytes[b++]= (byte)(((int)letter)/256);
			treeBytes[b++]= (byte)(((int)letter)%256);
			treeBytes[b++]= (byte)(freq/256); 
			treeBytes[b++]= (byte)(freq%256);			
		}
	}
    return Arrays.copyOf(treeBytes, b);
 }
 
 	/**** inner class to Huffman tree that implements a Node in the tree ****/
    // nothing to be changed in this inner class
 	public class HuffmanNode implements Comparable<HuffmanNode> {
		
		int letter; // if the node is a leaf it will store a letter, otherwise it store null
	    int frequency; // stores the sum of the frequencies of all leaves of the tree rooted at this node
		private HuffmanNode parent, left, right; // reference to parent, left and right nodes.
		
		public HuffmanNode() {
			parent=left=right=null;
			frequency=-1;
		}
		
		public HuffmanNode(int letter, int frequency, HuffmanNode parent, HuffmanNode left, HuffmanNode right) {
			this.letter= letter;
			this.frequency=frequency;
			this.parent=parent; 
			this.left=left;
			this.right=right;
		}
		
		
		boolean isLeaf() { return (left==null && right==null);}
		
		// getter methods
		
		HuffmanNode leftChild() { return left;}
		
		HuffmanNode rightChild() { return right;}
		
		HuffmanNode parent() { return parent;}
		
		int getLetter() {return letter;}
		
		int getFrequency() {return frequency;}

		// setter methods
		
		void setLeftChild(HuffmanNode leftVal) { left=leftVal;	}
		
		void setRightChild(HuffmanNode rightVal) { right=rightVal;	}
		
		void setParent(HuffmanNode parentVal) { parent=parentVal;	}
		
		void setLetter(char letterVal) { letter = letterVal;}
		
		void setFrequency(int freqVal) { frequency = freqVal; }

		@Override
		public int compareTo(HuffmanNode o) {
			if (this.frequency==o.frequency) {
				return this.letter-o.letter;
			}
			else return this.frequency-o.frequency;
			
		}
		
	}

 
 
}
 
