package com.enter4ward.lwjgl;

import com.enter4ward.math.BoundingFrustum;
import com.enter4ward.math.BoundingSphere;
import com.enter4ward.math.ContainmentType;
import com.enter4ward.math.Group;
import com.enter4ward.math.IBufferObject;
import com.enter4ward.math.IModel3D;
import com.enter4ward.math.IObject3D;
import com.enter4ward.math.IntersectionInfo;
import com.enter4ward.math.Matrix;
import com.enter4ward.math.Ray;
import com.enter4ward.math.RayCollisionHandler;
import com.enter4ward.math.Space;
import com.enter4ward.math.Vector3;

public class Object3D extends IObject3D {


	public Object3D(Vector3 position, IModel3D model) {
		super(position, model);
	}

	public void draw(ShaderProgram program, BoundingFrustum frustum) {
		final Matrix matrix = getModelMatrix();
		final LWJGLModel3D model = (LWJGLModel3D) getModel();
		for (final Group g : model.getGroups()) {
			for (final IBufferObject ib : g.getBuffers()) {
				BufferObject b = (BufferObject) ib;
				final BoundingSphere sph =  new BoundingSphere(b) {{
					add(matrix.getTranslation());
				}};
				if (frustum.contains(sph) != ContainmentType.Disjoint) {
					b.bind(program);
					program.setModelMatrix(matrix);
					b.draw(program);
				}
			}
		}
	}
}
