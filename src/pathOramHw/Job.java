/*
 *  You can use this class to test your Oram (for correctness, not security).
 *  
 *  You can experiment modifying this class, but we will not take it into account (we will test your ORAM implementations on this as well as other Jobs)
 *  
 */

package pathOramHw;

import java.util.Arrays;

import pathOramHw.ORAMInterface.Operation;

public class Job {

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		int bucket_size = 4;
		int num_blocks = (int) Math.pow(2, 10);
		
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
			System.out.println("dbg written block " + i + " has stash size: " + oram.getStashSize());
		}
		
		for(int i = 0; i < num_blocks; i++){
			System.out.println("dbg read from " + i + " value is :" + Arrays.toString(oram.access(Operation.READ, i, new byte[32])));
		}
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println(totalTime);

				
	}
	
}
