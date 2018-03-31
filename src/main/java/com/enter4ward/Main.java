package com.enter4ward;

import com.enter4ward.lwjgl.*;
import com.enter4ward.math.*;
import org.lwjgl.opengl.GL11;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL20.glUseProgram;


public class Main extends Game {

    private static final int NUMBER_OF_OBJECTS = 0;
  //  private static final int NUMBER_OF_OBJECTS = 500000;
    /**
     * The buffer builder.
     */
    private static IBufferBuilder bufferBuilder = () -> new BufferObject(true);

    DrawableSphere sphere;
    LWJGLModel3D boxModel;
    Object3D box;
    Object hit;
    List<Object> tests = new ArrayList<>();


    private static final Random RANDOM = new Random();

    public final int MAP_SIZE = 1024;
    public final float DISTANCE = 32;
    public final float SPEED = 0.5f;

    Camera camera;
    float time = 0;


    /**
     * The space.
     */
    private Space space;
    private DrawableBox cubeModel;
    private Vector3 cameraPos;

    public Main(int w, int h) {
        super(w, h);
    }

    public static void main(String[] args) {
        new Main(1280, 720);
    }

    @Override
    public void setup() {
        camera = new Camera(0.1f, 128f);
        cameraPos = new Vector3(48, 24, 48);
        camera.update(1280, 720);
        space = new Space(16);
        cubeModel = new DrawableBox();

        try {
            boxModel = new LWJGLModel3D("box.json", 1f, bufferBuilder);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        sphere = new DrawableSphere();
        box = new Object3D(new Vector3(0, 0, 0), boxModel);
        box.setNode(space.insert(box.getBoundingSphere(), box));

        new Object3D(new Vector3(0, 0, 0), boxModel) {{
            space.insert(getBoundingSphere(), this);
        }};
        new Object3D(new Vector3(DISTANCE, 0, 0), boxModel) {{
            space.insert(getBoundingSphere(), this);
        }};
        new Object3D(new Vector3(-DISTANCE, 0, 0), boxModel) {{
            space.insert(getBoundingSphere(), this);
        }};
        new Object3D(new Vector3(0, 0, DISTANCE), boxModel) {{
            space.insert(getBoundingSphere(), this);
        }};
        new Object3D(new Vector3(0, 0, -DISTANCE), boxModel) {{
            space.insert(getBoundingSphere(), this);
        }};

        for(int i=0; i < NUMBER_OF_OBJECTS; ++i ){
            new Object3D(new Vector3((RANDOM.nextFloat()-0.5f)*MAP_SIZE, (RANDOM.nextFloat()-0.5f)*MAP_SIZE,(RANDOM.nextFloat()-0.5f)*MAP_SIZE), boxModel) {{
                space.insert(getBoundingSphere(), this);
            }};
        }


        camera.lookAt(cameraPos, new Vector3(0, 0, 0), new Vector3(0, 1, 0));
        getProgram().setLightPosition(0, new Vector3(3, 3, 3));
        getProgram().setAmbientColor(0, 0, 0);
        getProgram().setDiffuseColor(1, 1, 1);
        getProgram().setMaterialShininess(1000);
        getProgram().setLightColor(0, new Vector3(1, 1, 1));
        getProgram().setLightPosition(0, new Vector3(128, 128, 128));
    }

    @Override
    public void update(float deltaTime) {
        time += deltaTime;
        getProgram().setTime(time);
        getProgram().update(camera);


        box.setPosition(new Vector3(DISTANCE * (float) Math.sin(SPEED * time), 0f, DISTANCE * (float) Math.cos(SPEED * time)));
        box.setNode(space.update(box.getBoundingSphere(), box.getNode(), box));
        tests = new ArrayList<>();
        hit = null;
        tests.clear();
        BoundingSphere boxSphere = box.getBoundingSphere();
        space.handleObjectCollisions(boxSphere, obj -> {
            Object3D o3d = (Object3D) obj;

            if (box != o3d && boxSphere.intersects(o3d.getBoundingSphere())) {
                hit = o3d;
            }
            else {
                tests.add(o3d);
            }
        });

    }

    @Override
    public void draw() {
        camera.update();

        getProgram().setModelMatrix(Matrix.IDENTITY);
        getProgram().setLightPosition(0, new Vector3(0, 32, 0));
        getProgram().use();
        glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        useDefaultShader();

        space.handleVisibleObjects(camera, obj -> {
            getProgram().reset();
            getProgram().setMaterialAlpha(1f);
            getProgram().setOpaque(true);

            if (obj instanceof Space.Node) {
                Space.Node node = (Space.Node) obj;
                if (node.containerSize() > 0) {
                    getProgram().setAmbientColor(0f, 1f, 0f);
                } else {
                    return;
                }

                Vector3 min = new Vector3(node.getMinX(), node.getMinY(), node.getMinZ());
                Vector3 max = new Vector3(node.getMaxX(), node.getMaxY(), node.getMaxZ());
                cubeModel.draw(getProgram(), min, max);
            } else if (obj instanceof Object3D) {
                Object3D obj3d = (Object3D) obj;
                if (obj3d == hit) {
                    getProgram().setAmbientColor(1f, 0f, 0f);
                }
                else if (obj3d == box) {
                    getProgram().setAmbientColor(0f, 0f, 1f);
                }
                else if(tests.contains(obj3d)){
                    getProgram().setAmbientColor(1f, 1f, 0f);
                }

                obj3d.draw(getProgram(), camera);

                getProgram().setOpaque(false);
                sphere.draw(getProgram(), obj3d.getBoundingSphere());

            }
        });

        space.handleVisibleObjects(camera, obj -> {
            getProgram().setMaterialAlpha(.2f);
            getProgram().setOpaque(false);

            if (obj instanceof Space.Node) {
                Space.Node node = (Space.Node) obj;
                if (node.containerSize() == 0) {
                    getProgram().setAmbientColor(1f, 1f, 1f);
                } else {
                    return;
                }
                Vector3 min = new Vector3(node.getMinX(), node.getMinY(), node.getMinZ());
                Vector3 max = new Vector3(node.getMaxX(), node.getMaxY(), node.getMaxZ());
                cubeModel.draw(getProgram(), min, max);
            }
        });

        getProgram().setAmbientColor(0f, 0f, 0f);
        glUseProgram(0);
        // setTitle();

    }

}