package stellarium.client.gui.content;

import net.minecraft.util.MathHelper;

public class RectangleBound implements IRectangleBound {
	
	public float posX, posY, width, height;
	
	public RectangleBound(float posX, float posY, float width, float height) {
		this.posX = posX;
		this.posY = posY;
		this.width = width;
		this.height = height;
	}
	
	public RectangleBound(IRectangleBound bound) {
		this.set(bound);
	}

	public void set(float posX, float posY, float width, float height) {
		this.posX = posX;
		this.posY = posY;
		this.width = width;
		this.height = height;
	}
	
	public void set(IRectangleBound bound) {
		this.posX = bound.getLeftX();
		this.posY = bound.getUpY();
		this.width = bound.getWidth();
		this.height = bound.getHeight();
	}
	
	public void setAsIntersection(IRectangleBound outer) {
		float rightX = this.getRightX();
		float downY = this.getDownY();
		this.posX = Math.max(this.posX, outer.getLeftX());
		this.posY = Math.max(this.posY, outer.getUpY());
		this.width = Math.min(rightX, outer.getRightX()) - this.posX;
		this.height = Math.min(downY, outer.getDownY()) - this.posY;
	}
	
	public void extend(float leftX, float upY, float rightX, float downY) {
		this.posX -= leftX;
		this.posY -= upY;
		this.width += (leftX + rightX);
		this.height += (upY + downY);
	}

	@Override
	public float getLeftX() {
		return this.posX;
	}

	@Override
	public float getUpY() {
		return this.posY;
	}

	@Override
	public float getWidth() {
		return this.width;
	}

	@Override
	public float getHeight() {
		return this.height;
	}

	@Override
	public float getRightX() {
		return this.posX + this.width;
	}

	@Override
	public float getDownY() {
		return this.posY + this.height;
	}

	@Override
	public boolean isInBound(float x, float y) {
		return x >= this.getLeftX() && y >= this.getUpY() && x < this.getRightX() && y < this.getDownY();
	}

	@Override
	public boolean isEmpty() {
		return this.width <= 0.0 || this.height <= 0.0;
	}

	@Override
	public float getRatioX(float x) {
		return MathHelper.clamp_float((x - this.posX) / this.width, 0.0f, 1.0f);
	}

	@Override
	public float getRatioY(float y) {
		return MathHelper.clamp_float((y - this.posY) / this.height, 0.0f, 1.0f);
	}

	@Override
	public float getMainX(float ratioX) {
		return this.posX + ratioX * this.width;
	}

	@Override
	public float getMainY(float ratioY) {
		return this.posY + ratioY * this.height;
	}

}
