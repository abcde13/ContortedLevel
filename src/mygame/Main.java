package mygame;

import com.bulletphysics.collision.shapes.SphereShape;
import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.ChaseCamera;
import com.jme3.input.FlyByCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Plane;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Sphere.TextureMode;

/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    BulletAppState bas;
   
    private float ROTATE_ANGLE = 2;
    private Vector3f CAM_ZOOM = new Vector3f(-5,-5,-5);
    private float  CAM_X = 55;
    private float PURE_ISO_ANGLE = 36.246f;
    private Node building;
    private Geometry ball;
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
        setDisplayFps(true);
        setDisplayStatView(true);
        
        bas = new BulletAppState();
        stateManager.attach(bas);
        
        flyCam.setEnabled(false);
        FlyByCamera camera = new FlyByCamera(cam);
        float CAM_Y = (float) (Math.tan(Math.toRadians(PURE_ISO_ANGLE)) * CAM_X * Math.sqrt(2));
        
        cam.setLocation(new Vector3f(CAM_X,CAM_Y,CAM_X));
        cam.lookAt(Vector3f.ZERO.add(0, 10, 0), Vector3f.UNIT_Y);
        

        
        initScene();
        
        Sphere sphere = new Sphere(30,30,1f);
        sphere.setTextureMode(TextureMode.Projected);
        ball = new Geometry("ball",sphere);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        ball.setMaterial(mat);
        RigidBodyControl ballControl = new RigidBodyControl(1f);
        ball.addControl(ballControl);
        rootNode.attachChild(ball);
        bas.getPhysicsSpace().add(ball);
        ballControl.setPhysicsLocation(new Vector3f(-5, 50, -5));
        ballControl.setLinearDamping(.1f);
        ballControl.setAngularDamping(.1f);
        ballControl.setRestitution(0);
        
        
        setUpKeys();
        
        
        bas.setDebugEnabled(true);
        
    }

    @Override
    public void simpleUpdate(float tpf) {
        
        
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    private void initScene(){
        
        building = (Node)assetManager.loadModel("Models/Tower.j3o");
        Vector3f extent = ((BoundingBox) building.getWorldBound()).getExtent(new Vector3f());
        CompoundCollisionShape sceneShape = new CompoundCollisionShape();
        BoxCollisionShape floor = new BoxCollisionShape(new Vector3f(extent.getX(),.5f,extent.getZ()));
        sceneShape.addChildShape(floor, Vector3f.ZERO);
        BoxCollisionShape twostory = new BoxCollisionShape(
                new Vector3f(extent.getX()/4f+2.5f,extent.getY()/2f-1.5f,extent.getZ()/4f+1.5f));
        sceneShape.addChildShape(twostory, new Vector3f(extent.getX()/2-3.2f,extent.getY()/2-1f,extent.getZ()/2-3));
        BoxCollisionShape threestory = new BoxCollisionShape(
                new Vector3f(extent.getX()/4f+2.5f,extent.getY()/1.5f,extent.getZ()/4f+1.5f));
        sceneShape.addChildShape(threestory, new Vector3f(extent.getX()/2-3.5f,extent.getY()/1.5f,-extent.getZ()/2+3f));
        BoxCollisionShape fourstory = new BoxCollisionShape(
                new Vector3f(extent.getX()/4f+2.5f,extent.getY()/1.1f,extent.getZ()/4f+1.5f));
        sceneShape.addChildShape(fourstory, new Vector3f(-extent.getX()/2+3.5f,extent.getY()/1.1f-.5f,-extent.getZ()/2+3f));
        RigidBodyControl scenePhysics = new RigidBodyControl(sceneShape,1f);
        scenePhysics.setKinematic(true);
        building.addControl(scenePhysics);
        rootNode.attachChild(building);
        bas.getPhysicsSpace().add(scenePhysics);
        System.out.println(extent);
        
        
    }
    
    private void setUpKeys() {
        
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("RotateLeft",new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("RotateRight",new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("ZoomIn",new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("ZoomOut",new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addListener(analogListener,"Left");
        inputManager.addListener(analogListener,"Right");
        inputManager.addListener(analogListener,"Jump");
        inputManager.addListener(analogListener,"Down");
        inputManager.addListener(analogListener,"Up");
        inputManager.addListener(analogListener,"RotateLeft");
        inputManager.addListener(analogListener,"RotateRight");
        inputManager.addListener(analogListener,"ZoomIn");
        inputManager.addListener(analogListener,"ZoomOut");

    }
    
    private AnalogListener analogListener = new AnalogListener() {

        

        public void onAnalog(String name, float value, float tpf) {
            if(name.equals("Left")){
                building.rotate(0,0,tpf*.5f);
            } else if(name.equals("Right")){
                building.rotate(0,0,-tpf*.5f);
            } else if(name.equals("Up")){
                building.rotate(-tpf*.5f,0,0);
            } else if(name.equals("Down")){
                building.rotate(tpf*.5f,0,0);
            } else if(name.equals("RotateLeft")){
                Vector3f curr = cam.getLocation();
                Quaternion turn = new Quaternion();
                turn.fromAngleAxis(-ROTATE_ANGLE * tpf,Vector3f.UNIT_Y);
                turn.multLocal(curr);
                cam.setLocation(curr);
                cam.lookAt(Vector3f.ZERO.add(0, 10, 0), Vector3f.UNIT_Y);
            } else if(name.equals("RotateRight")){
                Vector3f curr = cam.getLocation();
                Quaternion turn = new Quaternion();
                turn.fromAngleAxis(ROTATE_ANGLE * tpf,Vector3f.UNIT_Y);
                turn.multLocal(curr);
                cam.setLocation(curr);
                cam.lookAt(Vector3f.ZERO.add(0, 10, 0), Vector3f.UNIT_Y);
            } else if(name.equals("ZoomIn")){
                cam.setLocation(cam.getLocation().mult(.995f));
            } else if(name.equals("ZoomOut")){
                cam.setLocation(cam.getLocation().mult(1.005f));
            }
            
        }
        
    };
}
