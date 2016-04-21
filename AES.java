class AES
{
	public static void main (String[] args) 
	{

		AES aes = new AES();
				
		String message = "mark_frequency_";
		System.out.println("Original text : " + message);
		
		System.out.println();
		
		String encMessage = aes.Encrypt(message); 
		System.out.println("Encrypted text : " + encMessage);
		
		System.out.println();
				
		String decMessage = aes.Decrypt(encMessage); 
		System.out.println("Decrypted text : " + decMessage);
		
		System.out.println();
	}
	
	//Encryption Key for AES to use
	public static final String key = "2b7e151628aed2a6abf7158809cf4f3c";
	
	//Diagnostic trace output
	public int traceLevel = 0;
	
	//Diagnostic output for display
	public String traceInfo = "";
	
	//AES constants
	public static final int
	ROUNDS = 14,
	BLOCK_SIZE = 16,
	KEY_LENGTH = 32;
	
	//No. of rounds 
	int numRounds;

	byte[][] eKey; //Encryption rounds keys from Key
	byte[][] dKey; //Decryption rounds keys from Key
	
	//Store all Hex values for conversion
	public static final char[] HEX_DIGITS = 
	{
		'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'
	};

	//AES encryption S-Box
	static final byte[] S = 
	{
		99, 124, 119, 123, -14, 107, 111, -59, 48, 1, 103, 43, -2, -41, -85, 118,
		-54, -126, -55, 125, -6, 89, 71, -16, -83, -44, -94, -81, -100, -92, 114, -64,
		-73, -3, -109, 38, 54, 63, -9, -52, 52, -91, -27, -15, 113, -40, 49, 21,
		4, -57, 35, -61, 24, -106, 5, -102, 7, 18, -128, -30, -21, 39, -78, 117,
		9, -125, 44, 26, 27, 110, 90, -96, 82, 59, -42, -77, 41, -29, 47, -124,
		83, -47, 0, -19, 32, -4, -79, 91, 106, -53, -66, 57, 74, 76, 88, -49,
		-48, -17, -86, -5, 67, 77, 51, -123, 69, -7, 2, 127, 80, 60, -97, -88,
		81, -93, 64, -113, -110, -99, 56, -11, -68, -74, -38, 33, 16, -1, -13, -46,
		-51, 12, 19, -20, 95, -105, 68, 23, -60, -89, 126, 61, 100, 93, 25, 115,
		96, -127, 79, -36, 34, 42, -112, -120, 70, -18, -72, 20, -34, 94, 11, -37,
		-32, 50, 58, 10, 73, 6, 36, 92, -62, -45, -84, 98, -111, -107, -28, 121,
		-25, -56, 55, 109, -115, -43, 78, -87, 108, 86, -12, -22, 101, 122, -82, 8,
		-70, 120, 37, 46, 28, -90, -76, -58, -24, -35, 116, 31, 75, -67, -117, -118,
		112, 62, -75, 102, 72, 3, -10, 14, 97, 53, 87, -71, -122, -63, 29, -98,
		-31, -8, -104, 17, 105, -39, -114, -108, -101, 30, -121, -23, -50, 85, 40, -33,
		-116, -95, -119, 13, -65, -26, 66, 104, 65, -103, 45, 15, -80, 84, -69, 22 
	};
	
	//AES decryption S-Box
	static final byte[] Si = 
	{
		82, 9, 106, -43, 48, 54, -91, 56, -65, 64, -93, -98, -127, -13, -41, -5,
		124, -29, 57, -126, -101, 47, -1, -121, 52, -114, 67, 68, -60, -34, -23, -53,
		84, 123, -108, 50, -90, -62, 35, 61, -18, 76, -107, 11, 66, -6, -61, 78,
		8, 46, -95, 102, 40, -39, 36, -78, 118, 91, -94, 73, 109, -117, -47, 37,
		114, -8, -10, 100, -122, 104, -104, 22, -44, -92, 92, -52, 93, 101, -74, -110,
		108, 112, 72, 80, -3, -19, -71, -38, 94, 21, 70, 87, -89, -115, -99, -124,
		-112, -40, -85, 0, -116, -68, -45, 10, -9, -28, 88, 5, -72, -77, 69, 6,
		-48, 44, 30, -113, -54, 63, 15, 2, -63, -81, -67, 3, 1, 19, -118, 107,
		58, -111, 17, 65, 79, 103, -36, -22, -105, -14, -49, -50, -16, -76, -26, 115,
		-106, -84, 116, 34, -25, -83, 53, -123, -30, -7, 55, -24, 28, 117, -33, 110,
		71, -15, 26, 113, 29, 41, -59, -119, 111, -73, 98, 14, -86, 24, -66, 27,
		-4, 86, 62, 75, -58, -46, 121, 32, -102, -37, -64, -2, 120, -51, 90, -12,
		31, -35, -88, 51, -120, 7, -57, 49, -79, 18, 16, 89, 39, -128, -20, 95,
		96, 81, 127, -87, 25, -75, 74, 13, 45, -27, 122, -97, -109, -55, -100, -17,
		-96, -32, 59, 77, -82, 42, -11, -80, -56, -21, -69, 60, -125, 83, -103, 97,
		23, 43, 4, 126, -70, 119, -42, 38, -31, 105, 20, 99, 85, 33, 12, 125 
	};

	//AEs key schedule round constant table
	static final byte[] rcon = 
	{
		0,
		1, 2, 4, 8, 16, 32,
		64, -128, 27, 54, 108, -40,
		-85, 77, -102, 47, 94, -68,
		99, -58, -105, 53, 106, -44,
		-77, 125, -6, -17, -59, -111 
	};

	public static final int
	COL_SIZE = 4, //depth of each column
	NUM_COLS = BLOCK_SIZE / COL_SIZE, //number of columns
	ROOT = 0x11B; //generator polynomial used in GF(2^8)

	//Shift rows = shift amount for each row in state
	static final int[] row_shift = {0, 1, 2, 3};

	//alog table for field GF(2^m) for faster calculations
	static final int[] alog = new int[256];
	
	//log table for field GF(2^m) for faster calculations
	static final int[] log =  new int[256];

	//Initalise alog and log tables
	static 
	{
		int i, j;

		alog[0] = 1;
		
		for (i = 1; i < 256; i++) 
		{
			j = (alog[i-1] << 1) ^ alog[i-1];
			if ((j & 0x100) != 0) j ^= ROOT;
			alog[i] = j;
		}
		
		for (i = 1; i < 255; i++) 
			log[alog[i]] = i;
	}

	//AES constructor
	public AES() 
	{
		setKey();
	}

	//No. of rounds needed for that key size
	public static int getRounds(int keySize) 
	{
		switch (keySize)
		{
			case 16:
				return 10; //128 bit Encryption
			case 24:
				return 12; //192 bit Encryption
			default:
				return 14; //256 bit Encryption
		}
	}

	//Multiply two GF(2^m) variables
	static final int mul(int a, int b) 
	{
		return (a != 0 && b != 0) ?
				alog[(log[a & 0xFF] + log[b & 0xFF]) % 255] :
					0;
	}

	//Encrypt plaintext using key
	public byte[] encrypt(byte[] plainText) 
	{
		byte [] a = new byte[BLOCK_SIZE]; //AES variable
		byte [] ta = new byte[BLOCK_SIZE]; //Temp AES variable
		byte [] enKey; //Encrypt key for that round
		int i, j, row, col;

		//Reset diagnostic info
		traceInfo = "";
		
		if (traceLevel > 0) traceInfo = "encryptAES(" + toHex(plainText) + ")";

		//Copy plaintext into state
		enKey = eKey[0];
		
		//Add round key to state
		for (i = 0; i < BLOCK_SIZE; i++) 
			a[i] = (byte)(plainText[i] ^ enKey[i]);
		
		if (traceLevel > 2) traceInfo += "n  R0 (Key = " + toHex(enKey) + ")ntAK = " + toHex(a);
		
		else if (traceLevel > 1) traceInfo += "n  R0 (Key = " + toHex(enKey) + ")t = " + toHex(a);

		//Apply round transformations to all rounds except the last one
		for (int r = 1; r < numRounds; r++) 
		{
			enKey = eKey[r]; //Session keys for that round
			
			if (traceLevel > 1) traceInfo += "n  R"+r+" (Key = " + toHex(enKey) + ")t";

			//SubByte(state) into ta using S-Box "S"
			for (i = 0; i < BLOCK_SIZE; i++) 
				ta[i] = S[a[i] & 0xFF];
			
			if (traceLevel > 2) traceInfo += "ntSB = " + toHex(ta);

			// ShiftRows(state) into a
			for (i = 0; i < BLOCK_SIZE; i++) 
			{
				row = i % COL_SIZE;
				j = (i + (row_shift[row] * COL_SIZE)) % BLOCK_SIZE;
				a[i] = ta[j];
			}
			if (traceLevel > 2) traceInfo += "ntSR = " + toHex(a);

			//MixColumns(state) into ta by expanding mul for each column
			for (col = 0; col < NUM_COLS; col++) 
			{
				i = col * COL_SIZE;
				
				ta[i]   = (byte)(mul(2, a[i]) ^ mul(3, a[i + 1]) ^ a[i + 2] ^ a[i + 3]);
				ta[i+1] = (byte)(a[i] ^ mul(2, a[i + 1]) ^ mul(3,a[i + 2]) ^ a[i + 3]);
				ta[i+2] = (byte)(a[i] ^ a[i + 1] ^ mul(2,a[i + 2]) ^ mul(3,a[i + 3]));
				ta[i+3] = (byte)(mul(3, a[i]) ^ a[i + 1] ^ a[i + 2] ^ mul(2,a[i + 3]));
			}
			
			if (traceLevel > 2) traceInfo += "ntMC = " + toHex(ta);

			// AddRoundKey(state) into a
			for (i = 0; i < BLOCK_SIZE; i++) 
				a[i] = (byte)(ta[i] ^ enKey[i]);
			
			if (traceLevel > 2) traceInfo += "ntAK";
			
			if (traceLevel > 1) traceInfo += " = " + toHex(a);
		}

		//Get session keys for final round
		enKey = eKey[numRounds];
		
		if (traceLevel > 1) traceInfo += "n  R" + numRounds + " (Key = " + toHex(enKey) + ")t";

		//SubBytes(state) into a using S-Box
		for (i = 0; i < BLOCK_SIZE; i++) 
			a[i] = S[a[i] & 0xFF];
		
		if (traceLevel > 2) traceInfo += "ntSB = " + toHex(a);

		//ShiftRows(state) into ta
		for (i = 0; i < BLOCK_SIZE; i++) 
		{
			row = i % COL_SIZE;
			j = (i + (row_shift[row] * COL_SIZE)) % BLOCK_SIZE;
			ta[i] = a[j];
		}
		
		if (traceLevel > 2) traceInfo += "ntSR = " + toHex(a);

		// AddRoundKey(state) into a
		for (i = 0; i < BLOCK_SIZE; i++) 
			a[i] = (byte)(ta[i] ^ enKey[i]);
		
		if (traceLevel > 2) traceInfo += "ntAK";
		
		if (traceLevel > 1) traceInfo += " = " + toHex(a) + "n";
		
		if (traceLevel > 0) traceInfo += " = " + toHex(a) + "n";
		
		return (a);
	}

	//Decrypt ciphertext using key
	public byte[] decrypt(byte[] cipherText) 
	{
		byte [] a = new byte[BLOCK_SIZE]; //AES state
		byte [] ta = new byte[BLOCK_SIZE]; //Temp AES state
		byte [] deKey; //Decrypt key for that round
		
		int i, j, row, col;

		//Reset diagnostic info
		traceInfo = "";

		if (traceLevel > 0) traceInfo = "decryptAES(" + toHex(cipherText) + ")";

		//Copy ciphertext into state
		deKey = dKey[0];
		
		//Add round key to each state
		for (i = 0; i < BLOCK_SIZE; i++) 
			a[i] = (byte)(cipherText[i] ^ deKey[i]);
		
		if (traceLevel > 2) traceInfo += "n  R0 (Key = " + toHex(deKey) + ")nt AK = " + toHex(a);
		
		else if (traceLevel > 1) traceInfo += "n  R0 (Key = " + toHex(deKey)+")t = " + toHex(a);

		//Apply round transformations to all rounds except the last one
		for (int r = 1; r < numRounds; r++) 
		{
			deKey = dKey[r]; //Session key for that round
			
			if (traceLevel > 1) traceInfo += "n  R"+r+" (Key = " + toHex(deKey) + ")t";

			 // InvShiftRows(state) into ta
			for (i = 0; i < BLOCK_SIZE; i++) 
			{
				row = i % COL_SIZE;
				j = (i + BLOCK_SIZE - (row_shift[row] * COL_SIZE)) % BLOCK_SIZE;
				ta[i] = a[j];
			}
			
			if (traceLevel > 2) traceInfo += "ntISR = " + toHex(ta);

			//InvSubBytes(state) into a using inverse S-box "Si"
			for (i = 0; i < BLOCK_SIZE; i++) a[i] = Si[ta[i] & 0xFF];
			
			if (traceLevel > 2) traceInfo += "ntISB = " + toHex(a);

			//AddRoundKey(state) into ta
			for (i = 0; i < BLOCK_SIZE; i++) 
				ta[i] = (byte)(a[i] ^ deKey[i]);
			
			if (traceLevel > 2) traceInfo += "nt AK = " + toHex(ta);

			//InvMixColumns(state) into ta by expanding mul for each column
			for (col = 0; col < NUM_COLS; col++) 
			{
				i = col * COL_SIZE;
				
				a[i]   = (byte)(mul(0x0e, ta[i]) ^ mul(0x0b, ta[i + 1]) ^ mul(0x0d, ta[i + 2]) ^ mul(0x09,ta[i + 3]));
				a[i+1] = (byte)(mul(0x09, ta[i]) ^ mul(0x0e, ta[i + 1]) ^ mul(0x0b, ta[i + 2]) ^ mul(0x0d,ta[i + 3]));
				a[i+2] = (byte)(mul(0x0d, ta[i]) ^ mul(0x09, ta[i + 1]) ^ mul(0x0e, ta[i + 2]) ^ mul(0x0b,ta[i + 3]));
				a[i+3] = (byte)(mul(0x0b, ta[i]) ^ mul(0x0d, ta[i + 1]) ^ mul(0x09, ta[i +2 ]) ^ mul(0x0e,ta[i + 3]));
			}
			
			if (traceLevel > 2) traceInfo += "ntIMC";
			
			if (traceLevel > 1) traceInfo += " = " + toHex(a);
		}

		//Get session key for final round
		deKey = dKey[numRounds];

		if (traceLevel > 1) traceInfo += "n  R"+numRounds+" (Key = " + toHex(deKey) + ")t";

		//InvShiftRows(state) into ta
		for (i = 0; i < BLOCK_SIZE; i++) 
		{
			row = i % COL_SIZE;
			j = (i + BLOCK_SIZE - (row_shift[row] * COL_SIZE)) % BLOCK_SIZE;
			ta[i] = a[j];
		}
		
		if (traceLevel > 2) traceInfo += "ntISR = " + toHex(a);
		
		//InvSubBytes(state) into ta using inverse S-box "Si"
		for (i = 0; i < BLOCK_SIZE; i++) 
			ta[i] = Si[ta[i] & 0xFF];
		
		if (traceLevel > 2) traceInfo += "ntISB = " + toHex(a);

		//AddRoundKey(state) into a
		for (i = 0; i < BLOCK_SIZE; i++) 
			a[i] = (byte)(ta[i] ^ deKey[i]);
		
		if (traceLevel > 2) traceInfo += "nt AK";
		
		if (traceLevel > 1) traceInfo += " = " + toHex(a) + "n";
		
		if (traceLevel > 0) traceInfo += " = " + toHex(a) + "n";
		
		return (a);
	}

	public void setKey() 
	{
		byte[] bKey = stringToByteArray(key); //Convert "key" string into a byte array
		
		final int BC = BLOCK_SIZE / 4;
		final int Klen = bKey.length;
		final int Nk = Klen / 4;

		int i, j, r;

		traceInfo = "";
		
		if (traceLevel > 0) traceInfo = "setKey(" + toHex(bKey) + ")n";

		//set master number of rounds given size of this key
		numRounds = getRounds(Klen);
		
		final int ROUND_KEY_COUNT = (numRounds + 1) * BC;

		//allocate 4 arrays of bytes to hold the session key values
	    //each holding 1 of the 4 bytes in each word
		byte[] w0 = new byte[ROUND_KEY_COUNT];
		byte[] w1 = new byte[ROUND_KEY_COUNT];
		byte[] w2 = new byte[ROUND_KEY_COUNT];
		byte[] w3 = new byte[ROUND_KEY_COUNT];

		 //allocate arrays to hold encryption and decryption session keys
		eKey = new byte[numRounds + 1][BLOCK_SIZE];
		dKey = new byte[numRounds + 1][BLOCK_SIZE];

		//copy key into start of session array
		for (i=0, j=0; i < Nk; i++) 
		{
			w0[i] = bKey[j++]; 
			w1[i] = bKey[j++]; 
			w2[i] = bKey[j++]; 
			w3[i] = bKey[j++];
		}

		//Temp byte for each word
		byte t0, t1, t2, t3, old0;
		
		//implement key expansion algorithm
		for (i = Nk; i < ROUND_KEY_COUNT; i++) 
		{
			t0 = w0[i - 1]; 
			t1 = w1[i - 1]; 
			t2 = w2[i - 1]; 
			t3 = w3[i - 1];
			
			if (i % Nk == 0) 
			{
				old0 = t0;  
				
				t0 = (byte)(S[t1 & 0xFF] ^ rcon[i / Nk]);
				t1 = (byte)(S[t2 & 0xFF]);
				t2 = (byte)(S[t3 & 0xFF]);
				t3 = (byte)(S[old0 & 0xFF]);
			} else if ((Nk > 6) && (i % Nk == 4)) {
				t0 = S[t0 & 0xFF]; 
				t1 = S[t1 & 0xFF]; 
				t2 = S[t2 & 0xFF]; 
				t3 = S[t3 & 0xFF];
			}
			
			w0[i] = (byte)(w0[i-Nk] ^ t0);
			w1[i] = (byte)(w1[i-Nk] ^ t1);
			w2[i] = (byte)(w2[i-Nk] ^ t2);
			w3[i] = (byte)(w3[i-Nk] ^ t3);
		}

		//Copy values into en/decrypt session arrays by round & byte in round
		for (r = 0, i = 0; r < numRounds + 1; r++) 
		{ 
			for (j = 0; j < BC; j++) 
			{  
				eKey[r][4*j] = w0[i];
				eKey[r][4*j+1] = w1[i];
				eKey[r][4*j+2] = w2[i];
				eKey[r][4*j+3] = w3[i];
				
				dKey[numRounds - r][4 * j] = w0[i];
				dKey[numRounds - r][4 * j + 1] = w1[i];
				dKey[numRounds - r][4 * j + 2] = w2[i];
				dKey[numRounds - r][4 * j + 3] = w3[i];
				
				i++;
			}
		}

		if (traceLevel > 3) 
		{
			traceInfo += "  Encrypt Round keys:n";
			
			for(r=0; r <numRounds + 1 ;r++) 
				traceInfo += "  R" + r + "t = " + toHex(eKey[r]) + "n";
			
			traceInfo += "  Decrypt Round keys:n";
			
			for(r=0; r < numRounds + 1; r++) 
				traceInfo += "  R" + r + "t = " + toHex(dKey[r]) + "n";
		}
	}

	//Convert a Byte array to a String
	public static String byteArrayToString(byte[] data) 
	{
		String res = "";
		StringBuffer sb = new StringBuffer();
		
		for(int i = 0; i < data.length; i++) 
		{
			int n = (int) data[i];
			if(n<0) n += 256;
			sb.append((char) n);
		}
		
		res = sb.toString();
		return res;
	}

	//Convert a String to a Byte array
	public static byte[] stringToByteArray(String s)
	{
		byte[] temp = new byte[s.length()];
		
		for(int i = 0;i < s.length(); i++)
			temp[i] = (byte) s.charAt(i);
		
		return temp;
	}

	//Either encypt or decrypt string from either wrapper class
	public String aes(String data, char mode)  
	{
		AES aes = this;
		
		if(data.length() / 16 > ((int) data.length() / 16)) 
		{
			int rest = data.length() - ((int) data.length() / 16) * 16;
			
			for(int i = 0; i < rest; i++)
				data += " ";
		}
		
		int nParts = (int) data.length() / 16;
		byte[] res = new byte[data.length()];
		
		String partStr = "";
		byte[] partByte = new byte[16];
		
		for(int p = 0; p < nParts; p++)
		{
			partStr = data.substring(p * 16, p * 16 + 16);
			partByte = stringToByteArray(partStr);
			
			if(mode == 'E') partByte = aes.encrypt(partByte);
			
			if(mode == 'D') partByte = aes.decrypt(partByte);
			
			for(int b = 0; b < 16; b++)
				res[p * 16 + b] = partByte[b];
		}
		
		return byteArrayToString(res);
	}

	//Wrapper class for AES encyption
	public String Encrypt(String data) 
	{
		while((data.length() % 32) != 0) 
			data += " ";
		
		return aes(data, 'E');
	}
	
	//Wrapper class for AES decyption
	public String Decrypt(String data) 
	{
		return aes(data, 'D').trim();
	}

	//Convert byte array to HEX values
	public static String toHex (byte[] ba) 
	{
		int length = ba.length;
			
		char[] buf = new char[length * 2];
			
		for (int i = 0, j = 0, k; i < length; ) 
		{
			k = ba[i++];
				
			buf[j++] = HEX_DIGITS[(k >>> 4) & 0x0F];
			buf[j++] = HEX_DIGITS[ k & 0x0F];
		}
			
		return new String(buf);
	}

	//Find corresponding HEX char from char 
	public static int hexDigit(char ch) 
	{
		if (ch >= '0' && ch <= '9')
			return ch - '0';
			
		if (ch >= 'A' && ch <= 'F')
			return ch - 'A' + 10;
			
		if (ch >= 'a' && ch <= 'f')
			return ch - 'a' + 10;
		
		return(0);
	}
}