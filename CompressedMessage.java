/**
 * Student name: James Granleese
 * Student number: 40103149
 * Module code: CSC2008
 * Practical day: Thursday
 * References: practical02, practical05, practical06, practical07
 */

import java.io.Serializable;
import java.util.ArrayList;

public class CompressedMessage implements Serializable
{
	String message;
	
	public CompressedMessage(String str)
	{
		message = str;
	}
	
	private boolean punctuationChar(String str)
    {   
       	return(str.charAt(str.length()-1) == ',' || str.charAt(str.length()-1) == '.');
    }

	private String getWord(String str)
   	{   
   		if(punctuationChar(str))
      		str = str.substring(0, str.length()-1);
       	return str;
   	}
	
	public String compress()
	{
		ArrayList<String> dictionary = new ArrayList<String>();
		String compressedStr = "";

		String[] words = message.split(" \\s*");
		for(int i = 0; i < words.length; i++)
		{	
			int foundPos = dictionary.indexOf(getWord(words[i]));
			if(foundPos == -1)
			{
   				dictionary.add(getWord(words[i]));
    			compressedStr += getWord(words[i]);
        	}
       		else {
       			compressedStr += foundPos;
       		}
     			
    		if(punctuationChar(words[i]))
     			compressedStr += words[i].charAt(words[i].length()-1);
           	compressedStr += " ";
		}

  		message = compressedStr;
  		//return message as well to save extra command in client/server
		return message;
	}
	
	public String decompress()
	{
		ArrayList<String> dictionary = new ArrayList<String>();
		String decompressedStr = "";
		int position;

		String[] words = message.split(" \\s*");
		for(int i = 0; i < words.length; i++)
		{	// test if the first character of this string is a digit
			if (words[i].charAt(0) >= '0' && words[i].charAt(0) <= '9')
			{
				position = Integer.parseInt(getWord(words[i]));
				decompressedStr += dictionary.get(position);
			}
			else
			{	// this string is a word - add to previous words list
				dictionary.add(getWord(words[i]));
				// add word to compressed message
				decompressedStr += getWord(words[i]);
			}

			if(punctuationChar(words[i]))
         		decompressedStr += words[i].charAt(words[i].length()-1);
         	decompressedStr += " ";
		}
   		message = decompressedStr;
   		//return message as well to save extra command in client/server
		return message;
	}
}