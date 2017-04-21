package pathOramHw;

import java.util.ArrayList;

import javax.management.RuntimeErrorException;

/*
 * Name: Abdullah Al Mamun
 * NetID: amamun
 */

public class Bucket{
	private static boolean is_init = false;
	private static int max_size_Z = -1;

	private ArrayList<Block> BucketBlocks;
	protected int size; // this is the counter I added
	
	//TODO Add necessary variables
	
	Bucket(){
		if(is_init == false)
		{
			throw new RuntimeException("Please set bucket size before creating a bucket");
		}
		//TODO Must complete this method for submission
		BucketBlocks = new ArrayList<Block>(max_size_Z);
		for (int i = 0; i<max_size_Z; i++){
			BucketBlocks.add(new Block());
		}
		size = 0; // this is where i initialized it
	}
	
	// Copy constructor
	Bucket(Bucket other)
	{
		if(other == null)
		{
			throw new RuntimeException("the other bucket is not malloced.");
		}
		BucketBlocks = new ArrayList<Block>(max_size_Z);
		for (int i=0; i<other.getBlocks().size(); i++){
			BucketBlocks.add(new Block(other.getBlocks().get(i)));
		}
		size = other.size;
	}
	
	//Implement and add your own methods.
	Block getBlockByKey(int key){

		for (int i=0; i<BucketBlocks.size(); i++){
			if (BucketBlocks.get(i).index == key){
				return BucketBlocks.get(i);
			}
		}

		return null;
	}
	
	void addBlock(Block new_blk){

		BucketBlocks.set(size, new_blk);
		// BucketBlocks.add(new_blk);
		size++;
	}
	
	boolean removeBlock(Block rm_blk)
	{
		for (int i=0; i<BucketBlocks.size(); i++){
			if (BucketBlocks.get(i).index == rm_blk.index){
				BucketBlocks.remove(i);
				BucketBlocks.add(new Block());
				size--;
				return true;
			}
		}
		return false;
	}
	
	
	ArrayList<Block> getBlocks(){

		return BucketBlocks;
	}
	
	int returnRealSize(){

		return size;
	}

	static void resetState()
	{
		is_init = false;
	}

	static void setMaxSize(int maximumSize)
	{
		if(is_init == true)
		{
			throw new RuntimeException("Max Bucket Size was already set");
		}
		max_size_Z = maximumSize;
		is_init = true;
	}

}