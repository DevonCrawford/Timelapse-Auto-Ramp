import java.io.File;
import java.util.ArrayList;

/* All changes need to store similar data. This class is used as
 * the main object to store any setting change between images.
 * Other objects inherit this class, adding data, such as exposure
 * change, temperature change, etc. 
 * 
 * by Devon Crawford
 */

public class Change {

	Util util = new Util();
	ArrayList<Image> images;
	private double magnitude;
	protected double increments;
	protected int lastListNum, startListNum;
	protected int totalImages;
	
	// Initializing change data
	public Change(ArrayList<Image> imgs, int sln, int pln, double mag) {
		images = imgs;
		startListNum = sln;
		lastListNum = pln;
		totalImages = (lastListNum - startListNum) + 1;
		magnitude = mag;
		increments = magnitude / totalImages;
	}
	
	// Gets last image before change
	public int getLastListNum() {
		return lastListNum;
	}
	
	// Updates xmp file (settings) of all images within change sequence
	public void updateMetadata(String key) {
		// Gets start value of change
		double startValue = Double.parseDouble(util.find(images.get(startListNum).getXMP(), key));
		
		for(int i = startListNum; i <= lastListNum; i++) {
			Image currImg = images.get(i);
			
			// Gets the xmp file
			File file = currImg.getXMP();
			
			// Creates new value, incremented
			double newValue = startValue + (increments * (i-startListNum));
			
			// Replaces string data with new value
			String newData = util.replace(file, key, newValue);
			
			if(key.equals("Exposure")) {
				double exposure = Double.parseDouble(util.find(currImg.getXMP(), "Exposure2012"));
				currImg.setExposureOffset(exposure);
			}
			else if(key.equals("Temperature")) {
				int whiteBalance = Integer.parseInt(util.find(currImg.getXMP(), "Temperature"));
				currImg.setWhiteBalance(whiteBalance);
			}
			
			// Overwrites xmp file with new string data
			util.writeFile(currImg, newData);
		}
	}

	// Finds an image name from its list number
	private String NumToName(int num) {
		for (int i = 0; i < images.size(); i++) {
			if (i == num) {
				return images.get(i).getName();
			}
		}
		return null;
	}

	// Returns a string representation of a Change for human readability.
	public String toString() {
		return NumToName(startListNum) + " - " + NumToName(lastListNum)
			+ " (" + startListNum + " - " + lastListNum + ")"
			+ " [" + totalImages + " images]\n"
			+ "(" + magnitude + ")"
			+ " [" + increments + "]";
	}
}
