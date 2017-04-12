/*
 * You are NOT allowed to change this class.
 * 
 * This is an class that provides the methods ORAM needs to interact with its (possibly remote) untrusted storage.
 * 
 */

package pathOramHw;

public class Block{
	public int index;
	public byte[] data;
	
	Block(int index, byte[] data) {
		this.index = index;
		this.data = new byte[32];
		System.arraycopy(data, 0, this.data, 0, this.data.length);
	}
	
	Block(Block b){
		this.index = b.index;
		this.data = new byte[32];
		System.arraycopy(b.data, 0, this.data, 0, this.data.length);
	}

	Block(){
		this.index = -1; //dummy index
		this.data = new byte[32];
	}
	
}
