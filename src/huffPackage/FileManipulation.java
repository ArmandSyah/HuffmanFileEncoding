package huffPackage;


import java.io.*;

/**
 * FileManipulation Class encompasses all the file reading/writing the Huffman project does.
 * It can read and write strings, and currently can also convert strings into bits and write them in a textfile.
 * That is how the textfile is compressed. Eventually, it will also be able to read a compressed file 
 * @author Armand
 *
 */

public class FileManipulation {
	
	/**
	 * Reads from a textfile and converts text into a string by placing it into a StringBuilder
	 * and converting that into a string
	 * @param fileName
	 * @return StringBuilder sb with textfile info on it, which is then converted into a String
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public String Read(String fileName){
		String textIn;
		StringBuilder sb = new StringBuilder();
		
		try{
			FileReader fileReader = new FileReader(fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			textIn = bufferedReader.readLine();
			
			while(textIn != null){
				sb.append(textIn);
				sb.append("\n");
				textIn = bufferedReader.readLine();
			}
			bufferedReader.close();
			return sb.toString();
			
		}
		catch(FileNotFoundException ex) {
            System.out.println(
              "Unable to open file '" + 
                    fileName + "'");                
        }
		catch(IOException ex) {
            System.out.println(
               "Error reading file '" 
                 + fileName + "'");                  
        }
		return "0";
	}
	
	/**
	 * Reads from a compressed file, converts the bits into one string and then returned for decoding.
	 * @param file
	 * @return
	 */
	public String ReadBit(String file){
		int bit;
		String textIn;
		StringBuilder sb = new StringBuilder();
		
		try{
			BufferedBitReader bbr = new BufferedBitReader(file);
			 bit = bbr.readBit();
			 textIn = Integer.toString(bit);
			 
			 while (bit != -1){
				sb.append(textIn);
				bit = bbr.readBit();
				textIn = Integer.toString(bit);
			 }
			 
			 
			 return sb.toString();
		}
		catch(FileNotFoundException ex){
			ex.printStackTrace();
		}
		catch(IOException ex){
			ex.printStackTrace();
		}
		return "0";
	}
	
	/**
	 * Takes the codedText String from TestHuffmanWithStrings class, converts it into binary bits, and then is written into a file
	 * @param s, the codedText String
	 * @param file, the textfile the String get written into
	 */
	public void WriteToBits(String s, String file){
		try{
			BufferedBitWriter bbw = new BufferedBitWriter(file);
			
			for(int i = 0; i < s.length(); i++){
				char c = s.charAt(i);
				bbw.writeBit(Character.getNumericValue(c));
			}
			bbw.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * Simple write method that take a string and writes it onto a file
	 * @param s, a string value
	 * @param file, the textfile the String gets written into
	 */
	
	public void Write(String s, String file){
		
		
		try{
			FileWriter fileWriter = new FileWriter(file);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			
			bufferedWriter.write(s);
			bufferedWriter.close();
		}catch(IOException ex) {
            System.out.println(
                    "Error writing to file '"
                    + file + "'");
            }
	}
}
