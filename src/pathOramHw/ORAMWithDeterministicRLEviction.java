package pathOramHw;

import java.util.ArrayList;

/*
 * Name: Abdullah Al Mamun
 * NetID: amamun
 */

public class ORAMWithDeterministicRLEviction implements ORAMInterface{

	private int[] posMap;
	private int totalBlocks;
	private int treeHeight;
	private int blockSize; 
	private ArrayList<Block> clientStash;
	
	private UntrustedStorageInterface strg;
	private RandForORAMInterface randOram;
	private int buck_size;

	private int cap;
	private int comCap;

	private int G;
		
	public ORAMWithDeterministicRLEviction(UntrustedStorageInterface storage, RandForORAMInterface rand_gen, int bucket_size, int num_blocks){
		
		this.G = 0;

		this.strg = storage;
		this.totalBlocks = num_blocks;

		this.randOram = rand_gen;
		this.buck_size = bucket_size;
		this.randOram.setBound(getNumLeaves());
		this.clientStash = new ArrayList<Block>();
		this.posMap = new int[totalBlocks];

		cap = (int)( Math.ceil (Math.log(totalBlocks) / Math.log(2)) );
		comCap = (int) Math.pow(2, cap+1) - 1;

		this.strg.setCapacity(comCap);
		for (int i=0; i< comCap; i++){
			Bucket tBucket = new Bucket();
			for (int j=0; j<buck_size; j++){
				tBucket.addBlock(new Block());
			}
			this.strg.WriteBucket (i, tBucket);

		}
	}
		
	@Override
	public int[] getPositionMap(){

		return posMap;
	}


	@Override
	public byte[] access(Operation op, int blockIndex, byte[] newdata) {

		int x = posMap[blockIndex];
		posMap[blockIndex] = randOram.getRandomLeaf();

		for (int i=0; i<=treeHeight; i++){

			Bucket b = new Bucket();
			b.getBlocks().addAll(strg.ReadBucket( P(x,i) ).getBlocks());

			if (b.getBlockByKey(blockIndex).index == blockIndex){
				Block rmBlock = new Block();
				rmBlock = b.getBlockByKey(blockIndex);
				b.removeBlock(rmBlock);
				b.addBlock(new Block());
				clientStash.add(rmBlock);
			}
			strg.WriteBucket(P(x,i), b);
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

		int g = gFunc(G, treeHeight);
		G = G + 1;
		for (int i=0; i<=treeHeight; i++){
			clientStash.addAll( strg.ReadBucket( P(g,i) ).getBlocks() );
		}

		ArrayList<Block> tStash;
		
		for (int i=treeHeight; i>=0; i--){
			tStash = new ArrayList<Block>();

			for (int j=0; j<tStash.size(); j++){
				Block tempBlock = clientStash.get(j);
				if ( P(g,i) == P(posMap[tempBlock.index], i) ){
					tStash.add(tempBlock);
				}
			}
			
			int k = Math.min(tStash.size(), buck_size);
			
			for (int j=tStash.size(); j>k; j--){
				System.out.println(j);
				tStash.remove(j);
			}
			clientStash.removeAll(tStash);
			Bucket tBucket = new Bucket();
			tBucket.getBlocks().addAll(tStash);
			strg.WriteBucket(P(g, i), tBucket);
		}

		return Data;
	}

	public int gFunc(int BigG, int L){
		int k = (int)(BigG % Math.pow(2,L));

		int rev = ReverseBits(k);
		return rev;
	}
	public static int ReverseBits(int k){
        if (k == 1){
            return 2;
        }else if (k > 1){
            String a = new StringBuilder(Integer.toBinaryString(k)).reverse().toString();
            return Integer.parseInt(a, 2);
        }else{
            return -1;
        }
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

		return (int) (2 * ( Math.pow ( 2, ( Math.ceil (Math.log(totalBlocks) / Math.log(2)) ) + 1 ) ) - 1 );
	}
	
	public int getGlobalG() {

		return G;
	}
	
}
