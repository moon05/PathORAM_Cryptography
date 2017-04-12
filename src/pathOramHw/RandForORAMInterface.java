package pathOramHw;

import java.util.ArrayList;

public interface RandForORAMInterface {
	
	/**
	 * @return a number from the uniform distribution
	 */
	abstract int getRandomLeaf();

	abstract void setBound(int num_leaves);
	
}
