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
		this.randOram = rand_gen;
		this.buck_size = buck_size;
		this.totalBlocks = num_blocks;
		this.randOram.setBound(getNumLeaves());

	}


	@Override
	public byte[] access(Operation op, int blockIndex, byte[] newdata) {

		int x = posMap[blockIndex];
		posMap[blockIndex] = randOram;
		for (int i=0; i<=treeHeight; i++){
			clientStash.addAll( strg.ReadBucket(P(x,i)) );
		}
		byte Data;
		for (int i=0; i<clientStash.size(); i++){
			if (clientStash.get(i).index == blockIndex) {
				Data = clientStash.get(i).data;
			}
		}
		if (op == Operation.WRITE) {

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
			int k = Math.min(tStash.size(), buck_size);
			for (int j=tStash.size(); j>=k; j--){
				tStash.remove(j);
			}
			clientStash.removeAll(tStash);
			strg.WriteBucket(P(x, i), tStash);
			}
		}

		return Data;
	}


	@Override
	public int P(int leaf, int level) {

		return BinSearch(0, getNumLeaves(), 2*leaf, level);
	}

	//helper function for P(leaf, level)
	public int BinSearch(int left, int right, int leaf, int level){
		int mid = (right + left) / 2 ;
		if (level == 0){
			return mid;
		}
		if (leaf <= mid){
			return BinSearch(left, mid-1, leaf, level-1);
		}
		else {
			return BinSearch(mid+1, right, leaf, level-1);
		}

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

		return Math.pow(2,treeHeight);
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

		return 2 * ( Math.pow ( 2, ( Math.ceil (Math.log(totalBlocks) / Math.log(2)) ) ) ) - 1 ;

	}


	
}
