package com.enter4ward.math;


// TODO: Auto-generated Javadoc
/**
 * The Class IObject3D.
 */
public abstract class IObject3D {

    /** The position. */
    private Vector3 position;

    /** The rotation. */
    private Quaternion rotation = new Quaternion().identity();

    /** The model. */
    private IModel3D model;

    private Space.Node node;

    public void setModel(IModel3D model) {
        this.model = model;
    }

    /**
     * Gets the model matrix.
     *
     * @return the model matrix
     */
    public Matrix getModelMatrix() {
        return new Matrix().createFromQuaternion(rotation).translate(
                position);
    }

    /**
     * Gets the model.
     *
     * @return the model
     */
    public IModel3D getModel() {
        return model;
    }

    /**
     * Instantiates a new i object3 d.
     *
     * @param position
     *            the position
     * @param model
     *            the model
     */
    public IObject3D(Vector3 position, IModel3D model) {
        this.position = position;
        this.model = model;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.enter4ward.math.api.ISphere#getPosition()
     */
    /**
     * Gets the bounding sphere.
     *
     * @return the bounding sphere
     */
    public BoundingSphere getBoundingSphere() {
        // return new
        // Vector3(model.getContainer().getCenter()).transform(rotation).add(position);
        BoundingSphere cont = model.getContainer();
        return new BoundingSphere(){{
            set(cont).transform(rotation).add(position);
            setRadius(cont.getRadius());
        }};
    }

    /**
     * Gets the position.
     *
     * @return the position
     */
    public Vector3 getPosition() {
        return position;
    }

    /**
     * Sets the position.
     *
     * @param position
     *            the new position
     */
    public void setPosition(final Vector3 position) {
        this.position = position;
    }

    /**
     * Insert.
     *
     * @param space
     *            the space
     * @return the i object3 d
     */
    protected Space.Node insert(final Space space) {
        return space.insert(getBoundingSphere(), this);
    }

    /**
     * Gets the rotation.
     *
     * @return the rotation
     */
    public Quaternion getRotation() {
        return rotation;
    }

    /**
     * Closest triangle.
     *
     * @param ray
     *            the ray
     * @return the intersection info
     */
    public IntersectionInfo closestTriangle(final Ray ray) {
        IntersectionInfo info = null;
        final Model3D model = (Model3D) getModel();

        for (Group g : model.getGroups()) {
            for (IBufferObject b : g.getBuffers()) {

                for (Triangle t : b.getTriangles()) {
                    final Float i = ray.intersects(t);
                    if (i != null && (info == null || i < info.distance)) {
                        if (info == null)
                            info = new IntersectionInfo();
                        info.distance = i;
                        info.triangle = t;
                    }
                }
            }
        }
        return info;
    }

    public Space.Node getNode() {
        return node;
    }

    public void setNode(Space.Node node) {
        this.node = node;
    }
}
