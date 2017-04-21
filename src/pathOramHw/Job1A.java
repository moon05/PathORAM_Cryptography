/*
 *  You can use this class to test your Oram (for correctness, not security).
 *  
 *  You can experiment modifying this class, but we will not take it into account (we will test your ORAM implementations on this as well as other Jobs)
 *  
 */

package pathOramHw;

import java.util.*;

import pathOramHw.ORAMInterface.Operation;

public class Job1A {

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		int bucket_size = 4;
		int num_blocks = (int) Math.pow(2, 10);

		ArrayList<Integer> NumberMap = new ArrayList<Integer>();
		
		//Set the Bucket size for all the buckets.
		Bucket.setMaxSize(bucket_size);
				
		//Initialize new UntrustedStorage
		UntrustedStorageInterface storage = new ServerStorageForHW();

		//Initialize a randomness generator
		RandForORAMInterface rand_gen = new RandomForORAMHW();
		
		//Initialize a new Oram
		ORAMInterface oram = new ORAMWithReadPathEviction(storage, rand_gen, bucket_size, num_blocks);
		
		//Initialize a buffer value
		byte[] write_bbuf = new byte[32];
		for(int i = 0; i < 32; i++)
		{
			write_bbuf[i] = (byte) 0xa;
		}

//		Do same sample computation: fill an array with numbers, then read it back.
		for(int i = 0; i < 3000000; i++){
			oram.access(Operation.WRITE, i % num_blocks, write_bbuf);
			System.out.println(i);
		}
		
		for(int i = 0; i < 500000; i++){

			oram.access(Operation.WRITE, i % num_blocks, write_bbuf);

			int size = NumberMap.size();
			int StackSize = oram.getStashSize();
			System.out.println(StackSize);
			if (size <= StackSize){
				for (int j=size; j<StackSize; j++){
					NumberMap.add(0);
				}
				NumberMap.add(1);
			}
			else{
				NumberMap.set(StackSize, NumberMap.get(StackSize)+1);
			}

		}

		int ASize = NumberMap.size();
		System.out.println(-1 + "," + 500000);
		for (int i=0; i<ASize; i++){
			int counter = 0;
			for (int j=i+1; j<ASize; j++){
				counter += NumberMap.get(j);
			}
			System.out.println(i + "," + counter);
		}


		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;

				
	}
	
}
