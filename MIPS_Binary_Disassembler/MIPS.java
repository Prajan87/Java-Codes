import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MIPS 
{
	
	static int MIPSadd = 496;
	
	// For ADDI, ADDIU, SLTI
	public String addimm(String s,String bin)
	{
		String opcode,rs,rt,bit,imm,immvalue,negimm,value2,str=null;
		int opd;
		opcode = s.substring(0,6);
		rs = s.substring(6,11);
		rt = s.substring(11,16);
		imm = s.substring(16,32);
		bit = imm.substring(0, 1);
		negimm = s.substring(17,32);
		if (bit.matches("0"))
		{
			immvalue = Integer.toString(Integer.parseInt(imm,2));
		}
		else
		{
			value2 = negimm.replace('0','t').replace('1','0').replace('t', '1');
			immvalue = "-"+Integer.toString(Integer.parseInt(value2,2)+1);
		}
		opd = Integer.parseInt(opcode,2);
		switch(opd)
		{
			case 8:
				str = bin + "	"+Integer.toString(MIPSadd)+ "	ADDI	R"+Integer.toString(Integer.parseInt(rt,2))+", R"+Integer.toString(Integer.parseInt(rs,2))+", #"+immvalue;
				
				break;
			case 9:
				str = bin + "	"+Integer.toString(MIPSadd)+ "	ADDIU	R"+Integer.toString(Integer.parseInt(rt,2))+", R"+Integer.toString(Integer.parseInt(rs,2))+", #"+immvalue;
				break;
			case 10:
				str = bin + "   "+Integer.toString(MIPSadd)+ "	SLTI	R"+Integer.toString(Integer.parseInt(rt,2))+", R"+Integer.toString(Integer.parseInt(rs,2))+", #"+immvalue;
				break;
				
			
		}
		return str;
	}
	// For ADD, ADDU, SUB, SUBU
	public String addsub(String s,String bin)
	{
		String opcode,rs,rt,rd,str=null;
		int opd;
		opcode = s.substring(26,32);
		rs = s.substring(6,11);
		rt = s.substring(11,16);
		rd = s.substring(16,21);
		
		opd = Integer.parseInt(opcode,2);
		switch(opd)
		{
			case 32:
				str = bin + "	"+Integer.toString(MIPSadd)+"	ADD	R"+Integer.toString(Integer.parseInt(rd,2))+", R"+Integer.toString(Integer.parseInt(rs,2))+", R"+Integer.toString(Integer.parseInt(rt,2));
				
				break;
			case 33:
				str = bin + "	"+Integer.toString(MIPSadd)+"	ADDU	R"+Integer.toString(Integer.parseInt(rd,2))+", R"+Integer.toString(Integer.parseInt(rs,2))+", R"+Integer.toString(Integer.parseInt(rt,2));
				break;
			case 34:
				str = bin + "	"+Integer.toString(MIPSadd)+"	SUB	R"+Integer.toString(Integer.parseInt(rd,2))+", R"+Integer.toString(Integer.parseInt(rs,2))+", R"+Integer.toString(Integer.parseInt(rt,2));
				break;
			case 35:
				str = bin + "	"+Integer.toString(MIPSadd)+"	SUBU	R"+Integer.toString(Integer.parseInt(rd,2))+", R"+Integer.toString(Integer.parseInt(rs,2))+", R"+Integer.toString(Integer.parseInt(rt,2));
				break;
			
		}
		return str;
	}
	
	// For BEQ, BNE, BLEZ, BGTZ
	public String branch(String s,String bin)
	{
		String opcode,rs,rt,imm,immvalue,bit,negimm,value2,str=null;
		int opd;
		opcode = s.substring(0,6);
		rs = s.substring(6,11);
		rt = s.substring(11,16);
		
		imm = s.substring(16,32);
		bit = imm.substring(0, 1);
		negimm = s.substring(17,32);
		if (bit.matches("0"))
		{
			immvalue = Integer.toString(Integer.parseInt(imm,2));
		}
		else
		{
			value2 = negimm.replace('0','t').replace('1','0').replace('t', '1');
			immvalue = "-"+Integer.toString(Integer.parseInt(value2,2)+1);
		}
		
		opd = Integer.parseInt(opcode,2);
		switch(opd)
		{
			case 4:
				str = bin + "	"+Integer.toString(MIPSadd)+ "	BEQ	R"+Integer.toString(Integer.parseInt(rs,2))+", R"+Integer.toString(Integer.parseInt(rt,2))+", #"+immvalue;//Integer.toString(Integer.parseInt(s.substring(16,32),2));
				
				break;
			case 5:
				str = bin + "   "+Integer.toString(MIPSadd)+ "	BNE	R"+Integer.toString(Integer.parseInt(rs,2))+", R"+Integer.toString(Integer.parseInt(rt,2))+", #"+immvalue;//Integer.toString(Integer.parseInt(s.substring(16,32),2));
				break;		
			case 6:
				str = bin + "   "+Integer.toString(MIPSadd)+ "	BLEZ	R"+Integer.toString(Integer.parseInt(rs,2))+", #"+immvalue;//Integer.toString(Integer.parseInt(s.substring(16,32),2));
				break;	
			case 7:
				str = bin + "   "+Integer.toString(MIPSadd)+ "	BGTZ	R"+Integer.toString(Integer.parseInt(rs,2))+", #"+immvalue;//Integer.toString(Integer.parseInt(s.substring(16,32),2));
				break;	
			
		}
		return str;
	}
	
	// For BLTZ, BGEZ
	public String branch2(String s,String bin)
	{

		String opcode,rs,str=null;
		int opd;
		opcode = s.substring(11,16);
		rs = s.substring(6,11);
		
		
		opd = Integer.parseInt(opcode,2);
		switch(opd)
		{
			case 0:
				str = bin + "	"+Integer.toString(MIPSadd)+ "	BLTZ	R"+Integer.toString(Integer.parseInt(rs,2))+", #"+Integer.toString(Integer.parseInt(s.substring(16,32),2));
				
				break;
			case 1:
				str = bin + "	"+Integer.toString(MIPSadd)+ "	BGEZ	R"+Integer.toString(Integer.parseInt(rs,2))+", #"+Integer.toString(Integer.parseInt(s.substring(16,32),2));
				break;		
			
			
		}
		return str;	
	}
	
	//For SLL, SRL, SRA, SLT
	public String shifter(String s,String bin)
	{
		String opcode,rd,rt,rs,str=null;
		int opd;
		opcode = s.substring(26,32);
		rt = s.substring(11,16);
		rd = s.substring(16,21);
		rs = s.substring(6,11);
		
		opd = Integer.parseInt(opcode,2);
		switch(opd)
		{
			case 0:
				str = bin + "	"+Integer.toString(MIPSadd)+ "	SLL	R"+Integer.toString(Integer.parseInt(rd,2))+", R"+Integer.toString(Integer.parseInt(rt,2))+", #"+Integer.toString(Integer.parseInt(s.substring(21,26),2));
				
				break;
			case 2:
				str = bin + "   "+Integer.toString(MIPSadd)+ "	SRL	R"+Integer.toString(Integer.parseInt(rd,2))+", R"+Integer.toString(Integer.parseInt(rt,2))+", #"+Integer.toString(Integer.parseInt(s.substring(21,26),2));
				break;		
			case 3:
				str = bin + "   "+Integer.toString(MIPSadd)+ "	SRA	R"+Integer.toString(Integer.parseInt(rd,2))+", R"+Integer.toString(Integer.parseInt(rt,2))+", #"+Integer.toString(Integer.parseInt(s.substring(21,26),2));
				break;	
			case 42:
				str = bin + "   "+Integer.toString(MIPSadd)+ "	SLT	R"+Integer.toString(Integer.parseInt(rd,2))+", R"+Integer.toString(Integer.parseInt(rs,2))+", R"+Integer.toString(Integer.parseInt(rt,2));
				break;	
		}
		return str;	
	}
	
	// For ADD, OR, XOR, NOR
	public String logical(String s,String bin)
	{
		String opcode,rd,rt,rs,str=null;
		int opd;
		opcode = s.substring(26,32);
		rt = s.substring(11,16);
		rd = s.substring(16,21);
		rs = s.substring(6,11);
		
		opd = Integer.parseInt(opcode,2);
		switch(opd)
		{
			case 36:
				str = bin + "	"+Integer.toString(MIPSadd)+ "	ADD	R"+Integer.toString(Integer.parseInt(rd,2))+", R"+Integer.toString(Integer.parseInt(rt,2))+", R"+Integer.toString(Integer.parseInt(rt,2));
				
				break;
			case 37:
				str = bin + "	"+Integer.toString(MIPSadd)+ "	OR	R"+Integer.toString(Integer.parseInt(rd,2))+", R"+Integer.toString(Integer.parseInt(rt,2))+", R"+Integer.toString(Integer.parseInt(rt,2));
				break;		
			case 38:
				str = bin + "   "+Integer.toString(MIPSadd)+ "	XOR	R"+Integer.toString(Integer.parseInt(rd,2))+", R"+Integer.toString(Integer.parseInt(rt,2))+", R"+Integer.toString(Integer.parseInt(rt,2));
				break;	
			case 39:
				str = bin + "   "+Integer.toString(MIPSadd)+ "	OR	R"+Integer.toString(Integer.parseInt(rd,2))+", R"+Integer.toString(Integer.parseInt(rs,2))+", R"+Integer.toString(Integer.parseInt(rt,2));
				break;	
		}
		return str;	
	}
	
	// FOR SW, LW
	public String loadstore(String s,String bin)
	{
		String opcode,offset,rt,base,str=null;
		int opd;
		opcode = s.substring(0,6);
		rt = s.substring(11,16);
		offset = s.substring(16,32);
		base = s.substring(6,11);
		
		opd = Integer.parseInt(opcode,2);
		switch(opd)
		{
			case 43:
				str = bin + "	"+Integer.toString(MIPSadd)+ "	SW	R"+Integer.toString(Integer.parseInt(rt,2))+", "+Integer.toString(Integer.parseInt(offset,2))+" (R"+Integer.toString(Integer.parseInt(base,2))+")";
				
				break;
			case 35:
				str = bin + "	"+Integer.toString(MIPSadd)+ "	LW	R"+Integer.toString(Integer.parseInt(rt,2))+", "+Integer.toString(Integer.parseInt(offset,2))+" (R"+Integer.toString(Integer.parseInt(base,2))+")";
				break;		
			
		}
		return str;	
	}
	
	// For BREAK, NOP
	public String breaknop(String s,String bin)
	{
		String opcode,str=null;
		int opd;
		opcode = s.substring(26,32);
		opd = Integer.parseInt(opcode,2);
		switch(opd)
		{
			case 13:
				str = bin + "	"+Integer.toString(MIPSadd)+ "	BREAK";
				
				break;
			case 0:
				str = bin + "	"+Integer.toString(MIPSadd)+ "	NOP";
				break;		
			
		}
		return str;	
	}
	
	// For J
	public String jump(String s,String bin)
	{
		String opcode,target,starget,str=null;
		int opd;
		opcode = s.substring(0,6);
		target = s.substring(6,32);
		starget = Integer.toBinaryString(Integer.parseInt(target,2)<<2);
		opd = Integer.parseInt(opcode,2);
		switch(opd)
		{
			case 2:
				str = bin + "	"+Integer.toString(MIPSadd)+ "	J	#"+Integer.toString(Integer.parseInt(starget,2));
				
				break;
						
		}
		return str;		
	}
	
	// For JR
	public String jumpreg(String s,String bin)
	{
		String opcode,rs,str=null;
		int opd;
		opcode = s.substring(26,32);
		opd = Integer.parseInt(opcode,2);
		rs = s.substring(6,11);
		switch(opd)
		{
			case 8:
				str = bin + "	"+Integer.toString(MIPSadd)+ "	JR	R"+Integer.toString(Integer.parseInt(rs,2));	
			
		}
		return str;	
	}
	
	// Binary to Decimal converter ************** Using 2's complement for negative numbers
	public String binaryconverter(String s)
	{
		String firstbit,valuebits,value2,value = null;
		firstbit = s.substring(0,1);
		valuebits = s.substring(1,32);
		if (firstbit.matches("0"))
		{
			value = s + "	"+Integer.toString(MIPSadd)+"	"+Integer.toString(Integer.parseInt(valuebits,2));
		}
		else
		{
			value2 = valuebits.replace('0','t').replace('1','0').replace('t', '1');
			value = s + "	"+Integer.toString(MIPSadd)+"	-"+Long.toString((Long.parseLong(value2,2)+1));
		}
		return value;
	}

	
	public static void main(String[] args) throws FileNotFoundException
	{
		String data = null,str = null,cmdline,bin=null,endcmd=null;
		int stop=0;
		String [] command;
		MIPS m = new MIPS();
		StringBuilder output = new StringBuilder();
		try 
		{
			data =  new String(Files.readAllBytes(Paths.get("D:\\Studies\\Java Eclipse\\MIPS Disassembler\\src\\fib-bin.txt" )));
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		command = data.split(System.getProperty("line.separator"));
		
		for (int i=0;i<command.length;i++)
		{
			cmdline = command[i];
			int val,val2;
			bin = cmdline.substring(0,6)+" "+cmdline.substring(6,11)+" "+cmdline.substring(11,16)+" "+cmdline.substring(16,21)+" "+cmdline.substring(21,26)+" "+cmdline.substring(26,32);			
			val = Integer.parseInt(cmdline.substring(0, 6),2);
			val2 = Integer.parseInt(cmdline.substring(26,32),2);			
			if (Double.parseDouble(cmdline.substring(0,26)) == 0)
			{
				str = m.breaknop(cmdline,bin);
				output.append(str);
				output.append(System.getProperty("line.separator"));				
				if(val2 == 13)
				{
					endcmd = "True";
					stop = i;
					break;
				
				}
			}
			else
			{
				switch (val)
				{
				case 0:
					if (val2 == 0||val2==2||val2==3)
					{
						str = m.shifter(cmdline,bin);
						output.append(str);
						output.append(System.getProperty("line.separator"));
					}
					else if (val2==32||val2==33||val2==34||val2==35||val2==42)
					{
						str = m.addsub(cmdline,bin);	
						output.append(str);
						output.append(System.getProperty("line.separator"));
					}
					else if (val2==36||val2==37||val2==38||val2==39)
					{
						str = m.logical(cmdline,bin);	
						output.append(str);
						output.append(System.getProperty("line.separator"));
					}
					else
					{
				
						str = m.branch2(cmdline,bin);
						output.append(str);
						output.append(System.getProperty("line.separator"));
					}
					break;
				case 1:
					str = m.branch2(cmdline,bin);
					output.append(str);
					output.append(System.getProperty("line.separator"));
					break;
				case 2:
					str = m.jump(cmdline,bin);
					output.append(str);
					output.append(System.getProperty("line.separator"));
					break;
				case 4:case 5:
					str = m.branch(cmdline,bin);
					output.append(str);
					output.append(System.getProperty("line.separator"));
					break;
				case 6:case 7:
					str = m.branch(cmdline,bin);
					output.append(str);
					output.append(System.getProperty("line.separator"));
					break;
				case 8:case 9:case 10:
					str = m.addimm(cmdline,bin);
					output.append(str);
					output.append(System.getProperty("line.separator"));
					break;
				case 35:case 43:
					str = m.loadstore(cmdline,bin);
					output.append(str);
					output.append(System.getProperty("line.separator"));
					break;
				}
		
		
			}
			MIPSadd = MIPSadd + 4;
		}
		if (endcmd.matches("True"))
			{
			
			for (int j = stop+1; j < command.length; j++)
				{
					MIPSadd = MIPSadd + 4;
					str = m.binaryconverter(command[j]);
					output.append(str);
					output.append(System.getProperty("line.separator"));
				
				}
			}
	
		
		PrintWriter p = new PrintWriter ("D:\\Studies\\Java Eclipse\\MIPS Disassembler\\src\\fib-out.txt");
		p.println(output);
		p.close();
		//System.out.println(output);
		
	}
	
}
