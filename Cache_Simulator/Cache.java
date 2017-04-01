import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cache {
	
	static int cacheSize, blockSize, associativity, numOfBlocks, numOfSets, offsetBits, indexBits, tagBits;
	static ArrayList<String> traceData = new ArrayList<String>();
	
	// Reading configuration file
	
	public void readConfig()
	{
		String data = null;
				
		try 
		{
			data =  new String(Files.readAllBytes(Paths.get("D:\\Studies\\Java Eclipse\\CacheSimulator\\src\\example.cfg" )));
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
				
		String[] spltData = data.split("\\D+");
		
		cacheSize = Integer.parseInt(spltData[1]);
		blockSize = Integer.parseInt(spltData[2]);
		associativity = Integer.parseInt(spltData[3]);
		numOfBlocks = cacheSize/blockSize;
		numOfSets = cacheSize/(associativity*blockSize);
		offsetBits = (int) (Math.log(blockSize) / Math.log(2));
		indexBits = (int) (Math.log(numOfSets) / Math.log(2));
		tagBits = 32 - (offsetBits + indexBits);
		
	}
	
	// Reading Memory Traces
	
	public void readTrace()
	{
		String trace=null;
		try 
		{
			trace =  new String(Files.readAllBytes(Paths.get("D:\\Studies\\Java Eclipse\\CacheSimulator\\src\\example.trc" )));
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		Pattern p = Pattern.compile("[0-9A-F]{8}+");
		Matcher m = p.matcher(trace);
		while (m.find())
		{
			traceData.add(m.group());
			
		}
		
	}
	
	// Initialize Cache
	
	public void initializeData(LinkedList<String> set,LinkedList<LinkedList<String>> cacheMem)
	{
		for (int i=0;i<associativity;i++)
		{
			set.add("0");
		}
		for (int i=0; i<numOfSets;i++)
		{
			
			cacheMem.add(set);
		}
	}
	
	// Initialize Cache Counter to follow LRU
	
	public void initializeCounter(LinkedList<Integer> setc,LinkedList<LinkedList<Integer>> cacheMemc)
	{
		for (int i=0;i<associativity;i++)
		{
			setc.add(0);
		}
		for (int i=0; i<numOfSets;i++)
		{
			
			cacheMemc.add(setc);
		}
	}
	
	// Main Function
	
	public static void main (String args[])
	{
		int nval,miss = 0;
		double hitRate, hit = 0.0d;
		String str=null, temp2=null;
		
		Cache c = new Cache();
		
		// Reading cache configuration and memory trace
		c.readConfig();
		c.readTrace();
		
		// Generating list to hold memory trace and its counter
		LinkedList<String> set = new LinkedList<String>();
		LinkedList<String> temp = new LinkedList<String>();
		LinkedList<Integer> tempc = new LinkedList<Integer>();
		LinkedList<LinkedList<String>> cacheMem = new LinkedList<LinkedList<String>>();
		
		LinkedList<Integer> setc = new LinkedList<Integer>();
		LinkedList<LinkedList<Integer>> cacheMemc = new LinkedList<LinkedList<Integer>>();
		
		// Initializing counter and cache
		
		c.initializeData(set, cacheMem);
		c.initializeCounter(setc, cacheMemc);
		
		// LRU Implementation
		
		for (String mem:traceData)
		{
			// Increasing non-zero counter value by 1 every time we access new trace
			for (int i=0;i<numOfSets;i++)
			{
				
				for (int j=0;j<associativity;j++)
				{
					temp = cacheMem.get(i);
					tempc = cacheMemc.get(i);
					if (Long.parseLong(temp.get(j),2)!=0)
					{
						tempc.remove(j);
						tempc.add(j,1);
						cacheMemc.add(i,tempc);
					}
						
				}
			}
			
			str = String.format("%32s", new BigInteger(mem,16).toString(2)).replace(' ', '0');
			nval = Integer.parseInt(str.substring(tagBits, (tagBits+indexBits-1)),2);
			
			for (int k=0;k<associativity;k++)
			{
				temp = cacheMem.get(nval);
				
				// When there is a value in the cache
				
				if (Long.parseLong(temp.get(k),2)!=0)
				{
					temp2 = temp.get(k);
					
					// When there is a hit with the value in the cache

					if (str.substring(0,(tagBits+indexBits-1)).equals(temp2.substring(0, (tagBits+indexBits-1))))
					{
						hit = hit+1;
						tempc = cacheMemc.get(nval);
						tempc.remove(k);
						tempc.add(k,0);
						cacheMemc.add(nval,tempc);
					}
					
					// When there is a miss with the value in the cache
					
					else
					{
						miss = miss+1;
						tempc = cacheMemc.get(nval);
						Integer max = Collections.max(tempc);
						int t = 0;
						for (int l = 0;l<associativity;l++)
						{
							tempc = cacheMemc.get(nval);
							if (tempc.get(l)==max)
							{
								t=l;
								break;
							}
						}
						temp = cacheMem.get(nval);
						temp.remove(t);
						temp.add(t,str);
						cacheMem.add(nval,temp);
					}
					break;
					
				}
				
				// When there is no value in the cache
				
				else if(temp.get(k).equals("0"))
				{
					miss = miss + 1;
					temp = cacheMem.get(nval);
					temp.remove(k);
					temp.add(k,str);
					cacheMem.add(nval,temp);
					
					tempc = cacheMemc.get(nval);
					tempc.remove(k);
					tempc.add(k,0);
					cacheMemc.add(nval,tempc);
				}
				break;
			}
		}
		
		hitRate = hit/traceData.size();
		
		System.out.println("Tag Length = "+tagBits);
		System.out.println("Index Length = "+indexBits);
		System.out.println("Offset Length = "+offsetBits);
		System.out.println("Total number of address accessed = "+traceData.size());
		System.out.println("No of hits = "+hit);
		System.out.println("No of misses = "+miss);
		System.out.println("Cache Hit Rate = "+hitRate);
					
	}

}
