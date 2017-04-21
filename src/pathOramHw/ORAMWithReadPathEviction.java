package pathOramHw;

import java.util.ArrayList;
import java.lang.*;

/*
 * Name: Abdullah Al Mamun
 * NetID: amamun
 */

public class ORAMWithReadPathEviction implements ORAMInterface{
	

	private int[] posMap;
	private int totalBlocks;
	private int treeHeight;
	private int blockSize; 
	private ArrayList<Block> clientStash;
	
	private UntrustedStorageInterface strg;
	private RandForORAMInterface randOram;
	private int buck_size;

	public ORAMWithReadPathEviction(UntrustedStorageInterface storage, RandForORAMInterface rand_gen, int bucket_size, int num_blocks){

		this.strg = storage;
		this.totalBlocks = num_blocks;

		this.randOram = rand_gen;
		this.buck_size = bucket_size;
		this.clientStash = new ArrayList<Block>();
		this.posMap = new int[totalBlocks];


		this.treeHeight = (int)( Math.ceil (Math.log(totalBlocks) / Math.log(2)) );;
		this.randOram.setBound(getNumLeaves());

		for (int i=0; i<posMap.length; i++){
			posMap[i] = randOram.getRandomLeaf();
		}

		
		this.strg.setCapacity(getNumBuckets());

		Bucket tBucket = new Bucket();
		for (int i=0; i< getNumBuckets(); i++){

			this.strg.WriteBucket (i, tBucket);

		}

	}


	@Override
	public byte[] access(Operation op, int blockIndex, byte[] newdata) {

		int x = posMap[blockIndex];
		posMap[blockIndex] = randOram.getRandomLeaf();

		for (int i=0; i<=treeHeight; i++){
			// clientStash.addAll( strg.ReadBucket( P(x,i) ).getBlocks() );

			Bucket tempBucket = strg.ReadBucket( P(x,i) );
			int counter = tempBucket.returnRealSize();
			
			for (int j=0; j<counter; j++){
				clientStash.add(tempBucket.getBlocks().get(j));
			}
		}

		byte[] Data = null;
		for (int i=0; i<clientStash.size(); i++){
			if (clientStash.get(i).index == blockIndex) {
				Data = clientStash.get(i).data;
			}
		}
		if (op == Operation.WRITE) {
			ArrayList<Block> tempStash;
			tempStash = clientStash;
			for (int i=0; i<tempStash.size(); i++){
				if (tempStash.get(i).index == blockIndex){
					tempStash.remove(i);
				}
			}
			Block kBlock = new Block(blockIndex, newdata);
			tempStash.add(kBlock);
			clientStash = tempStash;
		}

		ArrayList<Block> tStash;
		
		for (int i=treeHeight; i>=0; i--){
			tStash = new ArrayList<Block>();

			for (int j=0; j<clientStash.size(); j++){
				Block tempBlock = clientStash.get(j);
				if ( P(x,i) == P(posMap[tempBlock.index], i) ){
					tStash.add(tempBlock);
				}
			}
			
			int k = Math.min(tStash.size(), buck_size);


			tStash.removeAll(tStash.subList(k, tStash.size()));
			clientStash.removeAll(tStash);
			Bucket tBucket = new Bucket();


			for (int m=0; m<tStash.size(); m++){
				tBucket.addBlock(tStash.get(m));
			}
			strg.WriteBucket(P(x, i), tBucket);
		}


		return Data;
	}


	@Override
	public int P(int leaf, int level) {

		int a = (int) Math.pow(2, treeHeight-level);
		int b = 2 *(leaf/a) + 1;
		return a*b - 1;
	}


	@Override
	public int[] getPositionMap() {

		return posMap;
	}


	@Override
	public ArrayList<Block> getStash() {

		return clientStash;
	}


	@Override
	public int getStashSize() {

		return clientStash.size();
	}

	@Override
	public int getNumLeaves() {

		return (int) Math.pow(2,treeHeight);
	}


	@Override
	public int getNumLevels() {

		return treeHeight;
	}


	@Override
	public int getNumBlocks() {

		return totalBlocks;
	}


	@Override
	public int getNumBuckets() {

		return (int) Math.pow(2, this.treeHeight+1) - 1;

	}


	
}
