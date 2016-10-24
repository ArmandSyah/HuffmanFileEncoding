package huffPackage;
/**
 * 
 * Main program for the purpose of testing Huffman with several string texts;
 * the markers reserve the right to add some more strings to this test bed.
 * 
 * Testing Huffman with Strings is useful for testing the Huffman encoding
 * and for visualization of the output of bits for the encoded text.
 * The real usage of Huffman encoding would not produce a String with the
 * bits, since this would be wasteful (using 2 bytes to store a bit).
 * 
 * To use Huffman endcoding with files requires creating classes:
 * BitFeedInForFiles implementing BitFeedIn interface and 
 * BitFeedOutForFiles implementing BitFeedOut interface 
 * 
 * @author Lucia Moura
 */
public class TestHuffmanWithStrings {
	
	public static void main(String[] args) { 
		long startTime = System.currentTimeMillis();
		int index = 0;
		FileManipulation rf = new FileManipulation();
		String  codedText, decodedText, codedInBits;
		LetterFrequencies fq;
		
		//Textfiles of free E-Books from https://www.gutenberg.org/ in plaintext
		String [] textFiles = {
				"Test1.txt",
				"Test2.txt",
				"Test3.txt",
				"Test4.txt",
				"Test5.txt"
		};
		
		
		//Representation of compressed Textfiles to be made
		String [] compressedFiles = {
				"Test1_Compressed.txt",
				"Test2_Compressed.txt",
				"Test3_Compressed.txt",
				"Test4_Compressed.txt",
				"Test5_Compressed.txt"
		};
		
		//Representation of decompressed Textfiles to be made
		String[] decompressedFiles = {
				"Test1_Decompressed.txt",
				"Test2_Decompressed.txt",
				"Test3_Decompressed.txt",
				"Test4_Decompressed.txt",
				"Test5_Decompressed.txt"
		};
		
		String [] textsToTest = new String[textFiles.length];
		
		for(int i = 0; i < textFiles.length; i++){
			String s = rf.Read(textFiles[i]);
			textsToTest[i] = s;
		}
		
		for (String plainText: textsToTest) {
			System.out.println(">>>> Begining Encoding:\n"); 
			codedText= testStringEncode(plainText); // encodes plainText into codedText
			rf.WriteToBits(codedText, compressedFiles[index]);
			System.out.println("\n>>>> Begining Decoding:\n");
			codedInBits = rf.ReadBit(compressedFiles[index]);
			fq=new LetterFrequencies(plainText);
			decodedText=testStringDecode(codedInBits,fq); // decoded codedText into decodedText
			rf.Write(decodedText, decompressedFiles[index]);
			if (plainText.equals(decodedText)) // plainText must match decodedText
				System.out.println("RESULT: Correctly encoding-decoding!\n");
				
			else {
				System.out.println("WRONG: incorrect encoding-decoding");
			}
			index++;
		
		}
		
		long endTime   = System.currentTimeMillis();
		long totalTime = (endTime - startTime) / 1000;
		System.out.println(totalTime + " Seconds");
		
	}
	
	// This is the encoding of a String of characters using Huffman encoding
	// It test several funcionalities of HuffmanTree
	// It returns a String containing the sequence of bits of the encoding
	
	public static String testStringEncode(String inputText) {
		
		LetterFrequencies lf = new LetterFrequencies(inputText); // compute frequencies of letters
		
		// populate the frequency list
		HuffmanTree huffTree= new HuffmanTree(lf); // create Huffman tree based on letter frequencies
		
		huffTree.printCodeTable(); //print letters and their Huffman encoding
		
		BitFeedOutForString outSeq= new BitFeedOutForString(); 
		// create outSeq (which has interface BitFeedOut) that will receive bits during encoding
		for (int i=0; i< inputText.length();i++) 
			huffTree.encodeCharacter(inputText.charAt(i),outSeq); // each character is encoded and sent to outSeq
		
		huffTree.encodeCharacter(HuffmanTree.EndOfText,outSeq); // the special EndOfText character is encoded at put at the end
		
		String codedText = outSeq.output(); // obtain codedText from outSeq
	
        return codedText; // return coded text
	}
	
	// This is the encoding of a String of characters using Huffman encoding
	// It test several funcionalities of HuffmanTree
	// It returns a String containing the sequence of bits of the encoding

	public static String testStringDecode(String codedText, LetterFrequencies lf) {
			
		// populate the frequency list
		HuffmanTree huffTree= new HuffmanTree(lf); // using the same know frequencies to create tree (so tree will be the same)
		 										   // Huffman encoding with files would have to 
												   // read the frequence info from the header of the encoded file
		
		BitFeedInForString seq=new BitFeedInForString(codedText); // create BitFeedIn to send encoded bits one by one
		
		StringBuilder decodedText=new StringBuilder();
		while (seq.hasNext()) {
			 int symbol=huffTree.decodeCharacter(seq); // decode the next character by using a few bits of seq
			 if (symbol == Integer.MAX_VALUE)
				 break;
			 if (symbol!=HuffmanTree.EndOfText)
			     decodedText.append((char) symbol); // keep collecting the decoded characters
			 else break;
		}
		
		return decodedText.toString(); // return the decoded string
	}
	

}
