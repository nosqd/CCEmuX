package com.tom.peripherals.gpu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import com.tom.peripherals.api.TMLuaException;
import com.tom.peripherals.api.TMLuaMethod;
import com.tom.peripherals.api.TMLuaObject;
import com.tom.peripherals.gpu.BaseGPU.GPUContext;
import com.tom.peripherals.gpu.VRAM.VRAMObject;
import com.tom.peripherals.math.Matrix4d;
import com.tom.peripherals.math.Vec2d;
import com.tom.peripherals.math.Vec3d;
import com.tom.peripherals.math.Vec4d;
import com.tom.peripherals.util.ParamCheck;
import dan200.computercraft.api.lua.LuaException;

public class GPU3D extends TMLuaObject implements VRAMObject {
	protected TextureManager tm;
	protected TriBuilder buildingTri;
	protected Matrix4d mat = new Matrix4d().identity();
	protected Matrix4d proj = new Matrix4d();
	protected Stack<Matrix4d> mstack = new Stack<>();
	protected GPUContext ctx;
	protected List<Triangle> triangles = new ArrayList<>();
	protected Vec3d nearPlane;
	protected Vec3d camera = new Vec3d();
	protected Vec3d directionalLight = new Vec3d();
	protected Vec4d color = new Vec4d(1, 1, 1, 1);
	protected float[] depthBuffer;

	public GPU3D(GPUContext ctx) {
		this.ctx = ctx;
		this.tm = new TextureManager(ctx.getVRam());
		ctx.getVRam().alloc(this);
	}

	@TMLuaMethod
	public void glFrustum(Object[] a) throws TMLuaException {
		if (a.length < 3) throw new TMLuaException("Too few arguments, expected: number fov, number Znear, number Zfar");
		double fov = ParamCheck.getDouble(a, 0);
		double zNear = ParamCheck.getDouble(a, 1);
		double zFar = ParamCheck.getDouble(a, 2);
		double rtanFov = 1f / Math.tan(Math.toRadians(fov / 2));
		proj = new Matrix4d();
		proj.m00 = (ctx.getHeight() / (double) ctx.getWidth()) * rtanFov;
		proj.m11 = rtanFov;
		proj.m22 = zFar / (zFar - zNear);
		proj.m23 = 1;
		proj.m32 = (-zFar * zNear) / (zFar - zNear);
		nearPlane = new Vec3d(0, 0, zNear);
	}

	@TMLuaMethod
	public void glDirLight(Object[] a) throws TMLuaException {
		if (a.length < 3) throw new TMLuaException("Too few arguments, expected: number x, number y, number z");
		directionalLight.x = ParamCheck.getDouble(a, 0);
		directionalLight.y = ParamCheck.getDouble(a, 1);
		directionalLight.z = ParamCheck.getDouble(a, 2);
		directionalLight = mat.mul(directionalLight);
		directionalLight.normalize();
	}

	@TMLuaMethod
	public void render() throws TMLuaException {
		int W = ctx.getWidth();
		int H = ctx.getHeight();
		if (depthBuffer == null || depthBuffer.length != W * H) {
			ctx.getVRam().reallocEx(this, W * H * 4 + 1024);
			depthBuffer = new float[W * H];
		}
		List<Triangle> tris = new ArrayList<>();
		for (Triangle tri : triangles) {
			double light = 1;
			if (true) {
				Vec3d normal = tri.normal();

				double normalDot = normal.x * (tri.vert[0][Triangle.POS_DATA].x - camera.x)
						+ normal.y * (tri.vert[0][Triangle.POS_DATA].y - camera.y)
						+ normal.z * (tri.vert[0][Triangle.POS_DATA].z - camera.z);

				if (normalDot > 0) continue;
				if (true) {
					light = normal.dotProduct(directionalLight);
				}
				// Vertex Layer
			}
			Triangle dup = new Triangle(tri);
			List<Triangle> clipped = dup.triangleClipAgainstPlane(nearPlane, new Vec3d(0, 0, 1));

			for (Triangle ctri : clipped) {
				Triangle nt = new Triangle(ctri);

				for (int i = 0; i < tri.vert.length; i++) {
					Vec4d vec3d = tri.vert[i][Triangle.POS_DATA];
					Vec4d a = new Vec4d();
					proj.mul(vec3d, a);
					Vec4d uv = ctri.vert[i][Triangle.TEX_DATA];
					Vec4d nuv = new Vec4d();
					double w = a.w;
					if (w != 0) {
						nuv.x = uv.x / w;
						nuv.y = uv.y / w;
						nuv.z = 1d / w;
						a.x /= w;
						a.y /= w;
						a.z /= w;
					}
					a.x += 1;
					a.y += 1;
					a.x *= 0.5 * W;
					a.y *= 0.5 * H;
					nt.vert[i][Triangle.POS_DATA] = a;
					Vec4d c = ctri.vert[i][Triangle.COLOR_DATA].mulI(light);
					c.clip(0, 1);
					nt.vert[i][Triangle.COLOR_DATA] = c;
					nt.vert[i][Triangle.TEX_DATA] = nuv;
				}
				//nt.outline(this);

				tris.add(nt);
			}
		}
		tris.sort(Triangle.COMPARE_Z);
		for (Triangle tr : tris) {
			tr.clipAndBlit(this);
		}
	}

	@TMLuaMethod
	public void sync() throws LuaException {
		ctx.sync();
	}

	@TMLuaMethod
	public void clear() {
		for (int i = 0; i < ctx.getWidth(); i++) {
			for (int j = 0; j < ctx.getHeight(); j++) {
				ctx.set(i, j, 0);
			}
		}
		triangles.clear();
		mat.identity();
		if (depthBuffer != null) Arrays.fill(depthBuffer, 0f);
	}

	@TMLuaMethod
	public void glBegin(Object[] a) throws TMLuaException {
		if (buildingTri != null) throw new TMLuaException("Already building");
		int id = ParamCheck.optionalInt(a, 0, GLConstants.GL_TRIANGLES);
		buildingTri = TriBuilder.builder(id, triangles, () -> mat, tm::getTextureID);
	}

	@TMLuaMethod
	public void glEnd() throws TMLuaException {
		if (buildingTri == null) throw new TMLuaException("not building");
		buildingTri.finish();
		buildingTri = null;
	}

	@TMLuaMethod
	public void glVertex(Object[] a) throws TMLuaException {
		if (a.length < 3) throw new TMLuaException("Too few arguments, expected: number x, number y, number z");
		double x = ParamCheck.getDouble(a, 0);
		double y = ParamCheck.getDouble(a, 1);
		double z = ParamCheck.getDouble(a, 2);
		if (buildingTri == null) throw new TMLuaException("not building");
		buildingTri.append(new Vec3d(x, y, z));
		buildingTri.setColor(new Vec4d(color));
		buildingTri.setUV(new Vec2d());
	}

	@TMLuaMethod
	public void glTexCoord(Object[] a) throws TMLuaException {
		if (a.length < 2) throw new TMLuaException("Too few arguments, expected: number u, number v");
		double u = ParamCheck.getDouble(a, 0);
		double v = ParamCheck.getDouble(a, 1);
		if (buildingTri == null) throw new TMLuaException("not building");
		buildingTri.setUV(new Vec2d(u, v));
	}

	@TMLuaMethod
	public void glColor(Object[] a) throws TMLuaException {
		int c = ParamCheck.toColor(a, 0);
		color = new Vec4d(((c >> 16) & 0xFF) / 255f, ((c >> 8) & 0xFF) / 255f, (c & 0xFF) / 255f,
				((c >> 24) & 0xFF) / 255f);
		if (buildingTri != null) buildingTri.setColor(new Vec4d(color));
	}

	@TMLuaMethod
	public void glLoadIdentity() {
		mat.identity();
	}

	@TMLuaMethod
	public void glPushMatrix() {
		mstack.push(new Matrix4d(mat));
	}

	@TMLuaMethod
	public void glPopMatrix() throws TMLuaException {
		if (mstack.peek() == null) throw new TMLuaException("no element in stack");
		mat = mstack.pop();
	}

	@TMLuaMethod
	public void glTranslate(Object[] a) throws TMLuaException {
		if (a.length < 3) throw new TMLuaException("Too few arguments, expected: number x, number y, number z");
		double x = ParamCheck.getDouble(a, 0);
		double y = ParamCheck.getDouble(a, 1);
		double z = ParamCheck.getDouble(a, 2);
		mat.translate(x, y, z);
	}

	@TMLuaMethod
	public void glScale(Object[] a) throws TMLuaException {
		if (a.length < 3) throw new TMLuaException("Too few arguments, expected: number x, number y, number z");
		double x = ParamCheck.getDouble(a, 0);
		double y = ParamCheck.getDouble(a, 1);
		double z = ParamCheck.getDouble(a, 2);
		mat.scale(x, y, z);
	}

	@TMLuaMethod
	public void glRotate(Object[] a) throws TMLuaException {
		if (a.length < 4)
			throw new TMLuaException("Too few arguments, expected: number angle, number x, number y, number z");
		double d = ParamCheck.getDouble(a, 0);
		double x = ParamCheck.getDouble(a, 1);
		double y = ParamCheck.getDouble(a, 2);
		double z = ParamCheck.getDouble(a, 3);
		mat.rotate(Math.toRadians(d), x, y, z);
	}

	@TMLuaMethod
	public int glGenTextures(Object[] a) throws TMLuaException {
		return tm.genTextureID(a);
	}

	@TMLuaMethod
	public void glDeleteTextures(Object[] a) throws TMLuaException {
		tm.deleteTextures(a);
	}

	@TMLuaMethod
	public void glBindTexture(Object[] a) throws TMLuaException {
		tm.bindTexture(a);
	}

	@TMLuaMethod
	public void glTexImage(Object[] a) throws TMLuaException {
		tm.texImage(a);
	}

	@TMLuaMethod
	public Object getBounds() throws TMLuaException {
		return ctx.getBounds();
	}

	@Override
	public long getSize() {
		return (depthBuffer != null ? depthBuffer.length * 4 : 0) + 1024;
	}

	@TMLuaMethod
	public void glEnable(Object[] a) throws TMLuaException {
		int mode = ParamCheck.getInt(a, 0);
		switch (mode) {
		case GLConstants.GL_TEXTURE_2D -> tm.setTexEnabled(true);
		default ->
		throw new TMLuaException("Bad argument #1: unknown GL state" + mode);
		}
	}

	@TMLuaMethod
	public void glDisable(Object[] a) throws TMLuaException {
		int mode = ParamCheck.getInt(a, 0);
		switch (mode) {
		case GLConstants.GL_TEXTURE_2D -> tm.setTexEnabled(false);
		default ->
		throw new TMLuaException("Bad argument #1: unknown GL state" + mode);
		}
	}

	@TMLuaMethod
	public Object[] getConstants() {
		return GLConstants.ALL_CONST;
	}
}
