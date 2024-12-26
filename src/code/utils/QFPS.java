package code.utils;

/**
 *
 * @author Roman
 */
public class QFPS extends FPS {
	
	public static void frame() {
		FPS.frame();
		if(frameTime < 1) frameTime = 1;
		else if(frameTime > 200) frameTime = 200;
	}
	
}
