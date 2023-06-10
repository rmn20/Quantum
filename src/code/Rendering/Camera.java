package code.Rendering;

import code.Gameplay.Map.House;
import code.Math.MathUtils2;
import code.Math.Matrix;


/**
 *
 * @author DDDENISSS
 */
public class Camera {

    private Matrix tmpPos = new Matrix();
    private Matrix tmp = new Matrix(), tmp2 = new Matrix();
    private Matrix camera = new Matrix();

    public int y = 1400, x=500, z = 1000;
    public int rotX = 0, rotY = 0;
	public float currentRotX, currentRotY;
    public int smoothSteps = 8;

    private int oldPart = -1, part = -1;
    boolean fset = true;

    public Camera() {
    }

    public void set(Matrix pos, float newRotX, float newRotY, boolean absolutePos, boolean absoluteRot) {
        if(absoluteRot) {
			tmp.setIdentity();
			newRotX = 0;
			newRotY = 0;
			tmp.setPosition(pos.m03, pos.m13, pos.m23);
		} else tmp.set(pos);
		
		if(absolutePos) tmp.setPosition(0, 0, 0);
		
        tmp.translate(x, y, z);
        tmp2.setRotX(rotX);
        tmp.mul(tmp2);
        tmp.rotY(rotY);
		
		newRotX += rotX;
		newRotY += rotY;
        
        if(fset || smoothSteps == 1) {
            camera.set(tmp);
			currentRotX = newRotX;
			currentRotY = newRotY;
            fset = false;
        } else {
            interpolation(tmpPos, tmp);
			currentRotX += (newRotX - currentRotX) / smoothSteps;
			
			if(Math.abs(newRotY + (newRotY < currentRotY ? 360 : -360) - currentRotY) <
					Math.abs(newRotY - currentRotY)) {
				currentRotY += (newRotY + (newRotY < currentRotY ? 360 : -360) - currentRotY) / smoothSteps;
			} else {
				currentRotY += (newRotY - currentRotY) / smoothSteps;
			}
            camera.set(tmpPos);
        }
    }

    private void interpolation(Matrix m1, Matrix m2) {
        m1.m00 += (m2.m00-m1.m00)/smoothSteps;
        m1.m01 += (m2.m01-m1.m01)/smoothSteps;
        m1.m02 += (m2.m02-m1.m02)/smoothSteps;

        m1.m10 += (m2.m10-m1.m10)/smoothSteps;
        m1.m11 += (m2.m11-m1.m11)/smoothSteps;
        m1.m12 += (m2.m12-m1.m12)/smoothSteps;

        m1.m20 += (m2.m20-m1.m20)/smoothSteps;
        m1.m21 += (m2.m21-m1.m21)/smoothSteps;
        m1.m22 += (m2.m22-m1.m22)/smoothSteps;

        m1.m03 = m2.m03;
        m1.m13 = m2.m13;
        m1.m23 = m2.m23;

        float l = invLength(m1.m00, m1.m01, m1.m02) * Matrix.FP;
        m1.m00 *= l;
        m1.m01 *= l;
        m1.m02 *= l;


        l = invLength(m1.m10, m1.m11, m1.m12) * Matrix.FP;
        m1.m10 *= l;
        m1.m11 *= l;
        m1.m12 *= l;

        l = invLength(m1.m20, m1.m21, m1.m22) * Matrix.FP;
        m1.m20 *= l;
        m1.m21 *= l;
        m1.m22 *= l;
    }
    
    private float invLength(int x, int y, int z) {
        return MathUtils2.invSqrt( x*x + y*y + z*z );
    }

    public void addAngle(int i) {
        rotX += i;
    }
    
    public void addY(int i) {
        y += i;
    }
    
    public void addZ(int i) {
        z += i;
    }

    public void addX(int i) {
        x += i;
    }

    public Matrix getCamera() {
        return camera;
    }

    public void calcPart(House home) {
        part = home.calcPart(oldPart, camera.m03,camera.m13, camera.m23);
        oldPart = part;
    }

    public int getPart() {
        return part;
    }
    
    public void setPart(int part) {
        this.part = part;
    }

}
